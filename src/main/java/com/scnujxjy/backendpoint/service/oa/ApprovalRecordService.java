package com.scnujxjy.backendpoint.service.oa;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.oa.ApprovalRecordPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.oa.DropoutRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.oa.ApprovalRecordMapper;
import com.scnujxjy.backendpoint.dao.mapper.oa.DropoutRecordMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 事务申请记录表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-14
 */
@Service
public class ApprovalRecordService extends ServiceImpl<ApprovalRecordMapper, ApprovalRecordPO> implements IService<ApprovalRecordPO> {

}
