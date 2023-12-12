package com.scnujxjy.backendpoint.service.office_automation;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.mongodb.client.result.UpdateResult;
import com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.dao.mongoEntity.StudentTransferMajorDocument;
import com.scnujxjy.backendpoint.dao.repository.StudentTransferMajorRepository;
import com.scnujxjy.backendpoint.exception.BusinessException;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.college.CollegeAdminInformationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType.STUDENT_TRANSFER_MAJOR;
import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationStepStatus.WAITING;

@Component
@Slf4j
@Transactional
public class StudentTransferMajorOAHandler extends OfficeAutomationHandler {

    @Resource
    private StudentTransferMajorRepository studentTransferMajorRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private CollegeAdminInformationService collegeAdminInformationService;

    /**
     * 获取支持类型
     *
     * @return 支持类型
     */
    @Override
    public OfficeAutomationHandlerType supportType() {
        return STUDENT_TRANSFER_MAJOR;
    }

    /**
     * 新增一个审批记录
     * <p>需要添加可以审核用户的集合</p>
     *
     * @param approvalRecordPO 审批记录
     * @return 新增的审批记录
     * @see CommonOfficeAutomationHandler#createApprovalRecord(ApprovalRecordPO)
     */
    @Override
    protected Boolean createApprovalRecord(ApprovalRecordPO approvalRecordPO) {
        if (Objects.isNull(approvalRecordPO)
                || Objects.isNull(approvalRecordPO.getApprovalTypeId())
                || Objects.isNull(approvalRecordPO.getDocumentId())) {
            throw new BusinessException("OA记录为空或OA类型id为空");
        }
        // 获取申请表单
        StudentTransferMajorDocument studentTransferMajorDocument = studentTransferMajorRepository.findById(approvalRecordPO.getDocumentId()).orElse(null);
        if (Objects.isNull(studentTransferMajorDocument)) {
            throw new BusinessException("学生装专业表单为空");
        }
        // 根据转出学院以及转入学院 id 来获取可见用户名单
        List<Long> watchUserIdSet = ListUtil.toList(platformUserService.getUserIdByUsername(StpUtil.getLoginIdAsString()));
        CollUtil.addAll(watchUserIdSet, collegeAdminInformationService.adminUserIdByCollegeId(studentTransferMajorDocument.getFromCollegeId()));
        CollUtil.addAll(watchUserIdSet, collegeAdminInformationService.adminUserIdByCollegeId(studentTransferMajorDocument.getToCollegeId()));
        CollUtil.addAll(watchUserIdSet, collegeAdminInformationService.adminUserIdByCollegeId(studentTransferMajorDocument.getContinuingEducationCollegeId()));
        approvalRecordPO.setUserWatchSet(watchUserIdSet);
        // 根据类型id获取步骤
        Long typeId = approvalRecordPO.getApprovalTypeId();
        List<ApprovalStepPO> approvalStepPOS = selectApprovalStep(typeId);
        if (CollUtil.isEmpty(approvalStepPOS)) {
            throw new BusinessException("当前OA类型无步骤");
        }
        // 填充记录表数据
        DateTime date = DateUtil.date();
        ApprovalStepPO approvalStepPO = approvalStepPOS.get(0);
        Long userId = platformUserService.getUserIdByUsername(StpUtil.getLoginIdAsString());
        approvalRecordPO.setInitiatorUserId(userId)
                .setCreatedAt(date)
                .setUpdateAt(date)
                .setStatus(WAITING.getStatus())
                .setCurrentStepId(approvalStepPO.getId());
        int count = approvalRecordMapper.insert(approvalRecordPO);
        if (count == 0) {
            throw new BusinessException("插入OA记录表失败");
        }
        // 插入步骤记录表
        createApprovalStepRecord(approvalRecordPO.getId(), date, approvalRecordPO.getCurrentStepId());
        return true;
    }

    /**
     * 处理后的过程
     * <p>需要重写处理后每个事务需要执行的逻辑都不一样</p>
     *
     * @param approvalStepRecordPO
     * @see CommonOfficeAutomationHandler#afterProcess(ApprovalStepRecordPO)
     */
    @Override
    public void afterProcess(ApprovalStepRecordPO approvalStepRecordPO) {

    }

    /**
     * 处理完所有审核流程后会执行的方法
     * <p>实现时可以根据approvalRecordPO.status来定义不同的行为</p>
     *
     * @param approvalRecordPO     审批完成的审批记录
     * @param approvalStepRecordPO 最后一个步骤记录
     */
    @Override
    public void afterApproval(ApprovalRecordPO approvalRecordPO, ApprovalStepRecordPO approvalStepRecordPO) {
        log.info("学生转专业审核完成，审批记录：{}，最后一步记录：{}", approvalRecordPO, approvalStepRecordPO);
    }

    /**
     * 根据信息插入步骤记录
     * <p>需要重写拥有审核权限用户的集合的逻辑</p>
     * 请参阅下面的示例方法来实现
     *
     * @param approvalId 事件记录 Id
     * @param date       添加时间
     * @param stepId     步骤 id
     * @see CommonOfficeAutomationHandler#createApprovalStepRecord(Long, Date, Long)
     */
    @Override
    public int createApprovalStepRecord(Long approvalId, Date date, Long stepId) {
        if (Objects.isNull(approvalId)
                || Objects.isNull(date)
                || Objects.isNull(stepId)) {
            throw new BusinessException("新建步骤记录失败，缺失审核记录id、日期、步骤id等信息");
        }
        ApprovalRecordPO approvalRecordPO = approvalRecordMapper.selectById(approvalId);
        if (Objects.isNull(approvalRecordPO)) {
            throw new BusinessException("获取记录失败");
        }
        StudentTransferMajorDocument studentTransferMajorDocument = studentTransferMajorRepository.findById(approvalRecordPO.getDocumentId()).orElse(null);
        if (Objects.isNull(studentTransferMajorDocument)) {
            throw new BusinessException("审核表单不存在");
        }
        // 获取下一个流程步骤
        ApprovalStepPO approvalStepPO = approvalStepMapper.selectById(stepId);
        if (Objects.isNull(approvalStepPO)) {
            throw new BusinessException("获取下一个步骤失败，流转失败");
        }
        List<Long> userApprovalSet = ListUtil.toList();

        // 根据不同步骤填充不同的审核用户群
        if (stepId == 1) {
            userApprovalSet.add(studentTransferMajorDocument.getStudentUserId());
        } else if (stepId == 2) {
            List<Long> userIdSet = collegeAdminInformationService.adminUserIdByCollegeId(studentTransferMajorDocument.getFromCollegeId());
            CollUtil.addAll(userApprovalSet, userIdSet);
        } else if (stepId == 3) {
            List<Long> userIdSet = collegeAdminInformationService.adminUserIdByCollegeId(studentTransferMajorDocument.getToCollegeId());
            CollUtil.addAll(userApprovalSet, userIdSet);
        } else if (stepId == 4) {
            List<Long> userIdSet = collegeAdminInformationService.adminUserIdByCollegeId(studentTransferMajorDocument.getContinuingEducationCollegeId());
            CollUtil.addAll(userApprovalSet, userIdSet);
        } else if (stepId == 5) {
            // 财务处审批
        } else {
            // 成功不需要人审核
            CollUtil.addAll(userApprovalSet, ListUtil.of());
        }
        // 填充步骤记录
        ApprovalStepRecordPO recordPO = ApprovalStepRecordPO.builder()
                .approvalId(approvalId)
                .stepId(approvalStepPO.getId())
                .updateAt(date)
                .status(WAITING.getStatus())
                .approvalTypeId(approvalStepPO.getApprovalTypeId())
                .userApprovalSet(userApprovalSet)
                .build();
        int count = approvalStepRecordMapper.insert(recordPO);
        if (count == 0) {
            throw new BusinessException("插入新的步骤记录失败");
        }
        return count;
    }

    /**
     * 插入学生表单
     *
     * @param map 学生表单
     * @return
     */
    @Override
    public String insertDocument(Map<String, Object> map) {
        if (Objects.isNull(map)) {
            throw new BusinessException("专业表单为空");
        }
        StudentTransferMajorDocument studentTransferMajorDocument = JSONObject.parseObject(JSONObject.toJSONString(map), StudentTransferMajorDocument.class);
        if (Objects.isNull(studentTransferMajorDocument)) {
            throw new BusinessException("转专业表单为空");
        }
        if (Objects.isNull(studentTransferMajorDocument.getStudentUserId())) {
            studentTransferMajorDocument.setStudentUserId(platformUserService.getUserIdByUsername(StpUtil.getLoginIdAsString()));
        }
        if (Objects.isNull(studentTransferMajorDocument.getFromCollegeId())) {
            throw new BusinessException("转出学院 id 不能为空");
        }
        if (Objects.isNull(studentTransferMajorDocument.getToCollegeId())) {
            throw new BusinessException("转出学院 id 不能为空");
        }

        studentTransferMajorDocument = studentTransferMajorRepository.insert(studentTransferMajorDocument);
        return studentTransferMajorDocument.getId();
    }

    /**
     * 根据 id 删除表单
     *
     * @param id
     * @return
     */
    @Override
    public Integer deleteDocument(String id) {
        if (StrUtil.isBlank(id)) {
            throw new BusinessException("表单 id 为空");
        }
        studentTransferMajorRepository.deleteById(id);
        return 1;
    }

    /**
     * 根据 id 查询表单
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> selectDocument(String id) {
        if (StrUtil.isBlank(id)) {
            throw new BusinessException("表单 id 为空");
        }
        StudentTransferMajorDocument studentTransferMajorDocument = studentTransferMajorRepository.findById(id).orElseThrow(() -> new BusinessException("查询表单为空"));
        return JSON.parseObject(JSON.toJSONString(studentTransferMajorDocument), new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * 根据 id 更新表单信息
     *
     * @param map 表单信息
     * @param id  表单 id
     * @return
     */
    @Override
    public Map<String, Object> updateById(Map<String, Object> map, String id) {
        if (StrUtil.isBlank(id)) {
            throw new BusinessException("表单 id 为空");
        }
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update();
        map.forEach((key, value) -> {
            if (!"id".equals(key)) {
                update.set(key, value);
            }
        });
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, StudentTransferMajorDocument.class);
        if (Objects.nonNull(updateResult) && updateResult.wasAcknowledged() && updateResult.getModifiedCount() > 0) {
            return selectDocument(id);
        }
        throw new BusinessException("更新失败");
    }
}
