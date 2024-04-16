package com.scnujxjy.backendpoint.service.oa;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.constant.enums.OAEnum;
import com.scnujxjy.backendpoint.dao.entity.oa.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.oa.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.oa.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.dao.entity.oa.ApprovalTypePO;
import com.scnujxjy.backendpoint.dao.mongoEntity.OAApplicationForm;
import com.scnujxjy.backendpoint.dao.mongoEntity.StudentTransferApplication;
import com.scnujxjy.backendpoint.dao.repository.StudentTransferApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import net.polyv.vod.v1.App;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 转专业服务
 */
@Service
@Slf4j
public class StudentTransferApplicationService extends OATaskExecutorService {
    @Resource
    private StudentTransferApplicationRepository repository;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private ApprovalTypeService approvalTypeService;

    @Resource
    private ApprovalRecordService approvalRecordService;

    @Resource
    private ApprovalStepService approvalStepService;

    @Resource
    private ApprovalStepRecordService approvalStepRecordService;


    /**
     *
     * 添加一个新的转专业申请
     * @param application 转专业表单
     */
    @Override
    public boolean application(OAApplicationForm application) {
        try {
            // 对申请表单类型进行判断 判断其是否是 在籍生转专业
            if (application instanceof StudentTransferApplication) {
                StudentTransferApplication studentTransferApplication = (StudentTransferApplication) application;
                StudentTransferApplication savedApplication = mongoTemplate.save(studentTransferApplication, "studentTransferApplications");

                if(savedApplication.getId() == null){
                    return false;
                }

                // 根据申请表单类型 来确定审批类型
                ApprovalTypePO approvalTypePO = approvalTypeService.getBaseMapper().selectOne(new LambdaQueryWrapper<ApprovalTypePO>()
                        .eq(ApprovalTypePO::getApplicationName, OAEnum.OLD_STUDENT_MAJOR_CHANGE3.getOaType()));


                // 根据确定的审批类型来建立一条审批记录
                ApprovalRecordPO approvalRecordPO = new ApprovalRecordPO()
                        .setApplicationFormId(savedApplication.getId())
                        .setApplicationTypeId(approvalTypePO.getId())
                        .setUserIdentify(studentTransferApplication.getStudentId())
                        .setCurrentStepId(1)
                        .setCurrentStatus(OAEnum.APPROVAL_STATUS1.getOaType())
                        ;

                int insert = approvalRecordService.getBaseMapper().insert(approvalRecordPO);

                // 获取该审批类型的所有步骤 并将第一步插入审批步骤记录表
                List<ApprovalStepPO> sortedApprovalSteps = approvalStepService.getBaseMapper().selectList(
                        new LambdaQueryWrapper<ApprovalStepPO>()
                                .eq(ApprovalStepPO::getApplicationTypeId, approvalTypePO.getId())
                                .orderByAsc(ApprovalStepPO::getStepOrder) // 按 stepOrder 升序排序
                );
                // 构造审批步骤记录 将第一步插入进去 状态取决于 用户是保存 还是提交了 这里默认提交了
                int startStep = 0;
                ApprovalStepRecordPO approvalStepRecordPO = new ApprovalStepRecordPO()
                        .setApplicationRecordId(approvalRecordPO.getId())
                        .setStepId(sortedApprovalSteps.get(startStep).getStepOrder())
                        .setNextStepId(sortedApprovalSteps.get(startStep + 1).getStepOrder())
                        .setProcessUserIds(Arrays.asList(1L))  // 这个地方应该是一个步骤处理用户集合 包含了 每一步可以操作的用户
                        .setStatus(OAEnum.APPROVAL_STATUS6.getOaType())
                        ;
                int insert1 = approvalStepRecordService.getBaseMapper().insert(approvalStepRecordPO);

                if(insert > 0 && insert1 > 0){
                    return true;
                }else{
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("插入学生转专业表单到数据库失败: " + e.getMessage());
            return false;
        }
    }


    @Override
    protected void process(String applicationId) {

    }

    /**
     * 对审批流程中的每一步 都要做判断 看它是否符合 期望
     * @param applicationId 表单 ID
     */
    @Override
    protected void success(String applicationId) {

    }

    @Override
    protected void failed(String applicationId) {

    }

    // 根据学生ID更新专业
    public StudentTransferApplication updateMajor(String studentId, String newMajor) {
        StudentTransferApplication application = repository.findById(studentId).orElse(null);
        if (application != null) {
            application.setIntendedMajor(newMajor);
            return repository.save(application);
        }
        // 或者抛出一个异常
        return null;
    }

    // 根据学生ID更新姓名
    public StudentTransferApplication updateStudentName(String studentId, String newName) {
        StudentTransferApplication application = repository.findById(studentId).orElse(null);
        if (application != null) {
            application.setName(newName);
            return repository.save(application);
        }
        // 或者抛出一个异常
        return null;
    }

    // 根据ID获取转专业申请
    public StudentTransferApplication getApplicationById(String id) {
        return repository.findById(id).orElse(null);
        // 你也可以选择在找不到文档时抛出一个异常
    }
}
