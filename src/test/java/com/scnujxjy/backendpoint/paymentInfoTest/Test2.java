package com.scnujxjy.backendpoint.paymentInfoTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.constant.enums.MajorInformationEnum;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.admission_information.MajorInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.service.admission_information.AdmissionInformationService;
import com.scnujxjy.backendpoint.service.admission_information.MajorInformationService;
import com.scnujxjy.backendpoint.service.college.CollegeInformationService;
import com.scnujxjy.backendpoint.service.registration_record_card.ClassInformationService;
import com.scnujxjy.backendpoint.service.teaching_point.TeachingPointInformationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class Test2 {
    @Resource
    private ClassInformationService classInformationService;

    @Resource
    private MajorInformationService majorInformationService;

    @Resource
    private AdmissionInformationService admissionInformationService;

    @Resource
    private TeachingPointInformationService teachingPointInformationService;

    @Resource
    private CollegeInformationService collegeInformationService;

    @Test
    public void test1(){
        Map<String, Boolean> ret = new HashMap<>();
        List<ClassInformationPO> classInformationPOList = classInformationService.getBaseMapper().selectList(null);
        for(ClassInformationPO classInformation : classInformationPOList){
            if (!isGradeGreaterThan(classInformation.getGrade(), "2015")) {
                // Your logic here
                continue;
            }
            boolean b = MajorInformationEnum.existsByMajorNameAndLevel(classInformation.getMajorName(), classInformation.getLevel());
            if(!b){
//                log.info("\n" + classInformation + " 不在常量类中");
                String key = classInformation.getMajorName() + " " + classInformation.getLevel();
                ret.put(key, b);
            }
        }

        for(String key : ret.keySet()){
//            log.info("\n" + key + " 不在常量类中");
            System.out.println("\n" + key + " 不在常量类中");
        }
    }

    private static boolean isGradeGreaterThan(String grade, String compareYear) {
        try {
            int gradeYear = Integer.parseInt(grade);
            int compareYearInt = Integer.parseInt(compareYear);
            return gradeYear > compareYearInt;
        } catch (NumberFormatException e) {
            // Handle the case where the grade is not a valid number
            System.err.println("Invalid grade format: " + grade);
            return false;
        }
    }

    /**
     *
     */
    @Test
    public void test2(){
        // Assuming you have a service to get the list of MajorInformationPO
        List<MajorInformationPO> majorInformationPOS = majorInformationService.getBaseMapper().selectList(null);

        // Map to store the count of each (teachingPointId, collegeId) combination
        Map<String, Integer> countMap = new HashMap<>();

        // Populate the map with counts
        for (MajorInformationPO major : majorInformationPOS) {
            String key = major.getTeachingPointId() + ":" + major.getCollegeId() +
                    ":" + major.getMajorName() + ":" + major.getLevel() + ":" + major.getStudyForm() ;
            countMap.put(key, countMap.getOrDefault(key, 0) + 1);
        }

        // Filter and print the combinations with more than one occurrence
        List<String> duplicateKeys = countMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!duplicateKeys.isEmpty()) {
            System.out.println("Duplicate (teachingPointId, collegeId) combinations:");
            for (String key : duplicateKeys) {
                String[] ids = key.split(":");
                System.out.println("Teaching Point ID: " + ids[0] + ", College ID: " + ids[1] + ", Count: " + countMap.get(key));
            }
        } else {
            System.out.println("No duplicate (teachingPointId, collegeId) combinations found.");
        }
    }

    /**
     * 验证 2024 的专业信息是否 与班级信息 还有新生录取信息对应的上
     */
    @Test
    public void test3(){
        List<MajorInformationPO> majorInformationPOS = majorInformationService.getBaseMapper().selectList(null);
        for(MajorInformationPO majorInformationPO : majorInformationPOS){
            CollegeInformationPO collegeInformationPO = collegeInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<CollegeInformationPO>()
                    .eq(CollegeInformationPO::getCollegeId, majorInformationPO.getCollegeId()));
            TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                    .eq(TeachingPointInformationPO::getTeachingPointId, majorInformationPO.getTeachingPointId()));

            List<AdmissionInformationPO> admissionInformationPOS = admissionInformationService.getBaseMapper().selectList(new LambdaQueryWrapper<AdmissionInformationPO>()
                    .eq(AdmissionInformationPO::getGrade, majorInformationPO.getGrade())
                    .eq(AdmissionInformationPO::getCollege, collegeInformationPO.getCollegeName())
                    .eq(AdmissionInformationPO::getTeachingPoint, teachingPointInformationPO.getTeachingPointName())
                    .eq(AdmissionInformationPO::getMajorName, majorInformationPO.getMajorName())
                    .eq(AdmissionInformationPO::getStudyForm, majorInformationPO.getStudyForm())
                    .eq(AdmissionInformationPO::getLevel, majorInformationPO.getLevel())
            );

            List<AdmissionInformationPO> uniqueAdmissions = removeDuplicatesByMajorCode(admissionInformationPOS);

            if(uniqueAdmissions.size() > 1){
                log.info("出现专业 通过年级、学院、专业名称、学习形式、层次无法锁死 " + majorInformationPO);
            }else if(uniqueAdmissions.isEmpty()){
                log.info("通过 年级、学院、专业名称、学习形式、层次找不到任何新生信息");
            }
        }
    }

    private static List<AdmissionInformationPO> removeDuplicatesByMajorCode(List<AdmissionInformationPO> admissionInformationPOS) {
        Set<String> seenMajorCodes = new HashSet<>();
        return admissionInformationPOS.stream()
                .filter(admission -> seenMajorCodes.add(admission.getMajorCode()))
                .collect(Collectors.toList());
    }
}
