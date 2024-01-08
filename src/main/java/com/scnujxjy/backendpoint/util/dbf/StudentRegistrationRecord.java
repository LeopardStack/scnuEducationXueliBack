package com.scnujxjy.backendpoint.util.dbf;

import lombok.Data;

/**
 * 学籍注册
 */
@Data
public class StudentRegistrationRecord implements DbfRecord {
    private String ksh;    // 考生号
    private String xh;     // 学号
    private String xm;     // 姓名
    private String xb;     // 性别
    private String csrq;   // 出生日期
    private String sfzh;   // 身份证号
    private String zzmm;   // 政治面貌
    private String mz;     // 民族
    private String zydm;   // 专业代码
    private String zymc;   // 专业名称
    private String fy;     // 分院
    private String xsh;    // 系（所、函授站）
    private String bh;     // 班号
    private String cc;     // 层次
    private String xxxs;   // 学习形式
    private String xz;     // 学制
    private String rxrq;   // 入学日期
    private String yjbyrq; // 预计毕业日期

    public StudentRegistrationRecord(String ksh, String xh, String xm, String xb, String csrq, String sfzh, String zzmm, String mz, String zydm, String zymc, String fy, String xsh, String bh, String cc, String xxxs, String xz, String rxrq, String yjbyrq) {
        this.ksh = ksh;
        this.xh = xh;
        this.xm = xm;
        this.xb = xb;
        this.csrq = csrq;
        this.sfzh = sfzh;
        this.zzmm = zzmm;
        this.mz = mz;
        this.zydm = zydm;
        this.zymc = zymc;
        this.fy = fy;
        this.xsh = xsh;
        this.bh = bh;
        this.cc = cc;
        this.xxxs = xxxs;
        this.xz = xz;
        this.rxrq = rxrq;
        this.yjbyrq = yjbyrq;
    }

    @Override
    public Object[] toDbfRecord() {
        return new Object[]{ksh, xh, xm, xb, csrq, sfzh, zzmm, mz, zydm, zymc, fy, xsh, bh, cc, xxxs, xz, rxrq, yjbyrq};
    }
}

