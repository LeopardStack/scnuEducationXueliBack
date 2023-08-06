package com.scnujxjy.backendpoint.service.admission_information;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformation;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 录取学生信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
public class AdmissionInformationService extends ServiceImpl<AdmissionInformationMapper, AdmissionInformation> implements IService<AdmissionInformation> {

}