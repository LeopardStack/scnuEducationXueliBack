package com.scnujxjy.backendpoint.service.platform_message;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.constant.enums.UploadType;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.UserUploadsPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.UserUploadsMapper;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private PlatformMessageService platformMessageService;

    public long generateCourseScheduleListUploadMsg(String uploadFileUrl, String uploadType) {
        String userName = (String) StpUtil.getLoginId();
        try {
            Long userId = platformUserService.getUserIdByUsername(StpUtil.getLoginIdAsString());
            // 生成一个上传消息 状态为处理中
            // 获取当前的 LocalDateTime 实例
            LocalDateTime now = LocalDateTime.now();

            // 使用系统默认时区将 LocalDateTime 转换为 Instant
            Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();

            // 将 Instant 转换为 java.util.Date
            Date date = Date.from(instant);

            UserUploadsPO userUploadsPO = new UserUploadsPO();
            userUploadsPO.setUserName(String.valueOf(userId));
            userUploadsPO.setUploadTime(date);
            userUploadsPO.setUploadType(uploadType);
            userUploadsPO.setFileUrl(uploadFileUrl);
            userUploadsPO.setIsRead(false);
            int insert = getBaseMapper().insert(userUploadsPO);

            log.info(userName + " 上传文件的消息已生成 " + insert);
            Long generatedId = userUploadsPO.getId();
            PlatformMessagePO platformMessagePO = new PlatformMessagePO();
            platformMessagePO.setCreatedAt(date);
            platformMessagePO.setUserId(String.valueOf(userId));
            platformMessagePO.setIsRead(false);
            platformMessagePO.setRelatedMessageId(generatedId);
            platformMessagePO.setMessageType(MessageEnum.UPLOAD_MSG.getMessageName());
            int insert1 = platformMessageService.getBaseMapper().insert(platformMessagePO);
            log.info("用户上传文件的消息插入结果 " + insert1);
            return generatedId;

        } catch (Exception e) {
            log.error(userName + " 生成上传文件的消息失败 " + e.toString());
        }
        return -1;
    }

}
