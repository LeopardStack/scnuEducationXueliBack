package com.scnujxjy.backendpoint.service.teaching_process;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-14
 */
@Service
public class CourseInformationService extends ServiceImpl<CourseInformationMapper, CourseInformationPO> implements IService<CourseInformationPO> {

}
