package com.scnujxjy.backendpoint.util.dbf;


import lombok.Data;

/**
 * 学籍信息修改
 */
@Data
public class StudentInfoModificationRecord implements DbfRecord {
    private String ksh;    // 考生号
    private String xh;     // 学号
    private String xm;     // 姓名
    private String xb;     // 性别
    private String csrq;   // 出生日期
    private String zjlx;   // 证件类型
    private String zjhm;   // 证件号码
    private String zzmm;   // 政治面貌
    private String mz;     // 民族
    private String zydm;   // 专业代码
    private String zymc;   // 专业名称
    private String xxxs;   // 学习形式
    private String cc;     // 层次
    private String xz;     // 学制
    private String rxrq;   // 入学日期
    private String fy;     // 分院
    private String xsh;    // 系（所、函授站）
    private String bh;     // 班号
    private String yjbyrq; // 预计毕业日期

    public StudentInfoModificationRecord(String ksh, String xh, String xm, String xb, String csrq, String zjlx, String zjhm, String zzmm, String mz, String zydm, String zymc, String xxxs, String cc, String xz, String rxrq, String fy, String xsh, String bh, String yjbyrq) {
        this.ksh = ksh;
        this.xh = xh;
        this.xm = xm;
        this.xb = xb;
        this.csrq = csrq;
        this.zjlx = zjlx;
        this.zjhm = zjhm;
        this.zzmm = zzmm;
        this.mz = mz;
        this.zydm = zydm;
        this.zymc = zymc;
        this.xxxs = xxxs;
        this.cc = cc;
        this.xz = xz;
        this.rxrq = rxrq;
        this.fy = fy;
        this.xsh = xsh;
        this.bh = bh;
        this.yjbyrq = yjbyrq;
    }

    @Override
    public Object[] toDbfRecord() {
        return new Object[]{ksh, xh, xm, xb, csrq, zjlx, zjhm, zzmm, mz, zydm, zymc, xxxs, cc, xz, rxrq, fy, xsh, bh, yjbyrq};
    }

    // Lombok @Data generates all getters and setters.
}

