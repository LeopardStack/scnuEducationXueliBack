package com.scnujxjy.backendpoint.service.teaching_process;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 排课表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-18
 */
@Service
public class CourseScheduleService extends ServiceImpl<CourseScheduleMapper, CourseSchedulePO> implements IService<CourseSchedulePO> {

}
