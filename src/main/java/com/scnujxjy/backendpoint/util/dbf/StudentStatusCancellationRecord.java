package com.scnujxjy.backendpoint.util.dbf;


import lombok.Data;

/**
 * 学籍注销 退学
 */
@Data
public class StudentStatusCancellationRecord implements DbfRecord {
    private String ksh;    // 考生号
    private String xh;     // 学号
    private String xm;     // 姓名
    private String sfzh;   // 身份证号
    private String zxlx;   // 注销类型
    private String pzrq;   // 批准日期
    private String wh;     // 文号

    public StudentStatusCancellationRecord(String ksh, String xh, String xm, String sfzh, String zxlx, String pzrq, String wh) {
        this.ksh = ksh;
        this.xh = xh;
        this.xm = xm;
        this.sfzh = sfzh;
        this.zxlx = zxlx;
        this.pzrq = pzrq;
        this.wh = wh;
    }

    @Override
    public Object[] toDbfRecord() {
        return new Object[]{ksh, xh, xm, sfzh, zxlx, pzrq, wh};
    }

    // Lombok @Data generates all getters and setters.
}

