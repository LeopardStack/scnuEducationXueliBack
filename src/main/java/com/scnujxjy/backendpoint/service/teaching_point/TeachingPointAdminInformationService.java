package com.scnujxjy.backendpoint.service.teaching_point;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformation;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointAdminInformationMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 教学点教务员信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
public class TeachingPointAdminInformationService extends ServiceImpl<TeachingPointAdminInformationMapper, TeachingPointAdminInformation> implements IService<TeachingPointAdminInformation> {

}
