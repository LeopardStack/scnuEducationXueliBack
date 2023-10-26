package com.scnujxjy.backendpoint.service;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelCreateRequestBO;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public interface SingleLivingService {
    SaResult createChannel(ChannelCreateRequestBO channelCreateRequestBO) throws IOException, NoSuchAlgorithmException;

    SaResult testGetChannelInfo();

    SaResult setWatchCondition();


}
