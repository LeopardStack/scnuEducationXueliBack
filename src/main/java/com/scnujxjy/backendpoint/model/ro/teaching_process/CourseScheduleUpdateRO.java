package com.scnujxjy.backendpoint.model.ro.teaching_process;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseScheduleUpdateRO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排课表主键 ID
     */
    private Long id;

    /**
     * 直播开始时间
     */

    @JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone="Asia/Shanghai")
    private Date teachingStartDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone="Asia/Shanghai")
    private Date teachingEndDate;


    /**
     * 教师姓名
     */
    String teacherName;

    /**
     * 教师工号/学号
     */
    String teacherId;

    /**
     * 教师身份证号码
     */
    String teacherIdentity;
}
