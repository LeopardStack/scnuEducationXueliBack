package com.scnujxjy.backendpoint.service;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelCreateRequestBO;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface SingleLivingService {
    SaResult createChannel(ChannelCreateRequestBO channelCreateRequestBO, CourseSchedulePO courseSchedulePO) throws IOException, NoSuchAlgorithmException;

    SaResult deleteChannel(String channelIds) throws IOException, NoSuchAlgorithmException;

    SaResult setRecordSetting(ChannelInfoRequest channelInfoRequest) throws IOException, NoSuchAlgorithmException;

    SaResult getTeacherChannelUrl(String channelId);

    SaResult getStudentChannelUrl(String channelId);

    SaResult getTutorChannelUrl(String channelId,String userId);

    SaResult createTutor(String channelId, String tutorName);

    SaResult UpdateChannelNameAndImg(ChannelInfoRequest channelInfoRequest);

    SaResult GetChannelDetail(String channelId);
}
