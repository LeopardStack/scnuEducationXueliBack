package com.scnujxjy.backendpoint.util.dbf;

import lombok.Data;

/**
 * 学历注册
 */
@Data
public class AcademicRegistrationRecord implements DbfRecord {
    private String ksh;     // 考生号
    private String xm;      // 姓名
    private String xb;      // 性别
    private String csrq;    // 出生日期
    private String sfzh;    // 身份证号
    private String yxdm;    // 院校代码
    private String yxmc;    // 院校名称
    private String zydm;    // 专业代码
    private String zymc;    // 专业名称
    private String xz;      // 学制
    private String xxxs;    // 学习形式
    private String cc;      // 层次
    private String rxrq;    // 入学日期
    private String byrq;    // 毕业日期
    private String bjyjl;   // 毕结业结论
    private String zsbh;    // 证书编号
    private String xzm;     // 校长名
    private String bz;      // 备注

    public AcademicRegistrationRecord(String ksh, String xm, String xb, String csrq, String sfzh, String yxdm, String yxmc, String zydm, String zymc, String xz, String xxxs, String cc, String rxrq, String byrq, String bjyjl, String zsbh, String xzm, String bz) {
        this.ksh = ksh;
        this.xm = xm;
        this.xb = xb;
        this.csrq = csrq;
        this.sfzh = sfzh;
        this.yxdm = yxdm;
        this.yxmc = yxmc;
        this.zydm = zydm;
        this.zymc = zymc;
        this.xz = xz;
        this.xxxs = xxxs;
        this.cc = cc;
        this.rxrq = rxrq;
        this.byrq = byrq;
        this.bjyjl = bjyjl;
        this.zsbh = zsbh;
        this.xzm = xzm;
        this.bz = bz;
    }

    @Override
    public Object[] toDbfRecord() {
        return new Object[]{ksh, xm, xb, csrq, sfzh, yxdm, yxmc, zydm, zymc, xz, xxxs, cc, rxrq, byrq, bjyjl, zsbh, xzm, bz};
    }
}
