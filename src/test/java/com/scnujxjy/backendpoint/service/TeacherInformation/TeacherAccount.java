package com.scnujxjy.backendpoint.service.TeacherInformation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherAccount {
    /**
     * 账号类型：工号、手机号码、身份证号码
     */
    private String accountType = null;
    /**
     * 账号本身 T + XXXX
     */
    private String accountName = null;

    public void setAll(String accountType, String accountName){
        this.accountType = accountType;
        this.accountName = accountName;
    }
}
