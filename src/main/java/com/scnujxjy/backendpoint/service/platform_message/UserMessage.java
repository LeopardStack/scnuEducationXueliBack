package com.scnujxjy.backendpoint.service.platform_message;

import com.scnujxjy.backendpoint.constant.enums.AnnounceType;
import com.scnujxjy.backendpoint.model.ro.platform_message.UserAnnouncementRo;
import org.aspectj.weaver.AnnotationOnTypeMunger;

import java.util.Objects;

public class UserMessage extends  PlatformMessageService{
//    @Override
//    public boolean InsterAnnouncementMessage(UserAnnouncementRo userAnnouncementRo) {
//        return super.InsterAnnouncementMessage(userAnnouncementRo);
//    }

    public  boolean addAnnouncementMessage(AnnounceType announceType, UserAnnouncementRo userAnnouncementRo){
        if (Objects.equals(announceType.getType(), "all")){

        }
        return false;
    }
}
