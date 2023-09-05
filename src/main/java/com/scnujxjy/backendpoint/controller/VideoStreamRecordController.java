package com.scnujxjy.backendpoint.controller;


import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.model.ro.video_stream.VideoStreamRecordRO;
import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamRecordVO;
import com.scnujxjy.backendpoint.service.video_stream.VideoStreamRecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;

/**
 * <p>
 * 直播记录表 前端控制器
 * </p>
 *
 * @author leopard
 * @since 2023-08-21
 */
@RestController
@RequestMapping("/video-stream-record")
public class VideoStreamRecordController {

    @Resource
    private VideoStreamRecordService videoStreamRecordService;

    /**
     * 批量添加直播间
     *
     * @param videoStreamRecordROS 直播间信息
     * @return 添加后的频道信息
     */
    @PostMapping("/create/page")
    public SaResult createVideoStream(@RequestBody List<VideoStreamRecordRO> videoStreamRecordROS) {
        if (CollUtil.isEmpty(videoStreamRecordROS)) {
            throw dataMissError();
        }
        List<List<VideoStreamRecordVO>> generateVideoStream = videoStreamRecordService.generateVideoStream(videoStreamRecordROS);
        if (CollUtil.isEmpty(generateVideoStream)) {
            return SaResult.error().setMsg("创建失败，请联系管理员");
        }
        return SaResult.data(generateVideoStream);
    }

    /**
     * 根据id查询直播间信息
     *
     * @param id 直播间id
     * @return 直播间信息
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        VideoStreamRecordVO videoStreamRecordVO = videoStreamRecordService.detailById(id);
        if (Objects.isNull(videoStreamRecordVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(videoStreamRecordVO);
    }

    /**
     * 根据频道id关闭直播间
     *
     * @param channelId
     * @return
     */
    @GetMapping("/close")
    public SaResult closeByChannelId(String channelId) {
        if (StrUtil.isBlank(channelId)) {
            throw dataMissError();
        }
        Boolean closed = videoStreamRecordService.closeVideoStream(channelId);
        return SaResult.data(closed);
    }
}

