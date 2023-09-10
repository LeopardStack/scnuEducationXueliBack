package com.scnujxjy.backendpoint.inverter.teaching_process;

import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleWithLiveInfoVO;
import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamRecordVO;
import com.scnujxjy.backendpoint.service.video_stream.VideoStreamRecordService;
import com.scnujxjy.backendpoint.util.ApplicationContextProvider;
import org.mapstruct.*;

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
    default VideoStreamRecordVO liveInfo(String channelId) {
        if (StrUtil.isNotBlank(channelId)) {
            VideoStreamRecordService videoStreamRecordService = ApplicationContextProvider.getApplicationContext().getBean(VideoStreamRecordService.class);
            VideoStreamRecordVO videoStreamRecordVO = videoStreamRecordService.detailByChannelId(channelId);
            if (Objects.isNull(channelId)) {
                return null;
            }
            return videoStreamRecordVO;
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


