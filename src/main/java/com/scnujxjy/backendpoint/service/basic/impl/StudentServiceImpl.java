package com.scnujxjy.backendpoint.service.basic.impl;

import com.scnujxjy.backendpoint.entity.basic.Student;
import com.scnujxjy.backendpoint.mapper.basic.StudentMapper;
import com.scnujxjy.backendpoint.service.basic.StudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-07-02
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

}
