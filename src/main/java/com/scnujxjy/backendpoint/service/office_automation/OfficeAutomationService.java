package com.scnujxjy.backendpoint.service.office_automation;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalTypePO;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalRecordMapper;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalStepMapper;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalStepRecordMapper;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalTypeMapper;
import com.scnujxjy.backendpoint.exception.BusinessException;
import com.scnujxjy.backendpoint.inverter.office_automation.ApprovalInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalRecordAllInformation;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalStepWithRecordList;
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

    @Resource
    private ApprovalRecordMapper approvalRecordMapper;

    @Resource
    private ApprovalStepRecordMapper approvalStepRecordMapper;

    @Resource
    private ApprovalInverter approvalInverter;

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
     * @param approvalTypePOPageRO 分页查询参数
     * @return
     */
    public PageVO<ApprovalTypeAllInformation> pageQueryApprovalTypeAllInformation(PageRO<ApprovalTypePO> approvalTypePOPageRO) {
        if (Objects.isNull(approvalTypePOPageRO)) {
            return null;
        }
        ApprovalTypePO approvalTypePO = approvalTypePOPageRO.getEntity();
        if (Objects.isNull(approvalTypePO)) {
            approvalTypePO = new ApprovalTypePO();
        }
        LambdaQueryWrapper<ApprovalTypePO> wrapper = Wrappers.<ApprovalTypePO>lambdaQuery()
                .eq(Objects.nonNull(approvalTypePO.getId()), ApprovalTypePO::getId, approvalTypePO.getId())
                .like(StrUtil.isNotBlank(approvalTypePO.getName()), ApprovalTypePO::getName, approvalTypePO.getName())
                .like(StrUtil.isNotBlank(approvalTypePO.getDescription()), ApprovalTypePO::getDescription, approvalTypePO.getDescription());
        List<ApprovalTypeAllInformation> result = new ArrayList<>();
        if (approvalTypePOPageRO.getIsAll().equals(true)) {
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
            return new PageVO<>(approvalTypePOPageRO.getPage(), result);
        } else {
            Page<ApprovalTypePO> approvalTypePOPage = approvalTypeMapper.selectPage(approvalTypePOPageRO.getPage(), wrapper);
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
     * 分页查询OA 记录数据
     *
     * @param approvalRecordPOPageRO
     * @return
     */
    public PageVO<ApprovalRecordAllInformation> pageQueryApprovalRecordAllInformation(PageRO<ApprovalRecordPO> approvalRecordPOPageRO) {
        if (Objects.isNull(approvalRecordPOPageRO)) {
            throw new BusinessException("分页参数为空，无法查询");
        }
        ApprovalRecordPO approvalRecordPO = approvalRecordPOPageRO.getEntity();
        if (Objects.isNull(approvalRecordPO)) {
            approvalRecordPO = new ApprovalRecordPO();
        }
        LambdaQueryWrapper<ApprovalRecordPO> wrapper = Wrappers.<ApprovalRecordPO>lambdaQuery()
                .eq(Objects.nonNull(approvalRecordPO.getApprovalTypeId()), ApprovalRecordPO::getApprovalTypeId, approvalRecordPO.getApprovalTypeId())
                .eq(StrUtil.isNotBlank(approvalRecordPO.getInitiatorUserId()), ApprovalRecordPO::getInitiatorUserId, approvalRecordPO.getInitiatorUserId())
                .eq(StrUtil.isNotBlank(approvalRecordPO.getStatus()), ApprovalRecordPO::getStatus, approvalRecordPO.getStatus());
        Page<ApprovalRecordPO> approvalRecordPOPage = approvalRecordMapper.selectPage(approvalRecordPOPageRO.getPage(), wrapper);
        if (Objects.isNull(approvalRecordPOPage) || CollUtil.isEmpty(approvalRecordPOPage.getRecords())) {
            throw new BusinessException("OA 记录数据查询为空");
        }
        List<ApprovalRecordAllInformation> result = approvalRecordPOPage.getRecords()
                .stream()
                .map(record -> {
                    // 查询排序好的步骤
                    List<ApprovalStepPO> approvalStepPOS = selectStepByType(record.getApprovalTypeId());
                    List<ApprovalStepWithRecordList> approvalStepWithRecordLists = new ArrayList<>();
                    if (CollUtil.isNotEmpty(approvalStepPOS)) {
                        approvalStepPOS.forEach(step -> {
                            List<ApprovalStepRecordPO> approvalStepRecordPOS = approvalStepRecordMapper.selectList(Wrappers.<ApprovalStepRecordPO>lambdaQuery()
                                    .eq(ApprovalStepRecordPO::getStepId, step.getId())
                                    .eq(ApprovalStepRecordPO::getApprovalId, record.getId()));
                            approvalStepWithRecordLists.add(approvalInverter.step2ApprovalStepWithRecordList(step, approvalStepRecordPOS));
                        });
                    }
                    return approvalInverter.approvalRecordStep2Information(record, approvalStepWithRecordLists);
                })
                .collect(Collectors.toList());
        return new PageVO<>(approvalRecordPOPage, result);
    }

    /**
     * 根据typeId查询步骤
     * <p>结果已经根据stepOrder排序</p>
     *
     * @param typeId 类型id
     * @return
     */
    private List<ApprovalStepPO> selectStepByType(Long typeId) {
        if (Objects.isNull(typeId)) {
            return null;
        }
        return approvalStepMapper.selectList(Wrappers.<ApprovalStepPO>lambdaQuery()
                .eq(ApprovalStepPO::getApprovalTypeId, typeId)
                .orderBy(true, true, ApprovalStepPO::getStepOrder));
    }

}
