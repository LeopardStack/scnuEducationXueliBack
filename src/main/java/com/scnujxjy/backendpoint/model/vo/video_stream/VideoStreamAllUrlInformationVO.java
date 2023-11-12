package com.scnujxjy.backendpoint.model.vo.video_stream;

import com.scnujxjy.backendpoint.dao.entity.video_stream.TutorInformation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 直播所有角色链接信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class VideoStreamAllUrlInformationVO {
    /**
     * 教室网页端链接
     */
    private String teacherWebUrl;

    /**
     * 教师客户端链接
     */
    private String teacherClientUrl;

    /**
     * 频道 id
     */
    private String channelId;

    /**
     * 教师端密码
     */
    private String teacherPassword;

    /**
     * 助教信息列表
     */
    private List<TutorInformation> tutorInformationList;

    /**
     * 观众链接
     */
    private String audienceUrl;

}
