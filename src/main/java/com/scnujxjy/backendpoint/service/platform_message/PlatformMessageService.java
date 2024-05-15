package com.scnujxjy.backendpoint.service.platform_message;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.constant.enums.announceMsg.AnnouncementMsgStatusEnum;
import com.scnujxjy.backendpoint.dao.entity.platform_message.*;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.AnnouncementMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.AttachmentMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.DownloadMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.UserUploadsMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.UserUploadsRO;
import com.scnujxjy.backendpoint.model.vo.platform_message.AnnouncementMessageVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.AttachmentVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.DownloadMessageVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.PlatformMessageVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 缴费信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class PlatformMessageService extends ServiceImpl<PlatformMessageMapper, PlatformMessagePO> implements IService<PlatformMessagePO> {

    @Resource
    private DownloadMessageMapper downloadMessageMapper;

    @Resource
    private UserUploadsMapper userUploadsMapper;

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private AnnouncementMessageMapper announcementMessageMapper;
    @Resource
    private AttachmentMapper attachmentMapper;

    public PlatformMessageVO getUserMsg(String msgType) {
        PlatformMessageVO platformMessageVO = new PlatformMessageVO();
        Long userId = platformUserService.getUserIdByUsername(StpUtil.getLoginIdAsString());


        // 获取与用户相关的所有PlatformMessagePO
        List<PlatformMessagePO> platformMessagePOS = baseMapper.selectList(
                new LambdaQueryWrapper<PlatformMessagePO>().eq(PlatformMessagePO::getUserId, userId));

        if (msgType.equals(MessageEnum.DOWNLOAD_MSG.getMessageName())) {
            List<Long> relatedMessageIds = platformMessagePOS.stream()
                    .map(PlatformMessagePO::getRelatedMessageId)
                    .filter(Objects::nonNull)  // 过滤掉null值
                    .collect(Collectors.toList());

            // 一次性查询所有相关的DownloadMessagePO
            if (relatedMessageIds != null && !relatedMessageIds.isEmpty()) {
                List<DownloadMessagePO> downloadMessages = downloadMessageMapper.selectList(
                        new LambdaQueryWrapper<DownloadMessagePO>().in(DownloadMessagePO::getId, relatedMessageIds)
                );
                for (DownloadMessagePO downloadMessage : downloadMessages) {
                    DownloadMessageVO tempMsg = new DownloadMessageVO();
                    BeanUtils.copyProperties(downloadMessage, tempMsg);
                    tempMsg.setIsRead(platformMessagePOS.stream()
                            .filter(po -> downloadMessage.getId().equals(po.getRelatedMessageId()))
                            .findFirst()
                            .map(PlatformMessagePO::getIsRead)
                            .orElse(false));  // Set default value as false if not found
                    platformMessageVO.getDownloadMessagePOList().add(tempMsg);
                }
            }


            // 处理那些relatedMessageId为null的PlatformMessagePO
            for (PlatformMessagePO messagePO : platformMessagePOS) {
                if (messagePO.getRelatedMessageId() == null) {
                    DownloadMessageVO tempMsg = new DownloadMessageVO();
                    tempMsg.setCreatedAt(messagePO.getCreatedAt());
                    tempMsg.setIsRead(messagePO.getIsRead());
                    // 文件名为空
                    tempMsg.setFileName(null);
                    platformMessageVO.getDownloadMessagePOList().add(tempMsg);
                }
            }

            // 对整个downloadMessagePOList列表按照时间降序排序
            platformMessageVO.getDownloadMessagePOList().sort(Comparator.comparing(DownloadMessageVO::getCreatedAt).reversed());
        } else if (msgType.equals(MessageEnum.UPLOAD_MSG.getMessageName())) {
            // 处理上传消息
            List<UserUploadsPO> userUploadsPOS = userUploadsMapper.selectList(new LambdaQueryWrapper<UserUploadsPO>()
                    .eq(UserUploadsPO::getUserName, userId));
            // 按照时间顺序 新的时间在前面
            List<UserUploadsPO> sortedList = userUploadsPOS.stream()
                    .sorted(Comparator.comparing(UserUploadsPO::getUploadTime).reversed())
                    .collect(Collectors.toList());
            platformMessageVO.setUserUploadsPOList(sortedList);
        }else if(msgType.equals(MessageEnum.ANNOUNCEMENT_MSG.getMessageName())){
            log.info(StpUtil.getLoginIdAsString() +  " 查询公告消息");

            List<AnnouncementMessageVO> announcementMessageVOList = new ArrayList<>();

            List<PlatformMessagePO> platformMessagePOList = getBaseMapper()
                    .selectList(new LambdaQueryWrapper<PlatformMessagePO>()
                            .eq(PlatformMessagePO::getUserId, userId)
                            .eq(PlatformMessagePO::getMessageType, MessageEnum.ANNOUNCEMENT_MSG.getMessageName())
                    );
            for(PlatformMessagePO platformMessagePO: platformMessagePOList){

                Long relatedMessageId = platformMessagePO.getRelatedMessageId();
                AnnouncementMessagePO announcementMessagePO = announcementMessageMapper.selectOne(new LambdaQueryWrapper<AnnouncementMessagePO>()
                        .eq(AnnouncementMessagePO::getId, relatedMessageId));

                // 获取公告消息时 要记得看 是否有公告截止时间 有的话 只能获取 公告截止时间之前 以及公告消息为 已发布状态的
                Date dueDate = announcementMessagePO.getDueDate();
                if(dueDate != null && dueDate.before(new Date())){
                    continue;
                }
                if(!announcementMessagePO.getStatus().equals(AnnouncementMsgStatusEnum.PUBLISHED.getStatus())){
                    continue;
                }

                AnnouncementMessageVO announcementMessageVO = new AnnouncementMessageVO();
                BeanUtils.copyProperties(announcementMessagePO, announcementMessageVO);
                announcementMessageVO.getCreatedAt();
                announcementMessagePO.getCreatedAt();

                List<Long> attachmentIds = announcementMessagePO.getAttachmentIds();
                List<AttachmentVO> attachmentVOList = new ArrayList<>();
                if(attachmentIds != null && !attachmentIds.isEmpty()){
                    for(Long attachmentId : attachmentIds){
                        AttachmentPO attachmentPO = attachmentMapper.selectOne(new LambdaQueryWrapper<AttachmentPO>()
                                .eq(AttachmentPO::getId, attachmentId));
                        AttachmentVO attachmentVO = new AttachmentVO()
                                .setUsername(attachmentPO.getUsername())
                                .setAttachmentOrder(attachmentPO.getAttachmentOrder())
                                .setRelatedId(attachmentPO.getRelatedId())
                                .setAttachmentSize(attachmentPO.getAttachmentSize())
                                .setAttachmentType(attachmentPO.getAttachmentType())
                                .setAttachmentMinioPath(attachmentPO.getAttachmentMinioPath())
                                .setId(attachmentPO.getId())
                                .setAttachmentName(attachmentPO.getAttachmentName())
                                ;
                        attachmentVOList.add(attachmentVO);
                    }
                }
                announcementMessageVO.setAttachmentVOS(attachmentVOList);
                announcementMessageVOList.add(announcementMessageVO);
            }
            List<AnnouncementMessageVO> sortedList = announcementMessageVOList.stream()
                    .sorted(Comparator.comparing(AnnouncementMessageVO::getCreatedAt).reversed())
                    .collect(Collectors.toList());
            platformMessageVO.setAnnouncementMessageVOList(sortedList);
        }
        return platformMessageVO;
    }

    public PlatformMessageVO getUserMessage(PageRO<UserUploadsRO> userUploadsROPageRO) {
        PlatformMessageVO platformMessageVO = new PlatformMessageVO();
        UserUploadsRO entity = userUploadsROPageRO.getEntity();

        if (entity.getMsgType().equals(MessageEnum.UPLOAD_MSG.getMessageName())) {
            // 创建一个Page对象，使用userUploadsROPageRO提供的分页参数
            Page<UserUploadsPO> page = userUploadsROPageRO.getPage();

            // 使用Page对象执行分页查询
            Page<UserUploadsPO> resultPage = userUploadsMapper.selectPage(page, new LambdaQueryWrapper<UserUploadsPO>()
                    .eq(UserUploadsPO::getUserName, platformUserService.getUserIdByUsername(StpUtil.getLoginIdAsString()))
                    .orderByDesc(UserUploadsPO::getUploadTime)  // 按上传时间降序排序
            );

            // 从结果Page对象中获取结果列表
            List<UserUploadsPO> sortedList = resultPage.getRecords();
            platformMessageVO.setUserUploadsPOList(sortedList);
        }
        return platformMessageVO;
    }

    /**
     * 获取用户系统消息
     * @return
     */
    public PlatformMessageVO getUserSystemMsg() {
        PlatformMessageVO platformMessageVO = new PlatformMessageVO();
        Long userId = platformUserService.getUserIdByUsername(StpUtil.getLoginIdAsString());
        List<AnnouncementMessageVO> announcementMessageVOList = new ArrayList<>();

        // 获取与用户相关的所有PlatformMessagePO
        List<PlatformMessagePO> platformMessagePOList = baseMapper.selectList(
                new LambdaQueryWrapper<PlatformMessagePO>()
                        .eq(PlatformMessagePO::getUserId, userId)
                        .eq(PlatformMessagePO::getMessageType, MessageEnum.ANNOUNCEMENT_MSG.getMessageName())
                        );
        for(PlatformMessagePO platformMessagePO: platformMessagePOList){

            Long relatedMessageId = platformMessagePO.getRelatedMessageId();
            AnnouncementMessagePO announcementMessagePO = announcementMessageMapper.selectOne(new LambdaQueryWrapper<AnnouncementMessagePO>()
                    .eq(AnnouncementMessagePO::getId, relatedMessageId));

            // 获取公告消息时 要记得看 是否有公告截止时间 有的话 只能获取 公告截止时间之前 以及公告消息为 已发布状态的
            Date dueDate = announcementMessagePO.getDueDate();
            if(dueDate != null && dueDate.before(new Date())){
                continue;
            }
            if(!announcementMessagePO.getStatus().equals(AnnouncementMsgStatusEnum.PUBLISHED.getStatus())){
                continue;
            }

            AnnouncementMessageVO announcementMessageVO = new AnnouncementMessageVO();
            BeanUtils.copyProperties(announcementMessagePO, announcementMessageVO);
            announcementMessageVO.getCreatedAt();
            announcementMessagePO.getCreatedAt();

            List<Long> attachmentIds = announcementMessagePO.getAttachmentIds();
            List<AttachmentVO> attachmentVOList = new ArrayList<>();
            if(attachmentIds != null && !attachmentIds.isEmpty()){
                for(Long attachmentId : attachmentIds){
                    AttachmentPO attachmentPO = attachmentMapper.selectOne(new LambdaQueryWrapper<AttachmentPO>()
                            .eq(AttachmentPO::getId, attachmentId));
                    AttachmentVO attachmentVO = new AttachmentVO()
                            .setUsername(attachmentPO.getUsername())
                            .setAttachmentOrder(attachmentPO.getAttachmentOrder())
                            .setRelatedId(attachmentPO.getRelatedId())
                            .setAttachmentSize(attachmentPO.getAttachmentSize())
                            .setAttachmentType(attachmentPO.getAttachmentType())
                            .setAttachmentMinioPath(attachmentPO.getAttachmentMinioPath())
                            .setId(attachmentPO.getId())
                            .setAttachmentName(attachmentPO.getAttachmentName())
                            ;
                    attachmentVOList.add(attachmentVO);
                }
                announcementMessageVO.setAttachmentVOS(attachmentVOList);
            }

            announcementMessageVOList.add(announcementMessageVO);
        }
        List<AnnouncementMessageVO> sortedList = announcementMessageVOList.stream()
                .sorted(Comparator.comparing(AnnouncementMessageVO::getCreatedAt).reversed())
                .limit(10) // 限制最大数量为 10
                .collect(Collectors.toList());
        platformMessageVO.setAnnouncementMessageVOList(sortedList);


        return platformMessageVO;
    }
}

