package com.scnujxjy.backendpoint.service.oa;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.oa.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.oa.ApprovalTypePO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.oa.ApprovalStepMapper;
import com.scnujxjy.backendpoint.dao.mapper.oa.ApprovalTypeMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 事务申请类型表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-14
 */
@Service
public class ApprovalTypeService extends ServiceImpl<ApprovalTypeMapper, ApprovalTypePO> implements IService<ApprovalTypePO> {

}
