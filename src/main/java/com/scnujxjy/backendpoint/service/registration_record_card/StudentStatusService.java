package com.scnujxjy.backendpoint.service.registration_record_card;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatus;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class StudentStatusService extends ServiceImpl<StudentStatusMapper, StudentStatus> implements IService<StudentStatus> {

}
