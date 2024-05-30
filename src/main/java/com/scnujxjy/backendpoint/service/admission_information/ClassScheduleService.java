package com.scnujxjy.backendpoint.service.admission_information;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.admission_information.ClassSchedulePO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.ClassScheduleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 开班计划表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-05-29
 */
@Service
@Slf4j
public class ClassScheduleService extends ServiceImpl<ClassScheduleMapper, ClassSchedulePO> implements IService<ClassSchedulePO> {

}
