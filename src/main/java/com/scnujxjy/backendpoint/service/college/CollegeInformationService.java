package com.scnujxjy.backendpoint.service.college;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformation;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学院基础信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
public class CollegeInformationService extends ServiceImpl<CollegeInformationMapper, CollegeInformation> implements IService<CollegeInformation> {

}
