package com.scnujxjy.backendpoint.service.college;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeLeaderInformation;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeLeaderInformationMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 负责人信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
public class CollegeLeaderInformationService extends ServiceImpl<CollegeLeaderInformationMapper, CollegeLeaderInformation> implements IService<CollegeLeaderInformation> {

}
