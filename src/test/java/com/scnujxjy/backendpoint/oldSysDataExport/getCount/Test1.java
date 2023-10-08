package com.scnujxjy.backendpoint.oldSysDataExport.getCount;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.GraduationInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.GraduationInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.util.SCNUXLJYDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private ScoreInformationMapper scoreInformationMapper;

    @Resource
    private ClassInformationMapper classInformationMapper;

    @Resource
    private CourseInformationMapper courseInformationMapper;

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private GraduationInfoMapper graduationInfoMapper;

    @Test
    public void test1(){
        int grade = 2022;
        Integer integer = scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().eq(ScoreInformationPO::getGrade,
                grade));
        log.info("新系统中导入的成绩记录数 " + integer);

        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
        Object value = scnuxljyDatabase.getValue("select count(*) from RESULT_VIEW_FULL where nj='" + grade + "'");
        Object value_fwp = scnuxljyDatabase.getValue("select count(*) from RESULT_VIEW_FULL where nj='" + grade +
                "' and bshi LIKE 'WP%';");
        log.info(grade + " 年的成绩总记录数是 " + value);
        log.info(grade + " 年的非文凭成绩总记录数是 " + value_fwp);
    }

    /**
     * 检查班级信息是否一致
     */
    @Test
    public void test2(){
        Integer integer = classInformationMapper.selectCount(null);
        log.info("新系统中导入的班级记录数 " + integer);

        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
        Object value = scnuxljyDatabase.getValue("SELECT count(*) FROM classdata");
        Object value_fwp = scnuxljyDatabase.getValue("SELECT count(*) FROM classdata where bshi like'WP%'");
        log.info(" 年的成绩总记录数是 " + value);
        log.info(" 年的非文凭成绩总记录数是 " + value_fwp);
    }

    /**
     * 检查教学计划是否一致
     */
    @Test
    public void test3(){
        Integer integer = courseInformationMapper.selectCount(null);
        log.info("新系统中导入的教学计划记录数 " + integer);

        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
        Object value = scnuxljyDatabase.getValue("SELECT count(*) FROM courseDATA");
        Object value_fwp = scnuxljyDatabase.getValue("SELECT count(*) FROM courseDATA where bshi like'WP%'");
        log.info("旧系统非文凭教学计划总记录数是 " + value);
        log.info("旧系统文凭教学计划总记录数是 " + value_fwp);
    }

    /**
     * 检查学籍数据和毕业数据
     */
    @Test
    public void test4(){
        int grade = 2018;
        Integer integer = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().eq(StudentStatusPO::getGrade,
                grade));
        log.info("新系统中导入的学生数 " + integer);

        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
        Object value = scnuxljyDatabase.getValue("select count(*) from STUDENT_VIEW_WITHPIC where nj='" + grade + "'");
        Object value_fwp = scnuxljyDatabase.getValue("select count(*) from STUDENT_VIEW_WITHPIC where nj='" + grade +
                "' and bshi LIKE 'WP%';");
        log.info(grade + " 年的学生数是 " + value);
        log.info(grade + " 年的非文凭学生数是 " + value_fwp);

        int graduation_value = (int) scnuxljyDatabase.getValue("select count(*) from STUDENT_VIEW_WITHPIC where nj='" + grade +
                "' and bshi not LIKE 'WP%' and byrq is not null;");
        Integer integer1 = graduationInfoMapper.selectCount(new LambdaQueryWrapper<GraduationInfoPO>().eq(GraduationInfoPO::getGrade,
                grade).isNotNull(GraduationInfoPO::getGraduationNumber));
        log.info(grade +  "年 旧系统毕业学生 " + graduation_value  + " 新系统毕业学生 " + integer1);
    }
}
