package com.scnujxjy.backendpoint.service.video_stream;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.TutorAllInformation;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelCreateRequestBO;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelViewRequest;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelViewStudentRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface SingleLivingService {

    SaResult createTeacherAndTutorUrl(String channelId,String loginId);

    SaResult getChannelInformation(Long sectionId);

    SaResult createChannel(ChannelCreateRequestBO channelCreateRequestBO, CourseSchedulePO courseSchedulePO) throws IOException, NoSuchAlgorithmException;

    SaResult deleteChannel(String channelIds) throws IOException, NoSuchAlgorithmException;

    SaResult setRecordSetting(ChannelInfoRequest channelInfoRequest) throws IOException, NoSuchAlgorithmException;

    SaResult getRecordSetting(String channelId) ;

    SaResult getTeacherChannelUrl(String channelId);

    SaResult getStudentChannelUrl(String channelId);

    SaResult getTutorChannelUrl(String channelId, String userId);

    SaResult createTutorChannel(String channelId, String userId);

    SaResult createTutor(String channelId, String tutorName);

    SaResult UpdateChannelNameAndImg(ChannelInfoRequest channelInfoRequest);

    SaResult getChannelWhiteList(ChannelInfoRequest channelInfoRequest);

    SaResult GetChannelDetail(String channelId);

    SaResult addChannelWhiteStudent(ChannelInfoRequest channelInfoRequest);

    SaResult addChannelWhiteStudentByFile(ChannelInfoRequest channelInfoRequest);

    SaResult deleteChannelWhiteStudent(ChannelInfoRequest channelInfoRequest);

    void exportStudentSituation(Long sectionId,  String loginId, PlatformMessagePO platformMessagePO) throws IOException;

    void exportAllCourseSituation(Long courseId, String loginId, PlatformMessagePO platformMessagePO) throws IOException;

    List<TutorAllInformation> selectTutorInformationByBatchIndex(Long batchIndex);

    SaResult getChannelCardPush(ChannelViewRequest channelViewRequest) throws IOException, NoSuchAlgorithmException;

    SaResult getChannelSessionInfo(ChannelInfoRequest channelInfoRequest);

    SaResult getTotalTeachingTime(String courseId);

    SaResult getStudentViewlogDetail(ChannelViewStudentRequest channelViewStudentRequest) throws IOException, NoSuchAlgorithmException;

    SaResult getChannelStatus(List<String> channelIdList);


    SaResult getChannelBasicInformation(String channelId);
}
