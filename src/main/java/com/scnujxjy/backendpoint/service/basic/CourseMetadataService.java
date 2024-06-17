package com.scnujxjy.backendpoint.service.basic;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.basic.CourseMetadataPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamAssistantsPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.CourseMetadataMapper;
import com.scnujxjy.backendpoint.dao.mapper.exam.CourseExamAssistantsMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程元信息表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-06-07
 */
@Service
public class CourseMetadataService extends ServiceImpl<CourseMetadataMapper, CourseMetadataPO> implements IService<CourseMetadataPO> {

}
