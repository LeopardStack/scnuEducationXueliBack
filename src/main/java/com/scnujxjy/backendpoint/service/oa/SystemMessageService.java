package com.scnujxjy.backendpoint.service.oa;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scnujxjy.backendpoint.dao.entity.oa.SystemMessagePO;
import com.scnujxjy.backendpoint.dao.entity.oa.SystemPopOutMessagePO;
import com.scnujxjy.backendpoint.model.ro.oa.SystemMessageRO;
import com.scnujxjy.backendpoint.model.vo.oa.SystemMessageVO;

import java.util.List;

public interface SystemMessageService {

    boolean generateSystemMessage(SystemMessageRO systemMessageRO);

    List<Long> findInvalidUsers(List<Long> userIds);

    boolean updateSystemMessageStatus(Long messageId, String messageStatus);

    Page<SystemMessageVO> getSystemMessagesByPage(Page<SystemMessageVO> page, SystemMessageRO searchParams);

}
