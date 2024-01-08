package com.scnujxjy.backendpoint.util.dbf;

import lombok.Data;

@Data
public class StudentStatusChangeRecord implements DbfRecord {
    private String ksh;       // 考生号
    private String xm;        // 姓名
    private String ydlx;      // 异动类型
    private String pzrq;      // 批准日期
    private String wh;        // 文号
    private String yy;        // 原因
    private String sm;        // 说明
    private String zydm;      // 专业代码
    private String zymc;      // 专业名称
    private String xz;        // 学制
    private String dqszj;     // 当前所在级
    private String xh;        // 学号
    private String fy;        // 分院
    private String xsh;       // 系（所、函授站）
    private String bh;        // 班号
    private String yjbyrq;    // 预计毕业日期

    public StudentStatusChangeRecord(String ksh, String xm, String ydlx, String pzrq, String wh, String yy, String sm, String zydm, String zymc, String xz, String dqszj, String xh, String fy, String xsh, String bh, String yjbyrq) {
        this.ksh = ksh;
        this.xm = xm;
        this.ydlx = ydlx;
        this.pzrq = pzrq;
        this.wh = wh;
        this.yy = yy;
        this.sm = sm;
        this.zydm = zydm;
        this.zymc = zymc;
        this.xz = xz;
        this.dqszj = dqszj;
        this.xh = xh;
        this.fy = fy;
        this.xsh = xsh;
        this.bh = bh;
        this.yjbyrq = yjbyrq;
    }

    // Getter and setter methods for all fields

    @Override
    public Object[] toDbfRecord() {
        return new Object[] { ksh, xm, ydlx, pzrq, wh, yy, sm, zydm, zymc, xz, dqszj, xh, fy, xsh, bh, yjbyrq };
    }
}

