package com.scnujxjy.backendpoint.controller.platform_message;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.AnnouncementMessageRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.AnnouncementMessageVO;
import com.scnujxjy.backendpoint.service.platform_message.AnnouncementMessageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 公告消息表
 *
 * @author 谢辉龙
 * @since 2023-09-23
 */
@RestController
@RequestMapping("/announcement-message")
public class AnnouncementMessageController {

    @Resource
    private AnnouncementMessageService announcementMessageService;

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
}

