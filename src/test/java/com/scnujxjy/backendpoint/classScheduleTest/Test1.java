package com.scnujxjy.backendpoint.classScheduleTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.admission_information.MajorInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.service.admission_information.AdmissionInformationService;
import com.scnujxjy.backendpoint.service.admission_information.ClassScheduleService;
import com.scnujxjy.backendpoint.service.admission_information.MajorInformationService;
import com.scnujxjy.backendpoint.service.teaching_point.TeachingPointInformationService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 开班计划审核初始数据生成
 * 即根据某一年级的录取数据 生成开班审核数据
 */
@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    private AdmissionInformationService admissionInformationService;

    @Resource
    private ClassScheduleService classScheduleService;

    @Resource
    private TeachingPointInformationService teachingPointInformationService;

    @Resource
    private MajorInformationService majorInformationService;


    // 将 collegeCodes 声明为静态成员变量
    private static final Map<String, String> collegeCodes = new HashMap<String, String>() {{
        put("外国语言文化学院", "18");
        put("法学院", "2");
        put("文学院", "20");
        put("心理学院", "25");
        put("哲学与社会发展学院", "30");
        put("政治与公共管理学院", "31");
        put("计算机学院", "5");
        put("教育科学学院", "7");
        put("教育信息技术学院", "8");
        put("经济与管理学院", "9");
        put("数学科学学院", "16");
        // 在这里添加更多学院和它们的代码
    }};

    @Test
    public void test1() throws Exception {
        String grade = "2024";
        List<AdmissionInformationPO> admissionInformationPOS = admissionInformationService.getBaseMapper().selectList(new LambdaQueryWrapper<AdmissionInformationPO>()
                .eq(AdmissionInformationPO::getGrade, grade));
//        Set<String> majorCodeSet = admissionInformationPOS.stream().map(AdmissionInformationPO::getMajorCode).collect(Collectors.toSet());
//        Set<String> gradeSet = admissionInformationPOS.stream().map(AdmissionInformationPO::getGrade).collect(Collectors.toSet());
//        List<MajorInformationPO> majorInformationPOS = majorInformationService.getBaseMapper().selectList(Wrappers.<MajorInformationPO>lambdaQuery()
//                .in(MajorInformationPO::getAdmissionMajorCode, majorCodeSet)
//                .in(MajorInformationPO::getGrade, gradeSet));
//        Map<String, List<MajorInformationPO>> majorCodeGrade2MajorInformationMap = majorInformationPOS.stream()
//                .peek(ele -> ele.setAdmissionMajorCode(ele.getGrade() + ele.getAdmissionMajorCode()))
//                .collect(Collectors.groupingBy(MajorInformationPO::getAdmissionMajorCode));
        boolean ident = true;
        for(AdmissionInformationPO admissionInformationPO : admissionInformationPOS){
            String majorCode = admissionInformationPO.getMajorCode();
            MajorInformationPO majorInformationPO = majorInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<MajorInformationPO>()
                    .eq(MajorInformationPO::getAdmissionMajorCode, majorCode)
                    .eq(MajorInformationPO::getGrade, grade)
            );
//            if (!majorCodeGrade2MajorInformationMap.containsKey(grade + majorCode)) {
//                ident = false;
//                log.info(admissionInformationPO + "\n 该学生专业代码找不到专业信息");
//            }
            if(majorInformationPO == null){
                ident = false;
                log.info(admissionInformationPO + "\n 该学生专业代码找不到专业信息");
            }
        }
        if(ident){
            log.info("没有一个学生的专业代码是找不到专业信息的");
        }

        // 判断没有任何学生的专业代码找不到专业信息后 就开始生成模拟班级数据
        Map<String, List<ClassBasicInfo> > collegeClassNumberCount = new HashMap<>();
        Map<String, Integer> classCountMap = new HashMap<>();

        for(AdmissionInformationPO admissionInformationPO : admissionInformationPOS){
            String college = admissionInformationPO.getCollege();
            String majorName = admissionInformationPO.getMajorName();
            String studyForm = admissionInformationPO.getStudyForm();
            String level = admissionInformationPO.getLevel();
            String teachingPoint = admissionInformationPO.getTeachingPoint();

            // 教学点不分班
            String classKey = college + "-"  + majorName + "-" + studyForm + "-" + level + "-" + teachingPoint;
            String classIdentifier = null;
            try{

                if(!collegeClassNumberCount.containsKey(college)){
                    classIdentifier = grade.substring(2, 4) +
                            String.format("%02d", Integer.parseInt(collegeCodes.get(college))) + String.format("%02d",(1));
                }else{
                    classIdentifier = grade.substring(2, 4) +
                            String.format("%02d", Integer.parseInt(collegeCodes.get(college))) + String.format("%02d",
                            (collegeClassNumberCount.get(college).size() + 1));
                }
            }catch (Exception e){
                log.info("空指针" + classKey);
            }
            if(classIdentifier == null){
                continue;
            }


            if(!classCountMap.containsKey(classKey)){
                // 该班级不存在
                classCountMap.put(classKey, 1);
                // 当第一次创建班级时，看它所属的学院，如果学院不同 则序号不同
                if(!collegeClassNumberCount.containsKey(college)){
                    collegeClassNumberCount.put(college, new ArrayList<>());
                }
                String classNumber = (collegeClassNumberCount.get(college).size() + 1) + "";
                if(classNumber.length() >= 3){
                    throw new Exception("一个学院的班级数量超过了 3 位");
                }

                String prefix = grade.substring(2, 4) + getLevelCode(level) +
                        (studyForm.equals("函授") ? "4" : "5") +
                        String.format("%02d", Integer.parseInt(collegeCodes.get(college))) + String.format("%02d",
                        (collegeClassNumberCount.get(college).size() + 1)) + "6";

                TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                        .eq(TeachingPointInformationPO::getTeachingPointName, teachingPoint.replace("校外", "")));
                if(teachingPointInformationPO == null){
                    throw new Exception("该教学点不存在" + teachingPoint);
                }

                String majorCode = admissionInformationPO.getMajorCode();
                MajorInformationPO majorInformationPO = majorInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<MajorInformationPO>()
                        .eq(MajorInformationPO::getAdmissionMajorCode, majorCode)
                        .eq(MajorInformationPO::getGrade, grade)
                );

                ClassBasicInfo classBasicInfo = new ClassBasicInfo()
                        .setClassIdentifier(classIdentifier)
                        .setClassIndex(classNumber)
                        .setClassName(teachingPointInformationPO.getAlias())
                        .setStudentNumberPrefix(prefix)
                        .setTuition(majorInformationPO.getTuition())
                        .setStudentCount(0)
                        ;
                collegeClassNumberCount.get(college).add(classBasicInfo);
            }else{
                // 计算每个班的人数
                classCountMap.put(classKey, classCountMap.get(classKey) + 1);
            }

            // 更新每个班的人数
            // 使用新的局部变量来避免 lambda 表达式中的变量修改问题
            final String finalClassIdentifier = classIdentifier;
            ClassBasicInfo targetClass = collegeClassNumberCount.get(college).stream()
                    .filter(classInfo -> classInfo.getClassIdentifier().equals(finalClassIdentifier))
                    .findFirst()
                    .orElse(null);

            if (targetClass != null) {
                targetClass.setStudentCount(targetClass.getStudentCount() + 1);
            } else {
                log.info(collegeClassNumberCount.toString());
                throw new Exception("未找到对应的班级信息：" + classIdentifier);
            }

        }

        log.info("各个学院的班级信息如下");
        for(String key : collegeClassNumberCount.keySet()){
            ClassBasicInfo classBasicInfo = collegeClassNumberCount.get(key).get(0);
            log.info(String.valueOf(classBasicInfo));
        }

    }

    public String getLevelCode(String level){
        if(level.equals("专升本")){
            return "5";
        }else if(level.equals("高起本")){
            return "4";
        }else if(level.equals("高起专")){
            return "6";
        }
        throw new IllegalArgumentException("异常的层次 " + level);
    }
}


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
class ClassBasicInfo{
    /**
     * 班级标识符
     */
    private String classIdentifier;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 班级序号
     */
    private String classIndex;

    /**
     * 学号前缀
     */
    private String studentNumberPrefix;

    /**
     * 班级里的学生人数
     */
    private Integer studentCount;

    /**
     * 学费标准
     */
    private BigDecimal tuition;
}
