package com.scnujxjy.backendpoint.service.platform_message;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.dao.entity.platform_message.AnnouncementMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.DownloadMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.UserUploadsPO;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.AnnouncementMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.DownloadMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.UserUploadsMapper;
import com.scnujxjy.backendpoint.inverter.platform_message.AnnouncementMessageInverter;
import com.scnujxjy.backendpoint.inverter.platform_message.PlatformMessageInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.UserAnnouncementRo;
import com.scnujxjy.backendpoint.model.ro.platform_message.UserUploadsRO;
import com.scnujxjy.backendpoint.model.vo.platform_message.DownloadMessageVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.PlatformMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
    private PlatformMessageInverter platformMessageInverter;

    @Resource
    private DownloadMessageMapper downloadMessageMapper;

    @Resource
    private UserUploadsMapper userUploadsMapper;

    @Resource
    private  PlatformMessageMapper platformMessageMapper;

    @Resource
    private AnnouncementMessageMapper announcementMessageMapper;

    @Resource
    private AnnouncementMessageInverter announcementMessageInverter;

    /**
    * @Version：1.0.0
    * @Description：插入公告消息
    * @Author：3304393868@qq.com
    * @Date：2023/12/8-9:15
    */
    public boolean InsterAnnouncementMessage (UserAnnouncementRo userAnnouncementRo){

        AnnouncementMessagePO announcementMessagePO = announcementMessageInverter.ro2PO(userAnnouncementRo);
        int count =  announcementMessageMapper.insert(announcementMessagePO);
        userAnnouncementRo.setRelatedMessageId(announcementMessagePO.getId());
        if (count>0){
            PlatformMessagePO platformMessagePO = platformMessageInverter.ro2PO(userAnnouncementRo);
            return platformMessageMapper.insert(platformMessagePO) > 0;
        }
        return false;



    }




    public PlatformMessageVO getUserMsg(String msgType) {
        PlatformMessageVO platformMessageVO = new PlatformMessageVO();
        String userName = (String) StpUtil.getLoginId();


        // 获取与用户相关的所有PlatformMessagePO
        List<PlatformMessagePO> platformMessagePOS = baseMapper.selectList(
                new LambdaQueryWrapper<PlatformMessagePO>().eq(PlatformMessagePO::getUserId, userName)
        );

        if (msgType.equals(MessageEnum.DOWNLOAD_MSG.getMessage_name())) {
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
                            .filter(po -> po.getRelatedMessageId().equals(downloadMessage.getId()))
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
        }else if (msgType.equals(MessageEnum.UPLOAD_MSG.getMessage_name())) {
            // 处理上传消息
            List<UserUploadsPO> userUploadsPOS = userUploadsMapper.selectList(new LambdaQueryWrapper<UserUploadsPO>()
                    .eq(UserUploadsPO::getUserName, userName)
            );

            // 按照时间顺序 新的时间在前面
            List<UserUploadsPO> sortedList = userUploadsPOS.stream()
                    .sorted(Comparator.comparing(UserUploadsPO::getUploadTime).reversed())
                    .collect(Collectors.toList());
            platformMessageVO.setUserUploadsPOList(sortedList);
        }
        return platformMessageVO;
    }

    public PlatformMessageVO getUserMessage(PageRO<UserUploadsRO> userUploadsROPageRO) {
        PlatformMessageVO platformMessageVO = new PlatformMessageVO();
        String userName = (String) StpUtil.getLoginId();
        UserUploadsRO entity = userUploadsROPageRO.getEntity();

        if (entity.getMsgType().equals(MessageEnum.UPLOAD_MSG.getMessage_name())) {
            // 创建一个Page对象，使用userUploadsROPageRO提供的分页参数
            Page<UserUploadsPO> page = userUploadsROPageRO.getPage();

            // 使用Page对象执行分页查询
            Page<UserUploadsPO> resultPage = userUploadsMapper.selectPage(page, new LambdaQueryWrapper<UserUploadsPO>()
                    .eq(UserUploadsPO::getUserName, userName)
                    .orderByDesc(UserUploadsPO::getUploadTime)  // 按上传时间降序排序
            );

            // 从结果Page对象中获取结果列表
            List<UserUploadsPO> sortedList = resultPage.getRecords();
            platformMessageVO.setUserUploadsPOList(sortedList);
        }
        return platformMessageVO;
    }

}

