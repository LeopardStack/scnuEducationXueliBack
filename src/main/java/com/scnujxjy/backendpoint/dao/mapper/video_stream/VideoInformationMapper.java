package com.scnujxjy.backendpoint.dao.mapper.video_stream;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoInformation;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface VideoInformationMapper extends BaseMapper<VideoInformation> {

    @Select("select * from video_information where status=0 order by id asc limit #{size}")
    List<VideoInformation> seletctVideoUrl(int size);

    @Select("select session_id from video_information")
    List<String> selectAllSession();
}
