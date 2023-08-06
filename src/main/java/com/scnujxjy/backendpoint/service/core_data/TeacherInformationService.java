package com.scnujxjy.backendpoint.service.core_data;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformation;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 教师信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
public class TeacherInformationService extends ServiceImpl<TeacherInformationMapper, TeacherInformation> implements IService<TeacherInformation> {

}
