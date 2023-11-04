package com.scnujxjy.backendpoint.model.vo.teaching_process;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.model.ro.video_stream.VideoStreamRecordRO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 这个类是为了获取排序后的教师端的直播课表 合班的抽取公共信息 保留行政班别
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class TeacherSchedulesVO {
    /**
     * 课程信息
     */
    private String courseName;

    /**
     * 学生人数
     */
    private Integer StudentCount;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 授课方式 线下、直播、点播
     */
    private String teachingMethod;

    /**
     * 考核类型
     */
    private String examType;

    /**
     * 教学班别 统一命名
     * eg. 学前教育 李四
     **/
    private String teachingClass;

    /**
     * 存储这门课 的所有直播间信息
     */
    private List<VideoStreamRecordPO> videoStreamRecordROList;

    /**
     * 班级信息
     */
    private List<ClassInformationPO> classInformationPOList;
}
