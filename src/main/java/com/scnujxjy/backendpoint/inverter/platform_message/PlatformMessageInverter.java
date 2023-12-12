package com.scnujxjy.backendpoint.inverter.platform_message;


import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.model.ro.platform_message.UserAnnouncementRo;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public abstract class PlatformMessageInverter {
    @Mappings({})
    public abstract PlatformMessagePO ro2PO(UserAnnouncementRo userAnnouncementRo);

}
