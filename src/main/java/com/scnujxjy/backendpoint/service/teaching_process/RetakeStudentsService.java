package com.scnujxjy.backendpoint.service.teaching_process;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.RetakeStudentsPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.RetakeStudentsMapper;

/**
 * <p>
 * 存储学生学号和批次ID的表，批次ID代表教师、课程以及合班的班级 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-10
 */
public class RetakeStudentsService extends ServiceImpl<RetakeStudentsMapper, RetakeStudentsPO> implements IService<RetakeStudentsPO>  {

}
