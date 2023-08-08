package com.scnujxjy.backendpoint.inverter.basic;

import com.scnujxjy.backendpoint.dao.entity.basic.PermissionPO;
import com.scnujxjy.backendpoint.model.ro.basic.PermissionRO;
import com.scnujxjy.backendpoint.model.vo.basic.PermissionVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper
public abstract class PermissionInverter {

    @Mappings({})
    public abstract PermissionVO po2VO(PermissionPO permissionPO);

    @Mappings({})
    public abstract List<PermissionVO> po2VO(List<PermissionPO> permissionPOS);

    @Mappings({})
    public abstract PermissionPO ro2PO(PermissionRO permissionRO);

}
