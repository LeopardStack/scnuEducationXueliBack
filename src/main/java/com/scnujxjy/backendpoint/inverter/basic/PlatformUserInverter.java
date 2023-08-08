package com.scnujxjy.backendpoint.inverter.basic;

import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PlatformUserInverter {

    @Mappings({})
    public abstract PlatformUserVO po2VO(PlatformUserPO platformUserPO);

    @Mappings({})
    public abstract List<PlatformUserVO> po2VO(List<PlatformUserPO> platformUserPOS);

    @Mappings({})
    public abstract PlatformUserPO ro2PO(PlatformUserRO platformUserRO);
}
