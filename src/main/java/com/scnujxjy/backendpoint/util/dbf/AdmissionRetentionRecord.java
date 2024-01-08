package com.scnujxjy.backendpoint.util.dbf;

import lombok.Data;

/**
 * 保留入学资格
 */
@Data
public class AdmissionRetentionRecord implements DbfRecord {
    private String ksh; // 考生号 (Examinee Number)
    private String xm;  // 姓名 (Name)
    private String ydyy;// 原因 (Reason for Retention)

    public AdmissionRetentionRecord(String ksh, String xm, String ydyy) {
        this.ksh = ksh;
        this.xm = xm;
        this.ydyy = ydyy;
    }

    @Override
    public Object[] toDbfRecord() {
        return new Object[] { ksh, xm, ydyy };
    }
}

