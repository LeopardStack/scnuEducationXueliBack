package com.scnujxjy.backendpoint.service.video_stream;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
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

    public static final String WATCH_URL_FORMAT = "https://live.polyv.cn/watch/%s";
    public static final String TUTOR_URL_FORMAT = "https://live.polyv.net/teacher.html";
    public static final String TEACHER_URL_FORMAT = "https://live.polyv.net/web-start/login?channelId=%s";
    @Resource
    private VideoStreamUtils videoStreamUtils;
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
        VideoStreamRecordPO videoStreamRecordPO = baseMapper.selectById(id);
        return videoStreamInverter.po2VO(videoStreamRecordPO);
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
            String dateStr = DateUtil.format(courseScheduleVO.getTeachingDate(), "yyyy-MM-dd") + " " + courseScheduleVO.getTeachingTime();
            videoStreamRecordRO.setName(courseScheduleVO.getCourseName())
                    .setPublisher(courseScheduleVO.getTutorName())
                    .setStartTime(DateUtil.parse(dateStr, "yyyy-MM-dd HH:mm"))
                    .setMainTeacherName(courseScheduleVO.getMainTeacherName());
        }
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
            // 子频道信息入库
/*            List<VideoStreamRecordRO> sonRecordROS = videoStreamInverter.sonChannelResponseBO2RO(channelResponseBO.getSonChannelResponseBOS());
            if (CollUtil.isNotEmpty(sonRecordROS)) {
                List<VideoStreamRecordVO> sonRecordVOS = createBatch(sonRecordROS);
                if (CollUtil.isNotEmpty(sonRecordVOS)) {
                    // 子频道id给到主频道
                    List<Long> sonIds = sonRecordVOS.stream().map(VideoStreamRecordVO::getId).collect(Collectors.toList());
                    mainRecordRO.setSonId(sonIds);
                    // 将子频道放入结果中
                    videoStreamRecordVOS.addAll(sonRecordVOS);
                }
            }*/
            // 设置观看链接以及教师链接
            mainRecordRO.setUrl(String.format(TEACHER_URL_FORMAT, channelId));
            mainRecordRO.setWatchUrl(String.format(WATCH_URL_FORMAT, channelId));
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
            courseScheduleService.editById(CourseScheduleRO.builder().id(videoStreamRecordRO.getCourseScheduleId()).onlinePlatform(String.valueOf(videoStreamRecordVO.getId())).build());
            // todo 通知教师
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
     * 根据频道id关闭直播间
     *
     * @param channelId 频道id
     * @return
     */
    public Boolean closeVideoStream(String channelId) {
        if (StrUtil.isBlank(channelId)) {
            log.error("参数缺失");
            return false;
        }
        Map<String, Object> response = videoStreamUtils.videoStreamClose(channelId);
        if (Objects.equals(response.get("code"), HttpStatus.HTTP_OK)) {
            return true;
        }
        log.error("关闭失败，响应：{}", response);
        return false;
    }

}
