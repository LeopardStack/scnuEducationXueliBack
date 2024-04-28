package com.scnujxjy.backendpoint.service.oa;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.constant.enums.oa.SystemMessageType1Enum;
import com.scnujxjy.backendpoint.constant.enums.oa.SystemMessageType2Enum;
import com.scnujxjy.backendpoint.constant.enums.YesOrNoEnum;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.oa.SystemMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.oa.SystemMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.oa.SystemMessageRO;
import com.scnujxjy.backendpoint.model.vo.oa.SystemMessageVO;
import com.scnujxjy.backendpoint.service.platform_message.PlatformMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    private PlatformMessageMapper platformMessageMapper;

    /**
     * 生成系统消息
     * @param systemMessageRO
     * @return
     */
    @Transactional
    public boolean generateSystemMessage(SystemMessageRO systemMessageRO) {
        if (Objects.isNull(systemMessageRO)) {
            log.error("systemMessageRO is null");
            return false;
        }
        if (Objects.isNull(systemMessageRO.getUserIds()) || systemMessageRO.getUserIds().isEmpty()) {
            log.error("无法找到审批人的相关信息");
            return false;
        }
        Long userId = systemMessageRO.getUserId();

        PlatformUserPO platformUserPO = platformUserMapper.selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                .eq(PlatformUserPO::getUserId, userId));

        if (Objects.isNull(platformUserPO)) {
            log.error("无法找到当前请求用户的相关信息");
            return false;
        }

        List<Long> validUserIds = systemMessageRO.getUserIds();
        List<Long> invalidUserIds = findInvalidUsers(validUserIds);

        if(!invalidUserIds.isEmpty()){
            log.error("用户id为{}的审批人不存在", invalidUserIds);
            return false;
        }

        String systemMessageType1 = systemMessageRO.getSystemMessageType1();
        String systemMessageType2 = systemMessageRO.getSystemMessageType2();
        String messageStatus = systemMessageRO.getMessageStatus();

        if (Objects.isNull(systemMessageType1) || Objects.isNull(systemMessageType2) || Objects.isNull(messageStatus)) {
            log.error("消息类型或消息状态为空");
            return false;
        }

        SystemMessageType1Enum match1 = SystemMessageType1Enum.match(systemMessageType1);
        SystemMessageType2Enum match2 = SystemMessageType2Enum.match(systemMessageType2);

        if (Objects.isNull(match1) || Objects.isNull(match2)) {
            log.error("消息类型1或消息类型2不匹配");
            return false;
        }

        // 创建系统消息实体并保存
        SystemMessagePO systemMessagePO = new SystemMessagePO();
        systemMessagePO.setSystemMessageType1(systemMessageType1);
        systemMessagePO.setSystemMessageType2(systemMessageType2);
        systemMessagePO.setSystemRelatedId(userId);
        systemMessagePO.setMessageStatus(messageStatus);
        systemMessagePO.setSystemRelatedId(systemMessagePO.getSystemRelatedId());
        systemMessagePO.setCreatedAt(new Date());

        Integer res = systemMessageMapper.insert(systemMessagePO);
        if (res == 0) {
            log.error("系统消息插入失败");
            return false;
        }

        LambdaQueryWrapper<SystemMessagePO> queryWrapper = new LambdaQueryWrapper<SystemMessagePO>()
                .eq(SystemMessagePO::getSystemMessageType1, systemMessageType1)
                .eq(SystemMessagePO::getSystemMessageType2, systemMessageType2)
                .eq(SystemMessagePO::getSystemRelatedId, userId);

        SystemMessagePO systemMessage = systemMessageMapper.selectOne(queryWrapper);
        Long messageID = systemMessage.getId();
        //为每个审批用户插入一条平台消息
        List<PlatformMessagePO> platformMessagePOS = validUserIds.stream().map(approverId -> {
            PlatformMessagePO message = PlatformMessagePO.builder()
                    .userId(approverId.toString())
                    .messageType(MessageEnum.SYSTEM_MSG.getMessageName())
                    .relatedMessageId(messageID)
                    .createdAt(new Date())
                    .isRead(false)
                    .isPopup(YesOrNoEnum.NO.getTypeName())
                    .build();

            return message;
        }).collect(Collectors.toList());

        return platformMessageService.saveBatch(platformMessagePOS);
    }

    /**
     * 检查用户是否存在与数据库中
     * @param userIds
     * @return
     */
    public List<Long> findInvalidUsers(List<Long> userIds) {
        List<Long> invalidUserIds = new ArrayList<>();

        if (userIds == null || userIds.isEmpty()) {
            return invalidUserIds;
        }

        // 使用Java Stream API来检查每一个userId
        userIds.forEach(userId -> {
            PlatformUserPO platformUserPO = platformUserMapper.selectOne(new LambdaQueryWrapper<>());
            if (platformUserPO == null || !platformUserPO.getUserId().equals(userId)) {
                invalidUserIds.add(userId);
            }
        });

        return invalidUserIds;
    }


    /**
     * 更新系统消息状态，把当前消息的状态更新为messageStatus
     * @param messageId 系统消息ID
     * @param messageStatus 消息状态
     * @return
     */
    public boolean updateSystemMessageStatus(Long messageId, String messageStatus) {
        if (Objects.isNull(messageId) || Objects.isNull(messageStatus)) {
            log.error("messageId或messageStatus为空");
            return false;
        }

        SystemMessagePO systemMessagePO = systemMessageMapper.selectOne(new LambdaQueryWrapper<SystemMessagePO>()
                .eq(SystemMessagePO::getId, messageId));

        if (Objects.isNull(systemMessagePO)) {
            log.error("无法找到对应的系统消息");
            return false;
        }

        systemMessagePO.setMessageStatus(messageStatus);
        systemMessageMapper.updateById(systemMessagePO);

        return true;
    }

    /**
     * 分页查询系统消息，并转换成VO对象返回
     * @param pageRO 包含分页信息和筛选条件的PageRO对象
     * @return 分页的系统消息视图对象（IPage接口类型）
     */
    public IPage<SystemMessageVO> getSystemMessagesByPage(PageRO<SystemMessageRO> pageRO) {
        SystemMessageRO searchParams = pageRO.getEntity();
        LambdaQueryWrapper<SystemMessagePO> queryWrapper = new LambdaQueryWrapper<>();

        // 添加筛选条件
        if (searchParams != null) {
            if (searchParams.getSystemMessageType1() != null) {
                queryWrapper.eq(SystemMessagePO::getSystemMessageType1, searchParams.getSystemMessageType1());
            }
            if (searchParams.getSystemMessageType2() != null) {
                queryWrapper.eq(SystemMessagePO::getSystemMessageType2, searchParams.getSystemMessageType2());
            }
            if (searchParams.getMessageStatus() != null) {
                queryWrapper.eq(SystemMessagePO::getMessageStatus, searchParams.getMessageStatus());
            }
            if (searchParams.getSystemRelatedId() != null) {
                queryWrapper.eq(SystemMessagePO::getSystemRelatedId, searchParams.getSystemRelatedId());
            }
        }
        // 设置排序, 默认按照创建时间降序排序
        queryWrapper.orderBy(true, true, SystemMessagePO::getCreatedAt);

        // 获取分页配置
        Page<SystemMessagePO> poPage = new Page<>(pageRO.getPageNumber(), pageRO.getPageSize());

        // 执行查询
        IPage<SystemMessagePO> resultPage = systemMessageMapper.selectPage(poPage, queryWrapper);

        // 转换结果到VO
        IPage<SystemMessageVO> voPage = resultPage.convert(po -> {
            PlatformUserPO platformUserPO = platformUserMapper.selectById(po.getSystemRelatedId());
            return SystemMessageVO.builder()
                    .systemMessageType1(po.getSystemMessageType1())
                    .systemMessageType2(po.getSystemMessageType2())
                    .messageStatus(po.getMessageStatus())
                    .systemRelatedId(po.getSystemRelatedId())
                    .formattedCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(po.getCreatedAt()))
                    .formattedUpdatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(po.getUpdatedAt()))
                    .relatedUserName(platformUserPO != null ? platformUserPO.getUsername() : "")
                    .build();
        });

        return voPage;
    }
}


