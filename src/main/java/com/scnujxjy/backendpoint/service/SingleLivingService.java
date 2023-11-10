package com.scnujxjy.backendpoint.service;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.TutorAllInformation;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelCreateRequestBO;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelViewRequest;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelViewStudentRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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

    SaResult addChannelWhiteStudent(ChannelInfoRequest channelInfoRequest);

    SaResult getChannelCardPush(ChannelInfoRequest channelInfoRequest) throws IOException, NoSuchAlgorithmException;

    public List<TutorAllInformation> selectTutorInformationByBatchIndex(Long batchIndex);

    SaResult getChannelCardPush(ChannelViewRequest channelViewRequest) throws IOException, NoSuchAlgorithmException;

    SaResult getChannelSessionInfo(ChannelInfoRequest channelInfoRequest);

    SaResult getStudentViewlogDetail(ChannelViewStudentRequest channelViewStudentRequest) throws IOException, NoSuchAlgorithmException;
}
