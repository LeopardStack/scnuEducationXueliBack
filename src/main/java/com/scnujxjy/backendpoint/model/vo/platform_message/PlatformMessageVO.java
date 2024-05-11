package com.scnujxjy.backendpoint.model.vo.platform_message;

import com.scnujxjy.backendpoint.dao.entity.platform_message.UserUploadsPO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlatformMessageVO {

    /**
     * 下载消息列表
     */
    private List<DownloadMessageVO> downloadMessagePOList = new ArrayList<>();

    /**
     * 上传消息列表
     */
    private List<UserUploadsPO> userUploadsPOList = new ArrayList<>();

    /**
     * 公告消息列表
     */
    private List<AnnouncementMessageVO> announcementMessageVOList = new ArrayList<>();
}
