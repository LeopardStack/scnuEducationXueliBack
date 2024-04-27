package com.scnujxjy.backendpoint.service.office_automation.approval;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalStepMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ApprovalStepService extends ServiceImpl<ApprovalStepMapper, ApprovalStepPO> implements IService<ApprovalStepPO> {

    /**
     * 根据类型查询审批流程
     * <p>根据步骤先后排序</p>
     *
     * @param typeId 类型 id
     * @return 所属类型的步骤列表
     */
    public List<ApprovalStepPO> selectByTypeId(Long typeId) {
        if (Objects.isNull(typeId)) {
            return Lists.newArrayList();
        }
        List<ApprovalStepPO> approvalRecordPOS = baseMapper.selectList(Wrappers.<ApprovalStepPO>lambdaQuery()
                .eq(ApprovalStepPO::getApprovalTypeId, typeId)
                .orderBy(true, true, ApprovalStepPO::getStepOrder));
        if (CollUtil.isEmpty(approvalRecordPOS)) {
            return Lists.newArrayList();
        }
        return approvalRecordPOS;
    }

    /**
     * 根据步骤id查询步骤详细信息
     *
     * @param id 步骤id
     * @return 步骤信息
     */
    public ApprovalStepPO selectById(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return baseMapper.selectById(id);
    }
}
