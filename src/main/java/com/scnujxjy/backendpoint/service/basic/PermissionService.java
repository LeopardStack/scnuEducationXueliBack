package com.scnujxjy.backendpoint.service.basic;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.basic.PermissionPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PermissionMapper;
import com.scnujxjy.backendpoint.inverter.basic.PermissionInverter;
import com.scnujxjy.backendpoint.model.vo.basic.PermissionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class PermissionService extends ServiceImpl<PermissionMapper, PermissionPO> implements IService<PermissionPO> {

    @Resource
    private PermissionInverter permissionInverter;

    /**
     * 根据权限id列表批量查询权限信息
     *
     * @param permissionIds 权限id列表
     * @return 权限列表
     */
    public List<PermissionVO> detailById(List<Long> permissionIds) {
        // 参数校验
        if (CollUtil.isEmpty(permissionIds)) {
            log.error("参数缺失");
            return null;
        }
        // 查询
        List<PermissionPO> permissionPOS = baseMapper.selectBatchIds(permissionIds);
        // 查询结果校验
        if (CollUtil.isEmpty(permissionPOS)) {
            log.error("该权限id列表：{} 查询结果为空", permissionPOS);
            return null;
        }
        // 返回数据
        return permissionInverter.po2VO(permissionPOS);

    }

}
