package com.scnujxjy.backendpoint.service.office_automation.handler;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.scnujxjy.backendpoint.constant.enums.RoleEnum;
import com.scnujxjy.backendpoint.constant.enums.office_automation.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.dao.mongoEntity.oa.SuspensionOfStudyDocument;
import com.scnujxjy.backendpoint.dao.repository.SuspensionOfStudyDocumentRepository;
import com.scnujxjy.backendpoint.exception.BusinessException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
@Transactional
public class SuspensionOfStudyOAHandler extends OfficeAutomationHandler {

    @Resource
    private SuspensionOfStudyDocumentRepository suspensionOfStudyDocumentRepository;

    @Override
    public OfficeAutomationHandlerType supportType() {
        return OfficeAutomationHandlerType.STUDENT_SUSPENSION_OF_STUDY;
    }

    protected Set<String> buildWatchUsernameSet(ApprovalRecordPO approvalRecordPO) {
        SuspensionOfStudyDocument document = suspensionOfStudyDocumentRepository.findById(approvalRecordPO.getDocumentId()).orElse(null);
        if (Objects.isNull(document)) {
            throw new BusinessException("休学申请表单为空");
        }
        Set<String> watchUsernameSet = CollUtil.newHashSet(StpUtil.getLoginIdAsString());
        watchUsernameSet.addAll(platformUserService.selectUsernameByRoleName(Sets.newHashSet(RoleEnum.ACADEMIC_ADMIN.getRoleName())));
        return watchUsernameSet;
    }

    /**
     * 构建审核步骤记录的审核人群
     *
     * @param approvalStepPO 审核步骤
     * @param documentId     申请表单编号
     * @return username集合
     */
    @Override
    protected Set<String> buildApprovalUsernameSet(ApprovalStepPO approvalStepPO, String documentId) {
        SuspensionOfStudyDocument document = suspensionOfStudyDocumentRepository.findById(documentId).orElse(null);
        if (Objects.isNull(document)) {
            throw new BusinessException("审核表单不存在");
        }

        Set<String> usernameSet = CollUtil.newHashSet();
        // 根据步骤顺序，设置不同的审核人群
        // 例如：根据学院ID添加院系管理员等
        return usernameSet;
    }

    @Override
    public void afterProcess(ApprovalStepRecordPO approvalStepRecordPO, ApprovalRecordPO approvalRecordPO) {
        log.info("处理休学申请步骤完成, 当前步骤: {}, 当前记录: {}", approvalStepRecordPO, approvalRecordPO);
        // 这里可以添加更多逻辑处理休学申请的细节
    }

    @Override
    public void afterApproval(ApprovalRecordPO approvalRecordPO, ApprovalStepRecordPO approvalStepRecordPO) {
        log.info("休学申请审核完成, 审批记录: {}, 最后一步记录: {}", approvalRecordPO, approvalStepRecordPO);
        // 发送通知等后续操作
    }

    /**
     * 插入休学表单
     *
     * @param map 休学表单数据
     * @return 表单 id
     */
    @Override
    public String insertDocument(Map<String, Object> map) {
        if (Objects.isNull(map)) {
            throw new BusinessException("休学表单数据不能为空");
        }
        SuspensionOfStudyDocument document = JSONObject.parseObject(JSONObject.toJSONString(map), SuspensionOfStudyDocument.class);
        if (StrUtil.isBlank(document.getStudentUsername())) {
            throw new BusinessException("学生用户名不能为空");
        }
        document = suspensionOfStudyDocumentRepository.insert(document);
        return document.getId();
    }

    /**
     * 根据 id 删除休学表单
     *
     * @param id 表单 id
     * @return 成功删除的数量
     */
    @Override
    public Integer deleteDocument(String id) {
        if (StrUtil.isBlank(id)) {
            throw new BusinessException("表单 id 不能为空");
        }
        suspensionOfStudyDocumentRepository.deleteById(id);
        return 1;
    }

    /**
     * 根据 id 查询休学表单
     *
     * @param id 表单 id
     * @return 返回找到的休学表单
     */
    @Override
    public SuspensionOfStudyDocument selectDocument(String id) {
        if (StrUtil.isBlank(id)) {
            throw new BusinessException("表单 id 不能为空");
        }
        return suspensionOfStudyDocumentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("未找到指定的休学表单"));
    }

    /**
     * 根据 id 更新休学表单信息
     *
     * @param map 表单信息
     * @param id  表单 id
     * @return 更新后的休学表单
     */
    @Override
    public SuspensionOfStudyDocument updateById(Map<String, Object> map, String id) {
        if (StrUtil.isBlank(id)) {
            throw new BusinessException("表单 id 不能为空");
        }
        if (map == null || map.isEmpty()) {
            throw new BusinessException("更新的数据不能为空");
        }

        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update();
        map.forEach((key, value) -> {
            if (!"id".equals(key)) {
                update.set(key, value);
            }
        });
        mongoTemplate.findAndModify(query, update, SuspensionOfStudyDocument.class); // Update and return the updated object
        return selectDocument(id); // Fetch the updated document
    }

}
