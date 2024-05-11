package com.scnujxjy.backendpoint.service.platform_message;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.constant.enums.RedisKeysEnum;
import com.scnujxjy.backendpoint.constant.enums.announceMsg.AnnounceAttachmentEnum;
import com.scnujxjy.backendpoint.constant.enums.announceMsg.AnnounceMsgUserTypeEnum;
import com.scnujxjy.backendpoint.constant.enums.announceMsg.AnnouncementMsgStatusEnum;
import com.scnujxjy.backendpoint.constant.enums.course_learning.CourseAttachementsEnum;
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
import com.scnujxjy.backendpoint.model.bo.platform_message.ManagerInfoBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.*;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.AnnouncementMessageVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.AttachmentVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.StudentStatusAllVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
import com.scnujxjy.backendpoint.util.MinioUtil;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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

    @Resource
    private StudentStatusService studentStatusService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Resource
    private MinioService minioService;

    @Resource
    private AttachmentService attachmentService;

    @Value("${minio.systemCommonBucket}")
    private String systemCommonBucket;


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
                        .username(StpUtil.getLoginIdAsString())
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

    @Transactional(rollbackFor = RuntimeException.class)
    public void insertPlatformMsgForUserList(AnnouncementMessageUsersRO announcementMessageRO,
                                             AnnouncementMessagePO announcementMessagePO,
                                             List<String> userNameList){
        for(String username : userNameList){
            PlatformUserPO platformUserPO = platformUserMapper.selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                    .eq(PlatformUserPO::getUsername, username));
            if(platformUserPO == null){
                announcementMessagePO.setRemark("公告群体发布失败");
                int i = getBaseMapper().updateById(announcementMessagePO);
                throw new RuntimeException("该账户不存在 创建公告失败");
            }
            PlatformMessagePO platformMessagePO = new PlatformMessagePO()
                    .setMessageType(ANNOUNCEMENT_MSG.getMessageName())
                    .setUserId(String.valueOf(platformUserPO.getUserId()))
                    .setRelatedMessageId(announcementMessagePO.getId())
                    .setIsPopup(announcementMessageRO.getIsPopup() == null ? "N"
                            : announcementMessageRO.getIsPopup() ? "Y" : "N")
                    ;
            int insert = platformMessageMapper.insert(platformMessagePO);
            if(insert < 0){
                announcementMessagePO.setRemark("公告群体发布失败");
                int i = getBaseMapper().updateById(announcementMessagePO);
                throw new RuntimeException("插入用户公告消息失败 创建公告失败");
            }
        }
        announcementMessagePO.setRemark("公告群体发布成功");
        int i = getBaseMapper().updateById(announcementMessagePO);
    }

    /**
     * 该方法 是一个异步方法 用来 生成用户群体的公告消息
     * @param announcementMessageRO
     */
    @Async
    protected void generatePlatformUserAnnouncementMsg(AnnouncementMessageUsersRO announcementMessageRO,
                                                       AnnouncementMessagePO announcementMessagePO){
        if(announcementMessageRO.getUserNameList() != null &&
                !announcementMessageRO.getUserNameList().isEmpty()){
            // 单个挑选出来的用户群体
            log.info("开始生成公告 , 用户群体筛选参数为 " + announcementMessageRO.getUserNameList());
            List<String> userNameList = announcementMessageRO.getUserNameList();
            insertPlatformMsgForUserList(announcementMessageRO, announcementMessagePO, userNameList);
        }else{
            if(AnnounceMsgUserTypeEnum.MANAGER.getUserType().equals(announcementMessageRO.getUserType())){
                // 当用户群体为 管理员时  根据 这个去筛选
                ManagerRO managerRO = (ManagerRO)announcementMessageRO.getParsedAnnouncementMsgUserFilterRO();
                log.info("开始生成公告 , 用户群体筛选参数为 " + managerRO);
            }else if(AnnounceMsgUserTypeEnum.OLD_STUDENT.getUserType().equals(announcementMessageRO.getUserType())){
                // 当用户群体为 在籍生时  根据 这个去筛选
            }else if(AnnounceMsgUserTypeEnum.NEW_STUDENT.getUserType().equals(announcementMessageRO.getUserType())){
                // 当用户群体为 新生时  根据 这个去筛选
            }
        }

    }

    /**
     * 创建公告 升级版
     * @param announcementMessageRO
     * @return
     */
    @Transactional
    public SaResult createAnnouncementMsg(AnnouncementMessageUsersRO announcementMessageRO) {
        String loginIdAsString = StpUtil.getLoginIdAsString();
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                .eq(PlatformUserPO::getUsername, loginIdAsString));

        AnnouncementMessagePO announcementMessagePO = new AnnouncementMessagePO()
                .setTitle(announcementMessageRO.getTitle())
                .setContent(announcementMessageRO.getContent())
                .setStatus(announcementMessageRO.getStatus())
                .setName(platformUserPO.getName())
                .setFilterArgs(announcementMessageRO.getParsedAnnouncementMsgUserFilterRO().filterArgs())
                .setUserId(platformUserPO.getUserId())
                .setDueDate(announcementMessageRO.getDueDate() != null
                        ? announcementMessageRO.getDueDate() : null)
                ;
        int insert = getBaseMapper().insert(announcementMessagePO);

        if(insert > 0){
            // 自己不需要收到该公告消息  用异步方法去一个个写 用户群体 公告映射记录
            announcementMessagePO.setRemark("生成公告中");
//            PlatformMessagePO platformMessagePO = new PlatformMessagePO()
//                    .setMessageType(ANNOUNCEMENT_MSG.getMessageName())
//                    .setRelatedMessageId(announcementMessagePO.getId())
//                    .set
//                    .setIsPopup(announcementMessageRO.getIsPopup() == null ? "N"
//                            : announcementMessageRO.getIsPopup() ? "Y" : "N")
//                    ;
            if(announcementMessageRO.getAnnouncementAttachments() != null && !announcementMessageRO.getAnnouncementAttachments().isEmpty()){
                int order = 1;
                List<Long> attachmentPOList = new ArrayList<>();
                for (MultipartFile multipartFile : announcementMessageRO.getAnnouncementAttachments()) {
                    log.info("收到了这些文件 " + multipartFile.getOriginalFilename());
                    log.info("\n文件的信息 " + multipartFile.getContentType() + " " + multipartFile.getSize());
                    try {
                        String minioUrl = AnnounceAttachmentEnum.COMMON_ANNOUNCEMENT_MSG_PREFIX
                                .getSystemArg()
                                + "/" + StpUtil.getLoginIdAsString()
                                + "/" + announcementMessagePO.getName() + "-"
                                + new Date() + "-" + multipartFile.getOriginalFilename();
                        try (InputStream is = multipartFile.getInputStream()) {
                            minioService.uploadStreamToMinio(is, minioUrl, systemCommonBucket);
                        } catch (IOException e) {
                            log.error("处理文件上传时出错", e);
                            return ResultCode.ANNOUNCEMENT_MSG_FAIL6.generateErrorResultInfo();
                        }
                        AttachmentPO attachmentPO = new AttachmentPO()
                                .setAttachmentName(multipartFile.getOriginalFilename())
                                .setAttachmentMinioPath(systemCommonBucket + "/" + minioUrl)
                                .setAttachmentSize(multipartFile.getSize())
                                .setAttachmentOrder(order)
                                .setRelatedId(announcementMessagePO.getId())
                                .setAttachmentType(ScnuXueliTools.getFileExtension(multipartFile))
                                .setUsername(StpUtil.getLoginIdAsString());
                        int insert1 = attachmentService.getBaseMapper().insert(attachmentPO);
                        if (insert1 <= 0) {
                            return ResultCode.DATABASE_INSERT_ERROR.generateErrorResultInfo();
                        }
                        attachmentPOList.add(attachmentPO.getId());
                        order += 1;
                    } catch (Exception e) {
                        log.error(StpUtil.getLoginIdAsString() + "上传公告消息附件失败 " + e);
                        return ResultCode.ANNOUNCEMENT_MSG_FAIL6.generateErrorResultInfo();
                    }
                }

                announcementMessagePO.setAttachmentIds(attachmentPOList);
                int i = getBaseMapper().updateById(announcementMessagePO);
            }
            // 无附件 不需要更新
        }else{
            log.error("发布公告 公告消息插入数据库失败");
            return ResultCode.ANNOUNCEMENT_MSG_FAIL3.generateErrorResultInfo();
        }

        generatePlatformUserAnnouncementMsg(announcementMessageRO, announcementMessagePO);
        log.info("异步生成平台用户公告，再去将附件信息更新");
        return SaResult.ok("成功发布公告");
    }

    public PageVO getUsersInfo(PlatformUserFilterRO platformUserFilterRO) {

        if(AnnounceMsgUserTypeEnum.MANAGER.getUserType().equals(platformUserFilterRO.getUserType())){
            // 访问 redis 获取管理员信息 并且根据 RO 参数 来 做分页查询
            try{
                return getManagersInfo(platformUserFilterRO);
            }catch (Exception e){
                log.error("获取管理员群体信息失败 " + e);
            }

        }else if(AnnounceMsgUserTypeEnum.NEW_STUDENT.getUserType().equals(platformUserFilterRO.getUserType())){
            NewStudentRO newStudentRO = (NewStudentRO) platformUserFilterRO.getParsedAnnouncementMsgUserFilterRO();
            List<AdmissionInformationPO> admissionInformationPOList = admissionInformationMapper
                    .getAdmissionInformationByAnnouncementMsg(newStudentRO,
                            (platformUserFilterRO.getPageNumber()-1) * platformUserFilterRO.getPageSize(),
                            platformUserFilterRO.getPageSize());
            Long admissionInformationPOListCount = admissionInformationMapper
                    .getAdmissionInformationByAnnouncementMsgCount(newStudentRO);
            // 构建分页对象
            PageVO<AdmissionInformationPO> pageVO = new PageVO<>();
            pageVO.setRecords(admissionInformationPOList);
            pageVO.setSize(platformUserFilterRO.getPageSize());
            pageVO.setTotal(admissionInformationPOListCount);

            // 正确计算总页数，考虑余数
            long totalRecords = admissionInformationPOListCount;
            long pageSize = platformUserFilterRO.getPageSize();
            long totalPages = (totalRecords + pageSize - 1) / pageSize; // 向上取整处理余数

            pageVO.setPages(totalPages);
            pageVO.setCurrent(platformUserFilterRO.getPageNumber());


            return pageVO;
        }else if(AnnounceMsgUserTypeEnum.OLD_STUDENT.getUserType().equals(platformUserFilterRO.getUserType())){
            // 访问 学籍信息表 和 个人信息表 来访问旧生
            OldStudentRO oldStudentRO = (OldStudentRO) platformUserFilterRO.getParsedAnnouncementMsgUserFilterRO();

            List<StudentStatusAllVO> studentStatusAllVOList = studentStatusService.getBaseMapper()
                    .getAnnouncementMsgUsers(oldStudentRO,
                            (platformUserFilterRO.getPageNumber()-1) * platformUserFilterRO.getPageSize(),
                            platformUserFilterRO.getPageSize());
            Long studentStatusAllVOListCount = studentStatusService.getBaseMapper()
                    .getAnnouncementMsgUsersCount(oldStudentRO);

            // 构建分页对象
            PageVO<StudentStatusAllVO> pageVO = new PageVO<>();
            pageVO.setRecords(studentStatusAllVOList);
            pageVO.setSize(platformUserFilterRO.getPageSize());
            pageVO.setTotal(studentStatusAllVOListCount);

            // 正确计算总页数，考虑余数
            long totalRecords = studentStatusAllVOListCount;
            long pageSize = platformUserFilterRO.getPageSize();
            long totalPages = (totalRecords + pageSize - 1) / pageSize; // 向上取整处理余数

            pageVO.setPages(totalPages);
            pageVO.setCurrent(platformUserFilterRO.getPageNumber());

            return pageVO;
        }
        log.info("异常的 用户类型 " + platformUserFilterRO.getUserType());
        return null;
    }


    private PageVO<ManagerInfoBO> getManagersInfo(PlatformUserFilterRO filterRO) throws Exception {
        // 解析筛选实体
        filterRO.parseUserFilter();
        ManagerRO managerRO = (ManagerRO) filterRO.getParsedAnnouncementMsgUserFilterRO();

        // 从Redis获取数据
        String key = RedisKeysEnum.PLATFORM_MANAGER_INFO.getRedisKeyOrPrefix();
        List<ManagerInfoBO> allManagers = (List<ManagerInfoBO>) redisTemplate.opsForValue().get(key);

        // 过滤数据
        List<ManagerInfoBO> filteredManagers = allManagers.stream()
                .filter(manager -> {
                    boolean matches = true;
                    try{
                        if (StringUtils.isNotBlank(managerRO.getUsername())) {
                            matches &= manager.getUsername().equals(managerRO.getUsername());
                        }
                        if (StringUtils.isNotBlank(managerRO.getName())) {
                            if(StringUtils.isBlank(manager.getName()) ){
                                matches = false;
                            }else{
                                matches &= manager.getName().equals(managerRO.getName());
                            }

                        }
                        if (StringUtils.isNotBlank(managerRO.getPhoneNumber())) {
                            if(StringUtils.isBlank(manager.getPhoneNumber()) ){
                                matches = false;
                            }else{
                                matches &= manager.getPhoneNumber().equals(managerRO.getPhoneNumber());
                            }

                        }
                        if (StringUtils.isNotBlank(managerRO.getWorkNumber())) {
                            if(StringUtils.isBlank(manager.getWorkNumber()) ){
                                matches = false;
                            }else{
                                matches &= manager.getWorkNumber().equals(managerRO.getWorkNumber());
                            }
                        }
                        if (StringUtils.isNotBlank(managerRO.getIdNumber())) {
                            if(StringUtils.isBlank(manager.getIdNumber()) ){
                                matches = false;
                            }else{
                                matches &= manager.getIdNumber().equals(managerRO.getIdNumber());
                            }
                        }


                        if (managerRO.getDepartment() != null && !managerRO.getDepartment().isEmpty()) {
                            if(manager.getDepartment() == null){
                                matches = false;
                            }else{
                                matches &= manager.getDepartment().equals(managerRO.getDepartment());
                            }
                        }

                        if (managerRO.getCollegeName() != null && !managerRO.getCollegeName().isEmpty()) {
                            if(manager.getCollegeName() == null){
                                matches = false;
                            }else{
                                matches &= manager.getCollegeName().equals(managerRO.getCollegeName());
                            }
                        }

                        if (managerRO.getTeachingPointName() != null && !managerRO.getTeachingPointName().isEmpty()) {
                            if(manager.getTeachingPointName() == null){
                                matches = false;
                            }else{
                                matches &= manager.getTeachingPointName().equals(managerRO.getTeachingPointName());
                            }
                        }
                        // 可以根据需要继续添加其他字段的检查

                    }catch (Exception e){
                        log.error("获取筛选参数条件 失败 " + e);
                        matches = false;
                    }
                    return matches;
                })
                .collect(Collectors.toList());


        // 分页计算
        int totalItems = filteredManagers.size();
        int pageSize = filterRO.getPageSize().intValue();
        int page = filterRO.getPageNumber().intValue() - 1; // PageRequest是从0开始计数
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        // 创建分页内容
        List<ManagerInfoBO> pageContent = (fromIndex < toIndex) ?
                filteredManagers.subList(fromIndex, toIndex) : Collections.emptyList();

        // 构建分页对象
        PageVO<ManagerInfoBO> pageVO = new PageVO<>();
        pageVO.setRecords(pageContent);
        pageVO.setSize(Long.valueOf(pageSize));
        pageVO.setTotal(Long.valueOf(totalItems));
        pageVO.setPages(Long.valueOf((totalItems + pageSize - 1) / pageSize));
        pageVO.setCurrent(Long.valueOf(page + 1));


        return pageVO;
    }

    /**
     * 删除公告消息
     * @param announcementMessagePO
     * @return
     */
    public SaResult deleteMsg(AnnouncementMessagePO announcementMessagePO) {
        List<Long> attachmentIds = announcementMessagePO.getAttachmentIds();
        if(attachmentIds != null && attachmentIds.size() > 0){
            // 删除附件信息
            for(Long id : attachmentIds){
                AttachmentPO attachmentPO = attachmentService.getBaseMapper().selectById(id);
                minioService.deleteFileByAbsolutePath(attachmentPO.getAttachmentMinioPath());
                int i = attachmentService.getBaseMapper().deleteById(attachmentPO.getId());
                if(i <= 0){
                    return ResultCode.ANNOUNCEMENT_MSG_FAIL7.generateErrorResultInfo();
                }
            }
        }

        // 删除公告消息本身
        int i = getBaseMapper().deleteById(announcementMessagePO.getId());
        if(i > 0){
            return SaResult.ok("删除成功");
        }else{
            return ResultCode.ANNOUNCEMENT_MSG_FAIL8.generateErrorResultInfo();
        }
    }

    /**
     * 获取登录用户的发布者的公告消息
     * @param platformUserPO
     * @return
     */
    public SaResult getUserCreatedAnnouncementMsgs(PlatformUserPO platformUserPO) {
        List<AnnouncementMessagePO> announcementMessagePOS = getBaseMapper().selectList(new LambdaQueryWrapper<AnnouncementMessagePO>()
                .eq(AnnouncementMessagePO::getUserId, platformUserPO.getUserId()));

        List<AnnouncementMessageVO> announcementMessageVOList = new ArrayList<>();
        for(AnnouncementMessagePO announcementMessagePO : announcementMessagePOS){
            AnnouncementMessageVO announcementMessageVO = new AnnouncementMessageVO();
            BeanUtils.copyProperties(announcementMessagePO, announcementMessageVO);

            List<AttachmentVO> attachmentVOList = new ArrayList<>();
            if(announcementMessagePO.getAttachmentIds() !=  null &&
                    !announcementMessagePO.getAttachmentIds().isEmpty()){
                List<Long> attachmentIds = announcementMessagePO.getAttachmentIds();
                for(Long id : attachmentIds){
                    AttachmentPO attachmentPO = attachmentService.getById(id);
                    AttachmentVO attachmentVO = new AttachmentVO()
                            .setId(attachmentPO.getId())
                            .setRelatedId(attachmentPO.getRelatedId())
                            .setAttachmentType(attachmentPO.getAttachmentType())
                            .setAttachmentOrder(attachmentPO.getAttachmentOrder())
                            .setAttachmentMinioPath(attachmentPO.getAttachmentMinioPath())
                            .setAttachmentName(attachmentPO.getAttachmentName())
                            .setAttachmentSize(attachmentPO.getAttachmentSize())
                            .setUsername(attachmentPO.getUsername())
                            ;
                    attachmentVOList.add(attachmentVO);
                }
                announcementMessageVO.setAttachmentVOS(attachmentVOList);
            }

            announcementMessageVOList.add(announcementMessageVO);
        }

        return SaResult.ok("成功获取创建的公告消息").setData(announcementMessageVOList);
    }

    /**
     * 更新公告消息
     *
     * 更新公告消息 依赖前端返回的 公告已有附件集合 这个可以让用户对已有的附件无需重复上传
     * announcementAttachmentIds
     *
     * @param announcementMessageRO
     * @return
     */
    public SaResult updateAnnouncementMsg(AnnouncementMessageUsersRO announcementMessageRO) {
        Long announceMsgId = announcementMessageRO.getAnnounceMsgId();
        // 获取原来的公告消息
        AnnouncementMessagePO announcementMessagePO = getBaseMapper().selectOne(new LambdaQueryWrapper<AnnouncementMessagePO>()
                .eq(AnnouncementMessagePO::getId, announceMsgId));

        if(announcementMessagePO == null){
            return ResultCode.ANNOUNCEMENT_MSG_FAIL15.generateErrorResultInfo();
        }

        // 看标题是否有替换 有的话 直接替换
        if(!announcementMessagePO.getTitle().equals(announcementMessageRO.getTitle())){
            announcementMessagePO.setTitle(announcementMessageRO.getTitle());
        }

        // 看公告内容是否有变化 有的话直接替换
        if(!announcementMessagePO.getContent().equals(announcementMessageRO.getContent())){
            announcementMessagePO.setContent(announcementMessageRO.getContent());
        }

        // 看公告内容是否有变化，有的话直接替换
        // 这种方式能处理 一个 为 null 一个不为 null 的情况
        if (!Objects.equals(announcementMessagePO.getDueDate(),
                announcementMessageRO.getDueDate())) {
            announcementMessagePO.setDueDate(announcementMessageRO.getDueDate());
        }

        // 看公告状态是否有变化 有的话直接替换
        if(!Objects.equals(announcementMessagePO.getStatus(),
                announcementMessageRO.getStatus())){
            announcementMessagePO.setStatus(announcementMessageRO.getStatus());
        }

        // 看公告发布者是否有变化 有的话直接替换
        String loginIdAsString = StpUtil.getLoginIdAsString();
        PlatformUserPO platformUserPO = platformUserMapper.selectByUserName(loginIdAsString);
        if(!platformUserPO.getUserId().equals(announcementMessagePO.getUserId())){
            announcementMessagePO.setUserId(platformUserPO.getUserId());
            announcementMessagePO.setName(platformUserPO.getName());
        }

        // 更新公告的筛选群体参数
        String filterArgs = announcementMessageRO.
                getParsedAnnouncementMsgUserFilterRO().filterArgs();
        if(!filterArgs.equals(announcementMessagePO.getFilterArgs())){
            announcementMessagePO.setFilterArgs(filterArgs);
        }

        // 根据前端传递过来的 附件 ID 集合 来更新附件信息
        List<Long> attachmentIds = announcementMessagePO.getAttachmentIds();
        List<Long> announcementAttachmentIds = announcementMessageRO.getAnnouncementAttachmentIds();
        // 计算 announcementAttachmentIds 存在 但是 attachmentIds 不存在的附件 ID
        List<Long> difference = calculateDifference(announcementAttachmentIds, attachmentIds);


        // 删除差集里面的 附件信息
        for(Long deleteId : difference){
            AttachmentPO deleteAttachment = attachmentService.getById(deleteId);
            minioService.deleteFileByAbsolutePath(deleteAttachment.getAttachmentMinioPath());
            attachmentService.getBaseMapper().deleteById(deleteId);
        }

        // 将 attachmentIds 的东西保存 即可
        // 再将 新的附件 进行写入就行 announcementAttachments 并且进行排序
        if(announcementMessageRO.getAnnouncementAttachments() != null && !announcementMessageRO.getAnnouncementAttachments().isEmpty()){
            int order = announcementAttachmentIds.size() + 1;
            List<Long> attachmentPOList = new ArrayList<>();
            for (MultipartFile multipartFile : announcementMessageRO.getAnnouncementAttachments()) {
                log.info("收到了这些文件 " + multipartFile.getOriginalFilename());
                log.info("\n文件的信息 " + multipartFile.getContentType() + " " + multipartFile.getSize());
                try {
                    String minioUrl = AnnounceAttachmentEnum.COMMON_ANNOUNCEMENT_MSG_PREFIX
                            .getSystemArg()
                            + "/" + StpUtil.getLoginIdAsString()
                            + "/" + announcementMessagePO.getName() + "-"
                            + new Date() + "-" + multipartFile.getOriginalFilename();
                    try (InputStream is = multipartFile.getInputStream()) {
                        minioService.uploadStreamToMinio(is, minioUrl, systemCommonBucket);
                    } catch (IOException e) {
                        log.error("处理文件上传时出错", e);
                        return ResultCode.ANNOUNCEMENT_MSG_FAIL6.generateErrorResultInfo();
                    }
                    AttachmentPO attachmentPO = new AttachmentPO()
                            .setAttachmentName(multipartFile.getOriginalFilename())
                            .setAttachmentMinioPath(systemCommonBucket + "/" + minioUrl)
                            .setAttachmentSize(multipartFile.getSize())
                            .setAttachmentOrder(order)
                            .setRelatedId(announcementMessagePO.getId())
                            .setAttachmentType(ScnuXueliTools.getFileExtension(multipartFile))
                            .setUsername(StpUtil.getLoginIdAsString());
                    int insert1 = attachmentService.getBaseMapper().insert(attachmentPO);
                    if (insert1 <= 0) {
                        return ResultCode.DATABASE_INSERT_ERROR.generateErrorResultInfo();
                    }
                    attachmentPOList.add(attachmentPO.getId());
                    order += 1;
                } catch (Exception e) {
                    log.error(StpUtil.getLoginIdAsString() + "上传公告消息附件失败 " + e);
                    return ResultCode.ANNOUNCEMENT_MSG_FAIL6.generateErrorResultInfo();
                }
            }

            // 更新一下 原来两个附件的顺序
            int newIndex = 1;
            List<Long> originList = new ArrayList<>();
            for(Long originId: announcementAttachmentIds){
                AttachmentPO byId = attachmentService.getById(originId);
                byId.setAttachmentOrder(newIndex);
                boolean b = attachmentService.updateById(byId);
                newIndex += 1;
                originList.add(originId);
            }

            // 创建一个新的列表用于存放组合后的结果

            // 先添加 originList 中的所有元素
            List<Long> combinedList = new ArrayList<>(originList);

            // 然后添加 attachmentPOList 中的所有元素的 ID
            // 假设 AttachmentPO 有一个 getId() 方法返回 Long 类型的 ID
            combinedList.addAll(attachmentPOList);

            announcementMessagePO.setAttachmentIds(combinedList);
            int i = getBaseMapper().updateById(announcementMessagePO);
        }



        return SaResult.ok("更新公告消息成功");
    }

    private List<Long> calculateDifference(List<Long> attachmentIds, List<Long> announcementAttachmentIds) {
        if (announcementAttachmentIds == null) {
            // 如果 announcementAttachmentIds 为 null，则差集不存在，返回空列表或 null
            return null; // 或者 new ArrayList<>();
        }

        if (attachmentIds == null) {
            // 如果 attachmentIds 为 null，则差集是 announcementAttachmentIds 本身
            return new ArrayList<>(announcementAttachmentIds);
        }

        // 创建一个新列表以存储差集
        List<Long> difference = new ArrayList<>(announcementAttachmentIds);
        // 移除所有 attachmentIds 中存在的元素
        difference.removeAll(attachmentIds);

        return difference;
    }

}
