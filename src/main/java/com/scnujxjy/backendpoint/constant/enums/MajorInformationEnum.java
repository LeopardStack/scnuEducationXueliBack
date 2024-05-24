package com.scnujxjy.backendpoint.constant.enums;

import lombok.Getter;

@Getter
public enum MajorInformationEnum {
    YINGYU("专升本", "文史类（含外语）", "英语", "业余", "3", 3450),
    CHUANBOXUE("专升本","文史类（含外语）", "传播学", "业余", "3", 3000),
    HANYUYANWENXUE("专升本","文史类（含外语）", "汉语言文学", "函授", "3", 3000),
    JIAOYUXUE("专升本","教育学类", "教育学", "函授", "3", 3000),
    XUEQIANJIAOYU("专升本","教育学类", "学前教育", "函授", "3", 3000),
    XIAOXUEJIAOYU("专升本","教育学类", "小学教育", "函授", "3", 3000),
    GUOJIJINGJIYUMAOYI("专升本","理工、经管类", "国际经济与贸易", "函授", "3", 3000),
    KUAIJIXUE("专升本","理工、经管类", "会计学", "函授", "3", 3000),
    RENLIZIYUANGUANLI("专升本","理工、经管类", "人力资源管理", "函授", "3", 3000),
    XINGZHENGGUANLI("专升本","理工、经管类", "行政管理", "函授", "3", 3000),
    JINGJIXUE("专升本","理工、经管类", "经济学", "函授", "3", 3000),
    JINGRONGXUE("专升本","理工、经管类", "金融学", "函授", "3", 3000),
    DIANZISHANGWU("专升本","理工、经管类", "电子商务", "函授", "3", 3000),
    GUANLIKEXUE("专升本","理工、经管类", "管理科学", "函授", "3", 3000),
    SHUXUEYUYINGYONGSHUXUE("专升本","理工、经管类", "数学与应用数学", "函授", "3", 3450),
    XINLIXUE("专升本","理工、经管类", "心理学", "函授", "3", 3450),
    YINGYONGXINLIXUE("专升本","理工、经管类", "应用心理学", "函授", "3", 3450),
    JISUANJIKEXUEYUJISHU("专升本","理工、经管类", "计算机科学与技术", "函授", "3", 3450),
    RENGONGZHINENG("专升本","理工、经管类", "人工智能", "函授", "3", 3450),
    SHEHUIGONGZUO("专升本","法学", "社会工作", "函授", "3", 3000),
    FAXUE("专升本","法学", "法学", "函授", "3", 3000),
    RENLIZIYUANGUANLI2("高起专","文史类", "人力资源管理", "业余", "3", 2800),
    XINGZHENGGUANLI2("高起专","文史类", "行政管理", "业余", "3", 2800),
    XUEQIANJIAOYU2("高起专","文史类", "学前教育", "函授", "3", 2800),
    XIANDAIWULIUGUANLI("高起专","文史类", "现代物流管理", "函授", "3", 2800);

    private final String level;
    private final String majorCategory;
    private final String majorName;
    private final String studyForm;
    private final String studyDuration;
    private final int tuitionFee;

    MajorInformationEnum(String level, String majorCategory, String majorName, String studyForm, String studyDuration, int tuitionFee) {
        this.level = level;
        this.majorCategory = majorCategory;
        this.majorName = majorName;
        this.studyForm = studyForm;
        this.studyDuration = studyDuration;
        this.tuitionFee = tuitionFee;
    }

    public static boolean existsByMajorNameAndLevel(String majorName, String level) {
        for (MajorInformationEnum major : MajorInformationEnum.values()) {
            if (major.getMajorName().equals(majorName) && major.getLevel().equals(level)) {
                return true;
            }
        }
        return false;
    }

    public static MajorInformationEnum getByMajorNameAndLevel(String majorName, String level) {
        for (MajorInformationEnum major : MajorInformationEnum.values()) {
            if (major.getMajorName().equals(majorName) && major.getLevel().equals(level)) {
                return major;
            }
        }
        return null;
    }

    public String getMajorCategory() {
        return majorCategory;
    }

    public String getMajorName() {
        return majorName;
    }

    public String getStudyForm() {
        return studyForm;
    }

    public String getStudyDuration() {
        return studyDuration;
    }

    public int getTuitionFee() {
        return tuitionFee;
    }
}
