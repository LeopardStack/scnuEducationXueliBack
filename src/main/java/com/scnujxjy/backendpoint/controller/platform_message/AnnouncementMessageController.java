package com.scnujxjy.backendpoint.controller.platform_message;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnujxjy.backendpoint.constant.enums.announceMsg.AnnounceAttachmentEnum;
import com.scnujxjy.backendpoint.constant.enums.SystemEnum;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.basic.GlobalConfigPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.*;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.AnnouncementMessageVO;
import com.scnujxjy.backendpoint.service.admission_information.AdmissionInformationService;
import com.scnujxjy.backendpoint.service.basic.GlobalConfigService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.platform_message.AnnouncementMessageService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.annotations.UserFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 消息表
 *
 * @author 谢辉龙
 * @since 2023-09-23
 */
@RestController
@RequestMapping("/announcement-message")
@Slf4j
public class AnnouncementMessageController {

    @Resource
    private AnnouncementMessageService announcementMessageService;

    @Resource
    private MinioService minioService;

    @Resource
    private GlobalConfigService globalConfigService;

    @Resource
    private AdmissionInformationService admissionInformationService;

    // 公告模块重构 支持全系统所有用户群体的公告发布 编辑 保存草稿
    @PostMapping("/create_announcement")
    public SaResult createAnnouncement(@ModelAttribute AnnouncementMessageUsersRO announcementMessageRO) {
        if (Objects.isNull(announcementMessageRO)) {
            return SaResult.error("公告参数缺失，无法插入");
        }
        // 参数的详细校验
        if(StringUtils.isBlank(announcementMessageRO.getTitle()) ){
            return ResultCode.ANNOUNCEMENT_MSG_FAIL1.generateErrorResultInfo();
        }

        List<MultipartFile> announcementAttachments = announcementMessageRO.getAnnouncementAttachments();
        if(announcementAttachments != null){
            if (announcementAttachments.size() > 3) {
                return ResultCode.ANNOUNCEMENT_MSG_FAIL2.generateErrorResultInfo();
            }

            for (MultipartFile file : announcementAttachments) {
                log.info("接收到的文件 " + file.getOriginalFilename());
                if (file.getSize() > 100_000_000) { // 文件大小超过100MB
                    return ResultCode.ANNOUNCEMENT_MSG_FAIL2.generateErrorResultInfo();
                }
            }
        }

        try{
            // 手动解析announcementMsgUserFilterRO
            announcementMessageRO.parseUserFilter();
        }catch (Exception e){
            log.error("解析筛选实体失败 " + e);
            return SaResult.error("解析筛选实体失败");
        }


        log.info("实体参数为 " + announcementMessageRO.getAnnouncementMsgUserFilterRO());

        AnnouncementMessageVO announcementMessageVO = announcementMessageService.createAnnouncementMsg(announcementMessageRO);
        return SaResult.data(announcementMessageVO);
    }

    /**
     * 获取用户群体信息
     * @param platformUserFilterRO
     * @return
     */
    @PostMapping("/get_users_info")
    public SaResult getUsersInfo(@RequestBody PlatformUserFilterRO platformUserFilterRO) {
        if (Objects.isNull(platformUserFilterRO)) {
            return SaResult.error("获取用户群体信息失败，筛选参数不能为空");
        }
        try{
            // 手动解析announcementMsgUserFilterRO
            platformUserFilterRO.parseUserFilter();
        }catch (Exception e){
            log.error("解析筛选实体失败 " + e);
            return SaResult.error("解析筛选实体失败");
        }

        log.info("获取用户群体的参数 " + platformUserFilterRO);

        return SaResult.ok("获取用户成功");
    }


    @GetMapping("/detail")
    public SaResult detail(Long announcementId) {
        if (Objects.isNull(announcementId)) {
            return SaResult.error("公告 id 缺失");
        }
        AnnouncementMessageVO announcementMessageVO = announcementMessageService.detailById(announcementId);
        return SaResult.data(announcementMessageVO);
    }

    @PostMapping("/create")
    public SaResult create(AnnouncementMessageRO announcementMessageRO, MultipartFile[] files) {
        if (Objects.isNull(announcementMessageRO)) {
            return SaResult.error("公告参数缺失，无法插入");
        }
        AnnouncementMessageVO announcementMessageVO = announcementMessageService.create(announcementMessageRO, files);
        return SaResult.data(announcementMessageVO);
    }

    @PostMapping("/page-query")
    public SaResult pageQuery(@RequestBody PageRO<AnnouncementMessageRO> announcementMessageROPageRO) {
        if (Objects.isNull(announcementMessageROPageRO)) {
            return SaResult.data("公告分页查询参数缺失");
        }
        PageVO<AnnouncementMessageVO> announcementMessageVOPageVO = announcementMessageService.pageQuery(announcementMessageROPageRO);
        return SaResult.data(announcementMessageVOPageVO);
    }

    @PutMapping("/update")
    public SaResult update(@RequestBody AnnouncementMessageRO announcementMessageRO) {
        if (Objects.isNull(announcementMessageRO)) {
            return SaResult.data("公告更新参数缺失");
        }
        AnnouncementMessageVO announcementMessageVO = announcementMessageService.update(announcementMessageRO);
        return SaResult.data(announcementMessageVO);
    }

    /**
     * 学生获取自己的录取公告，弹框显示
     *
     * @return 录取学生分页信息
     */
    @GetMapping("/get_admission_announcement_pop")
    public SaResult getAdmission_info() {
        String userName = StpUtil.getLoginIdAsString();
        String currentAdmissionYear = SystemEnum.NOW_NEW_STUDENT_GRADE.getSystemArg();
        // 查询数据
        AdmissionInformationVO admissionInformationVO = admissionInformationService.getAdmission_info();

        AdmissionInformationPO admissionInformationPO = admissionInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<AdmissionInformationPO>()
                .eq(AdmissionInformationPO::getIdCardNumber, userName)
                .eq(AdmissionInformationPO::getGrade, currentAdmissionYear)
        );
        if (admissionInformationPO == null) {
            return SaResult.error("获取公告信息失败").setCode(2001);
        }
        GlobalConfigPO globalConfigPO = globalConfigService.getBaseMapper().selectOne(new LambdaQueryWrapper<GlobalConfigPO>()
                .eq(GlobalConfigPO::getConfigKey, AnnounceAttachmentEnum.NOW_NEW_STUDENT_ADMISSION.getSystemArg()));
        String configValue = globalConfigPO.getConfigValue();
        String s = minioService.generatePresignedUrl(configValue);
        if (admissionInformationPO.getIsConfirmed().equals(1)) {
            // 已确认 不需要再弹框公告
            return SaResult.ok("已确认").setCode(201).setData(s);
        } else {
            return SaResult.ok().setData(s);
        }

    }

    /**
     * 获取下载公告
     *
     * @return 下载消息列表
     */
    @GetMapping("/get_download_announcement")
    public ResponseEntity<byte[]> downloadFile() {
        GlobalConfigPO globalConfigPO = globalConfigService.getBaseMapper().selectOne(new LambdaQueryWrapper<GlobalConfigPO>()
                .eq(GlobalConfigPO::getConfigKey, AnnounceAttachmentEnum.NOW_NEW_STUDENT_ADMISSION.getSystemArg()));
        String fileURL = globalConfigPO.getConfigValue();
        // 校验参数
        if (Objects.isNull(fileURL)) {
            throw new IllegalArgumentException("下载地址为空");
        }
        // 查询
        byte[] fileBytes = minioService.downloadFileFromMinio(fileURL);
        if (Objects.isNull(fileBytes)) {
            throw new RuntimeException("数据未找到");
        }
        log.info("下载的文件大小 " + fileBytes.length);
        // 设置响应头以下载文件
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(fileURL.substring(fileURL.lastIndexOf("/") + 1))
                .build());

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
}

