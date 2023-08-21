package com.scnujxjy.backendpoint.inverter.video_stream;

import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelRequestBO;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.model.bo.video_stream.SonChannelRequestBO;
import com.scnujxjy.backendpoint.model.bo.video_stream.SonChannelResponseBO;
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

}
