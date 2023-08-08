package com.scnujxjy.backendpoint.inverter.basic;

import com.scnujxjy.backendpoint.dao.entity.basic.PlatformRolePO;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformRoleRO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformRoleVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PlatformRoleInverter {

    @Mappings({})
    public abstract PlatformRoleVO po2VO(PlatformRolePO platformRolePO);

    @Mappings({})
    public abstract List<PlatformRoleVO> po2VO(List<PlatformRolePO> platformRolePOS);

    @Mappings({})
    public abstract PlatformRoleRO ro2PO(PlatformRolePO platformRolePO);

}
