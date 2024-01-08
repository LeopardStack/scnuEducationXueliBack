package com.scnujxjy.backendpoint.util.dbf;

import lombok.Data;

/**
 * 前置学历复查
 */
@Data
public class StudentStatusReviewRecord implements DbfRecord {
    private String ksh;        // 考生号
    private String xm;         // 姓名
    private String qzbh;       // 前置学历报告编号或在线验证码
    private String qzxlzsbh;   // 前置学历证书编号

    public StudentStatusReviewRecord(String ksh, String xm, String qzbh, String qzxlzsbh) {
        this.ksh = ksh;
        this.xm = xm;
        this.qzbh = qzbh;
        this.qzxlzsbh = qzxlzsbh;
    }

    @Override
    public Object[] toDbfRecord() {
        return new Object[]{ksh, xm, qzbh, qzxlzsbh};
    }

    // Lombok @Data generates all getters and setters.
}

