package com.scnujxjy.backendpoint.service.courses_learning;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CourseAssignmentSubmissionsPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CourseAssignmentSubmissionsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程作业提交表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-15
 */
@Service
@Slf4j
public class CourseAssignmentSubmissionsService extends ServiceImpl<CourseAssignmentSubmissionsMapper, CourseAssignmentSubmissionsPO> implements IService<CourseAssignmentSubmissionsPO> {

}
