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

    @Select(
            "<script>" +
                    "SELECT DISTINCT channel_id " +
                    "FROM video_stream_record " +
                    "<if test='videoList != null'>" +
                    "WHERE id IN " +
                    "<foreach collection='videoList' item='id' open='(' close=')' separator=','>" +
                    "#{id}" +
                    "</foreach>" +
                    "</if>" +
                    "</script>"
    )
    List<String> selectChannelIds(List<String> videoList);
}
