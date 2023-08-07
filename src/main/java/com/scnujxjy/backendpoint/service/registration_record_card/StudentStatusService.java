package com.scnujxjy.backendpoint.service.registration_record_card;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学籍信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
@Service
public class StudentStatusService extends ServiceImpl<StudentStatusMapper, StudentStatusPO> implements IService<StudentStatusPO> {

}
