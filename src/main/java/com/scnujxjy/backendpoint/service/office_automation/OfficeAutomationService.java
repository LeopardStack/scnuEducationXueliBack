package com.scnujxjy.backendpoint.service.office_automation;

import cn.dev33.satoken.stp.StpUtil;
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
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalRecordAllInformation;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalStepWithRecordList;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalTypeAllInformation;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
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

    @Resource
    private PlatformUserService platformUserService;

    public OfficeAutomationService(List<OfficeAutomationHandler> officeAutomationList, List<MongoRepository> mongoRepositories) {
        officeAutomationHandlers = officeAutomationList.stream()
                .collect(Collectors.toMap(
                        OfficeAutomationHandler::supportType,
                        Function.identity()
                ));
    }

    /**
     * 根据类型获取具体处理类
     *
     * @param typeId 类型 id
     * @return
     * @see OfficeAutomationHandlerType
     */
    private OfficeAutomationHandler getHandler(Long typeId) {
        OfficeAutomationHandlerType handlerType = OfficeAutomationHandlerType.match(typeId);
        return Optional.ofNullable(officeAutomationHandlers.get(handlerType))
                .orElseThrow(() -> new BusinessException("获取 OA 类型失败"));
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
                .eq(Objects.nonNull(approvalRecordPO.getInitiatorUsername()), ApprovalRecordPO::getInitiatorUsername, approvalRecordPO.getInitiatorUsername())
                .eq(StrUtil.isNotBlank(approvalRecordPO.getStatus()), ApprovalRecordPO::getStatus, approvalRecordPO.getStatus())
                .and(lambdaQueryWrapper -> {
                    lambdaQueryWrapper.last(String.format("JSON_CONTAINS(user_watch_set, JSON_ARRAY(%s))", String.valueOf(platformUserService.getUserIdByUsername(StpUtil.getLoginIdAsString()))));
                });
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

    /**
     * 根据审批记录id查询审批记录信息及其步骤信息
     *
     * @param approvalId 审批记录Id
     * @return 审批记录、步骤信息
     */
    public ApprovalRecordAllInformation approvalRecordDetail(Long approvalId) {
        if (Objects.isNull(approvalId)) {
            return null;
        }
        ApprovalRecordPO approvalRecordPO = approvalRecordMapper.selectById(approvalId);
        if (Objects.isNull(approvalRecordPO)
                || Objects.isNull(approvalRecordPO.getApprovalTypeId())
                || CollUtil.isEmpty(approvalRecordPO.getWatchUsernameSet())) {
            throw new BusinessException("获取审批记录失败");
        }
        PlatformUserVO platformUserVO = platformUserService.detailByUsername(StpUtil.getLoginIdAsString());
        if (Objects.isNull(platformUserVO)
                || Objects.isNull(platformUserVO.getUserId())) {
            throw new BusinessException("无法查询到账户信息");
        }
        if (!CollUtil.contains(approvalRecordPO.getWatchUsernameSet(), platformUserVO.getUsername())) {
            throw new BusinessException("该账户无权限查询");
        }
        List<ApprovalStepPO> approvalStepPOS = selectStepByType(approvalRecordPO.getApprovalTypeId());
        if (CollUtil.isEmpty(approvalStepPOS)) {
            throw new BusinessException("获取审批步骤为空");
        }
        List<ApprovalStepWithRecordList> approvalStepWithRecordLists = approvalStepPOS.stream()
                .map(step -> {
                    List<ApprovalStepRecordPO> approvalStepRecordPOS = approvalStepRecordMapper.selectList(Wrappers.<ApprovalStepRecordPO>lambdaQuery()
                            .eq(ApprovalStepRecordPO::getStepId, step.getId())
                            .eq(ApprovalStepRecordPO::getApprovalId, approvalRecordPO.getId()));
                    return approvalInverter.step2ApprovalStepWithRecordList(step, approvalStepRecordPOS);
                }).collect(Collectors.toList());
        if (CollUtil.isEmpty(approvalStepWithRecordLists)) {
            throw new BusinessException("获取步骤全部信息为空");
        }
        return approvalInverter.approvalRecordStep2Information(approvalRecordPO, approvalStepWithRecordLists);
    }

    @Transactional
    public Boolean createApprovalRecord(ApprovalRecordPO approvalRecordPO) {
        if (Objects.isNull(approvalRecordPO)
                || Objects.isNull(approvalRecordPO.getApprovalTypeId())) {
            throw new BusinessException("获取审核记录参数失败");
        }
        OfficeAutomationHandler handler = getHandler(approvalRecordPO.getApprovalTypeId());
        return handler.createApprovalRecord(approvalRecordPO);

    }

    /**
     * 审批流程
     *
     * @param approvalStepRecordPO 审批流程参数，其中：审批id、步骤id、审批意见、状态、审批类型必填
     * @return
     */
    @Transactional
    public Boolean processApproval(ApprovalStepRecordPO approvalStepRecordPO) {
        if (Objects.isNull(approvalStepRecordPO)
                || Objects.isNull(approvalStepRecordPO.getApprovalTypeId())) {
            throw new BusinessException("缺少参数，无法审批");
        }
        OfficeAutomationHandler automationHandler = getHandler(approvalStepRecordPO.getApprovalTypeId());
        if (Objects.isNull(automationHandler)) {
            throw new BusinessException("后台出错，请详细管理员");
        }
        approvalStepRecordPO.setUsername(StpUtil.getLoginIdAsString());
        return automationHandler.process(approvalStepRecordPO);
    }

    /**
     * 根据审批记录id删除审批记录
     *
     * @param approvalId 审批记录
     * @return
     */
    @Transactional
    public void deleteApprovalRecord(Long approvalId) {
        if (Objects.isNull(approvalId)) {
            throw new BusinessException("审批记录id缺失，无法删除");
        }
        approvalRecordMapper.deleteById(approvalId);
        approvalStepRecordMapper.delete(Wrappers.<ApprovalStepRecordPO>lambdaQuery()
                .eq(ApprovalStepRecordPO::getApprovalId, approvalId));
    }

    /**
     * 插入表单
     *
     * @param map
     * @param typeId
     * @return
     */
    public String insertDocument(Map<String, Object> map, Long typeId) {
        if (Objects.isNull(typeId)) {
            throw new BusinessException("类型 id 为空");
        }
        String id = getHandler(typeId).insertDocument(map);
        if (StrUtil.isBlank(id)) {
            throw new BusinessException("插入失败");
        }
        return id;
    }

    public Object updateDocument(Map<String, Object> map, Long typeId) {
        if (Objects.isNull(typeId)) {
            throw new BusinessException("类型 id 为空");
        }
        if (!map.containsKey("id")) {
            throw new BusinessException("表单 id 为空");
        }
        return getHandler(typeId).updateById(map, String.valueOf(map.get("id")));
    }

    public Object selectDocumentById(String id, Long typeId) {
        if (Objects.isNull(typeId)) {
            throw new BusinessException("类型 id 为空");
        }
        if (StrUtil.isBlank(id)) {
            throw new BusinessException("表单 id 为空");
        }
        return getHandler(typeId).selectDocument(id);
    }

    public Integer deleteDocument(String id, Long typeId) {
        if (Objects.isNull(typeId)) {
            throw new BusinessException("类型 id 为空");
        }
        if (StrUtil.isBlank(id)) {
            throw new BusinessException("表单 id 为空");
        }
        return getHandler(typeId).deleteDocument(id);
    }
}
