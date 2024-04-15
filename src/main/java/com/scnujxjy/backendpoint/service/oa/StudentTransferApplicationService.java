package com.scnujxjy.backendpoint.service.oa;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.constant.enums.OAEnum;
import com.scnujxjy.backendpoint.dao.entity.oa.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.oa.ApprovalTypePO;
import com.scnujxjy.backendpoint.dao.mongoEntity.OAApplicationForm;
import com.scnujxjy.backendpoint.dao.mongoEntity.StudentTransferApplication;
import com.scnujxjy.backendpoint.dao.repository.StudentTransferApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import net.polyv.vod.v1.App;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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


    /**
     *
     * 添加一个新的转专业申请
     * @param application 转专业表单
     */
    @Override
    public boolean application(OAApplicationForm application) {
        try {
            if (application instanceof StudentTransferApplication) {
                StudentTransferApplication studentTransferApplication = (StudentTransferApplication) application;
                StudentTransferApplication savedApplication = mongoTemplate.save(studentTransferApplication, "studentTransferApplications");

                if(savedApplication.getId() == null){
                    return false;
                }

                ApprovalTypePO approvalTypePO = approvalTypeService.getBaseMapper().selectOne(new LambdaQueryWrapper<ApprovalTypePO>()
                        .eq(ApprovalTypePO::getApplicationName, OAEnum.OLD_STUDENT_MAJOR_CHANGE3.getOaType()));



                ApprovalRecordPO approvalRecordPO = new ApprovalRecordPO()
                        .setApplicationFormId(savedApplication.getId())
                        .setApplicationTypeId(approvalTypePO.getId())
                        .setUserIdentify(studentTransferApplication.getStudentId())
                        .setCurrentStepId(1)
                        .setCurrentStatus(OAEnum.APPROVAL_STATUS1.getOaType())
                        ;

                int insert = approvalRecordService.getBaseMapper().insert(approvalRecordPO);

                // 构造审批步骤记录

                if(insert > 0){
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
