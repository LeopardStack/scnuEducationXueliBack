package com.scnujxjy.backendpoint.oaTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.oa.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.oa.ApprovalTypePO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.mongoEntity.StudentTransferApplication;
import com.scnujxjy.backendpoint.service.admission_information.AdmissionInformationService;
import com.scnujxjy.backendpoint.service.oa.ApprovalStepService;
import com.scnujxjy.backendpoint.service.oa.ApprovalTypeService;
import com.scnujxjy.backendpoint.service.oa.StudentTransferApplicationService;
import com.scnujxjy.backendpoint.service.registration_record_card.ClassInformationService;
import com.scnujxjy.backendpoint.service.registration_record_card.PersonalInfoService;
import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import javax.swing.plaf.PanelUI;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private StudentTransferApplicationService service;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private ApprovalTypeService approvalTypeService;

    @Resource
    private ApprovalStepService approvalStepService;

    @Test
    public void test1(){
//        StudentTransferApplication newApplication = service.addNewTransferApplication(new StudentTransferApplication());
//        log.info("ID 为 " + newApplication.getId());
//        String id = newApplication.getId();


    }

    @Test
    public void test2(){
        StudentTransferApplication application = new StudentTransferApplication();
        application.setName("John Doe");
        application.setGender("Male");
        application.setCandidateNumber("123456");
        application.setStudentId("20200101");
        application.setAdmissionDate(new Date());
        application.setAdmissionScore(127);
        // ... 设置其他字段 ...
        mongoTemplate.save(application, "studentTransferApplications");
    }

    /**
     * 创建学生转专业审批固定流程
     */
    @Test
    public void createApprovalOfStudentMajorChange(){
        Integer maxId = approvalTypeService.getBaseMapper().selectMaxId();
        ApprovalTypePO approvalTypePO = new ApprovalTypePO()
                .setId(maxId + 1)
                .setApplicationName("在籍生转专业")
                .setDescription("有了学号之后的在籍学生申请转专业学习")
                ;
        int insert = approvalTypeService.getBaseMapper().insert(approvalTypePO);
        if(insert > 0){
            log.info("\n插入事务类型成功 " + insert);
        }
    }

    /**
     * 创建学生转专业审批固定流程
     */
    @Test
    public void createApprovalOfStudentMajorChangeSteps(){
        Integer maxId = approvalTypeService.getBaseMapper().selectMaxId();
        log.info("最大的 ID " + maxId);
        ApprovalTypePO approvalTypePO = approvalTypeService.getById(1);
        ApprovalStepPO approvalStepPO1 = new ApprovalStepPO()
                .setApplicationTypeId(approvalTypePO.getId())
                .setStepOrder(1).setDescription("学生发起转专业申请")
                ;
        approvalStepService.getBaseMapper().insert(approvalStepPO1);

        ApprovalStepPO approvalStepPO2 = new ApprovalStepPO()
                .setApplicationTypeId(approvalTypePO.getId())
                .setStepOrder(2).setDescription("转出学院审核")
                ;
        approvalStepService.getBaseMapper().insert(approvalStepPO2);

        ApprovalStepPO approvalStepPO3 = new ApprovalStepPO()
                .setApplicationTypeId(approvalTypePO.getId())
                .setStepOrder(3).setDescription("转入学院审核")
                ;
        approvalStepService.getBaseMapper().insert(approvalStepPO3);

        ApprovalStepPO approvalStepPO4 = new ApprovalStepPO()
                .setApplicationTypeId(approvalTypePO.getId())
                .setStepOrder(4).setDescription("继续教育学院学籍异动教务员审核")
                ;
        approvalStepService.getBaseMapper().insert(approvalStepPO4);

        ApprovalStepPO approvalStepPO5 = new ApprovalStepPO()
                .setApplicationTypeId(approvalTypePO.getId())
                .setStepOrder(5).setDescription("继续教育学院学籍异动财务人员审核")
                ;
        approvalStepService.getBaseMapper().insert(approvalStepPO5);

    }



    @Resource
    private ClassInformationService classInformationService;

    @Resource
    private StudentStatusService studentStatusService;

    @Resource
    private PersonalInfoService personalInfoService;

    @Resource
    private AdmissionInformationService admissionInformationService;


    @Resource
    private StudentTransferApplicationService studentTransferApplicationService;

    /**
     *
     * 在籍生发起转专业申请
     */
    @Test
    public void studentApplyMajorChange(){
        List<ClassInformationPO> classInformationPOList = classInformationService.getBaseMapper().selectList(new LambdaQueryWrapper<ClassInformationPO>()
                .eq(ClassInformationPO::getCollege, "心理学院")
                .eq(ClassInformationPO::getGrade, "2023")
        );
        ClassInformationPO classInformationPO = null;
       // Check if the list is not empty
        if (!classInformationPOList.isEmpty()) {
            // Create a Random instance
            Random random = new Random();

            // Select a random index based on the size of the list
            int randomIndex = random.nextInt(classInformationPOList.size());

            // Return the class at the random index
            classInformationPO = classInformationPOList.get(randomIndex);
        }

        if(classInformationService != null){
            List<StudentStatusPO> studentStatusPOS = studentStatusService.getBaseMapper().selectList(new LambdaQueryWrapper<StudentStatusPO>()
                    .eq(StudentStatusPO::getClassIdentifier, classInformationPO.getClassIdentifier()));

            // Create a Random instance
            Random random = new Random();

            // Select a random index based on the size of the list
            int randomIndex = random.nextInt(studentStatusPOS.size());

            StudentStatusPO studentStatusPO = studentStatusPOS.get(randomIndex);
            log.info("随机获取了一个学生 " + studentStatusPO);
            PersonalInfoPO personalInfoPO = personalInfoService.getBaseMapper().selectOne(new LambdaQueryWrapper<PersonalInfoPO>()
                    .eq(PersonalInfoPO::getIdNumber, studentStatusPO.getIdNumber())
                    .eq(PersonalInfoPO::getGrade, studentStatusPO.getGrade())
            );

            AdmissionInformationPO admissionInformationPO = admissionInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<AdmissionInformationPO>()
                    .eq(AdmissionInformationPO::getGrade, studentStatusPO.getGrade())
                    .eq(AdmissionInformationPO::getIdCardNumber, studentStatusPO.getIdNumber())
            );

            // 随机选择 教科院的一个班
            List<ClassInformationPO> classInformationPOList1 = classInformationService.getBaseMapper().selectList(new LambdaQueryWrapper<ClassInformationPO>()
                    .eq(ClassInformationPO::getCollege, "教育科学学院")
                    .eq(ClassInformationPO::getGrade, "2023")
            );
            ClassInformationPO classInformationPO1 = null;
            // Check if the list is not empty
            if (!classInformationPOList1.isEmpty()) {
                // Create a Random instance
                Random random1 = new Random();

                // Select a random index based on the size of the list
                int randomIndex1 = random1.nextInt(classInformationPOList1.size());

                // Return the class at the random index
                classInformationPO1 = classInformationPOList1.get(randomIndex1);
            }
            if(classInformationPO1 != null){
                StudentTransferApplication studentTransferApplication = new StudentTransferApplication()
                        .setName(personalInfoPO.getName())
                        .setGender(personalInfoPO.getGender())
                        .setCandidateNumber(admissionInformationPO.getAdmissionNumber())
                        .setStudentId(studentStatusPO.getStudentNumber())
                        .setAdmissionDate(studentStatusPO.getEnrollmentDate())
                        .setAdmissionScore(admissionInformationPO.getTotalScore())
                        .setEducationLevel(studentStatusPO.getLevel())
                        .setAdmittedMajor(studentStatusPO.getMajorName())
                        .setOriginalTuitionFee(classInformationPO.getTuition())
                        .setOriginalStudyForm(studentStatusPO.getStudyForm())
                        .setOriginalClassName(classInformationPO.getClassName())
                        .setOriginalClassIdentifier(classInformationPO.getClassIdentifier())

                        .setIntendedMajor(classInformationPO1.getMajorName())
                        .setCurrentTuitionFee(classInformationPO1.getTuition())
                        .setIntendedStudyForm(classInformationPO1.getStudyForm())
                        .setIntendedClassName(classInformationPO1.getClassName())
                        .setIntendedClassIdentifier(classInformationPO1.getClassIdentifier())

                        .setApplicationReason("想换个专业 好找工作")
                        .setApplicationDate(new Date())
                        ;

                log.info("构造的转专业表单 \n " + studentTransferApplication);

                boolean application = studentTransferApplicationService.application(studentTransferApplication);
            }

        }


    }
}
