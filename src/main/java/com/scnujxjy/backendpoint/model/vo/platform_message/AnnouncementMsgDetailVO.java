package com.scnujxjy.backendpoint.model.vo.platform_message;

import com.scnujxjy.backendpoint.model.vo.PageVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class AnnouncementMsgDetailVO<T> {
    PageVO<T> users;

    /**
     * 公告阅读总人数
     */
    long total;


    /**
     * 公告已读人数
     */
    long isRead;

    /**
     * 公告未读人数
     */
    long unRead;
}
