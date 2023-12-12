package com.scnujxjy.backendpoint.service.platform_message;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.platform_message.AttachmentPO;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.AttachmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AttachmentService extends ServiceImpl<AttachmentMapper, AttachmentPO> implements IService<AttachmentPO> {


}
