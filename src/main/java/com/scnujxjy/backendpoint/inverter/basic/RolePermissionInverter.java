package com.scnujxjy.backendpoint.inverter.basic;

import com.scnujxjy.backendpoint.dao.entity.basic.RolePermissionPO;
import com.scnujxjy.backendpoint.model.ro.basic.RolePermissionRO;
import com.scnujxjy.backendpoint.model.vo.basic.RolePermissionVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class RolePermissionInverter {

    @Mappings({})
    public abstract RolePermissionVO po2VO(RolePermissionPO rolePermissionPO);

    @Mappings({})
    public abstract List<RolePermissionVO> po2VO(List<RolePermissionPO> rolePermissionPOS);

    @Mappings({})
    public abstract RolePermissionPO ro2PO(RolePermissionRO rolePermissionRO);

}
