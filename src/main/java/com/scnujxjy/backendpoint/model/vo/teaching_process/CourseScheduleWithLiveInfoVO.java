package com.scnujxjy.backendpoint.model.vo.teaching_process;

import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamRecordVO;
import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamRecordWithAutoUrlVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseScheduleWithLiveInfoVO extends CourseScheduleVO {
    /**
     * 直播间基本信息
     */
    VideoStreamRecordWithAutoUrlVO videoStreamRecord = null;
}
