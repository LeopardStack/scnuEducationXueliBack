package com.scnujxjy.backendpoint.service.platform_message;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.constant.enums.SystemMessageStatus;
import com.scnujxjy.backendpoint.constant.enums.YesOrNoEnum;
import com.scnujxjy.backendpoint.constant.enums.office_automation.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.constant.enums.office_automation.SystemMessageType1Enum;
import com.scnujxjy.backendpoint.constant.enums.office_automation.SystemMessageType2Enum;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.SystemMessagePO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.SystemMessageMapper;
import com.scnujxjy.backendpoint.inverter.platform_message.SystemMessageInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.SystemMessageRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.SystemMessageVO;
import com.scnujxjy.backendpoint.service.office_automation.approval.ApprovalStepRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.scnujxjy.backendpoint.constant.enums.office_automation.OfficeAutomationStepStatus.*;


@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class SystemMessageService extends ServiceImpl<SystemMessageMapper, SystemMessagePO> implements IService<SystemMessagePO> {

    @Resource
    private SystemMessageMapper systemMessageMapper;

    @Resource
    private PlatformUserMapper platformUserMapper;

    @Resource
    private PlatformMessageService platformMessageService;

    @Resource
    private SystemMessageInverter systemMessageInverter;

    @Resource
    private ApprovalStepRecordService approvalStepRecordService;

    /**
     * 生成系统消息
     *
     * @param systemMessageRO
     * @return
     */
    public Long createSystemMessage(SystemMessageRO systemMessageRO) {
        if (Objects.isNull(systemMessageRO)) {
            log.warn("systemMessageRO is null");
            return null;
        }
        if (StrUtil.isBlank(systemMessageRO.getSenderUsername())) {
            log.warn("发送人username不能为空");
            return null;
        }
        Set<String> receiverUsernameSet = systemMessageRO.getReceiverUsernameSet();
        if (CollUtil.isEmpty(receiverUsernameSet)) {
            log.warn("接收人username集合为空");
            return null;
        }
        List<PlatformUserPO> platformUserPOS = platformUserMapper.selectList(Wrappers.<PlatformUserPO>lambdaQuery()
                .select(PlatformUserPO::getUsername)
                .in(PlatformUserPO::getUsername, receiverUsernameSet));
        if (CollUtil.isEmpty(platformUserPOS)) {
            log.warn("接收人username集合不存在");
            return null;
        }
        Set<String> existUsernameSet = platformUserPOS.stream()
                .map(PlatformUserPO::getUsername)
                .collect(Collectors.toSet());
        Set<String> notExistUsernameSet = receiverUsernameSet.stream().filter(username -> !existUsernameSet.contains(username)).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(notExistUsernameSet)) {
            log.warn("不存在的username集合 {}", notExistUsernameSet);
            return null;
        }

        String messageStatus = systemMessageRO.getMessageStatus();
        if (Objects.isNull(messageStatus)) {
            log.warn("消息状态为空");
            return null;
        }

        SystemMessageType1Enum match1 = SystemMessageType1Enum.match(systemMessageRO.getSystemMessageType1());
        SystemMessageType2Enum match2 = SystemMessageType2Enum.match(systemMessageRO.getSystemMessageType2());
        if (Objects.isNull(match1) || Objects.isNull(match2)) {
            log.warn("消息类型1或消息类型2不匹配");
            return null;
        }

        Date date = new Date();
        systemMessageRO.setCreatedAt(date);
        systemMessageRO.setUpdatedAt(date);

        // 创建系统消息实体并保存
        SystemMessagePO systemMessagePO = systemMessageInverter.ro2PO(systemMessageRO);

        int res = systemMessageMapper.insert(systemMessagePO);
        if (res == 0) {
            log.warn("系统消息插入失败");
            return null;
        }
        Long messagePOId = systemMessagePO.getId();
        if (Objects.isNull(messagePOId)) {
            log.warn("系统消息id为空");
            return null;
        }
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

        boolean saveBatch = platformMessageService.saveBatch(platformMessagePOS);
        if (!saveBatch) {
            log.warn("平台消息插入失败");
            return null;
        }
        return systemMessagePO.getId();
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
     * 新增或更新系统消息
     * <p>根据systemType1、systemType2、systemRelateId来判断消息是否存在</p>
     *
     * @param systemMessageRO
     * @return
     */
    public Long saveOrUpdateBySystemRelatedId(SystemMessageRO systemMessageRO) {
        if (Objects.isNull(systemMessageRO)
                || Objects.isNull(systemMessageRO.getSystemRelatedId())
                || Objects.isNull(systemMessageRO.getSystemMessageType1())
                || Objects.isNull(systemMessageRO.getSystemMessageType2())) {
            log.warn("系统消息部分为空 {}", systemMessageRO);
            return null;
        }
        SystemMessagePO po = systemMessageInverter.ro2PO(systemMessageRO);
        Date date = new Date();
        po.setCreatedAt(date);
        po.setUpdatedAt(date);
        SystemMessagePO systemMessagePO = baseMapper.selectOne(Wrappers.<SystemMessagePO>lambdaQuery()
                .eq(SystemMessagePO::getSystemRelatedId, systemMessageRO.getSystemRelatedId())
                .eq(SystemMessagePO::getSystemMessageType1, systemMessageRO.getSystemMessageType1())
                .eq(SystemMessagePO::getSystemMessageType2, systemMessageRO.getSystemMessageType2()));
        if (Objects.isNull(systemMessagePO)) {
            Long messageId = createSystemMessage(systemMessageRO);
            if (Objects.isNull(messageId)) {
                return null;
            }
            return messageId;
        } else {
            po.setId(systemMessagePO.getId());
            Boolean updated = updateById(systemMessageRO);
            if (updated) {
                return po.getId();
            }
        }
        return null;
    }

    /**
     * 新增或更新审批信息
     * <p>当usernameSet为空时，默认发送给所有审批人</p>
     * <p>底层调用 {@link SystemMessageService#saveOrUpdateBySystemRelatedId(SystemMessageRO)}</p>
     *
     * @param approvalRecordPO            审批记录
     * @param usernameSet                 接收人群username
     * @param officeAutomationHandlerType {@link OfficeAutomationHandlerType}
     * @return 系统消息id
     */
    public Long saveOrUpdateApprovalMessage(ApprovalRecordPO approvalRecordPO, Set<String> usernameSet, OfficeAutomationHandlerType officeAutomationHandlerType) {
        if (Objects.isNull(approvalRecordPO)
                || Objects.isNull(approvalRecordPO.getId())
                || Objects.isNull(approvalRecordPO.getStatus())) {
            log.info("审批记录信息校验失败 {}", approvalRecordPO);
            return null;
        }
        if (CollUtil.isEmpty(usernameSet)) {
            // 拉取所有步骤的审核人，然后都通知一次
            List<ApprovalStepRecordPO> approvalStepRecordPOS = approvalStepRecordService.selectByApprovalRecordId(approvalRecordPO.getId());
            if (CollUtil.isEmpty(approvalStepRecordPOS)) {
                log.info("获取审批步骤记录失败");
                return null;
            }
            usernameSet = approvalStepRecordPOS.stream()
                    .map(ApprovalStepRecordPO::getApprovalUsernameSet)
                    .filter(CollUtil::isNotEmpty)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        }
        // 发送消息
        SystemMessageRO systemMessageRO = SystemMessageRO.builder()
                .systemMessageType1(SystemMessageType1Enum.TRANSACTION_APPROVAL.getTypeName())
                .systemMessageType2(SystemMessageType2Enum.match(officeAutomationHandlerType).getTypeName())
                .senderUsername(approvalRecordPO.getInitiatorUsername())
                .systemRelatedId(approvalRecordPO.getId())
                .receiverUsernameSet(usernameSet)
                .build();
        if (SUCCESS.getStatus().equals(approvalRecordPO.getStatus())) {
            // 发送成功消息
            systemMessageRO.setMessageStatus(SystemMessageStatus.SUCCESS.getDescription());
        } else if (FAILED.getStatus().equals(approvalRecordPO.getStatus())) {
            systemMessageRO.setMessageStatus(SystemMessageStatus.FAILED.getDescription());
        } else if (WAITING.getStatus().equals(approvalRecordPO.getStatus())) {
            systemMessageRO.setMessageStatus(SystemMessageStatus.WAITING.getDescription());
        }
        return saveOrUpdateBySystemRelatedId(systemMessageRO);
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