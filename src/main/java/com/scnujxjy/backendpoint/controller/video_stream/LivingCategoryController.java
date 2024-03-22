package com.scnujxjy.backendpoint.controller.video_stream;


import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson.JSONObject;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.CategoryRequest;
import com.scnujxjy.backendpoint.util.polyv.HttpUtil;
import com.scnujxjy.backendpoint.util.polyv.LiveSignUtil;
import lombok.extern.slf4j.Slf4j;
import net.polyv.live.v1.config.LiveGlobalConfig;
import org.apache.tika.utils.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/category")
@Slf4j
public class LivingCategoryController {

    //创建分类接口
    @PostMapping("/create")
    public SaResult createCategory(@RequestBody CategoryRequest categoryRequest) {

        if (StringUtils.isBlank(categoryRequest.getCategoryName())) {
            return SaResult.error("分类名称不能为空");
        }
        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        String timestamp = String.valueOf(System.currentTimeMillis());

        String url = "http://api.polyv.net/live/v3/user/category/create";

        try {
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("appId", appId);
            requestMap.put("timestamp", timestamp);
            requestMap.put("categoryName", categoryRequest.getCategoryName());
            requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));
            String response = HttpUtil.postFormBody(url, requestMap);
            JSONObject jsonObject = JSONObject.parseObject(response);

            return SaResult.data(jsonObject);
        } catch (Exception e) {
            log.error("创建失败,入参为:{}，异常信息为:{}", categoryRequest, e);
            return SaResult.error("创建频道分类失败，请联系管理员");
        }

    }


    @PostMapping("/query")
    public SaResult queryCategory() {

        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String url = "http://api.polyv.net/live/v3/user/category/list";

        try {
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("appId", appId);
            requestMap.put("timestamp", timestamp);
            requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));
            String response = HttpUtil.postFormBody(url, requestMap);
            JSONObject jsonObject = JSONObject.parseObject(response);

            return SaResult.data(jsonObject);
        } catch (Exception e) {
            log.error("查询失败,异常信息为", e);
            return SaResult.error("查询频道分类失败，请联系管理员");
        }

    }

    //删除分类接口
    @PostMapping("/delete")
    public SaResult deleteCategory(@RequestBody CategoryRequest categoryRequest) {

        if (StringUtils.isBlank(categoryRequest.getCategoryId())) {
            return SaResult.error("分类ID不能为空");
        }

        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String url = "http://api.polyv.net/live/v3/user/category/delete";

        try {
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("appId", appId);
            requestMap.put("timestamp", timestamp);
            requestMap.put("categoryId", categoryRequest.getCategoryId());
            requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));
            String response = HttpUtil.postFormBody(url, requestMap);
            JSONObject jsonObject = JSONObject.parseObject(response);
            String code = jsonObject.getString("code");

            return SaResult.data(jsonObject);
        } catch (Exception e) {
            log.error("删除失败,入参为:{}，异常信息为:{}", categoryRequest, e);
            return SaResult.error("删除频道分类失败，请联系管理员");
        }

    }

    //修改分类名称接口
    @PostMapping("/update")
    public SaResult updateCategory(@RequestBody CategoryRequest categoryRequest) {

        if (StringUtils.isBlank(categoryRequest.getCategoryId()) || StringUtils.isBlank(categoryRequest.getCategoryName())) {
            return SaResult.error("分类ID或者分类名称不能为空");
        }

        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String url = "http://api.polyv.net/live/v3/user/category/update-name";

        try {
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("appId", appId);
            requestMap.put("timestamp", timestamp);
            requestMap.put("categoryId", categoryRequest.getCategoryId());
            requestMap.put("categoryName", categoryRequest.getCategoryName());
            requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));
            String response = HttpUtil.postFormBody(url, requestMap);
            JSONObject jsonObject = JSONObject.parseObject(response);
            String code = jsonObject.getString("code");
            if ("200".equals(code)) {
                return SaResult.data(jsonObject);
            }
        } catch (Exception e) {
            log.error("修改频道分类名称失败,入参为:{}，异常信息为:{}", categoryRequest, e);
            return SaResult.error("修改频道分类名称失败，请联系管理员");
        }

        return SaResult.error("修改频道分类名称失败，请联系管理员");
    }


}

