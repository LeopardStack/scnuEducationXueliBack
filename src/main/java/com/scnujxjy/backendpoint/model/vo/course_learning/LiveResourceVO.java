package com.scnujxjy.backendpoint.model.vo.course_learning;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 直播资源
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LiveResourceVO extends CourseSectionContentVO{
    private Long id;

    private Long courseId;

    private Long sectionId;

    private String channelId;


    private String valid;

    private Date createdTime;

    private Date updatedTime;

}
