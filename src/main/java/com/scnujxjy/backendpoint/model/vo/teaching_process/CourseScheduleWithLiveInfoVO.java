package com.scnujxjy.backendpoint.model.vo.teaching_process;

import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseScheduleWithLiveInfoVO extends CourseScheduleVO{
    /**
     * 直播间基本信息
     */
    ChannelResponseBO channelResponseBO = null;
}
