package com.scnujxjy.backendpoint.service.oa;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.oa.ApprovalStepPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.oa.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.oa.ApprovalStepMapper;
import com.scnujxjy.backendpoint.dao.mapper.oa.ApprovalStepRecordMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 事务申请步骤表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-14
 */
@Service
public class ApprovalStepService extends ServiceImpl<ApprovalStepMapper, ApprovalStepPO> implements IService<ApprovalStepPO> {

}
