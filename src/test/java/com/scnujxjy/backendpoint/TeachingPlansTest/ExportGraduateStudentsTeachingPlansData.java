package com.scnujxjy.backendpoint.TeachingPlansTest;

import com.alibaba.excel.EasyExcel;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.StudentStatusAllVO;
import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@SpringBootTest
@Slf4j
public class ExportGraduateStudentsTeachingPlansData {

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private CourseInformationMapper courseInformationMapper;

    @Resource
    private ManagerFilter managerFilter;

    /**
     * 获取指定年月毕业的学生群体
     */
    @Test
    public void test1(){
        try{
            // 创建日期格式器
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

            // 解析字符串为日期
            Date graduationDate = dateFormat.parse("20240110");
            List<StudentStatusAllVO> studentStatusAllVOS = studentStatusMapper.downloadStudentStatusDataByManager0(new StudentStatusFilterRO()
                    .setGraduationDate(graduationDate)
                    .setLevel("本科")
            );
            log.info("\n总共 " + studentStatusAllVOS.size() + " 毕业");

            List<CourseInformationVO> allTeachingPlans = new ArrayList<>();
            HashSet<String> processedClasses = new HashSet<>();

            for (StudentStatusAllVO studentStatusAllVO : studentStatusAllVOS) {
                String classIdentifier = studentStatusAllVO.getClassIdentifier();
                // 检查这个班级序号是否已经处理过
                if (!processedClasses.contains(classIdentifier)) {
                    // 如果没有处理过，查询教学计划并添加到结果列表中
                    List<CourseInformationVO> courseInformationVOS = courseInformationMapper.selectByFilterAndPage(
                            new CourseInformationRO().setAdminClass(classIdentifier), null, null);
                    allTeachingPlans.addAll(courseInformationVOS);
                    // 标记这个班级序号为已处理
                    processedClasses.add(classIdentifier);
                }
            }
            // 使用 easyExcel 生成教学计划数据 excel
            // 定义文件输出位置
            String fileName = "./data_export/教学计划/" + "2024年 1 月成教毕业生教学计划.xlsx";

            // 使用easyExcel写数据到Excel文件
            EasyExcel.write(fileName, CourseInformationVO.class)
                    .sheet("Sheet1")
                    .doWrite(allTeachingPlans);

        }catch (Exception e) {
            log.error("\n解析日期时发生错误: " + e.getMessage());
        }

    }
}
