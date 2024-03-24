package com.scnujxjy.backendpoint.model.vo.course_learning;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoResourceVO extends CourseSectionContentVO{
    private Long id;

    private Long sectionId;

    private String videoName;

    private String minioVideoUrl;

    private String cdnVideoUrl;

    private String valid;

    private Date createdTime;

    private Date updatedTime;
}
