package com.scnujxjy.backendpoint.model.bo.SingleLiving;

import com.scnujxjy.backendpoint.model.vo.video_stream.StudentWhiteListVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ChannelInfoRequest {
    private String channelId;
    //"Y"是，"N"否
    private String playbackEnabled;
    private String channelName;
    private String ImgUrl;
    private List<String> channelIds;
    private Long sectionId;
    private Long courseId;

    //以下用于添加单个白名单字段
    private String code;
    private String name;
    private List<String> deleteCodeList;
    private List<StudentWhiteListVO> studentWhiteList;
    private String keyword;
    //Y一键清空，N根据入参部分删除
    private String isClear;

    //以下用于获取频道场次信息字段
    private Date startDate;//开始日期，格式yyyy-MM-dd HH:mm:ss
    private Date endDate;//结束日期，格式yyyy-MM-dd HH:mm:ss
    private Integer currentPage;
    private Integer pageSize;

    private String viewerId;//观众id
    //开始日期。格式为yyyy-MM-dd

}
