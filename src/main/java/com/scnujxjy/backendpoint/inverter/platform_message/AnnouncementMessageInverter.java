package com.scnujxjy.backendpoint.inverter.platform_message;

import com.scnujxjy.backendpoint.dao.entity.platform_message.AnnouncementMessagePO;
import com.scnujxjy.backendpoint.model.ro.platform_message.AnnouncementMessageRO;
import com.scnujxjy.backendpoint.model.vo.platform_message.AnnouncementMessageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class AnnouncementMessageInverter {
    @Mappings({})
    public abstract AnnouncementMessagePO ro2PO(AnnouncementMessageRO announcementMessageRO);

    @Mappings({})
    public abstract AnnouncementMessageVO po2VO(AnnouncementMessagePO announcementMessagePO);

    @Mappings({})
    public abstract List<AnnouncementMessageVO> po2VO(List<AnnouncementMessagePO> announcementMessagePOS);
}
