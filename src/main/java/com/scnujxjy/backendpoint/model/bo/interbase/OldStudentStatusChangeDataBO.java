package com.scnujxjy.backendpoint.model.bo.interbase;

import lombok.Data;

@Data
public class OldStudentStatusChangeDataBO {
    /**
     * 新的年级
     */
    private String new_NJ;
    /**
     * 办理时间
     */
    private String UPDATETIME;

    /**
     * 身份证号码
     */
    private String SFZH;

    /**
     * 开始时间1
     */
    private String FROM1;
    /**
     * 截止时间1
     */
    private String TO1;

    /**
     * 开始时间2
     */
    private String FROM2;

    /**
     * 截止时间2
     */
    private String TO2;

    /**
     * 学籍异动类型
     */
    private String CTYPE;
    /**
     * 专业名称
     */
    private String ZHY;
    /**
     * 新的专业名称
     */
    private String new_ZHY;
    /**
     * 班级标识
     */
    private String BSHI;
    /**
     * 办理号
     */
    private String IDNUM;

    /**
     * 新的班级标识
     */
    private String new_BSHI;
    /**
     * 新的学习形式
     */
    private String new_XSHI;
    /**
     * 准考证号码
     */
    private String ZKZH;
    /**
     * 姓名
     */
    private String XM;
    /**
     * 学习形式
     */
    private String XSHI;

    /**
     * 学号
     */
    private String XHAO;
    /**
     * 异动原因
     */
    private String REASON;
    /**
     * 年级
     */
    private String NJ;
    /**
     * 关联
     */
    private String ABOUT;
}
