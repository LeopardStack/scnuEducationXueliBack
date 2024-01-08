package com.scnujxjy.backendpoint.oldSysDataExport;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.*;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.GraduationInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.OriginalEducationInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.SCNUXLJYDatabase;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.*;

@Data
class ErrorStudentStatusExportExcel extends StudentStatusCommonPO {
    /**
     * 插入失败原因
     */
    @ExcelProperty(value = "插入失败原因", index = 26)
    private String errorMsg;
}

@SpringBootTest
@Slf4j
public class TestGetAllStudentStatusDataBetter {
    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private PersonalInfoMapper personalInfoMapper;

    @Resource
    private AdmissionInformationMapper admissionInformationMapper;

    @Resource
    private OriginalEducationInfoMapper originalEducationInfoMapper;

    @Resource
    private GraduationInfoMapper graduationInfoMapper;

    @Resource
    private MinioService minioService;

    // 记录成功插入了多少条记录
    private int success_insert = 0;
    // 记录失败插入了多少条记录
    private int failed_insert = 0;

    public void exportErrorListToExcelAndUploadToMinio(List<ErrorStudentStatusExportExcel> errorList, String fileName, String diyBucketName) {
        // Step 1: Write data to ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, ErrorStudentStatusExportExcel.class).sheet("Sheet1").doWrite(errorList);

        // Step 2: Convert ByteArrayOutputStream to ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Step 3: Upload to Minio
        boolean success = minioService.uploadStreamToMinio(inputStream, fileName, diyBucketName);

        if (success) {
            log.info("Successfully uploaded to Minio.");
        } else {
            log.error("Failed to upload to Minio.");
        }

        // Close the streams
        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            log.error("Error closing streams: " + e.getMessage());
        }
    }


    public void exportErrorListToExcel(List<ErrorStudentStatusExportExcel> errorList, String outputPath) {
        log.info(updateCountMap.toString());
        EasyExcel.write(outputPath, ErrorStudentStatusExportExcel.class).sheet("Sheet1").doWrite(errorList);
    }

    /**
     * 根据身份证号码判别港澳台身份证
     * @param id
     * @return
     */
    public String identifyID(String id) {
        if (id == null) {
            return null;
        }

        if (id.length() == 18 || id.length() == 15) {
            // Mainland ID
            return "中华人民共和国居民身份证";
        } else if (id.length() == 8 || id.length() == 10) {
            // Hong Kong ID
            return "港澳台证件";
        } else if (id.matches("^[A-Z][0-9]{9}$")) {
            // Taiwan ID
            return "港澳台证件";
        } else if (id.matches("^[157][0-9]{6}\\([0-9Aa]\\)$")) {
            // Macau ID
            return "港澳台证件";
        } else {
            return "非法证件";
        }
    }

    public Date getBirthday(String birthDateString, SimpleDateFormat dateFormat1, SimpleDateFormat dateFormat2,
                            SimpleDateFormat dateFormat3, SimpleDateFormat dateFormat6){
        Date birthDate = null;
        try {
            birthDate = dateFormat1.parse(birthDateString);
        } catch (ParseException e) {
            try {
                birthDate = dateFormat2.parse(birthDateString);
            } catch (ParseException e2) {
                try {
                    birthDate = dateFormat3.parse(birthDateString);
                } catch (ParseException e3) {
                    try{
                        birthDate = dateFormat6.parse(birthDateString);
                    }catch (ParseException e4){
                        throw new RuntimeException("出生日期解析失败");
                    }
                }
            }
        }
        return birthDate;
    }

    /**
     * 根据准考证号码来判别年份
     * @param code
     * @return
     */
    public static int getYearFromCode(String code) {
        if (code == null || code.length() < 2) {
            throw new IllegalArgumentException("Code must " +
                    "have at least 2 characters.");
        }

        int threshold = 50;  // 假设从 50 开始是 21 世纪
        int prefix = Integer.parseInt(code.substring(0, 2));

        if (prefix < threshold) {
            return 2000 + prefix;
        } else {
            return 1900 + prefix;
        }
    }

    private static final int CONSUMER_COUNT = 200;
    private ExecutorService executorService;

    private BlockingQueue<HashMap<String, String>> queue = new LinkedBlockingQueue<>();  // Unbounded queue

    private CountDownLatch latch;

    List<ErrorStudentStatusExportExcel> errorList = new ArrayList<>();


    // 教学点简称记录
    Map<String, String> jxd_jc = new HashMap<>();

    // 记录未定义的教学点
    Set<String> undefinedJxd = Collections.synchronizedSet(new HashSet<>());


    // 记录每年的更新记录数
    Map<String, Long> updateCountMap = new ConcurrentHashMap<>();

    // 记录额外的插入日志
    List<String> insertLogs = Collections.synchronizedList(new ArrayList<>());



    {
        jxd_jc.put("深圳中鹏", "深圳中鹏教学点");
        jxd_jc.put("深圳华智", "深圳华智教学点");
        jxd_jc.put("东莞师华", "东莞师华教学点");
        jxd_jc.put("深圳爱华", "深圳爱华教学点");
        jxd_jc.put("佛山华泰", "佛山华泰教学点");
        jxd_jc.put("深圳燕荣", "深圳燕荣教学点");
        jxd_jc.put("惠州岭南", "惠州岭南教学点");
        jxd_jc.put("中山火炬", "中山火炬职院教学点");
        jxd_jc.put("广州海珠蓝星", "广州海珠蓝星教学点");
        jxd_jc.put("梅州启航", "梅州启航教学点");
        jxd_jc.put("广州达德", "广州达德教学点");
        jxd_jc.put("深圳伴我学", "深圳伴我学教学点");
        jxd_jc.put("华成理工", "广州华成理工教学点");
        jxd_jc.put("增城职大", "广州增城职大教学点");
        jxd_jc.put("佛山七天", "佛山七天教学点");
        jxd_jc.put("英富教育", "江门英富教学点");
        jxd_jc.put("南方人才", "广州南方人才教学点");
        jxd_jc.put("汕头龙湖", "汕头龙湖教学点");
        jxd_jc.put("清远敦敏", "清远敦敏教学点");
        jxd_jc.put("深圳华信", "深圳华信教学点");
        jxd_jc.put("深圳宝安职训", "深圳宝安教学点");
        jxd_jc.put("佛山天天", "佛山天天教学点");
    }

    @Transactional
    int insertData(HashMap<String, String> studentData){
        StudentStatusPO studentStatusPO = new StudentStatusPO();
        PersonalInfoPO personalInfoPO = new PersonalInfoPO();
        OriginalEducationInfoPO originalEducationInfoPO = new OriginalEducationInfoPO();
        GraduationInfoPO graduationInfoPO = new GraduationInfoPO();

        // 请根据实际的字段名和数据类型调整以下代码
        studentStatusPO.setStudentNumber(studentData.get("XH"));
        studentStatusPO.setGrade(studentData.get("NJ"));
        studentStatusPO.setCollege(studentData.get("XSH"));

        ErrorStudentStatusExportExcel errorData = new ErrorStudentStatusExportExcel();

        // 处理各种日期格式转换
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat dateFormat4 = new SimpleDateFormat("yyyy/MM");
        SimpleDateFormat dateFormat5 = new SimpleDateFormat("yyyy.MM");
        SimpleDateFormat dateFormat6 = new SimpleDateFormat("yyyy.MM.dd");

//            TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai"); // 设置为北京时间
//            dateFormat1.setTimeZone(timeZone);
//            dateFormat2.setTimeZone(timeZone);
//            dateFormat3.setTimeZone(timeZone);
//            dateFormat4.setTimeZone(timeZone);
//            dateFormat5.setTimeZone(timeZone);
//            dateFormat6.setTimeZone(timeZone);
        try {
            if(studentData.get("XXXS").contains("文凭")){
                // 澳门培训的文凭班学生直接跳过
                throw new RuntimeException("澳门班学员");
            }

            // 教学点 BH，去掉末尾的数字
            String teachingPoint = studentData.get("BH");
            teachingPoint = teachingPoint.replaceAll("\\d+$", "");
            if (!jxd_jc.containsKey(teachingPoint)) {
                undefinedJxd.add(teachingPoint);
                if (teachingPoint.contains("校内")) {
                    teachingPoint = teachingPoint;
                } else {
                    teachingPoint = teachingPoint + "教学点";
                }

            } else {
                teachingPoint = jxd_jc.get(teachingPoint);
            }

            studentStatusPO.setTeachingPoint(teachingPoint);

            studentStatusPO.setMajorName(studentData.get("ZYMC"));
            studentStatusPO.setStudyForm(studentData.get("XXXS"));
            studentStatusPO.setLevel(studentData.get("CC"));
            studentStatusPO.setStudyDuration(studentData.get("XZ"));
            studentStatusPO.setAdmissionNumber(studentData.get("KSH"));
            studentStatusPO.setAcademicStatus(studentData.get("ZT"));

            String enrollDateString = studentData.get("RXRQ");
            Date enrollDate = null;
            enrollDate = dateFormat5.parse(enrollDateString);
            studentStatusPO.setEnrollmentDate(enrollDate);
            studentStatusPO.setIdNumber(studentData.get("SFZH"));
            studentStatusPO.setClassIdentifier(studentData.get("BSHI"));


            personalInfoPO.setGender(studentData.get("XB"));

            String birthDateString = studentData.get("CSRQ");
            Date birthDate = getBirthday(birthDateString, dateFormat1, dateFormat2, dateFormat3, dateFormat6);

            if (birthDate != null) {
                personalInfoPO.setBirthDate(birthDate);
            }

            personalInfoPO.setGender(studentData.get("XB"));

            // 根据考生号来获取新生数据中的个人信息
            String ksh = studentData.get("KSH");
            AdmissionInformationPO student = null;

            QueryWrapper<AdmissionInformationPO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("admission_number", ksh);
            List<AdmissionInformationPO> admissionInformationPOS = admissionInformationMapper.selectList(queryWrapper);
            if(admissionInformationPOS.size() == 1){
                student = admissionInformationMapper.selectOne(queryWrapper);
            }else if(admissionInformationPOS.size() > 1){
                for(int i = 0; i < admissionInformationPOS.size(); i++){
                    AdmissionInformationPO admissionInformationPO = admissionInformationPOS.get(i);
                    String grade1 = admissionInformationPO.getGrade();
                    if(grade1.equals(getYearFromCode(ksh)+"")){
                        student = admissionInformationPO;
                        break;
                    }
                }
            }
            if(student == null){
                throw new RuntimeException("录取表中获取不到该学生的原始个人信息，无法填充政治面貌等信息");
            }

            personalInfoPO.setPoliticalStatus(student.getPoliticalStatus());
            if (!studentData.get("MZ").equals(student.getEthnicity())) {
                String grade = studentStatusPO.getGrade();
                insertLogs.add(grade + "年 " + studentStatusPO.toString() + " 民族信息与新生信息不同 " + student.getEthnicity());
            }
            personalInfoPO.setEthnicity(studentData.get("MZ"));
            personalInfoPO.setIdType(identifyID(studentData.get("SFZH")));
            personalInfoPO.setIdNumber(studentData.get("SFZH"));
            personalInfoPO.setPostalCode(student.getPostalCode());
            personalInfoPO.setPhoneNumber(student.getPhoneNumber());
            personalInfoPO.setAddress(student.getAddress());
            personalInfoPO.setEntrancePhoto(studentData.get("RXPIC"));
            personalInfoPO.setGrade(studentData.get("NJ"));


            originalEducationInfoPO.setGrade(studentData.get("NJ"));
            originalEducationInfoPO.setIdNumber(studentData.get("SFZH"));
            originalEducationInfoPO.setGraduationSchool(student.getGraduationSchool());
            originalEducationInfoPO.setOriginalEducation(student.getOriginalEducation());
            originalEducationInfoPO.setGraduationDate(student.getGraduationDate());

            graduationInfoPO.setGrade(studentData.get("NJ"));
            graduationInfoPO.setIdNumber(studentData.get("SFZH"));
            graduationInfoPO.setStudentNumber(studentData.get("XH"));
            graduationInfoPO.setGraduationNumber(studentData.get("BYZH"));

            List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectList(new LambdaQueryWrapper<StudentStatusPO>().
                    eq(StudentStatusPO::getGrade, studentStatusPO.getGrade()).
                    eq(StudentStatusPO::getIdNumber, studentStatusPO.getIdNumber()));
            if(studentStatusPOS.size() > 1){
                throw new RuntimeException("同一个年级，同一个学生存在多条学籍记录");
            }else if(studentStatusPOS.size() == 1){
                if(updateAny){
                    // 条件更新
                    int update = studentStatusMapper.update(studentStatusPO, new LambdaQueryWrapper<StudentStatusPO>().
                            eq(StudentStatusPO::getGrade, studentStatusPO.getGrade()).
                            eq(StudentStatusPO::getIdNumber, studentStatusPO.getIdNumber()));
                    if(update > 0){
                        String key1 = studentStatusPO.getGrade() + " 更新学籍数据";
                        if(!updateCountMap.containsKey(key1)){
                            updateCountMap.put(key1, 0L);
                        }
                        updateCountMap.put(key1, updateCountMap.get(key1) + 1L);

                    }
                }

            }else{
                // 没有找到任何记录 直接插入
                int insert = studentStatusMapper.insert(studentStatusPO);
                if(insert > 0){
                    String key1 = studentStatusPO.getGrade() + " 新增学籍数据";
                    if(!updateCountMap.containsKey(key1)){
                        updateCountMap.put(key1, 0L);
                    }
                    updateCountMap.put(key1, updateCountMap.get(key1) + 1L);
                }
            }


            List<PersonalInfoPO> personalInfoPOS = personalInfoMapper.selectList(new LambdaQueryWrapper<PersonalInfoPO>().
                    eq(PersonalInfoPO::getGrade, personalInfoPO.getGrade()).
                    eq(PersonalInfoPO::getIdNumber, personalInfoPO.getIdNumber()));
            if(personalInfoPOS.size() > 1){
                throw new RuntimeException("同一个年级，同一个学生存在多条个人信息记录");
            }else if(personalInfoPOS.size() == 1){
                if(updateAny){
                    // 条件更新
                    int update = personalInfoMapper.update(personalInfoPO, new LambdaQueryWrapper<PersonalInfoPO>().
                            eq(PersonalInfoPO::getGrade, personalInfoPO.getGrade()).
                            eq(PersonalInfoPO::getIdNumber, personalInfoPO.getIdNumber()));
                    if(update > 0){
                        String key1 = personalInfoPO.getGrade() + " 更新学生个人信息数据";
                        if(!updateCountMap.containsKey(key1)){
                            updateCountMap.put(key1, 0L);
                        }
                        updateCountMap.put(key1, updateCountMap.get(key1) + 1L);

                    }
                }

            }else{
                // 没有找到任何记录 直接插入
                int insert = personalInfoMapper.insert(personalInfoPO);
                if(insert > 0){
                    String key1 = studentStatusPO.getGrade() + " 新增学生个人信息数据";
                    if(!updateCountMap.containsKey(key1)){
                        updateCountMap.put(key1, 0L);
                    }
                    updateCountMap.put(key1, updateCountMap.get(key1) + 1L);
                }
            }

            List<OriginalEducationInfoPO> originalEducationInfoPOS = originalEducationInfoMapper.selectList(new LambdaQueryWrapper<OriginalEducationInfoPO>().
                    eq(OriginalEducationInfoPO::getGrade, originalEducationInfoPO.getGrade()).
                    eq(OriginalEducationInfoPO::getIdNumber, originalEducationInfoPO.getIdNumber()));

            if(originalEducationInfoPOS.size() > 1){
                throw new RuntimeException("同一个年级，同一个学生存在多条原学历记录");
            }else if(originalEducationInfoPOS.size() == 1){
                if(updateAny){
                    // 条件更新
                    int update = originalEducationInfoMapper.update(originalEducationInfoPO, new LambdaQueryWrapper<OriginalEducationInfoPO>().
                            eq(OriginalEducationInfoPO::getGrade, originalEducationInfoPO.getGrade()).
                            eq(OriginalEducationInfoPO::getIdNumber, originalEducationInfoPO.getIdNumber()));
                    if(update > 0){
                        String key1 = originalEducationInfoPO.getGrade() + " 更新学生原学历数据";
                        if(!updateCountMap.containsKey(key1)){
                            updateCountMap.put(key1, 0L);
                        }
                        updateCountMap.put(key1, updateCountMap.get(key1) + 1L);

                    }
                }

            }else{
                // 没有找到任何记录 直接插入
                int insert = originalEducationInfoMapper.insert(originalEducationInfoPO);
                if(insert > 0){
                    String key1 = originalEducationInfoPO.getGrade() + " 新增学生原学历数据";
                    if(!updateCountMap.containsKey(key1)){
                        updateCountMap.put(key1, 0L);
                    }
                    updateCountMap.put(key1, updateCountMap.get(key1) + 1L);
                }
            }


            String graduateDateString = studentData.get("BYRQ");
            String bypic = studentData.get("BYPIC");
            if((graduateDateString != null && !graduateDateString.equals("NULL")) || bypic != null){
                Date graduateDate = null;
                if(graduateDateString != null && !graduateDateString.equals("NULL")
                        && !graduateDateString.isEmpty()){
                    graduateDate = dateFormat5.parse(graduateDateString);
                }
                graduationInfoPO.setGraduationDate(graduateDate);

                graduationInfoPO.setGraduationPhoto(studentData.get("BYPIC"));

                // 检查一下数据库中是否存在该毕业信息数据，如果存在根据是否覆盖标志 进行覆盖
                // 如果没有找到任何数据 直接插入，如果存在多条毕业数据 则报错
                List<GraduationInfoPO> graduationInfoPOS = graduationInfoMapper.selectList(new LambdaQueryWrapper<GraduationInfoPO>().
                        eq(GraduationInfoPO::getGrade, originalEducationInfoPO.getGrade()).
                        eq(GraduationInfoPO::getIdNumber, originalEducationInfoPO.getIdNumber()));
                if(graduationInfoPOS.size() > 1){
                    throw new RuntimeException("同一个年级，同一个学生存在多条毕业记录");
                }else if(graduationInfoPOS.size() == 1){
                    if(updateAny){
                        // 条件更新
                        int update = graduationInfoMapper.update(graduationInfoPO, new LambdaQueryWrapper<GraduationInfoPO>().
                                eq(GraduationInfoPO::getGrade, graduationInfoPO.getGrade()).
                                eq(GraduationInfoPO::getIdNumber, graduationInfoPO.getIdNumber()));
                        if(update > 0){
                            String key1 = graduationInfoPO.getGrade() + " 更新学生毕业数据";
                            if(!updateCountMap.containsKey(key1)){
                                updateCountMap.put(key1, 0L);
                            }
                            updateCountMap.put(key1, updateCountMap.get(key1) + 1L);

                        }
                    }

                }else{
                    // 没有找到任何记录 直接插入
                    int insert = graduationInfoMapper.insert(graduationInfoPO);
                    if(insert > 0){
                        String key1 = graduationInfoPO.getGrade() + " 新增学生毕业数据";
                        if(!updateCountMap.containsKey(key1)){
                            updateCountMap.put(key1, 0L);
                        }
                        updateCountMap.put(key1, updateCountMap.get(key1) + 1L);
                    }
                }

            }

        } catch (Exception e) {
            errorData.setId((long) failed_insert);
            errorData.setStudentNumber(studentData.get("XHAO"));
            errorData.setGrade(studentData.get("NJ"));
            errorData.setCollege(studentData.get("XSH"));

            String teachingPoint = studentData.get("BH");
            if(teachingPoint != null || teachingPoint.length() == 0 &&
                    teachingPoint.trim().equals("NULL")){
                teachingPoint = teachingPoint.replaceAll("\\d+$", "");
                errorData.setTeachingPoint(teachingPoint);
            }

            errorData.setMajorName(studentData.get("ZYMC"));
            errorData.setStudyForm(studentData.get("XXXS"));
            errorData.setLevel(studentData.get("CC"));
            errorData.setStudyDuration(studentData.get("XZ"));
            errorData.setAdmissionNumber(studentData.get("KSH"));
            errorData.setAcademicStatus(studentData.get("ZT"));

            String enrollDateString = studentData.get("RXRQ");
            Date enrollDate = null;
            try {
                enrollDate = dateFormat5.parse(enrollDateString);
                errorData.setEnrollmentDate(enrollDate);
            }catch (Exception p){
                log.error("解析入学日期失败 " + studentData + "\n" + e.toString());
            }

            errorData.setIdNumber(studentData.get("SFZH"));
            errorData.setClassIdentifier(studentData.get("BSHI"));
            errorData.setGender(studentData.get("XB"));
            errorData.setBirthDate(getBirthday(studentData.get("CSRQ"), dateFormat1, dateFormat2, dateFormat3, dateFormat6));
            errorData.setEthnicity(studentData.get("MZ"));
            errorData.setName(studentData.get("XM"));

            errorData.setErrorMsg(e.toString());
            synchronized(this) {
                errorList.add(errorData);
                failed_insert += 1;
            }
        }
        return -1;
    }

    @PostConstruct
    public void init() {

        latch = new CountDownLatch(CONSUMER_COUNT);
        // 创建消费者线程
        executorService = Executors.newFixedThreadPool(CONSUMER_COUNT);

        for (int i = 0; i < CONSUMER_COUNT; i++) {
            executorService.execute(() -> {
                try {
                    while (true) {
                        HashMap<String, String> hashMap = queue.take();
                        if(hashMap.containsKey("END")){
                            break;
                        }
                        insertData(hashMap);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();  // decrement the count
                }
            });
        }
    }


    // 强行更新标志，即无论数据数量是否一致，覆盖库中的数据进行强制更新
    boolean updateAny = true;

    @Test
    public void test1() throws InterruptedException {
        /**
         * 同步指定年级的学籍数据，包含个人信息、学籍数据、毕业数据、学位数据、原学历数据
         * 2023 年的数据需要单独校验
         */
        int startYear = 2023;
        int endYear = 2023;


        StringBuilder allGrades = new StringBuilder();
        for (int i = startYear; i >= endYear; i--) {
            // 检测新旧系统的学生数目是否相同，相同则不需要更新
            Integer integer = studentStatusMapper.selectCount(new
                    LambdaQueryWrapper<StudentStatusPO>().eq(StudentStatusPO::getGrade,
                    i));
            log.info(i + "年 新系统中导入的成绩记录数 " + integer);

            SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
            Object value = scnuxljyDatabase.getValue("select count(*) from STUDENT_VIEW_WITHPIC where nj='" + i + "'");
            Object value_fwp = scnuxljyDatabase.getValue("select count(*) from STUDENT_VIEW_WITHPIC where nj='" + i +
                    "' and bshi LIKE 'WP%';");
            boolean updateInsert = true;
            log.info(i + "年 旧系统中的非学历在籍学生数量为 " + value_fwp + " 学历学生数量为 " + value);
            try{
                int oldAll = (int) value;
                int oldFwpAll = (int) value_fwp;
                if(oldFwpAll + oldAll == integer){
                    updateInsert = false;
                }

            }catch (Exception e){
                log.error("获取新旧系统成绩数据失败 " + i + "\n" + e.toString());
            }

            if(updateInsert){
                ArrayList<HashMap<String, String>> studentStatusData = getStudentInfos(String.valueOf(i));
                for (HashMap<String, String> hashMap : studentStatusData) {

                    queue.put(hashMap); // Put the object in the queue
                }
                allGrades.append(i).append("_");
            }
        }

        // 传递毒药对象
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("END", "TRUE");
            queue.put(hashMap);
        }

        latch.await();

        if (executorService != null) {
            executorService.shutdown();
        }

        // 调用新方法导出errorList
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/studentStatusData/";
        String errorFileName = relativePath + currentDateTime + "_" + allGrades + "导入学籍数据失败的部分数据.xlsx";
//        exportErrorListToExcel(errorList, errorFileName);
        exportErrorListToExcelAndUploadToMinio(errorList, errorFileName, "datasynchronize");

    }


    /**
     * 检测部分学生同步失败的原因
     * @throws InterruptedException
     */
    @Test
    public void test2() throws InterruptedException {
        /**
         * 同步指定年级的学籍数据，包含个人信息、学籍数据、毕业数据、学位数据、原学历数据
         * 2023 年的数据需要单独校验
         */
        String sql = "SELECT * FROM STUDENT_VIEW_WITHPIC WHERE nj='2021' and sfzh='441424198512261389'";
        ArrayList<HashMap<String, String>> studentStatusData = getCertainStudent(sql);
        for (HashMap<String, String> hashMap : studentStatusData) {

            queue.put(hashMap); // Put the object in the queue
        }

        // 传递毒药对象
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("END", "TRUE");
            queue.put(hashMap);
        }

        latch.await();

        if (executorService != null) {
            executorService.shutdown();
        }

        // 调用新方法导出errorList
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/studentStatusData/";
        String errorFileName = relativePath + currentDateTime + "_" + "部分错误学生" + "导入学籍数据失败的部分数据.xlsx";
//        exportErrorListToExcel(errorList, errorFileName);
        exportErrorListToExcelAndUploadToMinio(errorList, errorFileName, "datasynchronize");

    }

}
