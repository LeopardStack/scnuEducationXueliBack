package com.scnujxjy.backendpoint.service.oa;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.scnujxjy.backendpoint.model.ro.oa.SystemMessageRO;
import com.scnujxjy.backendpoint.model.vo.oa.SystemMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
public class SystemMessageServiceImpl  extends ServiceImpl<SystemMessageMapper, SystemMessagePO> implements SystemMessageService {

    @Resource
    private SystemMessageMapper systemMessageMapper;

    @Resource
    private PlatformUserMapper platformUserMapper;

    @Resource
    private PlatformMessageMapper platformMessageMapper;

    /**
     * 生成系统消息
     * @param systemMessageRO
     * @return
     */
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

        boolean isValid1 = isValidEnumValue(systemMessageType1, SystemMessageType1Enum.class);
        boolean isValid2 = isValidEnumValue(systemMessageType2, SystemMessageType2Enum.class);

        if (!isValid1 || !isValid2) {
            log.error("当前事务的流程状态类型不合法");
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
        if (res == null || res == 0) {
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
        validUserIds.forEach(approverId -> {
            PlatformMessagePO message = PlatformMessagePO.builder()
                    .userId(approverId.toString())
                    .messageType(MessageEnum.SYSTEM_MSG.getMessageName())
                    .relatedMessageId(messageID)
                    .createdAt(new Date())
                    .isRead(false)
                    .isPopup(YesOrNoEnum.NO.getTypeName())
                    .build();

            platformMessageMapper.insert(message);
        });
        return true;
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
     * 检查枚举值是否合法
     * @param value
     * @param enumClass
     * @return
     */
    public static boolean isValidEnumValue(String value, Class<? extends Enum<?>> enumClass) {
        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            try {
                String typeName = (String) enumClass.getDeclaredField("typeName").get(enumConstant);
                if (typeName.equals(value)) {
                    return true;
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return false;
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
     * 获取系统消息列表
     * @param searchParams 包含分页信息和筛选条件的请求对象
     * @return 分页的系统消息视图对象
     */
    public Page<SystemMessageVO> getSystemMessagesByPage(SystemMessageRO searchParams) {
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

        // 使用 PageRO 信息进行分页和排序
        Page<SystemMessagePO> poPage = searchParams.getPage();
        queryWrapper.orderBy(true, searchParams.getOrderType().equals("ASC"), searchParams.getOrderBy());

        // 执行查询
        Page<SystemMessagePO> result = systemMessageMapper.selectPage(poPage, queryWrapper);

        // 转换结果到VO
        Page<SystemMessageVO> voPage = result.convert(po -> {
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
    /**
     * 根据主键更新当前系统消息（SystemMessage）的状态
     * @param record
     * @return
     */

}

