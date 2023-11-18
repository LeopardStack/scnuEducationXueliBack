package com.scnujxjy.backendpoint.dao.mapper.video_stream;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 直播记录表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-21
 */
public interface VideoStreamRecordsMapper extends BaseMapper<VideoStreamRecordPO> {

    @Select("SELECT DISTINCT channel_id from video_stream_record")
    List<String> selectDistinctChannelIds();

}
