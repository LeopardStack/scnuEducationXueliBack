package com.scnujxjy.backendpoint.inverter.teaching_process;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleWithLiveInfoVO;
import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamRecordVO;
import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamRecordWithAutoUrlVO;
import com.scnujxjy.backendpoint.service.video_stream.VideoStreamRecordService;
import com.scnujxjy.backendpoint.util.ApplicationContextProvider;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.*;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface CourseScheduleInverter {

    @Mappings({})
    @Named("po2VO")
    CourseScheduleVO po2VO(CourseSchedulePO courseSchedulePO);

    @Mappings({
            @Mapping(target = "videoStreamRecord", source = "onlinePlatform", qualifiedByName = "liveInfo")
    })
    @IterableMapping(qualifiedByName = "po2LiveVO")
    CourseScheduleWithLiveInfoVO po2LiveVO(CourseSchedulePO courseSchedulePO);

    @Named("liveInfo")
    default VideoStreamRecordWithAutoUrlVO liveInfo(@Param("channelId") String videoSteamId) {
        if (StrUtil.isNotBlank(videoSteamId)) {
            VideoStreamRecordService videoStreamRecordService = ApplicationContextProvider.getApplicationContext().getBean(VideoStreamRecordService.class);
            VideoStreamRecordVO videoStreamRecordVO = videoStreamRecordService.detailById(Long.valueOf(videoSteamId));

            VideoStreamRecordWithAutoUrlVO vo = new VideoStreamRecordWithAutoUrlVO();

            // 使用BeanUtils.copyProperties方法复制属性值
            BeanUtils.copyProperties(videoStreamRecordVO, vo);

            List<String> roleList = StpUtil.getRoleList();
            String roleName = roleList.get(0);
            if(!roleName.equals("教师") && !("学生").equals(roleName)){
                // 管理员
            }else{
                if("教师".equals(roleName)){
                    String autoURL = videoStreamRecordService.generateAutoURL(videoStreamRecordVO.getChannelId());
                    if(autoURL != null){
                        vo.setAutoUrl(autoURL);
                    }
                }else if("学生".equals(roleName)){
                    vo.setUrl(null);
                    vo.setChannelPasswd(null);
                    vo.setPublisher(null);
                    vo.setAutoUrl(vo.getWatchUrl());
                }
            }
            return vo;
        }
        return null;
    }

    @Mappings({})
    @IterableMapping(qualifiedByName = "po2VO")
    List<CourseScheduleVO> po2VO(List<CourseSchedulePO> courseSchedulePOS);

    @Mappings({})
    List<CourseScheduleWithLiveInfoVO> po2LiveVO(List<CourseSchedulePO> courseSchedulePOS);

    @Mappings({})
    CourseSchedulePO ro2PO(CourseScheduleRO courseScheduleRO);
}


