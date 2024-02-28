package com.scnujxjy.backendpoint.util.excelListener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.admission_information.MajorInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.MajorInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointInformationMapper;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationConfirmRO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationOldSystemImportVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationExcelOutputVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@NoArgsConstructor
public class ClassInformationListener extends AnalysisEventListener<ClassInformationConfirmRO> {
    private AdmissionInformationMapper admissionInformationMapper;

    private MajorInformationMapper majorInformationMapper;

    private CollegeInformationMapper collegeInformationMapper;

    private TeachingPointInformationMapper teachingPointInformationMapper;

    public ClassInformationListener(AdmissionInformationMapper admissionInformationMapper,
                                    MajorInformationMapper majorInformationMapper,
                                    CollegeInformationMapper collegeInformationMapper,
                                    TeachingPointInformationMapper teachingPointInformationMapper){
        this.admissionInformationMapper = admissionInformationMapper;
        this.majorInformationMapper = majorInformationMapper;
        this.collegeInformationMapper = collegeInformationMapper;
        this.teachingPointInformationMapper = teachingPointInformationMapper;
    }

    private List<ClassInformationOldSystemImportVO> classInformationOldSystemImportVOList = new ArrayList<>();
    private int countOfClassOpen = 0;
    private int countOfClassNotOpen = 0;

    private Map<String, List<String>> collegeClassNames = new HashMap<>(); // 记录 不同学院 不同层次 不同专业名称 是否 有相同的班级
    private Map<String, List<String>> collegeClassNamesForClassNumber = new HashMap<>(); // 记录 不同学院 的班级个数

    private int allCount = 0; // 开班人数
    private int classNotOpenAllCount = 0; // 不开班人数
    Map<String, String> collegeCodes = new HashMap<String, String>() {{
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

    @Override
    public void invoke(ClassInformationConfirmRO classInformationConfirmRO, AnalysisContext analysisContext) {
//        log.info("接收到一条开班信息 " + classInformationConfirmRO);
        ClassInformationOldSystemImportVO classInformationOldSystemImportVO = new ClassInformationOldSystemImportVO();

        CollegeInformationPO collegeInformationPO = collegeInformationMapper.selectOne(new LambdaQueryWrapper<CollegeInformationPO>()
                .eq(CollegeInformationPO::getCollegeName, classInformationConfirmRO.getCollege()));
        if (collegeInformationPO == null) {
            throw new IllegalArgumentException("找不到该学院信息 ");
        }

        TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationMapper.selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                .eq(TeachingPointInformationPO::getAlias, classInformationConfirmRO.getTeachingPointNameAlias()));
        if (teachingPointInformationPO == null) {
            if (classInformationConfirmRO.getTeachingPointName().equals(classInformationConfirmRO.getCollege())) {
                // 这是校内教学点 也就是校内班
                teachingPointInformationPO = teachingPointInformationMapper.selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                        .eq(TeachingPointInformationPO::getTeachingPointName, classInformationConfirmRO.getTeachingPointName()));
                if (teachingPointInformationPO == null) {
                    throw new IllegalArgumentException("找不到该校内教学点信息 ");
                }
            } else {
                throw new IllegalArgumentException("找不到该教学点信息 ");
            }
        }

        MajorInformationPO majorInformationPO = majorInformationMapper.selectOne(new LambdaQueryWrapper<MajorInformationPO>()
                .eq(MajorInformationPO::getGrade, "2024")
                .eq(MajorInformationPO::getCollegeId, collegeInformationPO.getCollegeId())
                .eq(MajorInformationPO::getTeachingPointId, teachingPointInformationPO.getTeachingPointId())
                .eq(MajorInformationPO::getMajorName, classInformationConfirmRO.getMajorName())
                .eq(MajorInformationPO::getLevel, classInformationConfirmRO.getLevel())
                .eq(MajorInformationPO::getStudyForm, classInformationConfirmRO.getStudyForm())
        );

        if(classInformationConfirmRO.getClassCreate() != null && classInformationConfirmRO.getClassCreate().equals("√")) {
            countOfClassOpen += 1;
            try {

                classInformationOldSystemImportVO.setGrade(majorInformationPO.getGrade());
                classInformationOldSystemImportVO.setAdmissionDate(majorInformationPO.getGrade() + ".03");
                classInformationOldSystemImportVO.setGraduateDate(checkGraduateDate(majorInformationPO.getGrade(), majorInformationPO));
                String collegeName = collegeInformationPO.getCollegeName();
                classInformationOldSystemImportVO.setCollege(collegeName);

                // 设置班名时 同一个学院的班 需要递增 除了校内班 因为校内班只有一个
                // 准确的说 应该是 同一个学院 同一个学习形式 同一个专业
                String key = collegeName + "_" + majorInformationPO.getMajorName() + "_" + majorInformationPO.getLevel();
                if (!collegeClassNames.containsKey(key)) {
                    collegeClassNames.put(key, new ArrayList<>());
                }
                if(!collegeClassNamesForClassNumber.containsKey(collegeName)){
                    collegeClassNamesForClassNumber.put(collegeName, new ArrayList<>());
                }
                String alternativeClassName = teachingPointInformationPO.getAlias();

                classInformationOldSystemImportVO.setMajorName(majorInformationPO.getMajorName());
                classInformationOldSystemImportVO.setMajorCode(majorInformationPO.getAdmissionMajorCode());
                classInformationOldSystemImportVO.setLevel(majorInformationPO.getLevel().equals("高起专") ? "专科" : "本科");
                classInformationOldSystemImportVO.setStudyDuration(majorInformationPO.getLevel().equals("高起本") ? "5" : "3");
                classInformationOldSystemImportVO.setStudyForm(majorInformationPO.getStudyForm());
                classInformationOldSystemImportVO.setTuitionFee("" + majorInformationPO.getTuition().intValue());

                // 这里仅用年级 和教学点会重复计算 因为 一个教学点 可能 与一个学院或者多个学院 有多个专业一起合作
                // 因此最好是使用专业代码
                AdmissionInformationRO admissionInformationRO = new AdmissionInformationRO();
                admissionInformationRO.setTeachingPoint(teachingPointInformationPO.getTeachingPointName());
                admissionInformationRO.setMajorCode(majorInformationPO.getAdmissionMajorCode());
                int studentCount = admissionInformationMapper.
                        batchSelectData(admissionInformationRO).size();
                int numClasses;
                allCount += studentCount;



                if (studentCount <= 50) {
                    // 如果总学生数不超过 50，至少设一个班
                    numClasses = 1;
                } else if (studentCount % 150 > 50) {
                    // 如果剩余的学生数超过 50，需要多创建一个班
                    numClasses = studentCount / 150 + 1;
                } else {
                    // 如果剩余的学生数不超过 50，按正常比例创建班级
                    numClasses = studentCount / 150;
                }
                // 2024 级不分班 就按照教学点来
                numClasses = 1;

                for(int i = 0; i < numClasses; i++){
                    ClassInformationOldSystemImportVO temp = new ClassInformationOldSystemImportVO();
                    BeanUtils.copyProperties(classInformationOldSystemImportVO, temp);
                    String finalClassName = checkClassName(alternativeClassName, key, collegeName);
                    temp.setClassName(finalClassName);
                    int index = collegeClassNamesForClassNumber.get(collegeName).lastIndexOf(finalClassName);
                    String classNumber = String.format("%02d", (index + 1));
                    temp.setClassNumber(classNumber);



                    temp.setCollegeCode(String.format("%02d", Integer.parseInt(collegeCodes.get(collegeName))));
                    String prefix = temp.getGrade().substring(2, 4) + getLevelCode(majorInformationPO.getLevel()) +
                            (temp.getStudyForm().equals("函授") ? "4" : "5") +
                            temp.getCollegeCode() + classNumber + "6";
                    temp.setStudentNumberPrefix(prefix);
                    temp.setClassIdentity(temp.getGrade().substring(2, 4) +
                            temp.getCollegeCode() + classNumber);
                    temp.setStatus("在籍新生");

                    // 获取班级人数
                    if(numClasses == 1){
                        temp.setClassStudentCount(studentCount);
                    }else if(i == (numClasses -1)){
                        temp.setClassStudentCount(studentCount - (numClasses -1) * 150);
                    }else{
                        temp.setClassStudentCount(150);
                    }

                    classInformationOldSystemImportVOList.add(temp);
                }


//            log.info("找到了该专业信息 " + majorInformationPO);
            } catch (Exception e) {
                log.error("查找专业信息失败 " + e + "\n" + classInformationConfirmRO);

            }
        }else{
            AdmissionInformationRO admissionInformationRO = new AdmissionInformationRO();
            admissionInformationRO.setTeachingPoint(teachingPointInformationPO.getTeachingPointName());
            admissionInformationRO.setGrade(majorInformationPO.getGrade());
            admissionInformationRO.setMajorCode(majorInformationPO.getAdmissionMajorCode());
            int studentCount = admissionInformationMapper.
                    batchSelectData(admissionInformationRO).size();
            countOfClassNotOpen += 1;
            classNotOpenAllCount += studentCount;
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

    public String checkClassName(String alternativeClassName, String collegeNameKey, String collegeName){
        List<String> classNames = collegeClassNames.get(collegeNameKey);
        String resultClassName = alternativeClassName;
        boolean contains = classNames.contains(resultClassName);
        while (contains){
            if(resultClassName.matches("[^0-9]+")){
                resultClassName = alternativeClassName + 1;
            }else{
                int i = Integer.parseInt(resultClassName.replaceAll("[^0-9]", ""));
                i += 1;
                resultClassName = alternativeClassName + i;
            }
            contains = classNames.contains(resultClassName);
        }
        collegeClassNames.get(collegeNameKey).add(resultClassName);
        collegeClassNamesForClassNumber.get(collegeName).add(resultClassName);
        return resultClassName;
    }

    /**
     * 检测入学年份 和专业信息 根据层次来判断正常毕业时间
     * @param admissionGrade
     * @param majorInformationPO
     * @return
     */
    private String checkGraduateDate(String admissionGrade, MajorInformationPO majorInformationPO){
        String graduateGrade = "";
        if(majorInformationPO.getLevel().equals("高起本")){
            graduateGrade = graduateGrade + (Integer.parseInt(admissionGrade) + 5);
        }else{
            graduateGrade = graduateGrade + (Integer.parseInt(admissionGrade) + 3);
        }
        return graduateGrade + ".01";
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("\n开班总数 " + countOfClassOpen + " 不开班总数 " + countOfClassNotOpen);
        if(!classInformationOldSystemImportVOList.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("Asia/Shanghai"));

            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/旧系统导入所需数据";
            String errorFileName = "2024" + "_不分班开班数据.xlsx";

            // 检查目录是否存在，不存在则创建
            File directory = new File(relativePath);
            if (!directory.exists()) {
                directory.mkdirs(); // 这将创建所需的所有目录，即使中间目录不存在
            }

            EasyExcel.write(relativePath + "/" + errorFileName, ClassInformationOldSystemImportVO.class).sheet("Sheet1").doWrite(classInformationOldSystemImportVOList);
            log.info("2024" + " 开班数据存在 " + countOfClassOpen + " 条记录，已写入 " + errorFileName);
            log.info("总共所有开班的总人数为 " + allCount);
            log.info("总共所有不开班的总人数为 " + classNotOpenAllCount);
        }else{
            log.info("未拿到任何开班数据");
        }
    }
}
