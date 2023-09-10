package com.scnujxjy.backendpoint.CourseScheduleTest;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.scnujxjy.backendpoint.TeacherInformationTest.TeacherInformationErrorRecord;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationExcelImportVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleExcelImportVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleExcelOutputVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Data
public class CourseScheduleListener extends AnalysisEventListener<CourseScheduleExcelImportVO> {
    private CourseScheduleMapper courseScheduleMapper;
    private ClassInformationMapper classInformationMapper;

    private TeacherInformationMapper teacherInformationMapper;

    private String collegeName;

    private int dataCount = 0; // 添加一个计数变量
    private boolean update = false; // 出现重复的排课表时是否需要覆盖

    private String commonClassB = null;    // 用于记录合班的教学班别名称第一个

    private String finalNewTeachingClass = null;    // 用于最终记录合班的教学班别名称，因为可能行政班别或者已有的教学班别（人工）出现了重复
    private HashMap<String, HashSet<String>> commonClassMap = new HashMap<>();    // 用于记录合班的教学班别所对应的每一个行政班
    private HashSet<String> commonClassSet = new HashSet<>();   // 记录Excel 已读入的合班的教学班别

    private List<CourseScheduleExcelOutputVO> outputDataList = new ArrayList<>();


    public CourseScheduleListener(CourseScheduleMapper courseScheduleMapper, ClassInformationMapper classInformationMapper,
                                  TeacherInformationMapper teacherInformationMapper, String collegeName) {
        this.courseScheduleMapper = courseScheduleMapper;
        this.classInformationMapper = classInformationMapper;
        this.teacherInformationMapper = teacherInformationMapper;
        this.collegeName = collegeName;
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
                .build();
        return po;
    }

    private int insertCourseScheduleData(CourseScheduleExcelImportVO data, CourseScheduleExcelOutputVO outputData, int ident){
        if(ident == 1) {
            int count = courseScheduleMapper.checkDuplicate(convertVOtoPO(data));
            if (count > 0) {
                int insert = courseScheduleMapper.updateCourseScheduleByConditions(convertVOtoPO(data));
                outputData.setErrorMessage("覆盖成功 " + insert + " 条");
            }else{
                int insert = courseScheduleMapper.insert(convertVOtoPO(data));
                outputData.setErrorMessage("导入成功 " + insert);
            }
        }else{
            int insert = courseScheduleMapper.updateCourseScheduleByConditions(convertVOtoPO(data));
            outputData.setErrorMessage("覆盖成功 " + insert + " 条");
        }
//                log.info("读入一行数据 " + data.toString() + "\n 上课时间为 " + startDateTime + " 下课时间为 " + endDateTime);
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

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(data.getTeachingDate());
            if(data.getTeachingTime().contains("-")){
                // 特殊字符
                data.setTeachingTime(data.getTeachingTime().replace("-", "—"));
            }
            String[] timeParts = data.getTeachingTime().split("[:-—]");
            // 这会把 "8:30-11:30" 分为 "8", "30", "11", "30"
            // 开始时间
            int startHour = Integer.parseInt(timeParts[0]);
            int startMinute = Integer.parseInt(timeParts[1]);
            calendar.set(Calendar.HOUR_OF_DAY, startHour);
            calendar.set(Calendar.MINUTE, startMinute);
            Date startDateTime = calendar.getTime();

            // 结束时间
            int endHour = Integer.parseInt(timeParts[2]);
            int endMinute = Integer.parseInt(timeParts[3]);
            calendar.set(Calendar.HOUR_OF_DAY, endHour);
            calendar.set(Calendar.MINUTE, endMinute);
            Date endDateTime = calendar.getTime();

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
                // grade 不是一个整数
                // 你可以在这里处理错误或抛出异常
                // 使用正则表达式去除所有中文字符
                grade = grade.replaceAll("[\u4e00-\u9fa5]", "");
            }

            List<String> grades = classInformationMapper.selectDistinctGrades();
            List<String> majorNames = classInformationMapper.selectDistinctMajorNames();
            List<String> levels = classInformationMapper.selectDistinctLevels();
            List<String> studyforms = classInformationMapper.selectDistinctStudyforms();

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
            if (!studyforms.contains(study_form)) {
                throw new RuntimeException("学习形式错误: " + data.getStudyForm() + ". 可选的学习形式有: " + String.join(", ", studyforms));
            }

            List<ClassInformationPO> classInformationPOS =
                    classInformationMapper.selectClassByCondition1(grade, major_name, level, study_form);


            // 将数据库中根据年级、专业名称、层次、学习形式获取的所有班名拿出来给他们 方便他们知道如何修改
            ArrayList<String> classList = new ArrayList<>();
            for(ClassInformationPO classInformationPO: classInformationPOS){
                classList.add(classInformationPO.getClassName());
            }

            if(classInformationPOS.size() == 1){
                // 找到班级后进下一步检测，是否班级名称与排课表中的行政班别一致以及合班的教学班别是否出现了重复班别命名
                ClassInformationPO classInformationPO = classInformationPOS.get(0);
                String classDBName = classInformationPO.getClassName();
                if(classDBName.equals(data.getAdminClass())){
                    // 检测是否有教学班别名称，如果没有则可以录入
                    if(data.getTeachingClass() == null || data.getTeachingClass().length() == 0){
//                        insertCourseScheduleData(data, outputData, 1);
                    }else{
                        String commonClassA = data.getTeachingClass().trim();
                        //合班的教学班名检测先不考虑了

                        // 考虑老师是否存在师资库中，以及老师姓名重复的问题
                        List<TeacherInformationPO> teacherInformationPOS = teacherInformationMapper.selectByName(data.getMainTeacherName());
                        if(teacherInformationPOS.size() > 1){
                            // 存在同名老师，需要匹配其工号、身份证号码或者手机号码，如果这三者不能唯一锁定才报错
                            String mainTeacherId = data.getMainTeacherId();
                            String mainTeacherIdentity = data.getMainTeacherIdentity();
                            List<TeacherInformationPO> teacherInformationPOS1 = teacherInformationMapper.selectByWorkNumber(mainTeacherId);
                            if(teacherInformationPOS1.size() == 1){
//                                insertCourseScheduleData(data, outputData, 1);
                            }else{
                                List<TeacherInformationPO> teacherInformationPOS2 = teacherInformationMapper.selectByIdCardNumber(mainTeacherIdentity);
                                if(teacherInformationPOS2.size() == 1){
//                                    insertCourseScheduleData(data, outputData, 1);
                                }else{
                                    outputData.setErrorMessage("主讲教师出现重复没找到唯一 " + data.getMainTeacherName());
                                }
                            }

                        }else if(teacherInformationPOS.size() == 1){
                            if(classList.contains(data.getAdminClass())){
//                                insertCourseScheduleData(data, outputData, 1);
                            }else{
                                outputData.setErrorMessage("没有找到该 年级、专业名称、层次、学习形式所匹配的 班级 根据表格中提供的年级、层次、专业、学习形式在系统中查到的班级如下\n" +
                                        classList.toString());
                            }
                        }else{
                            outputData.setErrorMessage("主讲教师没找到 " + data.getMainTeacherName());
                        }

                    }

                }else{
                    outputData.setErrorMessage("行政班别与系统中所对应的 年级、专业名称、层次、学习形式的班别名称出现不一致 " + classDBName);
                }


            }else if(classInformationPOS.size() > 1){
                // 这种情况确实存在 年级、专业、学习形式、层次 没法确定一个班级
                if(classList.contains(data.getAdminClass())){
                    // 检测数据库中是否有重复记录

//                    insertCourseScheduleData(data, outputData, 1);
                }else {
                    outputData.setErrorMessage("通过 年级、专业名称、层次、学习形式匹配数据库时出现了多个班级,但没有找到对应表格中的班级 根据表格中提供的年级、层次、专业、学习形式在系统中查到的班级如下\n" +
                            classList.toString());
                }
            }else{
                outputData.setErrorMessage("没有找到该 年级、专业名称、层次、学习形式所匹配的 班级 根据表格中提供的年级、层次、专业、学习形式在系统中查到的班级如下\n" +
                        classList.toString());
            }

            // 在班级信息确定后，检查班级是否属于指定的学院
            String actualCollegeName = classInformationMapper.selectCollegeByMultipleConditions(data.getGrade(), data.getMajorName(),
                    data.getLevel(), data.getStudyForm(), data.getAdminClass());
            if (actualCollegeName != null && !collegeName.equals(actualCollegeName)) {
                throw new RuntimeException("班级 " + data.getAdminClass() + " 不属于 " + collegeName + " 学院。");
            }else if(actualCollegeName == null){
                throw new RuntimeException(("该记录中的行政班别不属于任何学院 "));
            }


            // 年级、层次、专业名称、层次、学习形式以及行政班级没问题后 需要根据其是否写了教学班别 进行合班判断
            // 如果教学班别写了  说明要开始合班 或者说它手工补充了教学班别（单个行政班别）
            // 但无论怎样 如果出现了教学班别 下一行数据中的教学班别如果和这个教学班别相同则说明下一个行数据与其合班
            // 但是不允许出现重复（数据库和现有的 Excel 的数据）的教学班别，如果出现让程序自动加个序号直到没有重复
            if(commonClassB == null){
                // 读取第一条排课记录
                String adminClass = data.getAdminClass();
                String teachingClass = data.getTeachingClass();
                if(teachingClass == null || teachingClass.trim().length() == 0){
                    // 行政班单独成班
                    String newTeachingClass = adminClass;
                    int count = 1;
                    List<String> allDistinctTeachingClasses = courseScheduleMapper.getAllDistinctTeachingClasses();
                    while (allDistinctTeachingClasses.contains(newTeachingClass)){
                        newTeachingClass = adminClass + count;
                        count += 1;
                    }
                    data.setTeachingClass(newTeachingClass);
                    // 设好教学班后 一定要建立本地的 教学班别记录 防止后续数据重复
                    commonClassSet.add(newTeachingClass);
                    finalNewTeachingClass = newTeachingClass;
                }else{
                    // 教学班别被人工设置了
                    String newTeachingClass = data.getTeachingClass();
                    int count = 1;
                    List<String> allDistinctTeachingClasses = courseScheduleMapper.getAllDistinctTeachingClasses();
                    while (allDistinctTeachingClasses.contains(newTeachingClass)){
                        newTeachingClass = adminClass + count;
                        count += 1;
                    }
                    data.setTeachingClass(newTeachingClass);
                    // 设好教学班后 一定要建立本地的 教学班别记录 防止后续数据重复
                    commonClassSet.add(newTeachingClass);
                    finalNewTeachingClass = newTeachingClass;
                }
            }else{
                // 读取第一条以后的排课记录
                String adminClass = data.getAdminClass();
                String teachingClass = data.getTeachingClass();
                if(teachingClass == null || teachingClass.trim().length() == 0){
                    // 说明没合班 而且单独一个班作为教学班
                    String newTeachingClass = adminClass;
                    int count = 1;
                    List<String> allDistinctTeachingClasses = courseScheduleMapper.getAllDistinctTeachingClasses();
                    while (allDistinctTeachingClasses.contains(newTeachingClass) || commonClassSet.contains(newTeachingClass)){
                        newTeachingClass = adminClass + count;
                        count += 1;
                    }
                    data.setTeachingClass(newTeachingClass);
                    // 设好教学班后 一定要建立本地的 教学班别记录 防止后续数据重复
                    commonClassSet.add(newTeachingClass);
                    finalNewTeachingClass = newTeachingClass;
                }else{
                    // 可能要合班，人工设置了教学班，字符串长度不为 0
                    if(teachingClass.trim().equals(this.commonClassB.trim())){
                        // 与上一行的教学班别相等
                        data.setTeachingClass(this.finalNewTeachingClass);
                    }else{
                        // 不相等 与上一行的教学班别不同，那就直接先成立一个教学班 后面是否合班再看
                        String newTeachingClass = data.getTeachingClass();
                        int count = 1;
                        List<String> allDistinctTeachingClasses = courseScheduleMapper.getAllDistinctTeachingClasses();
                        while (allDistinctTeachingClasses.contains(newTeachingClass) || commonClassSet.contains(newTeachingClass)){
                            newTeachingClass = adminClass + count;
                            count += 1;
                        }
                        data.setTeachingClass(newTeachingClass);
                        // 设好教学班后 一定要建立本地的 教学班别记录 防止后续数据重复
                        commonClassSet.add(newTeachingClass);
                        finalNewTeachingClass = newTeachingClass;
                    }
                }
            }

            // 进行主讲教师判断
            String mainTeacherName = data.getMainTeacherName();
            List<TeacherInformationPO> teacherInformationPOS = teacherInformationMapper.selectByName(mainTeacherName);
            if(teacherInformationPOS.size() == 0){
                throw new RuntimeException("系统师资库中没有该主讲老师信息，请先提供该教师信息");
            }else if(teacherInformationPOS.size() == 1){
                // 年级、层次、学习形式、专业名称、行政班别、教学班别都没问题了 主讲老师也能找到唯一一个 接下来就可以将此排课记录写入数据库
                int count = courseScheduleMapper.checkDuplicate(convertVOtoPO(data));
                if (count > 0) {
                    if(count > 1){
                        throw new RuntimeException("已存在完全匹配的数据并且大于 2条! " + courseScheduleMapper.findDuplicateRecords(convertVOtoPO(data)));
                    }else{
                        if(update){
                            // 允许覆盖
                            insertCourseScheduleData(data, outputData, 0);
                        }else{
                            throw new RuntimeException("已存在完全匹配的数据 1 条! " + courseScheduleMapper.findDuplicateRecords(convertVOtoPO(data)));
                        }
                    }
                } else {
                    insertCourseScheduleData(data, outputData, 1);
//                    courseScheduleMapper.insert(convertVOtoPO(data));
                }
            }else{
                // 存在同名同性老师
                String workNumber = data.getMainTeacherId();    // 获取 Excel 中的主讲教师工号/学号
                String idNumber = data.getMainTeacherIdentity();    // 获取 Excel 中的主讲教师的身份证号码
                if(workNumber == null){
                    throw new RuntimeException("系统师资库中存在多名同名同性的教师，请提供工号或者身份证号码");
                }
                if(teacherInformationMapper.selectByWorkNumber(workNumber.trim()).size() == 1){
                    int count = courseScheduleMapper.checkDuplicate(convertVOtoPO(data));
                    if (count > 0) {
                        if(count > 1){
                            throw new RuntimeException("已存在完全匹配的数据并且大于 2条! " + courseScheduleMapper.findDuplicateRecords(convertVOtoPO(data)));
                        }else{
                            if(update){
                                // 允许覆盖
                                insertCourseScheduleData(data, outputData, 0);
                            }else{
                                throw new RuntimeException("已存在完全匹配的数据 1 条! " + courseScheduleMapper.findDuplicateRecords(convertVOtoPO(data)));
                            }
                        }

                    } else {
                        insertCourseScheduleData(data, outputData, 1);
                    }
                }else{
                    if(idNumber == null){
                        throw new RuntimeException("系统师资库中存在多名同名同性的教师，请提供工号或者身份证号码");
                    }
                    if(teacherInformationMapper.selectByIdCardNumber(idNumber.trim()).size() == 1){
                        int count = courseScheduleMapper.checkDuplicate(convertVOtoPO(data));
                        if (count > 0) {
                            if(count > 1){
                                throw new RuntimeException("已存在完全匹配的数据并且大于 2条! " + courseScheduleMapper.findDuplicateRecords(convertVOtoPO(data)));
                            }else{
                                if(update){
                                    // 允许覆盖
                                    insertCourseScheduleData(data, outputData, 0);
                                }else{
                                    throw new RuntimeException("已存在完全匹配的数据 1 条! " + courseScheduleMapper.findDuplicateRecords(convertVOtoPO(data)));
                                }
                            }
                        } else {
                            insertCourseScheduleData(data, outputData, 1);
                        }
                    }else{
                        throw new RuntimeException("系统师资库中存在多名同名同性的教师，请提供工号或者身份证号码");
                    }
                }
            }

        }catch (Exception e){
            log.error("插入数据失败 " + data.toString() + "\n" + e.toString());
            outputData.setErrorMessage(e.getMessage()); // 设置错误信息
        }
        outputDataList.add(outputData); // 将输出数据添加到列表中
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 可进行一些后置处理
        // 使用EasyExcel写入数据到新的Excel文件中

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/courseSchedule";
        String errorFileName = currentDateTime + "_errorImportCourseSchedule.xlsx";
        EasyExcel.write(relativePath + "/" + errorFileName,
                CourseScheduleExcelOutputVO.class).sheet("Sheet1").doWrite(outputDataList);
    }

}
