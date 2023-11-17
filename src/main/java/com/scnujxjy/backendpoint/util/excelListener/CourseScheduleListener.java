package com.scnujxjy.backendpoint.util.excelListener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.constant.enums.LiveStatusEnum;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.UserUploadsPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.UserUploadsMapper;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.BatchInfo;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleExcelImportVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleExcelOutputVO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
public class CourseScheduleListener extends AnalysisEventListener<CourseScheduleExcelImportVO> {
    private final static String importBucketName = "dataimport";


    private CourseScheduleMapper courseScheduleMapper;
    private ClassInformationMapper classInformationMapper;
    private StudentStatusMapper studentStatusMapper;
    private CourseInformationMapper courseInformationMapper;
    private UserUploadsMapper userUploadsMapper;
    private MinioService minioService;
    private UserUploadsPO userUploadsPO;
    private TeacherInformationMapper teacherInformationMapper;

    private String collegeName;

    private boolean allSuccess = true;
    private int dataCount = 0; // 添加一个计数变量
    private boolean update = false; // 出现重复的排课表时是否需要覆盖

    private String commonClassB = null;    // 用于记录合班的教学班别名称第一个
    public Boolean manger = false;    // 用于记录合班的教学班别名称第一个

    private String finalNewTeachingClass = null;    // 用于最终记录合班的教学班别名称，因为可能行政班别或者已有的教学班别（人工）出现了重复
    private HashMap<String, HashSet<String>> commonClassMap = new HashMap<>();    // 用于记录合班的教学班别所对应的每一个行政班
    private HashSet<String> commonClassSet = new HashSet<>();   // 记录Excel 已读入的合班的教学班别

    private List<CourseScheduleExcelOutputVO> outputDataList = new ArrayList<>();

    private List<String> grades;
    private List<String> majorNames;
    private List<String> levels;
    private List<String> studyForms;

    public CourseScheduleListener(CourseScheduleMapper courseScheduleMapper, ClassInformationMapper classInformationMapper,
                                  TeacherInformationMapper teacherInformationMapper, StudentStatusMapper studentStatusMapper,
                                  CourseInformationMapper courseInformationMapper, UserUploadsMapper userUploadsMapper, MinioService minioService,
                                  String collegeName, UserUploadsPO userUploadsPO) {
        this.courseScheduleMapper = courseScheduleMapper;
        this.classInformationMapper = classInformationMapper;
        this.teacherInformationMapper = teacherInformationMapper;
        this.studentStatusMapper = studentStatusMapper;
        this.courseInformationMapper = courseInformationMapper;
        this.userUploadsMapper = userUploadsMapper;
        this.minioService = minioService;
        this.userUploadsPO = userUploadsPO;
        this.collegeName = collegeName;
        grades = classInformationMapper.selectDistinctGrades();
        majorNames = classInformationMapper.selectDistinctMajorNames();
        levels = classInformationMapper.selectDistinctLevels();
        studyForms = classInformationMapper.selectDistinctStudyforms();
    }

    private CourseSchedulePO convertVOtoPO(CourseScheduleExcelImportVO vo) {
        CourseSchedulePO po = CourseSchedulePO.builder()
                .id(vo.getId())
                .grade(vo.getGrade())
                .majorName(vo.getMajorName())
                .level(vo.getLevel())
                .studyForm(vo.getStudyForm())
                .adminClass(vo.getAdminClass())
                .teachingClass(vo.getTeachingClass())
                .studentCount(vo.getStudentCount())
                .courseName(vo.getCourseName())
                .classHours(vo.getClassHours())
                .examType(vo.getExamType())
                .mainTeacherName(vo.getMainTeacherName())
                .mainTeacherId(vo.getMainTeacherId())
                .mainTeacherIdentity(vo.getMainTeacherIdentity())
                .tutorName(vo.getTutorName())
                .tutorId(vo.getTutorId())
                .tutorIdentity(vo.getTutorIdentity())
                .teachingMethod(vo.getTeachingMethod())
                .classLocation(vo.getClassLocation())
                .onlinePlatform(vo.getOnlinePlatform())
                .teachingDate(vo.getTeachingDate())
                .teachingTime(vo.getTeachingTime())
                .teacherUsername(vo.getTeacherUsername())
                .teachingAssistantUsername(vo.getTeachingAssistantUsername())
                .build();
        return po;
    }

    private int insertCourseScheduleData(CourseScheduleExcelImportVO data, CourseScheduleExcelOutputVO outputData) {
        int count = courseScheduleMapper.checkDuplicate(convertVOtoPO(data));
        if (count > 0) {
            if (count > 1) {
                throw new RuntimeException("已存在完全匹配的数据并且大于 2条! " + courseScheduleMapper.findDuplicateRecords(convertVOtoPO(data)));
            } else {
                if ("是".equals(outputData.getCover())) {
                    // 允许覆盖
                    int insert = courseScheduleMapper.updateCourseScheduleByConditions(convertVOtoPO(data));
                    outputData.setErrorMessage("覆盖成功 " + insert + " 条");
                } else {
                    throw new RuntimeException("已存在完全匹配的数据 1 条! " + courseScheduleMapper.findDuplicateRecords(convertVOtoPO(data)));
                }
            }
        } else {
            int insert = courseScheduleMapper.insert(convertVOtoPO(data));
            outputData.setErrorMessage("导入成功 " + insert);
        }
//        log.info("读入一行数据 " + data.toString() + "\n 上课时间为 " + data.getTeachingDate() + "  " + data.getTeachingTime());
        dataCount++;
        return 1;
    }

    @Override
    public void invoke(CourseScheduleExcelImportVO data, AnalysisContext context) {
        CourseScheduleExcelOutputVO outputData = new CourseScheduleExcelOutputVO();
        BeanUtils.copyProperties(data, outputData); // 将读入的数据复制到输出数据中

        // 检查studentCount字段是否包含加号，并进行处理
        if (data.getStudentCount() != null && data.getStudentCount().toString().contains("+")) {
            String[] counts = data.getStudentCount().toString().split("\\+");
            int total = 0;
            for (String count : counts) {
                total += Integer.parseInt(count.trim());
            }
            data.setStudentCount(total); // 更新学生人数字段
        }

        Set<String> commonClass = new HashSet<>();  // 合班后的教学班别名称集合
        // 将读取到的数据插入到数据库中
        try {

            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(data.getTeachingDate());

                // 全角转半角
                String teachingTime = data.getTeachingTime().replace("：", ":").replace("－", "-").replace("—", "-");


                String[] timeParts = teachingTime.split("[:-]");
                // 这会把 "8:30-11:30" 分为 "8", "30", "11", "30"
                // 开始时间
                int startHour = Integer.parseInt(timeParts[0].trim());
                int startMinute = Integer.parseInt(timeParts[1].trim());
                calendar.set(Calendar.HOUR_OF_DAY, startHour);
                calendar.set(Calendar.MINUTE, startMinute);
                Date startDateTime = calendar.getTime();

                // 结束时间
                int endHour = Integer.parseInt(timeParts[2].trim());
                int endMinute = Integer.parseInt(timeParts[3].trim());
                calendar.set(Calendar.HOUR_OF_DAY, endHour);
                calendar.set(Calendar.MINUTE, endMinute);
                Date endDateTime = calendar.getTime();

                data.setTeachingTime(teachingTime);
            } catch (Exception e) {
                throw new RuntimeException("上课时间错误: " + data.getTeachingDate() + " " + data.getTeachingTime()
                        + " 上课时间请按照标准格式来写  eg.2023-10-16 15:00-17:00");
            }

            // 你现在可以使用exactDateTime变量，这是一个具体的时间点。

            // 插入之前校验一下班级、年级、专业名称、层次、课程名称是否对得上教学计划、主讲教师是否在师资库、日期时间是否正确
            // 先校验排课表中的行政班别是否一致
            String grade = data.getGrade();
            String major_name = data.getMajorName();
            String level = data.getLevel();
            String study_form = data.getStudyForm();
            // 使用正则表达式来判断grade是否只包含整数
            if (grade.matches("^\\d+$")) {
                // grade 是一个整数
            } else {
                throw new RuntimeException("年级错误: " + data.getGrade() + "年级必须是整数 ");
            }


            // Check grade
            if (!grades.contains(grade)) {
                throw new RuntimeException("年级错误: " + data.getGrade() + ". 可选的年级有: " + String.join(", ", grades));
            }

            // Check major name
            if (!majorNames.contains(major_name)) {
                throw new RuntimeException("专业名称错误: " + data.getMajorName() + ". 可选的专业名称有: " + String.join(", ", majorNames));
            }

            // Check level
            if (!levels.contains(level)) {
                throw new RuntimeException("层次错误: " + data.getLevel() + ". 可选的层次有: " + String.join(", ", levels));
            }

            // Check study form
            if (!studyForms.contains(study_form)) {
                throw new RuntimeException("学习形式错误: " + data.getStudyForm() + ". 可选的学习形式有: " + String.join(", ", studyForms));
            }

            List<ClassInformationPO> classInformationPOS =
                    classInformationMapper.selectClassByCondition1(grade, major_name, level, study_form);


            // 将数据库中根据年级、专业名称、层次、学习形式获取的所有班名拿出来给他们 方便他们知道如何修改
            // 检查行政班别是否正确
            ArrayList<String> classList = new ArrayList<>();
            for (ClassInformationPO classInformationPO : classInformationPOS) {
                classList.add(classInformationPO.getClassName());
            }

            String targetClassName = data.getAdminClass().trim();
            ClassInformationPO matchedClassInfo = null;
            if (!classInformationPOS.isEmpty()) {
                long matchingCount = classInformationPOS.stream()
                        .filter(classInfo -> targetClassName.equals(classInfo.getClassName()))
                        .count();  // 计算匹配项的数量

                if (matchingCount > 1) {
                    throw new RuntimeException("存在多个班级信息记录: " + classInformationPOS);
                } else if (matchingCount == 1) {
                    matchedClassInfo = classInformationPOS.stream()
                            .filter(classInfo -> targetClassName.equals(classInfo.getClassName()))
                            .findFirst()
                            .orElse(null);  // 这应该不会发生，因为我们已经检查过匹配项的数量

                    // ...在这里处理匹配的班级信息
                } else {
                    outputData.setErrorMessage("行政班别与系统中所对应的 年级、专业名称、层次、学习形式的班别名称出现不一致 " + classInformationPOS);
                }

            } else {
                outputData.setErrorMessage("没有找到该 年级、专业名称、层次、学习形式所匹配的 班级 根据表格中提供的年级、层次、专业、学习形式在系统中查到的班级如下\n" +
                        classList.toString());
            }

            // 在班级信息确定后，检查课程信息是否与教学计划对得上 班级人数是否填对了
            if (matchedClassInfo != null) {
                try {
                    List<CourseInformationPO> courseInformationPOs = courseInformationMapper.selectList(new LambdaQueryWrapper<CourseInformationPO>()
                            .eq(CourseInformationPO::getGrade, grade)
                            .eq(CourseInformationPO::getMajorName, major_name)
                            .eq(CourseInformationPO::getLevel, level)
                            .eq(CourseInformationPO::getStudyForm, study_form)
                            .eq(CourseInformationPO::getAdminClass, matchedClassInfo.getClassIdentifier())
                            .eq(CourseInformationPO::getCourseName, data.getCourseName())
                    );
                    if (courseInformationPOs.isEmpty()) {
                        outputData.setErrorMessage("按照年级、专业名称、层次、学习形式和行政班别以及课程名称没有找到相关的教学计划 \n");

                    } else if (courseInformationPOs.size() > 1) {
                        outputData.setErrorMessage("按照年级、专业名称、层次、学习形式和行政班别以及课程名称找到多个教学计划 \n" + courseInformationPOs);
                    } else {
                        CourseInformationPO courseInformationPO = courseInformationPOs.get(0);
                        // 找到该课程了 开始比对人数、学时、考核类型
//                        Integer studentCount = data.getStudentCount();
//                        Integer i = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>()
//                                .eq(StudentStatusPO::getClassIdentifier, matchedClassInfo.getClassIdentifier())
//                        );
//                        if (!studentCount.equals(i)) {
//                            outputData.setErrorMessage("按照年级、专业名称、层次、学习形式和行政班别以及课程名称查找班级人数 人数与系统统计不相等 系统统计该班级人数为 \n" +
//                                    i);
//                        }
                        // 更新教学计划中的授课方式
                        courseInformationPO.setTeachingMethod(data.getTeachingMethod());
                        int i1 = courseInformationMapper.updateById(courseInformationPO);
                        if (i1 <= 0) {
                            log.error("更新教学计划的上课方式失败" + i1);
                        }

                    }
                } catch (Exception e) {
                    outputData.setErrorMessage("按照年级、专业名称、层次、学习形式和行政班别以及课程名称查找教学计划失败 \n" +
                            e.toString());
                }
            }
            // 在班级信息确定后，检查班级是否属于指定的学院
            // 前提是它不是继续教育学院管理员导入或者超级管理员导入
            if (!manger) {
                String actualCollegeName = classInformationMapper.selectCollegeByMultipleConditions(data.getGrade(), data.getMajorName(),
                        data.getLevel(), data.getStudyForm(), targetClassName);
                if (actualCollegeName != null && !collegeName.equals(actualCollegeName)) {
                    throw new RuntimeException("班级 " + data.getAdminClass() + " 不属于 " + collegeName + " 学院。");
                } else if (actualCollegeName == null) {
                    throw new RuntimeException(("该记录中的行政班别不属于任何学院 "));
                }
            }


            // 年级、层次、专业名称、层次、学习形式以及行政班级没问题后 需要根据其是否写了教学班别 进行合班判断
            // 如果教学班别写了  说明要开始合班 或者说它手工补充了教学班别（单个行政班别）
            // 但无论怎样 如果出现了教学班别 下一行数据中的教学班别如果和这个教学班别相同则说明下一个行数据与其合班
            // 但是不允许出现重复（数据库和现有的 Excel 的数据）的教学班别，如果出现让程序自动加个序号直到没有重复
//            if(commonClassB == null){
//                // 读取第一条排课记录
//                String adminClass = data.getAdminClass();
//                String teachingClass = data.getTeachingClass();
//                if(teachingClass == null || teachingClass.trim().length() == 0){
//                    // 行政班单独成班
//                    String newTeachingClass = adminClass;
//                    int count = 1;
//                    List<String> allDistinctTeachingClasses = courseScheduleMapper.getAllDistinctTeachingClasses();
//                    while (allDistinctTeachingClasses.contains(newTeachingClass)){
//                        newTeachingClass = adminClass + count;
//                        count += 1;
//                    }
//                    data.setTeachingClass(newTeachingClass);
//                    // 设好教学班后 一定要建立本地的 教学班别记录 防止后续数据重复
//                    commonClassSet.add(newTeachingClass);
//                    finalNewTeachingClass = newTeachingClass;
//                }else{
//                    // 教学班别被人工设置了
//                    String newTeachingClass = data.getTeachingClass();
//                    int count = 1;
//                    List<String> allDistinctTeachingClasses = courseScheduleMapper.getAllDistinctTeachingClasses();
//                    while (allDistinctTeachingClasses.contains(newTeachingClass)){
//                        newTeachingClass = adminClass + count;
//                        count += 1;
//                    }
//                    data.setTeachingClass(newTeachingClass);
//                    // 设好教学班后 一定要建立本地的 教学班别记录 防止后续数据重复
//                    commonClassSet.add(newTeachingClass);
//                    finalNewTeachingClass = newTeachingClass;
//                }
//            }else{
//                // 读取第一条以后的排课记录
//                String adminClass = data.getAdminClass();
//                String teachingClass = data.getTeachingClass();
//                if(teachingClass == null || teachingClass.trim().length() == 0){
//                    // 说明没合班 而且单独一个班作为教学班
//                    String newTeachingClass = adminClass;
//                    int count = 1;
//                    List<String> allDistinctTeachingClasses = courseScheduleMapper.getAllDistinctTeachingClasses();
//                    while (allDistinctTeachingClasses.contains(newTeachingClass) || commonClassSet.contains(newTeachingClass)){
//                        newTeachingClass = adminClass + count;
//                        count += 1;
//                    }
//                    data.setTeachingClass(newTeachingClass);
//                    // 设好教学班后 一定要建立本地的 教学班别记录 防止后续数据重复
//                    commonClassSet.add(newTeachingClass);
//                    finalNewTeachingClass = newTeachingClass;
//                }else{
//                    // 可能要合班，人工设置了教学班，字符串长度不为 0
//                    if(teachingClass.trim().equals(this.commonClassB.trim())){
//                        // 与上一行的教学班别相等
//                        data.setTeachingClass(this.finalNewTeachingClass);
//                    }else{
//                        // 不相等 与上一行的教学班别不同，那就直接先成立一个教学班 后面是否合班再看
//                        String newTeachingClass = data.getTeachingClass();
//                        int count = 1;
//                        List<String> allDistinctTeachingClasses = courseScheduleMapper.getAllDistinctTeachingClasses();
//                        while (allDistinctTeachingClasses.contains(newTeachingClass) || commonClassSet.contains(newTeachingClass)){
//                            newTeachingClass = adminClass + count;
//                            count += 1;
//                        }
//                        data.setTeachingClass(newTeachingClass);
//                        // 设好教学班后 一定要建立本地的 教学班别记录 防止后续数据重复
//                        commonClassSet.add(newTeachingClass);
//                        finalNewTeachingClass = newTeachingClass;
//                    }
//                }
//            }
            // 教学班后续更新
//            data.setTeachingClass(data.getMainTeacherName() + "-" + data.getCourseName());

            // 进行主讲教师判断
            String mainTeacherName = data.getMainTeacherName();
            List<TeacherInformationPO> teacherInformationPOS = teacherInformationMapper.selectByName(mainTeacherName);
            if (teacherInformationPOS.size() == 0) {
                throw new RuntimeException("系统师资库中没有该主讲老师信息，请先提供该教师信息");
            } else if (teacherInformationPOS.size() == 1) {
                TeacherInformationPO teacherInformationPO = teacherInformationPOS.get(0);
                data.setTeacherUsername(teacherInformationPO.getTeacherUsername());
            } else {
                // 存在同名同姓老师
                String workNumber = data.getMainTeacherId();    // 获取 Excel 中的主讲教师工号/学号
                String idNumber = data.getMainTeacherIdentity();    // 获取 Excel 中的主讲教师的身份证号码
                if (workNumber == null) {
                    throw new RuntimeException("系统师资库中存在多名同名同姓的教师，请提供工号/学号");
                }
                List<TeacherInformationPO> teacherInformationPOS1 = teacherInformationMapper.selectByWorkNumber(workNumber.trim());
                if (teacherInformationPOS1.size() == 1) {
                    TeacherInformationPO teacherInformationPO = teacherInformationPOS1.get(0);
                    data.setTeacherUsername(teacherInformationPO.getTeacherUsername());
                } else {
                    if (idNumber == null) {
                        throw new RuntimeException("系统师资库中存在多名同名同姓的教师，请提供身份证号码");
                    }
                    List<TeacherInformationPO> teacherInformationPOS2 = teacherInformationMapper.selectByIdCardNumber(idNumber.trim());
                    if (teacherInformationPOS2.size() == 1) {
                        TeacherInformationPO teacherInformationPO = teacherInformationPOS2.get(0);
                        data.setTeacherUsername(teacherInformationPO.getTeacherUsername());
                    } else {
                        throw new RuntimeException("系统师资库中存在多名同名同姓的教师，请提供工号/学号或者身份证号码");
                    }
                }
            }

            // 进行助教教师判断
            String tutorName = data.getTutorName();
            if (tutorName != null && tutorName.trim().length() != 0) {
                List<TeacherInformationPO> tutorTeachers = teacherInformationMapper.selectByName(tutorName);
                if (tutorTeachers.size() == 0) {
                    throw new RuntimeException("系统师资库中没有该助教老师信息，请先提供该教师信息");
                } else if (tutorTeachers.size() == 1) {
                    TeacherInformationPO teacherInformationPO = tutorTeachers.get(0);
                    data.setTeachingAssistantUsername(teacherInformationPO.getTeacherUsername());
                } else {
                    // 存在同名同姓老师
                    String workNumber = data.getTutorId();    // 获取 Excel 中的主讲教师工号/学号
                    String idNumber = data.getTutorIdentity();    // 获取 Excel 中的主讲教师的身份证号码
                    if (workNumber == null) {
                        throw new RuntimeException("系统师资库中存在多名同名同姓的助教，请提供工号/学号");
                    }
                    List<TeacherInformationPO> tutorTeachers1 = teacherInformationMapper.selectByWorkNumber(workNumber.trim());
                    if (tutorTeachers1.size() == 1) {
                        TeacherInformationPO teacherInformationPO = tutorTeachers.get(0);
                        data.setTeachingAssistantUsername(teacherInformationPO.getTeacherUsername());
                    } else {
                        if (idNumber == null) {
                            throw new RuntimeException("系统师资库中存在多名同名同姓的助教，请提供身份证号码");
                        }
                        List<TeacherInformationPO> tutorTeachers2 = teacherInformationMapper.selectByIdCardNumber(idNumber.trim());
                        if (tutorTeachers2.size() == 1) {
                            TeacherInformationPO teacherInformationPO = tutorTeachers2.get(0);
                            data.setTeachingAssistantUsername(teacherInformationPO.getTeacherUsername());
                        } else {
                            throw new RuntimeException("系统师资库中存在多名同名同姓的助教，请提供工号/学号或者身份证号码");
                        }
                    }
                }
            }


            log.info("插入的一条数据为 " + outputData);
            // 插入之前还要检查一下 online_platform 字段 看其是否是已经上过的可
            String onlinePlatform = data.getOnlinePlatform();
            if (onlinePlatform != null && !onlinePlatform.trim().isEmpty()) {
                if (LiveStatusEnum.END.status.equals(onlinePlatform)) {

                } else {
                    data.setOnlinePlatform(null);
                }
            }

            // 年级、层次、学习形式、专业名称、行政班别、教学班别都没问题了 主讲老师也能找到唯一一个 接下来就可以将此排课记录写入数据库
            if (data.getTeacherUsername() == null) {
                throw new IllegalArgumentException("非法数据 获取不到主讲老师的用户名");
            }
            // 一旦出现任何错误不要再执行插入语句了
            if (StringUtils.isBlank(outputData.getErrorMessage())) {
                insertCourseScheduleData(data, outputData);
            }


        } catch (Exception e) {
            log.error("插入数据失败 " + data.toString() + "\n" + e.toString());
            outputData.setErrorMessage(e.getMessage()); // 设置错误信息
        }
        outputDataList.add(outputData); // 将输出数据添加到列表中
        if (outputData.getErrorMessage() != null) {
            allSuccess = false;
        }
    }

    public void dohandleBitch() {

        //获取当前最大的批次值
        Long MaxBitch = courseScheduleMapper.selectMaxBitch();
        int updateCount = 0;
        //获取当前批次为空的所有排课表。其实就是刚导入的排课表
        QueryWrapper<CourseSchedulePO> courseQueryWrapper = new QueryWrapper<>();
        courseQueryWrapper.and(i -> i.isNull("batch_index").or().eq("batch_index", ""));
        Integer count = courseScheduleMapper.selectCount(courseQueryWrapper);

        List<CourseSchedulePO> schedulePOList = courseScheduleMapper.selectList(null);


        Map<BatchInfo, List<CourseSchedulePO>> batches = new HashMap<>();
        for (CourseSchedulePO schedule : schedulePOList) {
            //获取所有的合班课程
            List<CourseSchedulePO> courseSchedulePOS2 = courseScheduleMapper.selectList(new LambdaQueryWrapper<CourseSchedulePO>()
                    .eq(CourseSchedulePO::getMainTeacherName, schedule.getMainTeacherName())
                    .eq(CourseSchedulePO::getCourseName, schedule.getCourseName())
                    .eq(CourseSchedulePO::getTeachingDate, schedule.getTeachingDate())
                    .eq(CourseSchedulePO::getTeachingTime, schedule.getTeachingTime())
            );
            Map<String, Integer> classCountMap = new HashMap<>();
            for (CourseSchedulePO schedulePO : courseSchedulePOS2) {
                String className = schedulePO.getAdminClass();
                if (classCountMap.containsKey(className)) {
                    log.error("Duplicate class name found: " + className);
                } else {
                    classCountMap.put(className, 1);
                }
            }

            //获取到所有合班的className
            // At this point, no duplicates were found, so you can safely create a set of class names
            Set<String> classSet = classCountMap.keySet();
            // 获取当前日期
            LocalDate currentDate = LocalDate.now();

            // 获取当前年份
            int year = currentDate.getYear();

            // 获取当前月份
            int month = currentDate.getMonthValue();

            // 判断学期
            String semester = (month >= 2 && month <= 8) ? "夏季" : "冬季";


            BatchInfo key = new BatchInfo("" + year, semester, schedule.getMainTeacherName(), schedule.getCourseName(), classSet);
            if (batches.containsKey(key)) {
                batches.get(key).add(schedule);
            } else {
                batches.put(key, new ArrayList<>());
                batches.get(key).add(schedule);
            }
        }

        //这样拿到的所有的Map<BatchInfo, List<CourseSchedulePO>>
        for (BatchInfo key : batches.keySet()) {
            //该集合的所有className都是一样的，是合班或者部分合班的
            List<CourseSchedulePO> courseSchedulePOS = batches.get(key);

            if (courseSchedulePOS.size() == 0) {
                continue;
            }

            Long bitch = null;
            String teachingClass = null;

            for (CourseSchedulePO courseSchedulePO : courseSchedulePOS) {
                if (Objects.nonNull(courseSchedulePO.getBatchIndex())) {
                    bitch = courseSchedulePO.getBatchIndex();
                    teachingClass = courseSchedulePO.getTeachingClass();
                    break;
                }
            }
            if (Objects.isNull(bitch)) {
                bitch = ++MaxBitch;
            }

            if(teachingClass == null){
                // 如果没有找到教学班
                CourseSchedulePO courseSchedulePO = courseSchedulePOS.get(0);
                // 获取当前日期
                LocalDate currentDate = LocalDate.now();

                // 获取当前年份
                int year = currentDate.getYear();
                System.out.println("当前年份: " + year);
                int teachingClassCount = 1;
                teachingClass =  year + "-" + courseSchedulePO.getMainTeacherName() + "-" + courseSchedulePO.getCourseName() + "-"
                        + teachingClassCount + "班";
                List<CourseSchedulePO> courseSchedulePO1s = courseScheduleMapper.selectList(new LambdaQueryWrapper<CourseSchedulePO>().eq(CourseSchedulePO::getTeachingClass, teachingClass));

                while(!courseSchedulePO1s.isEmpty()){
                    teachingClassCount += 1;
                    teachingClass =  year + "-" + courseSchedulePO.getMainTeacherName() + "-" + courseSchedulePO.getCourseName() + "-"
                            + teachingClassCount + "班";
                    courseSchedulePO1s = courseScheduleMapper.selectList(new LambdaQueryWrapper<CourseSchedulePO>().eq(CourseSchedulePO::getTeachingClass, teachingClass));
                }
            }

            List<CourseSchedulePO> updateList = courseSchedulePOS.stream()
                    .filter(schedule -> schedule.getBatchIndex() == null)
                    .collect(Collectors.toList());

            for (CourseSchedulePO courseSchedulePO : updateList) {
                courseSchedulePO.setBatchIndex(bitch);
                courseSchedulePO.setTeachingClass(teachingClass);
                int i = courseScheduleMapper.updateById(courseSchedulePO);
                updateCount = updateCount + i;
            }

        }

        if (updateCount == count) {
            log.info("更新排课表批量导入成功");
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

        //执行更新批次id方法
        dohandleBitch();

        // 可进行一些后置处理
        // 使用EasyExcel写入数据到新的Excel文件中

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/courseSchedule";
        String errorFileName = currentDateTime + "_errorImportCourseSchedule.xlsx";

        // 确保目录存在
        try {
            Path directoryPath = Paths.get(relativePath);
            Files.createDirectories(directoryPath);

            // 现在写入文件
            EasyExcel.write(relativePath + "/" + errorFileName, CourseScheduleExcelOutputVO.class)
                    .sheet("Sheet1")
                    .doWrite(outputDataList);
        } catch (Exception e) {
            log.error("写入排课表处理结果反馈文件失败 " + e.toString());
        }

        // 使用 ByteArrayOutputStream 捕获 EasyExcel 写入的数据
        // 排课表导入/import/xuelijiaoyuTest1排课表信息导入（经管学院）(1)-2023-10-29T21:55:58.388.xlsx
        String fileUrl = this.userUploadsPO.getFileUrl().replace("import", "feedback");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 现在写入 ByteArrayOutputStream
            EasyExcel.write(baos, CourseScheduleExcelOutputVO.class)
                    .sheet("Sheet1")
                    .doWrite(outputDataList);
        } catch (Exception e) {
            log.error("写入排课表处理结果反馈文件失败 " + e.toString());
        }

        // 将 ByteArrayOutputStream 转换为 ByteArrayInputStream
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        // 现在你可以使用 uploadStreamToMinio 方法上传数据到 Minio
        boolean uploadSuccess = minioService.uploadStreamToMinio(bais, fileUrl, importBucketName);

        if (uploadSuccess) {
            log.info("排课表反馈文件上传成功");
            // 最后更新消息列表
            if (this.allSuccess) {
                this.userUploadsPO.setResultDesc("全部导入成功");
            } else {
                this.userUploadsPO.setResultDesc("存在部分错误，请下载反馈文件");
            }
            this.userUploadsPO.setResultUrl(fileUrl);
            int i = this.userUploadsMapper.updateById(this.userUploadsPO);
            log.info("上传消息已更新 " + i);
        } else {
            log.error("排课表反馈文件上传失败");
            // 最后更新消息列表
            this.userUploadsPO.setResultDesc("服务器处理失败");
            int i = this.userUploadsMapper.updateById(this.userUploadsPO);
            log.info("上传消息已更新 " + i);
        }


    }

}
