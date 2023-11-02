package com.scnujxjy.backendpoint.service.platform_message;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.UserUploadsPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.UserUploadsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-10-29
 */
@Service
@Slf4j
public class UserUploadsService extends ServiceImpl<UserUploadsMapper, UserUploadsPO> implements IService<UserUploadsPO> {

}
