package com.scnujxjy.backendpoint.dao.entity.video_stream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class TutorAllInformation {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 频道id
     */
    private String channelId;

    /**
     * 助教名称
     */
    private String tutorName;

    /**
     * 频道链接
     */
    private String tutorUrl;

    /**
     * 频道密码
     */
    private String tutorPassword;

    /**
     * 助教 userId
     */
    private String userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String gender;

    /**
     * 出生年月
     */
    private Date birthDate;

    /**
     * 政治面貌
     */
    private String politicalStatus;

    /**
     * 学历
     */
    private String education;

    /**
     * 学位
     */
    private String degree;

    /**
     * 专业技术职称
     */
    private String professionalTitle;

    /**
     * 职称级别
     */
    private String titleLevel;

    /**
     * 毕业学校
     */
    private String graduationSchool;

    /**
     * 现任职单位
     */
    private String currentPosition;

    /**
     * 所属学院
     */
    private String collegeId;

    /**
     * 所属教学点
     */
    private String teachingPoint;

    /**
     * 行政职务
     */
    private String administrativePosition;

    /**
     * 工号/学号
     */
    private String workNumber;

    /**
     * 身份证号码
     */
    private String idCardNumber;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 开始聘用学期
     */
    private String startTerm;

    /**
     * 教师类型1
     */
    private String teacherType1;

    /**
     * 教师类型2
     */
    private String teacherType2;

    /**
     * 教师的平台用户名
     */
    private String teacherUsername;
}
