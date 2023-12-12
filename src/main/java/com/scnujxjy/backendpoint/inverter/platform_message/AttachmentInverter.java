package com.scnujxjy.backendpoint.inverter.platform_message;

import com.scnujxjy.backendpoint.dao.entity.platform_message.AttachmentPO;
import com.scnujxjy.backendpoint.model.vo.platform_message.AttachmentVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import javax.annotation.Resource;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class AttachmentInverter {

    @Resource
    protected PlatformUserService platformUserService;

    @Mappings({
            @Mapping(target = "name", expression = "java(platformUserService.getNameByUserId(attachmentPO.getUserId()))")
    })
    public abstract AttachmentVO po2VO(AttachmentPO attachmentPO);

    @Mappings({})
    public abstract List<AttachmentVO> po2VO(List<AttachmentPO> attachmentPOS);

}
