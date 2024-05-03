package com.scnujxjy.backendpoint.constant.enums.office_automation;

import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalTypePO;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.approval.ApprovalTypeMapper;
import com.scnujxjy.backendpoint.util.ApplicationContextProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum OfficeAutomationHandlerType {

    /**
     * 保证数据库中的name与枚举类中的name一致
     */
    STUDENT_SCHOOL_IN_TRANSFER_MAJOR("student-school-in-transfer-major", "校内转专业"),
    STUDENT_SCHOOL_OUT_TRANSFER_MAJOR("student-school-out-transfer-major", "校外转专业"),
    COMMON("common", "默认审批流程");
    String type;

    String name;

    /**
     * 根据类型id查找数据库中的数据
     * <p>需要保证数据库中的name与枚举类中的name一致</p>
     *
     * @param typeId 类型id
     * @return
     */
    public static OfficeAutomationHandlerType match(Long typeId) {
        ApprovalTypeMapper approvalTypeMapper = ApplicationContextProvider.getApplicationContext().getBean(ApprovalTypeMapper.class);
        ApprovalTypePO approvalTypePO = approvalTypeMapper.selectById(typeId);
        if (Objects.isNull(approvalTypePO) || StrUtil.isBlank(approvalTypePO.getName())) {
            return null;
        }
        for (OfficeAutomationHandlerType value : OfficeAutomationHandlerType.values()) {
            if (approvalTypePO.getName().equals(value.getName())) {
                return value;
            }
        }
        return null;
    }


}
