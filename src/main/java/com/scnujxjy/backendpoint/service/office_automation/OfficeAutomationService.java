package com.scnujxjy.backendpoint.service.office_automation;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalTypePO;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalStepMapper;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalTypeMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalTypeAllInformation;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType.match;

@Service
public class OfficeAutomationService {

    private final Map<OfficeAutomationHandlerType, OfficeAutomationHandler> officeAutomationHandlers;

    @Resource
    private ApprovalStepMapper approvalStepMapper;

    @Resource
    private ApprovalTypeMapper approvalTypeMapper;

    public OfficeAutomationService(List<OfficeAutomationHandler> officeAutomationList) {
        officeAutomationHandlers = officeAutomationList.stream()
                .collect(Collectors.toMap(
                        OfficeAutomationHandler::supportType,
                        Function.identity()
                ));
    }

    /**
     * 根据类型获取具体处理类
     *
     * @param handlerType 处理类型
     * @return
     * @see OfficeAutomationHandlerType
     */
    private OfficeAutomationHandler getHandler(OfficeAutomationHandlerType handlerType) {
        return Optional.ofNullable(officeAutomationHandlers.get(handlerType))
                .orElseThrow(() -> new IllegalArgumentException("不支持的OA类型"));
    }

    public void trigger() {
        OfficeAutomationHandler automationHandler = getHandler(match("student-transfer-major"));
        System.out.println(automationHandler);
    }

    /**
     * 分页查询OA类型以及步骤
     *
     * @param approvalAllInformationPageRO 分页查询参数
     * @return
     */
    public PageVO<ApprovalTypeAllInformation> pageQuery(PageRO<ApprovalTypeAllInformation> approvalAllInformationPageRO) {
        if (Objects.isNull(approvalAllInformationPageRO)) {
            return null;
        }
        ApprovalTypeAllInformation approvalTypeAllInformation = approvalAllInformationPageRO.getEntity();
        if (Objects.isNull(approvalTypeAllInformation)) {
            approvalTypeAllInformation = new ApprovalTypeAllInformation();
        }
        LambdaQueryWrapper<ApprovalTypePO> wrapper = Wrappers.<ApprovalTypePO>lambdaQuery()
                .eq(Objects.nonNull(approvalTypeAllInformation.getId()), ApprovalTypePO::getId, approvalTypeAllInformation.getId())
                .like(StrUtil.isNotBlank(approvalTypeAllInformation.getName()), ApprovalTypePO::getName, approvalTypeAllInformation.getName())
                .like(StrUtil.isNotBlank(approvalTypeAllInformation.getDescription()), ApprovalTypePO::getDescription, approvalTypeAllInformation.getDescription());
        List<ApprovalTypeAllInformation> result = new ArrayList<>();
        if (approvalAllInformationPageRO.getIsAll().equals(true)) {
            List<ApprovalTypePO> approvalTypePOS = approvalTypeMapper.selectList(wrapper);
            if (CollUtil.isEmpty(approvalTypePOS)) {
                return null;
            }
            approvalTypePOS.forEach(ele -> {
                List<ApprovalStepPO> approvalStepPOS = selectStepByType(ele.getId());
                if (CollUtil.isNotEmpty(approvalStepPOS)) {
                    result.add(ApprovalTypeAllInformation.builder()
                            .id(ele.getId())
                            .description(ele.getDescription())
                            .name(ele.getName())
                            .approvalStepList(approvalStepPOS)
                            .build());
                }
            });
            return new PageVO<>(approvalAllInformationPageRO.getPage(), result);
        } else {
            Page<ApprovalTypePO> approvalTypePOPage = approvalTypeMapper.selectPage(approvalAllInformationPageRO.getPage(), wrapper);
            if (Objects.nonNull(approvalTypePOPage) && CollUtil.isNotEmpty(approvalTypePOPage.getRecords())) {
                List<ApprovalTypePO> approvalTypePOS = approvalTypePOPage.getRecords();
                approvalTypePOS.forEach(ele -> {
                    List<ApprovalStepPO> approvalStepPOS = selectStepByType(ele.getId());
                    if (CollUtil.isNotEmpty(approvalStepPOS)) {
                        result.add(ApprovalTypeAllInformation.builder()
                                .id(ele.getId())
                                .name(ele.getName())
                                .description(ele.getDescription())
                                .approvalStepList(approvalStepPOS)
                                .build());
                    }
                });
            }
            return new PageVO<>(approvalTypePOPage, result);
        }
    }

    /**
     * 根据typeId查询步骤
     *
     * @param typeId 类型id
     * @return
     */
    private List<ApprovalStepPO> selectStepByType(Long typeId) {
        if (Objects.isNull(typeId)) {
            return null;
        }
        return approvalStepMapper.selectList(Wrappers.<ApprovalStepPO>lambdaQuery()
                .eq(ApprovalStepPO::getApprovalTypeId, typeId));
    }

}
