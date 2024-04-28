package com.scnujxjy.backendpoint.constant.enums.office_automation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author lth
 * @version 1.0
 * @description TODO
 * @date 2024/4/26 15:39
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum SystemMessageType1Enum {
    TRANSACTION_APPROVAL("事务审批");

    String typeName;

    public static SystemMessageType1Enum match(String typeName){
        for(SystemMessageType1Enum systemMessageType1Enum : SystemMessageType1Enum.values()){
            if(systemMessageType1Enum.getTypeName().equals(typeName)){
                return systemMessageType1Enum;
            }
        }
        return null;
    }

}
