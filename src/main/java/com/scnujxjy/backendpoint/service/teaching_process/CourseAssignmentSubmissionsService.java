package com.scnujxjy.backendpoint.service.teaching_process;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseAssignmentSubmissionsPO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseAssignmentSubmissionsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程作业提交表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-12-07
 */
@Service
@Slf4j
public class CourseAssignmentSubmissionsService extends ServiceImpl<CourseAssignmentSubmissionsMapper, CourseAssignmentSubmissionsPO> implements IService<CourseAssignmentSubmissionsPO> {

}
