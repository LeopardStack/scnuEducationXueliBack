package com.scnujxjy.backendpoint.service.teaching_process;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.RetakeStudentsPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.TeachingAssistantsCourseSchedulePO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.RetakeStudentsMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.TeachingAssistantsCourseScheduleMapper;

/**
 * <p>
 * 存储助教用户名和批次ID的表，批次ID代表教师、课程以及合班的班级 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-10
 */
public class TeachingAssistantsCourseScheduleService extends ServiceImpl<TeachingAssistantsCourseScheduleMapper, TeachingAssistantsCourseSchedulePO> implements IService<TeachingAssistantsCourseSchedulePO> {

}
