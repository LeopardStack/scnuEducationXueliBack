package com.scnujxjy.backendpoint.service.video_stream;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.inverter.video_stream.VideoStreamInverter;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelRequestBO;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.ro.video_stream.VideoStreamRecordRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamRecordVO;
import com.scnujxjy.backendpoint.service.teaching_process.CourseScheduleService;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 直播记录表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-21
 */
@Service
@Slf4j
public class VideoStreamRecordService extends ServiceImpl<VideoStreamRecordsMapper, VideoStreamRecordPO> implements IService<VideoStreamRecordPO> {

    @Resource
    private VideoStreamInverter videoStreamInverter;


    @Resource
    private VideoStreamUtils videoStreamUtils;

    public static final String WATCH_URL_FORMAT = "https://live.polyv.cn/watch/%s";
    public static final String TUTOR_URL_FORMAT = "https://live.polyv.net/teacher.html";
    public static final String TEACHER_URL_FORMAT = "https://live.polyv.net/web-start/login?channelId=%s";
    @Resource
    private CourseScheduleService courseScheduleService;

    /**
     * 根据id查询直播间信息
     *
     * @param id
     * @return
     */
    public VideoStreamRecordVO detailById(Long id) {
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 根据api查询最新的情况
        VideoStreamRecordPO videoStreamRecordPO = baseMapper.selectById(id);
        if (Objects.isNull(videoStreamRecordPO)) {
            log.error("查询失败，id：{}", id);
            return null;
        }
        VideoStreamRecordPO res = videoStreamInverter.ro2PO(videoStreamInverter.channelResponseBO2RO(videoStreamUtils.getChannelBasicInfo(videoStreamRecordPO.getChannelId())));
        if (Objects.isNull(res)) {
            log.error("查询失败");
            return null;
        }
        res.setId(videoStreamRecordPO.getId());
        baseMapper.updateById(res);
        VideoStreamRecordVO videoStreamRecordVO = videoStreamInverter.po2VO(baseMapper.selectById(id));

        try {
            String s = videoStreamUtils.generateTeacherSSOLink(videoStreamRecordVO.getChannelId());
            videoStreamRecordVO.setAutoUrl(s);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return videoStreamRecordVO;
    }

    /**
     * 根据channelId查询直播间情况
     *
     * @param channelId
     * @return
     */
    public VideoStreamRecordVO detailByChannelId(String channelId) {
        if (StrUtil.isBlank(channelId)) {
            log.error("参数缺失");
            return null;
        }
        VideoStreamRecordPO res = videoStreamInverter.ro2PO(videoStreamInverter.channelResponseBO2RO(videoStreamUtils.getChannelBasicInfo(channelId)));
        baseMapper.update(res, Wrappers.<VideoStreamRecordPO>lambdaUpdate().eq(VideoStreamRecordPO::getChannelId, channelId));
        return videoStreamInverter.po2VO(baseMapper.selectOne(Wrappers.<VideoStreamRecordPO>lambdaQuery().eq(VideoStreamRecordPO::getChannelId, channelId)));
    }


    /**
     * 构建直播参数
     *
     * @param videoStreamRecordROS
     */
    private void generateVideoStreamRecord(List<VideoStreamRecordRO> videoStreamRecordROS) {
        if (CollUtil.isEmpty(videoStreamRecordROS)) {
            log.error("参数缺失");
        }
        for (VideoStreamRecordRO videoStreamRecordRO : videoStreamRecordROS) {
            if (Objects.isNull(videoStreamRecordRO.getCourseScheduleId())) {
                continue;
            }
            CourseScheduleVO courseScheduleVO = courseScheduleService.detailById(videoStreamRecordRO.getCourseScheduleId());
            Pair<DateTime, DateTime> startAndEndDateTime = verifyDateTime(courseScheduleVO.getTeachingDate(), courseScheduleVO.getTeachingTime());
            videoStreamRecordRO.setName(courseScheduleVO.getCourseName())
                    .setPublisher(courseScheduleVO.getTutorName())
                    .setStartTime(startAndEndDateTime.getKey())
                    .setEndTime(startAndEndDateTime.getValue())
                    .setMainTeacherName(courseScheduleVO.getMainTeacherName());
        }
    }

    /**
     * 对数据库中的时间进行转换
     * <p>如果使用12小时制度，则转换为24小时</p>
     * <p>如果时间格式错误，则使用当天时间</p>
     *
     * @param date 当天日期：yyyy-MM-dd
     * @param time 持续时间：HH:mm-HH:mm
     * @return 开始时间和结束时间
     */
    private Pair<DateTime, DateTime> verifyDateTime(Date date, String time) {
        String[] times = time.split("-");
        String start = "00:00";
        String end = "23:59";
        if (times.length >= 2) {
            start = times[0];
            end = times[1];
        }
        if (Objects.isNull(date)) {
            date = new Date();
        }
        String todoy = DateUtil.format(date, "yyyy-MM-dd");
        DateTime startDate = DateUtil.parse(todoy + " " + start, "yyyy-MM-dd HH:mm");
        DateTime endDate = DateUtil.parse(todoy + " " + end, "yyyy-MM-dd HH:mm");
        // 如果比当天的八点还要早，说明是下午
        if (DateUtil.compare(startDate, DateUtil.parse(todoy + " " + "8:00"), "yyyy-MM-dd HH:mm") < 0) {
            startDate = startDate.offsetNew(DateField.HOUR_OF_DAY, 12);
        }
        if (DateUtil.compare(endDate, DateUtil.parse(todoy + " " + "8:00"), "yyyy-MM-dd HH:mm") < 0) {
            endDate = endDate.offsetNew(DateField.HOUR_OF_DAY, 12);
        }

        return Pair.of(startDate, endDate);
    }

    /**
     * 根据直播信息批量创建
     *
     * @param videoStreamRecordROS 直播信息
     * @return 创建好的直播信息
     */
    @Transactional
    public List<List<VideoStreamRecordVO>> generateVideoStream(List<VideoStreamRecordRO> videoStreamRecordROS) {
        generateVideoStreamRecord(videoStreamRecordROS);
        if (CollUtil.isEmpty(videoStreamRecordROS)) {
            log.error("参数缺失");
            return null;
        }
        List<List<VideoStreamRecordVO>> res = new LinkedList<>();
        // 生成直播链接
        for (VideoStreamRecordRO videoStreamRecordRO : videoStreamRecordROS) {
            LinkedList<VideoStreamRecordVO> videoStreamRecordVOS = new LinkedList<>();
            String channelPasswd = RandomUtil.randomString(11);
            log.info("直播间密码密码为：{}", channelPasswd);
            ChannelRequestBO channelRequestBO = ChannelRequestBO.builder()
                    .name(videoStreamRecordRO.getName())
                    .channelPasswd(channelPasswd)
                    .linkMicLimit(-1)
                    .publisher(videoStreamRecordRO.getPublisher())
                    .startTime(videoStreamRecordRO.getStartTime().getTime())
                    .desc("测试用直播间")
                    .nickname(videoStreamRecordRO.getMainTeacherName())
                    .build();
            log.info("直播间信息：{}", channelRequestBO);
            ChannelResponseBO channelResponseBO = videoStreamUtils.createTeachChannel(channelRequestBO);
            String channelId = channelResponseBO.getChannelId();
            if (Objects.isNull(channelResponseBO)) {
                log.error("生成直播链接失败，排课表信息：{}", videoStreamRecordRO);
                videoStreamUtils.deleteView(channelId);
                // todo 通知管理员
                continue;
            }
            // 将生成的信息入库
            VideoStreamRecordRO mainRecordRO = videoStreamInverter.channelResponseBO2RO(channelResponseBO);
            // 设置观看链接以及教师链接
            mainRecordRO.setUrl(String.format(TEACHER_URL_FORMAT, channelId));
            mainRecordRO.setWatchUrl(String.format(WATCH_URL_FORMAT, channelId));
            mainRecordRO.setEndTime(videoStreamRecordRO.getEndTime());
            log.info("直播间信息：{}", mainRecordRO);
            // 主频道信息入库
            VideoStreamRecordVO videoStreamRecordVO = create(mainRecordRO);
            if (Objects.isNull(videoStreamRecordVO)) {
                log.error("频道信息入库失败");
                videoStreamUtils.deleteView(channelId);
                continue;
            }
            videoStreamRecordVOS.addFirst(videoStreamRecordVO);
            res.add(videoStreamRecordVOS);
            // 将生成的直播记录记录到排课表中
            courseScheduleService.generateVideoStream(CourseScheduleRO.builder().id(videoStreamRecordRO.getCourseScheduleId()).onlinePlatform(String.valueOf(videoStreamRecordVO.getId())).build());
        }
        log.info("课程信息生成完成");
        return res;
    }

    public VideoStreamRecordVO create(VideoStreamRecordRO videoStreamRecordRO) {
        if (Objects.isNull(videoStreamRecordRO)) {
            log.error("参数缺失");
            return null;
        }
        VideoStreamRecordPO videoStreamRecordPO = videoStreamInverter.ro2PO(videoStreamRecordRO);
        int count = baseMapper.insert(videoStreamRecordPO);
        if (count <= 0) {
            log.error("插入错误，数据：{}", videoStreamRecordPO);
            return null;
        }
        return detailById(videoStreamRecordPO.getId());
    }

    /**
     * 批量添加直播间
     *
     * @param videoStreamRecordROS
     * @return
     */
    @Transactional
    public List<VideoStreamRecordVO> createBatch(List<VideoStreamRecordRO> videoStreamRecordROS) {
        if (CollUtil.isEmpty(videoStreamRecordROS)) {
            log.error("参数缺失");
            return null;
        }
        List<VideoStreamRecordPO> videoStreamRecordPOS = videoStreamInverter.ro2PO(videoStreamRecordROS);
        boolean saved = saveBatch(videoStreamRecordPOS);
        if (!saved) {
            log.error("新增记录失败，已经回滚，数据：{}", videoStreamRecordPOS);
            return null;
        }
        Set<Long> idSet = videoStreamRecordPOS.stream().map(VideoStreamRecordPO::getId).collect(Collectors.toSet());
        List<VideoStreamRecordPO> recordPOS = baseMapper.selectBatchIds(idSet);
        return videoStreamInverter.po2VO(recordPOS);
    }

    /**
     * 开启或关闭直播间
     *
     * @param channelId 频道id
     * @param type      状态：0-关闭，1-开启
     * @return
     */
    public Boolean closeVideoStream(String channelId, Integer type) {
        if (StrUtil.isBlank(channelId) || Objects.isNull(type)) {
            log.error("参数缺失");
            return false;
        }
        Map<String, Object> map = null;
        if (Objects.equals(type, 0)) {
            map = videoStreamUtils.videoStreamClose(channelId);
        } else {
            map = videoStreamUtils.videoStreamResume(channelId);
        }
        if (Objects.isNull(map) || !Objects.equals(map.get("code"), HttpStatus.HTTP_OK)) {
            log.error("关闭失败，响应：{}", map);
            return false;
        }
        return true;
    }

    /**
     * 根据频道 ID 创建教师单点登录链接
     *
     * @param channelId 频道 ID
     * @return
     */
    public String generateAutoURL(String channelId) {
        try {
            return videoStreamUtils.generateTeacherSSOLink(channelId);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }

}
