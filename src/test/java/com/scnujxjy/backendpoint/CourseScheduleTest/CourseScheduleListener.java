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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class CourseScheduleListener extends AnalysisEventListener<CourseScheduleExcelImportVO> {
    private CourseScheduleMapper courseScheduleMapper;
    private ClassInformationMapper classInformationMapper;

    private TeacherInformationMapper teacherInformationMapper;

    private int dataCount = 0; // 添加一个计数变量

    private String commonClassB = null;    // 用于记录合班的教学班别名称第一个
    private HashMap<String, HashSet<String>> commonClassMap = new HashMap<>();    // 用于记录合班的教学班别所对应的每一个行政班

    private List<CourseScheduleExcelOutputVO> outputDataList = new ArrayList<>();


    public CourseScheduleListener(CourseScheduleMapper courseScheduleMapper, ClassInformationMapper classInformationMapper,
                                  TeacherInformationMapper teacherInformationMapper) {
        this.courseScheduleMapper = courseScheduleMapper;
        this.classInformationMapper = classInformationMapper;
        this.teacherInformationMapper = teacherInformationMapper;
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

    private int insertCourseScheduleData(CourseScheduleExcelImportVO data, CourseScheduleExcelOutputVO outputData){
//        int insert = courseScheduleMapper.insert(convertVOtoPO(data));
//                log.info("读入一行数据 " + data.toString() + "\n 上课时间为 " + startDateTime + " 下课时间为 " + endDateTime);
        dataCount++;
        outputData.setErrorMessage("导入成功");
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
                    String commonClassA = data.getTeachingClass().trim();
                    if(commonClassA.length() == 0){
                        insertCourseScheduleData(data, outputData);
                    }else{

                        //合班的教学班名检测先不考虑了

                        // 考虑老师是否存在师资库中，以及老师姓名重复的问题
                        List<TeacherInformationPO> teacherInformationPOS = teacherInformationMapper.selectByName(data.getMainTeacherName());
                        if(teacherInformationPOS.size() > 1){
                            // 存在同名老师，需要匹配其工号、身份证号码或者手机号码，如果这三者不能唯一锁定才报错
                            String mainTeacherId = data.getMainTeacherId();
                            String mainTeacherIdentity = data.getMainTeacherIdentity();
                            List<TeacherInformationPO> teacherInformationPOS1 = teacherInformationMapper.selectByWorkNumber(mainTeacherId);
                            if(teacherInformationPOS1.size() == 1){
                                insertCourseScheduleData(data, outputData);
                            }else{
                                List<TeacherInformationPO> teacherInformationPOS2 = teacherInformationMapper.selectByIdCardNumber(mainTeacherIdentity);
                                if(teacherInformationPOS2.size() == 1){
                                    insertCourseScheduleData(data, outputData);
                                }else{
                                    outputData.setErrorMessage("主讲教师出现重复没找到唯一 " + data.getMainTeacherName());
                                }
                            }

                        }else if(teacherInformationPOS.size() == 1){
                            if(classList.contains(data.getAdminClass())){
                                insertCourseScheduleData(data, outputData);
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
                    insertCourseScheduleData(data, outputData);
                }else {
                    outputData.setErrorMessage("通过 年级、专业名称、层次、学习形式匹配数据库时出现了多个班级,但没有找到对应表格中的班级 根据表格中提供的年级、层次、专业、学习形式在系统中查到的班级如下\n" +
                            classList.toString());
                }
            }else{
                outputData.setErrorMessage("没有找到该 年级、专业名称、层次、学习形式所匹配的 班级 根据表格中提供的年级、层次、专业、学习形式在系统中查到的班级如下\n" +
                        classList.toString());
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
