package com.scnujxjy.backendpoint.service.courses_learning;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CourseAssignmentsPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CourseAssignmentsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程作业表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-15
 */
@Service
@Slf4j
public class CourseAssignmentsService extends ServiceImpl<CourseAssignmentsMapper, CourseAssignmentsPO> implements IService<CourseAssignmentsPO> {

}
