package com.scnujxjy.backendpoint.util.excelListener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationExcelOutputVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
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
import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseInformationListener extends AnalysisEventListener<CourseInformationVO> {

    private CourseInformationMapper courseInformationMapper;

    private ClassInformationMapper classInformationMapper;

    private String collegeName;

    private List<CourseInformationExcelOutputVO> outputDataList = new ArrayList<>();

    public CourseInformationListener(CourseInformationMapper courseInformationMapper,
                                     ClassInformationMapper classInformationMapper,
                                     String collegeName){
        this.courseInformationMapper = courseInformationMapper;
        this.classInformationMapper = classInformationMapper;
        this.collegeName = collegeName;
    }

    @Override
    public void invoke(CourseInformationVO courseInformationVO, AnalysisContext analysisContext) {

        CourseInformationExcelOutputVO outputData = new CourseInformationExcelOutputVO();
        BeanUtils.copyProperties(courseInformationVO, outputData);
//        log.info(courseInformationVO.toString());
        try {
            List<CourseInformationPO> courseInformationPOS1 = courseInformationMapper.selectByGradeAndStudyFormAndLevelAndClassAndMajorName(
                    courseInformationVO.getGrade(),
                    courseInformationVO.getStudyForm(),
                    courseInformationVO.getLevel(),
                    courseInformationVO.getAdminClass(),
                    courseInformationVO.getMajorName()
            );

            if (courseInformationPOS1.size() != 0) {
                List<CourseInformationPO> courseInformationPOS = courseInformationMapper.selectByGradeAndStudyFormAndLevelAndClassAndMajorNameAndCourseName(
                        courseInformationVO.getGrade(),
                        courseInformationVO.getStudyForm(),
                        courseInformationVO.getLevel(),
                        courseInformationVO.getAdminClass(),
                        courseInformationVO.getMajorName(),
                        courseInformationVO.getCourseName()
                );
                if (courseInformationPOS.size() == 0) {
//                    log.info("找不到该教学计划，并且班级确实存在 可以插入" + courseInformationVO);
                    if("直播".equals(courseInformationVO.getTeachingMethod()) || "点播".equals(courseInformationVO.getTeachingMethod())){
                        // 目前只允许导入直播课和点播课
                        CourseInformationPO courseInformationPO = new CourseInformationPO();
                        BeanUtils.copyProperties(courseInformationVO, courseInformationPO);
                        courseInformationMapper.insert(courseInformationPO);
                        outputData.setErrorMessage("导入成功"); // 设置错误信息
                        outputDataList.add(outputData); // 将输出数据添加到列表中
                    }else{

                    }

                } else if (courseInformationPOS.size() == 1) {
                    // 是否需要覆盖
//                    log.error("找到相同的教学计划 " + courseInformationPOS.get(0));
                    String remark = courseInformationVO.getRemark();
                    if(remark != null && remark.trim().length() != 0 && remark.equals("覆盖")){
                        CourseInformationPO courseInformationPO = new CourseInformationPO();
                        BeanUtils.copyProperties(courseInformationVO, courseInformationPO);
                        int update = courseInformationMapper.update(courseInformationPO, new LambdaQueryWrapper<CourseInformationPO>().
                                eq(CourseInformationPO::getGrade, courseInformationVO.getGrade()).
                                eq(CourseInformationPO::getGrade, courseInformationVO.getStudyForm()).
                                eq(CourseInformationPO::getGrade, courseInformationVO.getLevel()).
                                eq(CourseInformationPO::getGrade, courseInformationVO.getAdminClass()).
                                eq(CourseInformationPO::getGrade, courseInformationVO.getMajorName())
                        );
                        outputData.setErrorMessage("覆盖成功 " + update + " 条 " + courseInformationPOS); // 设置错误信息
                        outputDataList.add(outputData); // 将输出数据添加到列表中
                    }else{
                        throw new RuntimeException("找到相同的教学计划，默认不覆盖 " + courseInformationPOS);
                    }
                } else {
                    // 出现多门重复的教学计划
//                    log.error("找到大于一门的相同的教学计划 " + courseInformationPOS);
                    throw new RuntimeException("找到大于一门的相同的教学计划 " + courseInformationPOS);
                }
            } else {
                throw new RuntimeException("该班级信息不存在，即该年级、专业、学习形式、层次、专业名称不对 ");
//                log.error("该班级信息不存在，即该年级、专业、学习形式、层次、专业名称不对 ");
            }
        }catch (Exception e){
            outputData.setErrorMessage(e.getMessage()); // 设置错误信息
            outputDataList.add(outputData); // 将输出数据添加到列表中
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 使用EasyExcel写入数据到新的Excel文件中
        if(outputDataList.size() > 0) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("Asia/Shanghai"));

            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/courseInformationData";
            String errorFileName = collegeName + "_" + currentDateTime + "_教学计划导入失败数据.xlsx";

            // 检查目录是否存在，不存在则创建
            File directory = new File(relativePath);
            if (!directory.exists()) {
                directory.mkdirs(); // 这将创建所需的所有目录，即使中间目录不存在
            }

            EasyExcel.write(relativePath + "/" + errorFileName, CourseInformationExcelOutputVO.class).sheet("Sheet1").doWrite(outputDataList);
            log.info(collegeName + " 教学计划存在错误记录，已写入 " + errorFileName);
        }else{
            log.info(collegeName + " 教学计划没有任何错误");
        }
    }
}
