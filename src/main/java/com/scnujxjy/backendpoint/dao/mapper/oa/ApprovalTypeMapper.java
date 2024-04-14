package com.scnujxjy.backendpoint.dao.mapper.oa;

import com.scnujxjy.backendpoint.dao.entity.oa.ApprovalTypePO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 事务申请类型表 Mapper 接口
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-14
 */
public interface ApprovalTypeMapper extends BaseMapper<ApprovalTypePO> {

    Integer selectMaxId();
}
