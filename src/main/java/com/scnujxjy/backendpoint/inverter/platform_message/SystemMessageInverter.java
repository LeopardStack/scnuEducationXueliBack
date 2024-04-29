package com.scnujxjy.backendpoint.inverter.platform_message;

import com.scnujxjy.backendpoint.dao.entity.platform_message.SystemMessagePO;
import com.scnujxjy.backendpoint.model.ro.platform_message.SystemMessageRO;
import com.scnujxjy.backendpoint.model.vo.platform_message.SystemMessageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class SystemMessageInverter {

    @Mappings({})
    public abstract SystemMessagePO ro2PO(SystemMessageRO systemMessageRO);

    @Mappings({})
    public abstract SystemMessageVO po2VO(SystemMessagePO systemMessagePO);

    @Mappings({})
    public abstract List<SystemMessageVO> po2VO(List<SystemMessagePO> systemMessagePOS);
}
