package com.scnujxjy.backendpoint.service.oa;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.oa.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.oa.ApprovalStepRecordMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 事务审批步骤记录表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-14
 */
@Service
public class ApprovalStepRecordService extends ServiceImpl<ApprovalStepRecordMapper, ApprovalStepRecordPO> implements IService<ApprovalStepRecordPO> {

}
