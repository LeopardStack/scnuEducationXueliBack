package com.scnujxjy.backendpoint.oldSysDataExport;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.exam.CourseExamInfoMapper;
import com.scnujxjy.backendpoint.model.ro.exam.BatchSetTeachersInfoRO;
import com.scnujxjy.backendpoint.util.DataImportScnuOldSys;
import com.scnujxjy.backendpoint.util.MyThread;
import com.scnujxjy.backendpoint.util.SCNUXLJYDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;

@SpringBootTest
@Slf4j
public class GetGraduationStudentsPhotosTest {

    @Resource
    CourseExamInfoMapper courseExamInfoMapper;
    /**
     * 获取指定学生群体的照片信息
     */
    @Test
    public void test1(){
        DataImportScnuOldSys dataImportScnuOldSys = new DataImportScnuOldSys();
        String sql = "select * from STUDENT_VIEW_WITHPIC where byrq='2024.01'";
        String batch = "2024年1月份毕业生";
        ArrayList<HashMap<String, String>> studentGraduationPhotos = dataImportScnuOldSys.
                getStudentGraduationPhotos(sql, batch);
        log.info("总人数 " + String.valueOf(studentGraduationPhotos.size()));
    }

    /**
     * 获取指定机考批次的照片信息
     */
    @Test
    public void test2(){
        List<CourseExamInfoPO> courseExamInfoPOS = courseExamInfoMapper.
                batchSelectData(new BatchSetTeachersInfoRO().setGrade("2023").setExamMethod("机考"));
        log.info("一共有 " + courseExamInfoPOS.size() + " 条机考记录");
        int count = 0;
        Set<String> set1 = new HashSet<>();
        for(CourseExamInfoPO courseExamInfoPO: courseExamInfoPOS){
            if(set1.contains(courseExamInfoPO.getClassIdentifier())){
                // 已重复的班级 没必要再导入
                continue;
            }
            DataImportScnuOldSys dataImportScnuOldSys = new DataImportScnuOldSys();
            String sql = "select * from STUDENT_VIEW_WITHPIC where bshi='" +
                    courseExamInfoPO.getClassIdentifier() + "'";
            String batch = "2023年冬季机考学生照片";
            ArrayList<HashMap<String, String>> studentGraduationPhotos = dataImportScnuOldSys.
                    getStudentPhotosByDIYSql(sql, batch, "机考");
            count += studentGraduationPhotos.size();
            set1.add(courseExamInfoPO.getClassIdentifier());
        }
        log.info("总人数 " + count);

    }
}
