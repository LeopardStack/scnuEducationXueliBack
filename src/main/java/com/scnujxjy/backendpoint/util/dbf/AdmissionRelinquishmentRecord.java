package com.scnujxjy.backendpoint.util.dbf;

import lombok.Data;

/**
 * 放弃入学资格
 */
@Data
public class AdmissionRelinquishmentRecord implements DbfRecord {
    private String ksh; // 考生号 (Examinee Number)
    private String xm;  // 姓名 (Name)
    private String bz;  // 原因/备注 (Reason/Remark)

    public AdmissionRelinquishmentRecord(String ksh, String xm, String bz) {
        this.ksh = ksh;
        this.xm = xm;
        this.bz = bz;
    }

    @Override
    public Object[] toDbfRecord() {
        return new Object[] { ksh, xm, bz };
    }
}

