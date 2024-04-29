package com.scnujxjy.backendpoint.service.platform_message;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.constant.enums.YesOrNoEnum;
import com.scnujxjy.backendpoint.constant.enums.office_automation.SystemMessageType1Enum;
import com.scnujxjy.backendpoint.constant.enums.office_automation.SystemMessageType2Enum;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.SystemMessagePO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.SystemMessageMapper;
import com.scnujxjy.backendpoint.inverter.platform_message.SystemMessageInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.SystemMessageRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.SystemMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class SystemMessageService extends ServiceImpl<SystemMessageMapper, SystemMessagePO> implements IService<SystemMessagePO> {

    @Resource
    private SystemMessageMapper systemMessageMapper;

    @Resource
    private PlatformUserMapper platformUserMapper;

    @Resource
    private PlatformMessageService platformMessageService;

    @Resource
    private SystemMessageInverter systemMessageInverter;

    /**
     * 生成系统消息
     *
     * @param systemMessageRO
     * @return
     */
    @Transactional
    public boolean createSystemMessage(SystemMessageRO systemMessageRO) {
        if (Objects.isNull(systemMessageRO)) {
            log.warn("systemMessageRO is null");
            return false;
        }
        if (StrUtil.isBlank(systemMessageRO.getSenderUsername())) {
            log.warn("发送人username不能为空");
            return false;
        }
        Set<String> receiverUsernameSet = systemMessageRO.getReceiverUsernameSet();
        if (CollUtil.isEmpty(receiverUsernameSet)) {
            log.warn("接收人username集合为空");
            return false;
        }
        List<PlatformUserPO> platformUserPOS = platformUserMapper.selectList(Wrappers.<PlatformUserPO>lambdaQuery()
                .select(PlatformUserPO::getUsername)
                .in(PlatformUserPO::getUsername, receiverUsernameSet));
        if (CollUtil.isEmpty(platformUserPOS)) {
            log.warn("接收人username集合不存在");
            return false;
        }
        Set<String> existUsernameSet = platformUserPOS.stream()
                .map(PlatformUserPO::getUsername)
                .collect(Collectors.toSet());
        Set<String> notExistUsernameSet = receiverUsernameSet.stream().filter(username -> !existUsernameSet.contains(username)).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(notExistUsernameSet)) {
            log.warn("不存在的username集合 {}", notExistUsernameSet);
            return false;
        }

        String messageStatus = systemMessageRO.getMessageStatus();
        if (Objects.isNull(messageStatus)) {
            log.warn("消息状态为空");
            return false;
        }

        SystemMessageType1Enum match1 = SystemMessageType1Enum.match(systemMessageRO.getSystemMessageType1());
        SystemMessageType2Enum match2 = SystemMessageType2Enum.match(systemMessageRO.getSystemMessageType2());
        if (Objects.isNull(match1) || Objects.isNull(match2)) {
            log.warn("消息类型1或消息类型2不匹配");
            return false;
        }

        Date date = new Date();
        if (Objects.isNull(systemMessageRO.getCreatedAt())) {
            systemMessageRO.setCreatedAt(date);
        }

        // 创建系统消息实体并保存
        SystemMessagePO systemMessagePO = systemMessageInverter.ro2PO(systemMessageRO);

        int res = systemMessageMapper.insert(systemMessagePO);
        if (res == 0) {
            log.warn("系统消息插入失败");
            return false;
        }
        Long messagePOId = systemMessagePO.getId();
        //为每个审批用户插入一条平台消息
        List<PlatformMessagePO> platformMessagePOS = existUsernameSet.stream()
                .map(username ->
                        PlatformMessagePO.builder()
                                .userId(username)
                                .messageType(MessageEnum.SYSTEM_MSG.getMessageName())
                                .relatedMessageId(messagePOId)
                                .createdAt(date)
                                .isRead(false)
                                .isPopup(YesOrNoEnum.NO.getTypeName())
                                .build())
                .collect(Collectors.toList());

        return platformMessageService.saveBatch(platformMessagePOS);
    }

    /**
     * @param systemMessageRO
     * @return
     */
    public Boolean updateById(SystemMessageRO systemMessageRO) {
        if (Objects.isNull(systemMessageRO) || Objects.isNull(systemMessageRO.getId())) {
            log.warn("messageId不存在");
            return false;
        }
        SystemMessagePO systemMessagePO = systemMessageInverter.ro2PO(systemMessageRO);
        int count = baseMapper.updateById(systemMessagePO);
        return count > 0;
    }

    /**
     * 分页查询系统消息，并转换成VO对象返回
     *
     * @param systemMessageROPageRO 包含分页信息和筛选条件的PageRO对象
     * @return 分页的系统消息视图对象（IPage接口类型）
     */
    public PageVO<SystemMessageVO> getSystemMessagesByPage(PageRO<SystemMessageRO> systemMessageROPageRO) {
        if (Objects.isNull(systemMessageROPageRO)) {
            return new PageVO<>();
        }
        SystemMessageRO entity = systemMessageROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new SystemMessageRO();
        }
        Long count = systemMessageMapper.selectEntitiesCount(entity);
        if (count == 0) {
            return new PageVO<>();
        }
        // 执行查询
        List<SystemMessageVO> systemMessageVOS = systemMessageMapper.selectEntities(entity, systemMessageROPageRO);
        if (CollUtil.isEmpty(systemMessageVOS)) {
            return new PageVO<>();
        }
        // 转换结果到VO
        return new PageVO<>(systemMessageROPageRO, count, systemMessageVOS);
    }
}



