package com.scnujxjy.backendpoint.inverter.video_stream;

import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.TutorAllInformation;
import com.scnujxjy.backendpoint.dao.entity.video_stream.TutorInformation;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelRequestBO;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.model.bo.video_stream.SonChannelRequestBO;
import com.scnujxjy.backendpoint.model.bo.video_stream.SonChannelResponseBO;
import com.scnujxjy.backendpoint.model.ro.video_stream.VideoStreamRecordRO;
import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamRecordVO;
import net.polyv.live.v1.entity.channel.operate.LiveChannelBasicInfoResponse;
import net.polyv.live.v1.entity.channel.operate.LiveCreateSonChannelListRequest;
import net.polyv.live.v1.entity.channel.operate.LiveSonChannelInfoResponse;
import net.polyv.live.v1.entity.quick.QuickCreateChannelResponse;
import net.polyv.live.v1.entity.quick.QuickCreatePPTChannelRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VideoStreamInverter {

    @Mappings({})
    QuickCreatePPTChannelRequest channelRequestBO2Polyv(ChannelRequestBO channelRequestBO);

    @Mappings({})
    LiveCreateSonChannelListRequest.SonChannel sonRequestBO2Polyv(SonChannelRequestBO sonChannelRequestBO);

    @Mappings({})
    List<LiveCreateSonChannelListRequest.SonChannel> sonRequestBO2Polyv(List<SonChannelRequestBO> sonChannelRequestBOS);

    @Named("polyv2SonChannelResponseBO")
    SonChannelResponseBO polyv2SonChannelResponseBO(LiveSonChannelInfoResponse liveSonChannelInfoResponse);

    @Mappings({
            @Mapping(target = "sonChannelResponseBOS", source = "quickCreateChannelResponse.sonChannelInfos", qualifiedByName = "polyv2SonChannelResponseBO"),
            @Mapping(target = ".", source = "liveChannelBasicInfoResponse")
    })
    ChannelResponseBO polyv2ChannelResponseBO(QuickCreateChannelResponse quickCreateChannelResponse);

    @Mappings({})
    VideoStreamRecordVO po2VO(VideoStreamRecordPO videoStreamRecordPO);

    @Mappings({})
    List<VideoStreamRecordVO> po2VO(List<VideoStreamRecordPO> videoStreamRecordPOS);

    @Mappings({})
    VideoStreamRecordPO ro2PO(VideoStreamRecordRO videoStreamRecordRO);

    @Mappings({})
    List<VideoStreamRecordPO> ro2PO(List<VideoStreamRecordRO> videoStreamRecordROS);

    @Mappings({})
    VideoStreamRecordRO channelResponseBO2RO(ChannelResponseBO channelResponseBO);

    @Mappings({})
    List<VideoStreamRecordRO> sonChannelResponseBO2RO(List<SonChannelResponseBO> sonChannelResponseBOS);

    @Mappings({})
    ChannelResponseBO liveChannelBasicInfoResponse2ChannelResponseBO(LiveChannelBasicInfoResponse liveChannelBasicInfoResponse);

    @Mappings({
            @Mapping(target = "userId", source = "tutorInformation.userId")
    })
    TutorAllInformation tutorInformationTeacherInformation2TutorAllInformation(TutorInformation tutorInformation, TeacherInformationPO teacherInformationPO);
}
