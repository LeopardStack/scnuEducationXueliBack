package com.scnujxjy.backendpoint.TeachingPlansTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.service.registration_record_card.ClassInformationService;
import com.scnujxjy.backendpoint.service.teaching_point.TeachingPointInformationService;
import com.scnujxjy.backendpoint.service.teaching_process.CourseInformationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootTest
@Slf4j
public class Test2 {

    @Resource
    private ClassInformationService classInformationService;

    @Resource
    private CourseInformationService courseInformationService;

    @Resource
    private TeachingPointInformationService teachingPointInformationService;

    @Test
    public void test1(){
        // 先找到 指定年级和班名的班级信息
        ClassInformationPO example = classInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                .eq(ClassInformationPO::getGrade, "2024")
                .eq(ClassInformationPO::getCollege, "政治与公共管理学院")
                .eq(ClassInformationPO::getClassName, "东莞欧龙")
        );
        log.info("\n" + example.toString());
        CourseInformationPO courseInformationPO = courseInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<CourseInformationPO>()
                .eq(CourseInformationPO::getAdminClass, example.getClassIdentifier())
                .eq(CourseInformationPO::getCourseName, "政治学原理")
        );
        log.info("\n" + courseInformationPO.toString());

        ArrayList<String> list1 = new ArrayList<>(Arrays.asList("东莞师华",
                "番禺学程",
                "佛山七天",
                "佛山三水",
                "海珠蓝星",
                "河源职院",
                "怀集育才",
                "惠州孚澳",
                "茂名青年",
                "梅州启航",
                "梅州文峰",
                "深圳宝安",
                "深圳明卓",
                "深圳燕荣",
                "云浮云城",
                "增城职大",
                "湛江蓝海",
                "中山公众",
                "湛江蓝海",
                "中山火炬",
                "珠海博实",
                "珠海东剑"
        ));

        // 遍历 list1 中的每个元素
        for (String alias : list1) {
            // 查询数据库中别名等于当前元素的记录数
            int count = teachingPointInformationService.getBaseMapper().selectCount(
                    new LambdaQueryWrapper<TeachingPointInformationPO>().eq(TeachingPointInformationPO::getAlias, alias));

            if(courseInformationPO != null){
                // 照着这门课 写入对应的教学计划
                ClassInformationPO classInformationPO = classInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                        .eq(ClassInformationPO::getGrade, "2024")
                        .eq(ClassInformationPO::getCollege, "政治与公共管理学院")
                        .eq(ClassInformationPO::getClassName, alias)
                );
                CourseInformationPO courseInformationPO1 = courseInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<CourseInformationPO>()
                        .eq(CourseInformationPO::getAdminClass, classInformationPO.getClassIdentifier())
                        .eq(CourseInformationPO::getCourseName, "政治学原理")
                );
                if(courseInformationPO1 == null){
                    // 重复的就不需要写入了
                    CourseInformationPO courseInformationPO2 = new CourseInformationPO();
                    BeanUtils.copyProperties(courseInformationPO, courseInformationPO2);
                    courseInformationPO2.setId(null);
                    courseInformationPO2.setAdminClass(classInformationPO.getClassIdentifier());
                    int insert = courseInformationService.getBaseMapper().insert(courseInformationPO2);
                    if(insert <= 0){
                        log.error("插入失败 " + insert);
                    }
                }
            }

            // 如果计数为0，说明该别名不在数据库中
            if (count == 0) {
                log.info("\n" + alias + " 不在数据库中");
            }
        }

    }
}
