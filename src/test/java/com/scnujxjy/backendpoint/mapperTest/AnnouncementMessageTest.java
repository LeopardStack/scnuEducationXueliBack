package com.scnujxjy.backendpoint.mapperTest;

import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.model.ro.platform_message.UserAnnouncementRo;
import com.scnujxjy.backendpoint.service.platform_message.PlatformMessageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class AnnouncementMessageTest {
    @Autowired
    PlatformMessageService platformMessageService;

    @Test
    public void InsertAnnoucementessage(){
        UserAnnouncementRo userAnnouncementRo = new UserAnnouncementRo();
        userAnnouncementRo.setMessageType(MessageEnum.ANNOUNCEMENT_MSG.getMessage_name());
        userAnnouncementRo.setContent("測試公告");
        userAnnouncementRo.setIsRead(false);
        userAnnouncementRo.setAttachmentId(2);
        userAnnouncementRo.setUserId(121424312);
        userAnnouncementRo.setTitle("hello");
      boolean a=   platformMessageService.InsterAnnouncementMessage(userAnnouncementRo);
      log.info("打印[{}]",a);
    }
}
