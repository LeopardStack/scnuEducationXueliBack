package com.scnujxjy.backendpoint.service.exam;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamAssistantsPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.exam.CourseExamAssistantsMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储阅卷助教 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-15
 */
@Service
public class CourseExamAssistantsService extends ServiceImpl<CourseExamAssistantsMapper, CourseExamAssistantsPO> implements IService<CourseExamAssistantsPO> {

}
