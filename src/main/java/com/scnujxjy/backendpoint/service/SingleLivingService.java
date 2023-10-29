package com.scnujxjy.backendpoint.service;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelCreateRequestBO;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface SingleLivingService {
    SaResult createChannel(ChannelCreateRequestBO channelCreateRequestBO) throws IOException, NoSuchAlgorithmException;

    SaResult deleteChannel(String[] channelArrays) throws IOException, NoSuchAlgorithmException;

    SaResult setRecordSetting(ChannelInfoRequest channelInfoRequest) throws IOException, NoSuchAlgorithmException;

    SaResult getTeacherChannelUrl(String channelId);

    SaResult getStudentChannelUrl(String channelId);

    SaResult getTutorChannelUrl(String channelId);

    SaResult createTutor(String channelId, String tutorName);

    SaResult UpdateChannelNameAndImg(ChannelInfoRequest channelInfoRequest);

//    SaResult setWatchCondition(String channelId);


}
