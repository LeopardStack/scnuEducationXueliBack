package com.scnujxjy.backendpoint.service.office_automation.approval;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalTypePO;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.approval.ApprovalTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApprovalTypeService extends ServiceImpl<ApprovalTypeMapper, ApprovalTypePO> implements IService<ApprovalTypePO> {
}
