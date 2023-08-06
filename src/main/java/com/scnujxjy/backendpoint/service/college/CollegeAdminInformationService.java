package com.scnujxjy.backendpoint.service.college;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformation;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeAdminInformationMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 教务员信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
public class CollegeAdminInformationService extends ServiceImpl<CollegeAdminInformationMapper, CollegeAdminInformation> implements IService<CollegeAdminInformation> {

}
