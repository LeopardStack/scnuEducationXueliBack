package com.scnujxjy.backendpoint.service.platform_message;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.dao.entity.platform_message.DownloadMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.DownloadMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.inverter.core_data.PaymentInfoInverter;
import com.scnujxjy.backendpoint.inverter.platform_message.PlatformMessageInverter;
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
    private PlatformMessageInverter paymentInfoInverter;

    @Resource
    private DownloadMessageMapper downloadMessageMapper;

    public PlatformMessageVO getUserMsg(String msgType) {
        PlatformMessageVO platformMessageVO = new PlatformMessageVO();
        String userId = (String) StpUtil.getLoginId();

        // 获取与用户相关的所有PlatformMessagePO
        List<PlatformMessagePO> platformMessagePOS = baseMapper.selectList(
                new LambdaQueryWrapper<PlatformMessagePO>().eq(PlatformMessagePO::getUserId, userId)
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
                    tempMsg.setFileName(null);  // 文件名为空
                    platformMessageVO.getDownloadMessagePOList().add(tempMsg);
                }
            }

            // 对整个downloadMessagePOList列表按照时间降序排序
            platformMessageVO.getDownloadMessagePOList().sort(Comparator.comparing(DownloadMessageVO::getCreatedAt).reversed());
        }
        return platformMessageVO;
    }


}

