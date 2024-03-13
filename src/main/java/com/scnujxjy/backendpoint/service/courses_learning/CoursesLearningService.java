package com.scnujxjy.backendpoint.service.courses_learning;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesClassMappingPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesLearningPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CoursesClassMappingMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CoursesLearningMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CoursesLearningRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author 谢辉龙
 * @since 2024-03-05
 */
@Service
@Slf4j
public class CoursesLearningService extends ServiceImpl<CoursesLearningMapper, CoursesLearningPO>
        implements IService<CoursesLearningPO> {

    public PageVO<CourseScheduleVO> pageQueryCoursesInfo(PageRO<CoursesLearningRO> courseScheduleROPageRO) {
        return null;
    }
}
