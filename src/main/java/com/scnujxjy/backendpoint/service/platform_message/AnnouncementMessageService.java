package com.scnujxjy.backendpoint.service.platform_message;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.AnnouncementMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.AttachmentPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.AnnouncementMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.AttachmentMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointInformationMapper;
import com.scnujxjy.backendpoint.exception.BusinessException;
import com.scnujxjy.backendpoint.inverter.platform_message.AnnouncementMessageInverter;
import com.scnujxjy.backendpoint.inverter.platform_message.AttachmentInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.AnnouncementMessageRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.AnnouncementMessageVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.util.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.scnujxjy.backendpoint.constant.enums.AttachmentType.ANNOUNCEMENT;
import static com.scnujxjy.backendpoint.constant.enums.MessageEnum.ANNOUNCEMENT_MSG;
import static com.scnujxjy.backendpoint.constant.enums.MessageStatus.PUBLISH;
import static com.scnujxjy.backendpoint.constant.enums.MinioBucketEnum.ANNOUNCEMENT_BUCKET;

@Slf4j
@Service
public class AnnouncementMessageService extends ServiceImpl<AnnouncementMessageMapper, AnnouncementMessagePO> implements IService<AnnouncementMessagePO> {

    @Resource
    private AnnouncementMessageInverter announcementMessageInverter;

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private MinioUtil minioUtil;

    @Resource
    private AttachmentMapper attachmentMapper;
    @Resource
    private AttachmentInverter attachmentInverter;

    @Resource
    private PlatformMessageMapper platformMessageMapper;

    @Resource
    private TeachingPointInformationMapper teachingPointInformationMapper;


    @Resource
    private PlatformUserMapper platformUserMapper;

    @Resource
    private PersonalInfoMapper personalInfoMapper;

    @Resource
    private AdmissionInformationMapper admissionInformationMapper;

    /**
     * 根据公告 id 查询公告详情
     *
     * @param announcementId
     * @return
     */
    public AnnouncementMessageVO detailById(Long announcementId) {
        if (Objects.isNull(announcementId)) {
            throw new BusinessException("公告 id 不能为空");
        }
        AnnouncementMessagePO announcementMessagePO = baseMapper.selectById(announcementId);
        AnnouncementMessageVO announcementMessageVO = announcementMessageInverter.po2VO(announcementMessagePO);
        // 查询附件信息
        List<AttachmentPO> attachmentPOS = attachmentMapper.selectList(Wrappers.<AttachmentPO>lambdaQuery()
                .eq(AttachmentPO::getRelatedId, announcementId)
                .eq(AttachmentPO::getAttachmentType, ANNOUNCEMENT.getType())
                .orderBy(true, true, AttachmentPO::getAttachmentOrder));
        announcementMessageVO.setAttachmentVOS(attachmentInverter.po2VO(attachmentPOS));
        return announcementMessageVO.setPageViews(announcementPageView(announcementId));
    }

    /**
     * 新增公告
     *
     * @param announcementMessageRO
     * @param files
     * @return
     */
    public AnnouncementMessageVO create(AnnouncementMessageRO announcementMessageRO, MultipartFile[] files) {
        if (Objects.isNull(announcementMessageRO)
                || StrUtil.isBlank(announcementMessageRO.getTitle())) {
            throw new BusinessException("公告参数不能为空");
        }
        AnnouncementMessagePO announcementMessagePO = announcementMessageInverter.ro2PO(announcementMessageRO);
        if (Objects.isNull(announcementMessagePO.getUserId())) {
            announcementMessagePO.setUserId(platformUserService.getUserIdByUsername(StpUtil.getLoginIdAsString()));
        }
        announcementMessagePO.setCreatedAt(DateUtil.date());
        int inserted = baseMapper.insert(announcementMessagePO);
        if (inserted == 0) {
            throw new BusinessException("新建公告失败");
        }

        // 保存附件
        if (Objects.nonNull(files)) {
            int order = 1;
            for (MultipartFile file : files) {
                String contentType = file.getContentType();
                String filename = file.getOriginalFilename();
                String filePath = ANNOUNCEMENT_BUCKET.getSubDirectory() + "/" + announcementMessagePO.getName() + "/" + filename;
                try {
                    minioUtil.putFile(ANNOUNCEMENT_BUCKET.getBucketName(), filePath, contentType, file.getInputStream());
                } catch (Exception e) {
                    throw new BusinessException(e);
                }
                attachmentMapper.insert(AttachmentPO.builder()
                        .relatedId(announcementMessagePO.getId())
                        .attachmentType(ANNOUNCEMENT.getType())
                        .attachmentOrder(order++)
                        .attachmentMinioPath(filePath)
                        .attachmentName(filename)
                        .attachmentSize(file.getSize())
                        .userId(platformUserService.getUserIdByUsername(StpUtil.getLoginIdAsString()))
                        .build());
            }
        }
        // 设置消息推送
        if (PUBLISH.getStatus().equals(announcementMessagePO.getStatus())) {
            Set<Long> userIdSet = getUserIdSet(announcementMessageRO.getGradeSet(), announcementMessageRO.getMajorIdSet(), announcementMessageRO.getTeachingPointIdSet());
            if (CollUtil.isNotEmpty(userIdSet)) {
                PlatformMessagePO platformMessagePO = PlatformMessagePO.builder()
                        .messageType(ANNOUNCEMENT_MSG.getMessageName())
                        .relatedMessageId(announcementMessagePO.getId())
                        .createdAt(DateUtil.date())
                        .isRead(false)
                        .build();
                userIdSet.forEach(userId -> {
                    platformMessageMapper.insert(platformMessagePO.setUserId(String.valueOf(userId)));
                });
            }
        }
        return detailById(announcementMessagePO.getId());
    }

    /**
     * 根据年级、专业、教学点信息获取userId
     *
     * @param graderSet
     * @param admissionMajorCodeSet
     * @param teachingPointIdSet
     * @return
     */
    private Set<Long> getUserIdSet(Set<Long> graderSet, Set<Long> admissionMajorCodeSet, Set<String> teachingPointIdSet) {
        Set<String> idCardNumberSet = new HashSet<>();
        // 年级查询身份证号码
        if (CollUtil.isNotEmpty(graderSet)) {
            List<PersonalInfoPO> personalInfoPOS = personalInfoMapper.selectList(Wrappers.<PersonalInfoPO>lambdaQuery()
                    .in(PersonalInfoPO::getGrade, graderSet)
                    .select(PersonalInfoPO::getIdNumber));
            if (CollUtil.isNotEmpty(personalInfoPOS)) {
                CollUtil.addAll(idCardNumberSet, personalInfoPOS.stream().map(PersonalInfoPO::getIdNumber).collect(Collectors.toSet()));
            }
        }
        // 专业代码查询身份证号码
        if (CollUtil.isNotEmpty(admissionMajorCodeSet)) {
            List<AdmissionInformationPO> admissionInformationPOS = admissionInformationMapper.selectList(Wrappers.<AdmissionInformationPO>lambdaQuery()
                    .in(AdmissionInformationPO::getMajorCode, admissionMajorCodeSet)
                    .select(AdmissionInformationPO::getIdCardNumber));
            if (CollUtil.isNotEmpty(admissionInformationPOS)) {
                CollUtil.addAll(idCardNumberSet, admissionInformationPOS.stream()
                        .map(AdmissionInformationPO::getIdCardNumber)
                        .collect(Collectors.toSet()));
            }
        }
        // 教学点查询身份证号码
        if (CollUtil.isNotEmpty(teachingPointIdSet)) {
            List<TeachingPointInformationPO> teachingPointInformationPOS = teachingPointInformationMapper.selectBatchIds(teachingPointIdSet);
            if (CollUtil.isNotEmpty(teachingPointInformationPOS)) {
                Set<String> teachingPointNameSet = teachingPointInformationPOS.stream().map(TeachingPointInformationPO::getTeachingPointName).collect(Collectors.toSet());
                List<AdmissionInformationPO> admissionInformationPOS = admissionInformationMapper.selectList(Wrappers.<AdmissionInformationPO>lambdaQuery()
                        .in(AdmissionInformationPO::getTeachingPoint, teachingPointNameSet)
                        .select(AdmissionInformationPO::getIdCardNumber));
                if (CollUtil.isNotEmpty(admissionInformationPOS)) {
                    CollUtil.addAll(idCardNumberSet, admissionInformationPOS.stream()
                            .map(AdmissionInformationPO::getIdCardNumber)
                            .collect(Collectors.toSet()));
                }
            }
        }
        if (CollUtil.isEmpty(idCardNumberSet)) {
            return null;
        }
        // 根据身份证号码查询 userId
        List<PlatformUserPO> platformUserPOS = platformUserMapper.selectList(Wrappers.<PlatformUserPO>lambdaQuery().in(PlatformUserPO::getUsername, idCardNumberSet)
                .select(PlatformUserPO::getUserId));
        if (CollUtil.isEmpty(platformUserPOS)) {
            throw new BusinessException("根据身份号码查询用户信息失败");
        }
        return platformUserPOS.stream()
                .map(PlatformUserPO::getUserId)
                .collect(Collectors.toSet());
    }

    /**
     * 分页查询
     *
     * @param announcementMessageROPageRO
     * @return
     */
    public PageVO<AnnouncementMessageVO> pageQuery(PageRO<AnnouncementMessageRO> announcementMessageROPageRO) {
        if (Objects.isNull(announcementMessageROPageRO)) {
            throw new BusinessException("公告信息分页查询参数为空");
        }
        AnnouncementMessageRO announcementMessageRO = announcementMessageROPageRO.getEntity();
        if (Objects.isNull(announcementMessageRO)) {
            announcementMessageRO = new AnnouncementMessageRO();
        }
        LambdaQueryWrapper<AnnouncementMessagePO> wrapper = Wrappers.<AnnouncementMessagePO>lambdaQuery()
                .eq(Objects.nonNull(announcementMessageRO.getId()), AnnouncementMessagePO::getId, announcementMessageRO.getId())
                .eq(Objects.nonNull(announcementMessageRO.getStatus()), AnnouncementMessagePO::getStatus, announcementMessageRO.getStatus())
                .select(AnnouncementMessagePO.class, info -> !info.getProperty().equals("content"));
        Page<AnnouncementMessagePO> announcementMessagePOPage = baseMapper.selectPage(announcementMessageROPageRO.getPage(), wrapper);
        List<AnnouncementMessagePO> announcementMessagePOS = announcementMessagePOPage.getRecords();
        if (CollUtil.isEmpty(announcementMessagePOS)) {
            return new PageVO<>(announcementMessagePOPage, ListUtil.of());
        }
        List<AnnouncementMessageVO> announcementMessageVOS = announcementMessageInverter.po2VO(announcementMessagePOS);
        announcementMessageVOS.forEach(announcement -> announcement.setPageViews(announcementPageView(announcement.getId())));
        return new PageVO<>(announcementMessagePOPage, announcementMessageVOS);
    }

    /**
     * 更新
     *
     * @param announcementMessageRO
     * @return
     */
    public AnnouncementMessageVO update(AnnouncementMessageRO announcementMessageRO) {
        if (Objects.isNull(announcementMessageRO)
                || Objects.isNull(announcementMessageRO.getId())) {
            throw new BusinessException("公告参数为空");
        }
        AnnouncementMessagePO announcementMessagePO = baseMapper.selectById(announcementMessageRO.getId());
        if (Objects.isNull(announcementMessagePO)) {
            throw new BusinessException("公告不存在");
        }
        int updated = baseMapper.updateById(announcementMessageInverter.ro2PO(announcementMessageRO));
        if (updated == 0) {
            return null;
        }
        // 只有在原先不是发布状态改变为发布状态的时候才会发送消息
        if (PUBLISH.getStatus().equals(announcementMessageRO.getStatus())
                && !PUBLISH.getStatus().equals(announcementMessagePO.getStatus())) {
            Set<Long> userIdSet = getUserIdSet(announcementMessageRO.getGradeSet(), announcementMessageRO.getMajorIdSet(), announcementMessageRO.getTeachingPointIdSet());
            if (CollUtil.isNotEmpty(userIdSet)) {
                PlatformMessagePO platformMessagePO = PlatformMessagePO.builder()
                        .messageType(ANNOUNCEMENT_MSG.getMessageName())
                        .relatedMessageId(announcementMessagePO.getId())
                        .createdAt(DateUtil.date())
                        .isRead(false)
                        .build();
                userIdSet.forEach(userId -> {
                    platformMessageMapper.insert(platformMessagePO.setUserId(String.valueOf(userId)));
                });
            }
        }
        return detailById(announcementMessageRO.getId());
    }

    /**
     * 获取公告发放量以及浏览量
     *
     * @param announcementId 浏览量
     * @return
     */
    private Long announcementPageView(Long announcementId) {
        if (Objects.isNull(announcementId)) {
            throw new BusinessException("公告 id 为空无法查询");
        }
        Integer count = platformMessageMapper.selectCount(Wrappers.<PlatformMessagePO>lambdaQuery()
                .eq(PlatformMessagePO::getRelatedMessageId, announcementId)
                .eq(PlatformMessagePO::getMessageType, ANNOUNCEMENT_MSG.getMessageName())
                .eq(PlatformMessagePO::getIsRead, true));
        return Long.valueOf(count);
    }

}
