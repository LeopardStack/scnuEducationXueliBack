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
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.*;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.*;
import com.scnujxjy.backendpoint.model.vo.teaching_process.StudentStatusAllVO;
import com.scnujxjy.backendpoint.service.basic.AdminInfoService;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.college.CollegeInformationService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
import com.scnujxjy.backendpoint.util.MinioUtil;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.scnujxjy.backendpoint.config.AsyncConfig.getIOTaskExecutor;
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
    @Lazy
    private AnnouncementMessageService announcementMessageService;

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
    private PlatformMessageService platformMessageService;

    @Resource
    private TeachingPointInformationMapper teachingPointInformationMapper;


    @Resource
    private PlatformUserMapper platformUserMapper;

    @Resource
    private PersonalInfoMapper personalInfoMapper;

    @Resource
    private AdmissionInformationMapper admissionInformationMapper;

    @Resource
    private AdminInfoService adminInfoService;
    @Resource
    private CollegeInformationService collegeInformationService;

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

    @Transactional
    public void generatePlatformUserAnnouncementMsg(AnnouncementMessageUsersRO announcementMessageRO,
                                                       AnnouncementMessagePO announcementMessagePO){
        if(announcementMessageRO.getUserNameList() != null &&
                !announcementMessageRO.getUserNameList().isEmpty()){
            // 单个挑选出来的用户群体
            log.info("开始生成公告 , 用户群体筛选参数为 " + announcementMessageRO.getUserNameList());
            List<String> originalUserNames = announcementMessageRO.getUserNameList();

            // 使用 Set 检测并去除重复用户名
            Set<String> userNameSet = new HashSet<>();
            List<String> duplicateUserNames = new ArrayList<>();

            for (String userName : originalUserNames) {
                if (!userNameSet.add(userName)) {
                    duplicateUserNames.add(userName); // 如果添加失败，说明是重复的
                }
            }

            // 打印重复的用户名
            if (!duplicateUserNames.isEmpty()) {
                log.error("公告发布群体存在重复的用户名: " + duplicateUserNames);
            }
            insertPlatformMsgForUserList(announcementMessageRO, announcementMessagePO, new ArrayList<>(userNameSet));
        }else{
            if(AnnounceMsgUserTypeEnum.MANAGER.getUserType().equals(announcementMessageRO.getUserType())){
                // 当用户群体为 管理员时  根据 这个去筛选
                ManagerRO managerRO = (ManagerRO)announcementMessageRO.getParsedAnnouncementMsgUserFilterRO();
                log.info("开始生成公告 , 用户群体筛选参数为 " + managerRO);
                // 获取符合公告群体筛选条件的用户
                try {
                    List<ManagerInfoBO> allManagersInfo = getAllManagersInfo(managerRO);
                    // 收集所有用户名
                    List<String> allUserNames = allManagersInfo.stream()
                            .map(ManagerInfoBO::getUsername)
                            .collect(Collectors.toList());

                    // 使用 Set 去重，并收集重复的用户名
                    Set<String> uniqueUserNames = new HashSet<>();
                    List<String> duplicateUserNames = allUserNames.stream()
                            .filter(name -> !uniqueUserNames.add(name)) // 如果 name 不能被添加，则说明它是重复的
                            .collect(Collectors.toList());

                    // 打印重复的用户名
                    if (!duplicateUserNames.isEmpty()) {
                        log.error("公告发布群体存在重复的用户名: " + duplicateUserNames);
                    }

                    // 将去重后的用户名转换为 List
                    List<String> distinctUserNames = new ArrayList<>(uniqueUserNames);
                    insertPlatformMsgForUserList(announcementMessageRO, announcementMessagePO, distinctUserNames);
                }catch (Exception e){
                    log.error(StpUtil.getLoginIdAsString() + " 为公告发布群体生成公告消息失败 " + e);
                }
            }else if(AnnounceMsgUserTypeEnum.OLD_STUDENT.getUserType().equals(announcementMessageRO.getUserType())){
                // 当用户群体为 在籍生时  根据 这个去筛选
                OldStudentRO oldStudentRO = (OldStudentRO) announcementMessageRO.getParsedAnnouncementMsgUserFilterRO();

                List<StudentStatusAllVO> studentStatusAllVOList = studentStatusService.getBaseMapper()
                        .getAllAnnouncementMsgUsers(oldStudentRO);
                List<String> allUserNames = studentStatusAllVOList.stream().map(StudentStatusAllVO::getIdNumber)
                        .collect(Collectors.toList());


                Set<String> uniqueUserNames = new HashSet<>();
                List<String> duplicateUserNames = allUserNames.stream()
                        .filter(name -> !uniqueUserNames.add(name)) // 如果 name 不能被添加，则说明它是重复的
                        .collect(Collectors.toList());

                // 打印重复的用户名
                if (!duplicateUserNames.isEmpty()) {
                    log.error("公告发布群体存在重复的用户名: " + duplicateUserNames);
                }

                // 将去重后的用户名转换为 List
                List<String> distinctUserNames = new ArrayList<>(uniqueUserNames);
                insertPlatformMsgForUserList(announcementMessageRO, announcementMessagePO, distinctUserNames);
            }else if(AnnounceMsgUserTypeEnum.NEW_STUDENT.getUserType().equals(announcementMessageRO.getUserType())){
                // 当用户群体为 新生时  根据 这个去筛选
                NewStudentRO newStudentRO = (NewStudentRO) announcementMessageRO.getParsedAnnouncementMsgUserFilterRO();
                List<AdmissionInformationPO> admissionInformationPOList = admissionInformationMapper
                        .getAllAdmissionInformationByAnnouncementMsg(newStudentRO);
                List<String> allUserNames = admissionInformationPOList.stream().map(AdmissionInformationPO::getIdCardNumber)
                        .collect(Collectors.toList());

                Set<String> uniqueUserNames = new HashSet<>();
                List<String> duplicateUserNames = allUserNames.stream()
                        .filter(name -> !uniqueUserNames.add(name)) // 如果 name 不能被添加，则说明它是重复的
                        .collect(Collectors.toList());

                // 打印重复的用户名
                if (!duplicateUserNames.isEmpty()) {
                    log.error("公告发布群体存在重复的用户名: " + duplicateUserNames);
                }

                // 将去重后的用户名转换为 List
                List<String> distinctUserNames = new ArrayList<>(uniqueUserNames);
                insertPlatformMsgForUserList(announcementMessageRO, announcementMessagePO, distinctUserNames);
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
                .setUserType(announcementMessageRO.getUserType())
                .setFilterArgs(announcementMessageRO.getParsedAnnouncementMsgUserFilterRO().filterArgs())
                .setUserId(platformUserPO.getUserId())
                .setDueDate(announcementMessageRO.getDueDate() != null
                        ? announcementMessageRO.getDueDate() : null)
                ;
        int insert = getBaseMapper().insert(announcementMessagePO);

        if(insert > 0){
            // 自己不需要收到该公告消息  用异步方法去一个个写 用户群体 公告映射记录
            announcementMessagePO.setRemark("生成公告中");
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
        log.info("异步生成平台用户公告，再去将附件信息更新1");
        getIOTaskExecutor().execute(()->
                announcementMessageService.generatePlatformUserAnnouncementMsg(announcementMessageRO,
                        announcementMessagePO));;
        log.info("异步生成平台用户公告，再去将附件信息更新2");
        return SaResult.ok("成功发布公告");
    }

    /**
     * 获取不同的用户群体信息
     * @param platformUserFilterRO
     * @return
     */
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
                    try {
                        boolean matches = true;

                        if (StringUtils.isNotBlank(managerRO.getUsername())) {
                            matches &= manager.getUsername().equals(managerRO.getUsername());
                        }
                        if (StringUtils.isNotBlank(managerRO.getName())) {
                            matches &= StringUtils.isNotBlank(manager.getName()) && manager.getName().equals(managerRO.getName());
                        }
                        if (StringUtils.isNotBlank(managerRO.getPhoneNumber())) {
                            matches &= StringUtils.isNotBlank(manager.getPhoneNumber()) && manager.getPhoneNumber().equals(managerRO.getPhoneNumber());
                        }
                        if (StringUtils.isNotBlank(managerRO.getWorkNumber())) {
                            matches &= StringUtils.isNotBlank(manager.getWorkNumber()) && manager.getWorkNumber().equals(managerRO.getWorkNumber());
                        }
                        if (StringUtils.isNotBlank(managerRO.getIdNumber())) {
                            matches &= StringUtils.isNotBlank(manager.getIdNumber()) && manager.getIdNumber().equals(managerRO.getIdNumber());
                        }
                        if (managerRO.getDepartmentList() != null && !managerRO.getDepartmentList().isEmpty()) {
                            matches &= manager.getDepartment() != null && matchList(manager.getDepartment(), managerRO.getDepartmentList());
                        }
                        if (managerRO.getCollegeNameList() != null && !managerRO.getCollegeNameList().isEmpty()) {
                            matches &= manager.getCollegeName() != null && matchList(manager.getCollegeName(), managerRO.getCollegeNameList());
                        }
                        if (managerRO.getTeachingPointNameList() != null && !managerRO.getTeachingPointNameList().isEmpty()) {
                            matches &= manager.getTeachingPointName() != null && matchList(manager.getTeachingPointName(), managerRO.getTeachingPointNameList());
                        }
                        if (managerRO.getRoleNameList() != null && !managerRO.getRoleNameList().isEmpty()) {
                            matches &= manager.getRoleName() != null && matchList(manager.getRoleName(), managerRO.getRoleNameList());
                        }

                        // 可以根据需要继续添加其他字段的检查

                        return matches;
                    } catch (Exception e) {
                        log.error("获取筛选参数条件失败", e);
                        return false;
                    }
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

    private boolean matchList(String element, List<String> elementList) {
        return elementList.contains(element);
    }


    private List<ManagerInfoBO> getAllManagersInfo(ManagerRO managerRO) throws Exception {

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


                        if (managerRO.getDepartmentList() != null && !managerRO.getDepartmentList().isEmpty()) {
                            if(manager.getDepartment() == null){
                                matches = false;
                            }else{
                                matches &= matchList(manager.getDepartment(), managerRO.getDepartmentList());
                            }
                        }

                        if (managerRO.getCollegeNameList() != null && !managerRO.getCollegeNameList().isEmpty()) {
                            if(manager.getCollegeName() == null){
                                matches = false;
                            }else{
                                matches &= matchList(manager.getCollegeName(), managerRO.getCollegeNameList());
                            }
                        }

                        if (managerRO.getTeachingPointNameList() != null && !managerRO.getTeachingPointNameList().isEmpty()) {
                            if(manager.getTeachingPointName() == null){
                                matches = false;
                            }else{
                                matches &= matchList(manager.getTeachingPointName(), managerRO.getTeachingPointNameList());
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

        return filteredManagers;
    }

    /**
     * 删除公告消息
     * @param announcementMessagePO
     * @return
     */
    public SaResult deleteMsg(AnnouncementMessagePO announcementMessagePO) {

        // 先删除该公告消息关联的用户
        deleteAnnouncementRelatedUsers(announcementMessagePO.getId());


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

    @Async
    protected void deleteAnnouncementRelatedUsers(Long announcementMessagePOId){
        int delete = platformMessageMapper.delete(new LambdaQueryWrapper<PlatformMessagePO>()
                .eq(PlatformMessagePO::getMessageType, ANNOUNCEMENT_MSG.getMessageName())
                .eq(PlatformMessagePO::getRelatedMessageId, announcementMessagePOId)
        );
        log.error("删除了 公告 ID 为 " + announcementMessagePOId +  " 的公告消息用户记录 " + delete + " 条");
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

            // 反序列化 筛选对象参数
            if(announcementMessagePO.getUserType() != null && !announcementMessagePO.getUserType().isEmpty()){
                AnnouncementMsgUserFilterRO announcementMsgUserFilterRO  = null;
                if(AnnounceMsgUserTypeEnum.MANAGER.getUserType().equals(announcementMessagePO.getUserType())){
                    ManagerRO managerRO = new ManagerRO();
                    announcementMsgUserFilterRO = managerRO.parseFilterArgs(announcementMessagePO.getFilterArgs());
                }else if(AnnounceMsgUserTypeEnum.NEW_STUDENT.getUserType().equals(announcementMessagePO.getUserType())){
                    NewStudentRO newStudentRO = new NewStudentRO();
                    announcementMsgUserFilterRO = newStudentRO.parseFilterArgs(announcementMessagePO.getFilterArgs());
                }else if(AnnounceMsgUserTypeEnum.OLD_STUDENT.getUserType().equals(announcementMessagePO.getUserType())){
                    OldStudentRO oldStudentRO = new OldStudentRO();
                    announcementMsgUserFilterRO = oldStudentRO.parseFilterArgs(announcementMessagePO.getFilterArgs());
                }
                announcementMessageVO.setParsedAnnouncementMsgUserFilterRO(announcementMsgUserFilterRO);
            }else{
                // 为空 时 说明用户传递的是 userNameList
                List<PlatformMessagePO> platformMessagePOList = platformMessageMapper.selectList(new LambdaQueryWrapper<PlatformMessagePO>()
                        .eq(PlatformMessagePO::getMessageType, ANNOUNCEMENT_MSG.getMessageName())
                        .eq(PlatformMessagePO::getRelatedMessageId, announcementMessagePO.getId())
                );
                List<Long> userIdList = platformMessagePOList.stream()
                        .map(PlatformMessagePO::getUserId)
                        .map(Long::valueOf)
                        .collect(Collectors.toList());

                List<PlatformUserPO> platformUserPOS = platformUserMapper.selectPlatformUserList(new PlatformUserRO().setUserIdList(userIdList));
                List<String> usernameList = platformUserPOS.stream().map(PlatformUserPO::getUsername).collect(Collectors.toList());
                announcementMessageVO.setUsernameList(usernameList);
            }

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
        // 设置是否需要覆盖写用户 - 公告消息映射记录
        boolean overwriteIdent = false;
        String filterArgs = announcementMessageRO.
                getParsedAnnouncementMsgUserFilterRO().filterArgs();
        if(!filterArgs.equals(announcementMessagePO.getFilterArgs())){
            announcementMessagePO.setFilterArgs(filterArgs);
            overwriteIdent = true;
        }

        // 修改了弹框消息 比如由弹框改为非弹框
        boolean isPopupChangeIdent = false;
        // 创建一个分页对象，限制查询结果为 5 条记录
        Page<PlatformMessagePO> page = new Page<>(1, 5);

        // 执行分页查询
        Page<PlatformMessagePO> platformMessagePOPage = platformMessageMapper.selectPage(page, new LambdaQueryWrapper<PlatformMessagePO>()
                .eq(PlatformMessagePO::getMessageType, ANNOUNCEMENT_MSG.getMessageName())
                .eq(PlatformMessagePO::getRelatedMessageId, announcementMessagePO.getId())
        );

        // 获取分页查询的结果列表
        List<PlatformMessagePO> platformMessagePOList = platformMessagePOPage.getRecords();
        if (platformMessagePOList != null && !platformMessagePOList.isEmpty()) {
            // 生成一个随机索引
            Random random = new Random();
            int randomIndex = random.nextInt(platformMessagePOList.size());

            // 获取随机的一条记录
            PlatformMessagePO randomMessage = platformMessagePOList.get(randomIndex);

            // 处理随机取出的记录
            String isPopup = randomMessage.getIsPopup();
            if ((isPopup == null || isPopup.equals("N")) && announcementMessageRO.getIsPopup() != null &&
            announcementMessageRO.getIsPopup().equals(true)) {
                // 如果用户的 PlatformMessagePO 里面的弹框属性为 N 或者为 null 并且
                // announcementMessageRO.getIsPopup() 不为 null 并且它也不为 false 那么就要替换
                // 反之也要替换
                // 此时要设置用户的平台消息弹框属性为 Y
                isPopupChangeIdent = true;
            }else if(isPopup != null && isPopup.equals("Y") && (announcementMessageRO.getIsPopup() == null ||
                    announcementMessageRO.getIsPopup().equals(false))){
                // 此时要设置用户的平台消息弹框属性为 N
                isPopupChangeIdent = true;
            }
        }

        if(overwriteIdent){
            // 根据这个最新的群体 来更新群体的用户公告消息记录
            log.info("删除公告消息之前关联的用户消息记录");
            deleteAnnouncementRelatedUsers(announcementMessagePO.getId());
            log.info("更新修改后的公告消息之前关联的用户消息记录");
            generatePlatformUserAnnouncementMsg(announcementMessageRO, announcementMessagePO);
        }else{
            if(isPopupChangeIdent){
                // 仅更新用户-公告消息记录的 弹框属性
                announcementMessageService.updatePlatformUserAnnouncementMsg(announcementMessageRO, announcementMessagePO);
            }
        }


        // 根据前端传递过来的 附件 ID 集合 来更新附件信息
        List<Long> attachmentIds = announcementMessagePO.getAttachmentIds();
        List<Long> announcementAttachmentIds = announcementMessageRO.getAnnouncementAttachmentIds();

        if(announcementAttachmentIds == null){
            announcementAttachmentIds = new ArrayList<>();
        }

        // 计算 announcementAttachmentIds 存在 但是 attachmentIds 不存在的附件 ID
        List<Long> difference = calculateDifference(announcementAttachmentIds, attachmentIds);


        // 删除差集里面的 附件信息
        for(Long deleteId : difference){
            AttachmentPO deleteAttachment = attachmentService.getById(deleteId);
            if(deleteAttachment != null){
                minioService.deleteFileByAbsolutePath(deleteAttachment.getAttachmentMinioPath());
                attachmentService.getBaseMapper().deleteById(deleteId);
            }
        }

        // 将 attachmentIds 的东西保存 即可
        // 再将 新的附件 进行写入就行 announcementAttachments 并且进行排序
        if(announcementMessageRO.getAnnouncementAttachments() != null && !announcementMessageRO.getAnnouncementAttachments().isEmpty()){

            int order = announcementAttachmentIds.size() + 1;
            List<Long> attachmentPOList = new ArrayList<>();
            boolean fileIsValid = true;
            for (MultipartFile multipartFile : announcementMessageRO.getAnnouncementAttachments()) {
                log.info("收到了这些文件 " + multipartFile.getOriginalFilename());
                if(StringUtils.isBlank(multipartFile.getOriginalFilename())){
                    fileIsValid = false;
                    continue;
                }

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

    @Transactional
    public void updatePlatformUserAnnouncementMsg(AnnouncementMessageUsersRO announcementMessageRO,
                                                   AnnouncementMessagePO announcementMessagePO){
        List<PlatformMessagePO> platformMessagePOList = platformMessageMapper.selectList(new LambdaQueryWrapper<PlatformMessagePO>()
                .eq(PlatformMessagePO::getMessageType, ANNOUNCEMENT_MSG.getMessageName())
                .eq(PlatformMessagePO::getRelatedMessageId, announcementMessagePO.getId())
        );
        // 更新 isPopup 字段
        platformMessagePOList.forEach(platformMessagePO -> {
            platformMessagePO.setIsPopup(announcementMessageRO.getIsPopup() == null ? "N" :
                    announcementMessageRO.getIsPopup().equals(Boolean.TRUE) ? "Y" : "N");
        });

        boolean updateResult = platformMessageService.saveOrUpdateBatch(platformMessagePOList);
        log.info("更新了 " + platformMessagePOList.size() + " 条用户公告消息记录，更新结果 " + updateResult);
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

    /**
     * 获取不同用户群体的筛选项
     * @param userType
     * @return
     */
    public AnnouncementMsgFilterArgsVO getUserFilterItems(String userType) {
        AnnouncementMsgFilterArgsVO announcementMsgFilterArgsVO = new AnnouncementMsgFilterArgsVO();
        if(AnnounceMsgUserTypeEnum.MANAGER.getUserType().equals(userType)){
            List<String> departments = adminInfoService.getBaseMapper().getAllDepartments();
            List<String> collegeNames = collegeInformationService.getBaseMapper().getAllCollegeNames();
            List<String> teachingPointNames = teachingPointInformationMapper.getAllTeachingPointNames();
            announcementMsgFilterArgsVO.setDepartmentList(departments);
            announcementMsgFilterArgsVO.setCollegeNameList(collegeNames);
            announcementMsgFilterArgsVO.setTeachingPointNameList(teachingPointNames);
            return announcementMsgFilterArgsVO;
        }else if(AnnounceMsgUserTypeEnum.NEW_STUDENT.getUserType().equals(userType)){
            // 获取近五年的新生筛选项信息
            // 获取数据并移除 null 值
            List<String> collegeNames = admissionInformationMapper.selectDistinctCollegeList()
                    .stream()
                    .filter(name -> name != null)
                    .collect(Collectors.toList());
            List<String> majorNames = admissionInformationMapper.selectDistinctMajorNameList()
                    .stream()
                    .filter(name -> name != null)
                    .collect(Collectors.toList());
            List<String> levels = admissionInformationMapper.selectDistinctLevelList()
                    .stream()
                    .filter(name -> name != null)
                    .collect(Collectors.toList());
            List<String> studyForms = admissionInformationMapper.selectDistinctStudyFormList()
                    .stream()
                    .filter(name -> name != null)
                    .collect(Collectors.toList());
            List<String> teachingPointNames = admissionInformationMapper.selectDistinctTeachingPointNameList()
                    .stream()
                    .filter(name -> name != null)
                    .collect(Collectors.toList());

            List<String> grades = admissionInformationMapper.selectDistinctGradeList()
                    .stream()
                    .filter(name -> name != null)
                    .collect(Collectors.toList());

            announcementMsgFilterArgsVO.setNewStudentCollegeList(collegeNames);
            announcementMsgFilterArgsVO.setOldStudentMajorNameList(majorNames);
            announcementMsgFilterArgsVO.setNewStudentLevelList(levels);
            announcementMsgFilterArgsVO.setNewStudentStudyFormList(studyForms);
            announcementMsgFilterArgsVO.setNewStudentTeachingPointList(teachingPointNames);
            announcementMsgFilterArgsVO.setGradeList(grades);
            return announcementMsgFilterArgsVO;
        }else if(AnnounceMsgUserTypeEnum.OLD_STUDENT.getUserType().equals(userType)){
            // 获取近五年的旧生筛选项信息
            List<String> distinctColleges = studentStatusService.getBaseMapper().getDistinctColleges(new StudentStatusFilterRO());
            List<String> distinctMajorNames = studentStatusService.getBaseMapper().getDistinctMajorNames(new StudentStatusFilterRO());
            List<String> distinctLevels = studentStatusService.getBaseMapper().getDistinctLevels(new StudentStatusFilterRO());
            List<String> distinctStudyForms = studentStatusService.getBaseMapper().getDistinctStudyForms(new StudentStatusFilterRO());
            List<String> distinctGrades = studentStatusService.getBaseMapper().getDistinctGrades(new StudentStatusFilterRO());
            List<String> distinctTeachingPointNames = teachingPointInformationMapper.getAllTeachingPointNames();
            List<String> distinctAcademicStatuss = studentStatusService.getBaseMapper().getDistinctAcademicStatuss(new StudentStatusFilterRO());
            List<String> distinctStudyDurations = studentStatusService.getBaseMapper().getDistinctStudyDurations(new StudentStatusFilterRO());
            announcementMsgFilterArgsVO.setOldStudentCollegeList(distinctColleges);
            announcementMsgFilterArgsVO.setOldStudentMajorNameList(distinctMajorNames);
            announcementMsgFilterArgsVO.setOldStudentLevelList(distinctLevels);
            announcementMsgFilterArgsVO.setOldStudentStudyFormList(distinctStudyForms);
            announcementMsgFilterArgsVO.setGradeList(distinctGrades);
            announcementMsgFilterArgsVO.setOldStudentTeachingPointList(distinctTeachingPointNames);
            announcementMsgFilterArgsVO.setOldStudentAcademicStatusList(distinctAcademicStatuss);
            announcementMsgFilterArgsVO.setOldStudentStudyDurationList(distinctStudyDurations);
            return announcementMsgFilterArgsVO;
        }else{
            log.error("获取公告用户群体筛选项， 传入了错误的用户群体类型 " + userType);
            return null;
        }
    }

    @Resource
    private Executor taskExecutor;

    public AnnouncementMsgFilterArgsVO parallelGetUserFilterItems(String userType) {
        AnnouncementMsgFilterArgsVO announcementMsgFilterArgsVO = new AnnouncementMsgFilterArgsVO();
        if (AnnounceMsgUserTypeEnum.MANAGER.getUserType().equals(userType)) {
            List<String> departments = adminInfoService.getBaseMapper().getAllDepartments();
            List<String> collegeNames = collegeInformationService.getBaseMapper().getAllCollegeNames();
            List<String> teachingPointNames = teachingPointInformationMapper.getAllTeachingPointNames();
            announcementMsgFilterArgsVO.setDepartmentList(departments);
            announcementMsgFilterArgsVO.setCollegeNameList(collegeNames);
            announcementMsgFilterArgsVO.setTeachingPointNameList(teachingPointNames);
            return announcementMsgFilterArgsVO;
        } else if (AnnounceMsgUserTypeEnum.NEW_STUDENT.getUserType().equals(userType)) {
            CompletableFuture<List<String>> collegeNamesFuture = CompletableFuture.supplyAsync(() ->
                    admissionInformationMapper.selectDistinctCollegeList().stream().filter(name -> name != null).collect(Collectors.toList()), taskExecutor);
            CompletableFuture<List<String>> majorNamesFuture = CompletableFuture.supplyAsync(() ->
                    admissionInformationMapper.selectDistinctMajorNameList().stream().filter(name -> name != null).collect(Collectors.toList()), taskExecutor);
            CompletableFuture<List<String>> levelsFuture = CompletableFuture.supplyAsync(() ->
                    admissionInformationMapper.selectDistinctLevelList().stream().filter(name -> name != null).collect(Collectors.toList()), taskExecutor);
            CompletableFuture<List<String>> studyFormsFuture = CompletableFuture.supplyAsync(() ->
                    admissionInformationMapper.selectDistinctStudyFormList().stream().filter(name -> name != null).collect(Collectors.toList()), taskExecutor);
            CompletableFuture<List<String>> teachingPointNamesFuture = CompletableFuture.supplyAsync(() ->
                    admissionInformationMapper.selectDistinctTeachingPointNameList().stream().filter(name -> name != null).collect(Collectors.toList()), taskExecutor);
            CompletableFuture<List<String>> gradesFuture = CompletableFuture.supplyAsync(() ->
                    admissionInformationMapper.selectDistinctGradeList().stream().filter(name -> name != null).collect(Collectors.toList()), taskExecutor);

            CompletableFuture.allOf(collegeNamesFuture, majorNamesFuture, levelsFuture, studyFormsFuture, teachingPointNamesFuture, gradesFuture).join();

            try {
                announcementMsgFilterArgsVO.setNewStudentCollegeList(collegeNamesFuture.get());
                announcementMsgFilterArgsVO.setOldStudentMajorNameList(majorNamesFuture.get());
                announcementMsgFilterArgsVO.setNewStudentLevelList(levelsFuture.get());
                announcementMsgFilterArgsVO.setNewStudentStudyFormList(studyFormsFuture.get());
                announcementMsgFilterArgsVO.setNewStudentTeachingPointList(teachingPointNamesFuture.get());
                announcementMsgFilterArgsVO.setGradeList(gradesFuture.get());
            } catch (Exception e) {
                log.error("Error fetching new student filter items", e);
            }

            return announcementMsgFilterArgsVO;
        } else if (AnnounceMsgUserTypeEnum.OLD_STUDENT.getUserType().equals(userType)) {
            CompletableFuture<List<String>> distinctCollegesFuture = CompletableFuture.supplyAsync(() ->
                    studentStatusService.getBaseMapper().getDistinctColleges(new StudentStatusFilterRO()), taskExecutor);
            CompletableFuture<List<String>> distinctMajorNamesFuture = CompletableFuture.supplyAsync(() ->
                    studentStatusService.getBaseMapper().getDistinctMajorNames(new StudentStatusFilterRO()), taskExecutor);
            CompletableFuture<List<String>> distinctLevelsFuture = CompletableFuture.supplyAsync(() ->
                    studentStatusService.getBaseMapper().getDistinctLevels(new StudentStatusFilterRO()), taskExecutor);
            CompletableFuture<List<String>> distinctStudyFormsFuture = CompletableFuture.supplyAsync(() ->
                    studentStatusService.getBaseMapper().getDistinctStudyForms(new StudentStatusFilterRO()), taskExecutor);
            CompletableFuture<List<String>> distinctGradesFuture = CompletableFuture.supplyAsync(() ->
                    studentStatusService.getBaseMapper().getDistinctGrades(new StudentStatusFilterRO()), taskExecutor);
            CompletableFuture<List<String>> distinctTeachingPointNamesFuture = CompletableFuture.supplyAsync(() ->
                    teachingPointInformationMapper.getAllTeachingPointNames(), taskExecutor);
            CompletableFuture<List<String>> distinctAcademicStatusFuture = CompletableFuture.supplyAsync(() ->
                    studentStatusService.getBaseMapper().getDistinctAcademicStatuss(new StudentStatusFilterRO()), taskExecutor);
            CompletableFuture<List<String>> distinctStudyDurationsFuture = CompletableFuture.supplyAsync(() ->
                    studentStatusService.getBaseMapper().getDistinctStudyDurations(new StudentStatusFilterRO()), taskExecutor);

            CompletableFuture.allOf(distinctCollegesFuture, distinctMajorNamesFuture, distinctLevelsFuture, distinctStudyFormsFuture, distinctGradesFuture,
                    distinctTeachingPointNamesFuture, distinctAcademicStatusFuture, distinctStudyDurationsFuture).join();

            try {
                announcementMsgFilterArgsVO.setOldStudentCollegeList(distinctCollegesFuture.get());
                announcementMsgFilterArgsVO.setOldStudentMajorNameList(distinctMajorNamesFuture.get());
                announcementMsgFilterArgsVO.setOldStudentLevelList(distinctLevelsFuture.get());
                announcementMsgFilterArgsVO.setOldStudentStudyFormList(distinctStudyFormsFuture.get());
                announcementMsgFilterArgsVO.setGradeList(distinctGradesFuture.get());
                announcementMsgFilterArgsVO.setOldStudentTeachingPointList(distinctTeachingPointNamesFuture.get());
                announcementMsgFilterArgsVO.setOldStudentAcademicStatusList(distinctAcademicStatusFuture.get());
                announcementMsgFilterArgsVO.setOldStudentStudyDurationList(distinctStudyDurationsFuture.get());
            } catch (Exception e) {
                log.error("Error fetching old student filter items", e);
            }

            return announcementMsgFilterArgsVO;
        } else {
            log.error("获取公告用户群体筛选项，传入了错误的用户群体类型 " + userType);
            return null;
        }
    }

    /**
     * 获取用户自己发布的公告所涉及的群体
     * @param announcementMsgUsersRO
     * @param announcementMessagePO
     * @return
     */
    public SaResult getUserCreatedAnnouncementMsgsUsers(PageRO<AnnouncementMsgUsersRO> announcementMsgUsersRO,
                                                        AnnouncementMessagePO announcementMessagePO) {
        int pageNumber = announcementMsgUsersRO.getPageNumber().intValue();
        int pageSize = announcementMsgUsersRO.getPageSize().intValue();

        // 分页查询
        Page<PlatformMessagePO> page = new Page<>(pageNumber, pageSize);
        List<PlatformMessagePO> platformMessagePOS = platformMessageMapper.selectPage(page,
                new LambdaQueryWrapper<PlatformMessagePO>()
                        .eq(PlatformMessagePO::getMessageType, ANNOUNCEMENT_MSG.getMessageName())
                        .eq(PlatformMessagePO::getRelatedMessageId, announcementMessagePO.getId())
        ).getRecords();

        // 统计
        List<PlatformMessagePO> platformMessagePOS1 = platformMessageMapper.selectList(new LambdaQueryWrapper<PlatformMessagePO>()
                .eq(PlatformMessagePO::getMessageType, ANNOUNCEMENT_MSG.getMessageName())
                .eq(PlatformMessagePO::getRelatedMessageId, announcementMessagePO.getId())
        );
        long total = platformMessagePOS1.size();
        // 使用 Stream API 计算已读消息数量
        long readCount = platformMessagePOS1.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsRead())) // 过滤出已读消息，安全地检查 Boolean 值
                .count();
        long unreadCount = total - readCount;

        // 根据不同的用户类型 来获取用户的详细信息
        List<String> userIds = platformMessagePOS.stream().map(PlatformMessagePO::getUserId).collect(Collectors.toList());

        List<String> usernames = new ArrayList<>();
        for(String userId : userIds){
            PlatformUserPO platformUserPO = platformUserMapper.selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                    .eq(PlatformUserPO::getUserId, Long.parseLong(userId)));
            usernames.add(platformUserPO.getUsername());
        }

        if(AnnounceMsgUserTypeEnum.MANAGER.getUserType().equals(announcementMsgUsersRO.getEntity().getUserType())){
            // 管理员
            // 从Redis获取数据
            String key = RedisKeysEnum.PLATFORM_MANAGER_INFO.getRedisKeyOrPrefix();
            List<ManagerInfoBO> allManagers = (List<ManagerInfoBO>) redisTemplate.opsForValue().get(key);

            // 过滤数据
            List<ManagerInfoBO> filteredManagers = allManagers.stream()
                    .filter(manager -> {
                        boolean matches = true;
                        try{
                            if (!userIds.isEmpty()) {
                                matches &= matchList("" + manager.getUserId(), userIds);
                            }

                        }catch (Exception e){
                            log.error("获取筛选参数条件 失败 " + e);
                            matches = false;
                        }
                        return matches;
                    })
                    .collect(Collectors.toList());

            PageVO<ManagerInfoVO> pageVO = new PageVO<>();

            // 更新已读未读情况
            List<ManagerInfoVO> managerInfoVOList = new ArrayList<>();
            for(ManagerInfoBO managerInfoBO : filteredManagers){
                ManagerInfoVO managerInfoVO = new ManagerInfoVO();
                BeanUtils.copyProperties(managerInfoBO, managerInfoVO);
                Long userId = managerInfoBO.getUserId();
                PlatformMessagePO platformMessagePO = platformMessageMapper.selectOne(new LambdaQueryWrapper<PlatformMessagePO>()
                        .eq(PlatformMessagePO::getUserId, userId)
                        .eq(PlatformMessagePO::getMessageType, ANNOUNCEMENT_MSG.getMessageName())
                        .eq(PlatformMessagePO::getRelatedMessageId, announcementMessagePO.getId())
                );
                managerInfoVO.setRead(platformMessagePO.getIsRead());
            }

            pageVO.setSize(announcementMsgUsersRO.getPageSize());
            pageVO.setCurrent(announcementMsgUsersRO.getPageNumber());
            pageVO.setRecords(managerInfoVOList);
            pageVO.setTotal(total);

            AnnouncementMsgDetailVO<ManagerInfoVO> result = new AnnouncementMsgDetailVO<>();
            result.setUsers(pageVO);
            result.setTotal(total);
            result.setIsRead(readCount);
            result.setUnRead(unreadCount);

            return SaResult.ok("成功获取公告群体阅读公告情况").setData(result);

        }else if(AnnounceMsgUserTypeEnum.NEW_STUDENT.getUserType().equals(announcementMsgUsersRO.getEntity().getUserType())){
            // 新生
            List<AdmissionInformationVO> admissionInformationPOList = admissionInformationMapper
                    .getAllAdmissionInformationByAnnouncementMsgVO(
                            new NewStudentRO().setUsernames(usernames));


            PageVO<AdmissionInformationVO> pageVO = new PageVO<>();

            for(AdmissionInformationVO admissionInformationVO : admissionInformationPOList){
                Long userId = admissionInformationVO.getUserId();
                PlatformMessagePO platformMessagePO = platformMessageMapper.selectOne(new LambdaQueryWrapper<PlatformMessagePO>()
                        .eq(PlatformMessagePO::getUserId, userId)
                        .eq(PlatformMessagePO::getMessageType, ANNOUNCEMENT_MSG.getMessageName())
                        .eq(PlatformMessagePO::getRelatedMessageId, announcementMessagePO.getId())
                );
                admissionInformationVO.setRead(platformMessagePO.getIsRead());
            }

            pageVO.setSize(announcementMsgUsersRO.getPageSize());
            pageVO.setCurrent(announcementMsgUsersRO.getPageNumber());
            pageVO.setRecords(admissionInformationPOList);
            pageVO.setTotal(total);

            AnnouncementMsgDetailVO<AdmissionInformationVO> result = new AnnouncementMsgDetailVO<>();
            result.setUsers(pageVO);
            result.setTotal(total);
            result.setIsRead(readCount);
            result.setUnRead(unreadCount);

            return SaResult.ok("成功获取公告群体阅读公告情况").setData(result);
        }else if(AnnounceMsgUserTypeEnum.OLD_STUDENT.getUserType().equals(announcementMsgUsersRO.getEntity().getUserType())){
            // 旧生
            List<com.scnujxjy.backendpoint.model.vo.platform_message.StudentStatusAllVO> studentStatusAllVOList = studentStatusService.getBaseMapper()
                    .getAllAnnouncementMsgUsersVO(new OldStudentRO().setUsernames(usernames));
            PageVO<com.scnujxjy.backendpoint.model.vo.platform_message.StudentStatusAllVO> pageVO = new PageVO<>();

            for(com.scnujxjy.backendpoint.model.vo.platform_message.StudentStatusAllVO studentStatusAllVO :
                    studentStatusAllVOList){
                Long userId = studentStatusAllVO.getUserId();
                PlatformMessagePO platformMessagePO = platformMessageMapper.selectOne(new LambdaQueryWrapper<PlatformMessagePO>()
                        .eq(PlatformMessagePO::getUserId, userId)
                        .eq(PlatformMessagePO::getMessageType, ANNOUNCEMENT_MSG.getMessageName())
                        .eq(PlatformMessagePO::getRelatedMessageId, announcementMessagePO.getId())
                );
                studentStatusAllVO.setRead(platformMessagePO.getIsRead());
            }

            pageVO.setSize(announcementMsgUsersRO.getPageSize());
            pageVO.setCurrent(announcementMsgUsersRO.getPageNumber());
            pageVO.setRecords(studentStatusAllVOList);
            pageVO.setTotal(total);

            AnnouncementMsgDetailVO<com.scnujxjy.backendpoint.model.vo.platform_message.StudentStatusAllVO> result = new AnnouncementMsgDetailVO<>();
            result.setUsers(pageVO);
            result.setTotal(total);
            result.setIsRead(readCount);
            result.setUnRead(unreadCount);

            return SaResult.ok("成功获取公告群体阅读公告情况").setData(result);
        }

        return ResultCode.ANNOUNCEMENT_MSG_FAIL20.generateErrorResultInfo();
    }

    /**
     * 查看 userId 是否在集合里
     * @param element
     * @param elementList
     * @return
     */
    private boolean matchList(Long element, List<Long> elementList) {
        return elementList.contains(element);
    }
}
