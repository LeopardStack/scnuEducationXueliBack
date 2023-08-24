package com.scnujxjy.backendpoint.service.video_stream;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.inverter.video_stream.VideoStreamInverter;
import com.scnujxjy.backendpoint.model.ro.video_stream.VideoStreamRecordRO;
import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 直播记录表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-21
 */
@Service
@Slf4j
public class VideoStreamRecordsService extends ServiceImpl<VideoStreamRecordsMapper, VideoStreamRecordPO> implements IService<VideoStreamRecordPO> {

    @Resource
    private VideoStreamInverter videoStreamInverter;

    public VideoStreamRecordVO detailById(Long id) {
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        VideoStreamRecordPO videoStreamRecordPO = baseMapper.selectById(id);
        return videoStreamInverter.po2VO(videoStreamRecordPO);
    }

    public VideoStreamRecordVO create(VideoStreamRecordRO videoStreamRecordRO) {
        if (Objects.isNull(videoStreamRecordRO)) {
            log.error("参数缺失");
            return null;
        }
        VideoStreamRecordPO videoStreamRecordPO = videoStreamInverter.ro2PO(videoStreamRecordRO);
        int count = baseMapper.insert(videoStreamRecordPO);
        if (count <= 0) {
            log.error("插入错误，数据：{}", videoStreamRecordPO);
            return null;
        }
        return detailById(videoStreamRecordPO.getId());
    }

    @Transactional
    public List<VideoStreamRecordVO> createBatch(List<VideoStreamRecordRO> videoStreamRecordROS) {
        if (CollUtil.isEmpty(videoStreamRecordROS)) {
            log.error("参数缺失");
            return null;
        }
        List<VideoStreamRecordPO> videoStreamRecordPOS = videoStreamInverter.ro2PO(videoStreamRecordROS);
        boolean saved = saveBatch(videoStreamRecordPOS);
        if (!saved) {
            log.error("新增记录失败，已经回滚，数据：{}", videoStreamRecordPOS);
            return null;
        }
        Set<Long> idSet = videoStreamRecordPOS.stream().map(VideoStreamRecordPO::getId).collect(Collectors.toSet());
        return videoStreamInverter.po2VO(baseMapper.selectBatchIds(idSet));
    }
}
