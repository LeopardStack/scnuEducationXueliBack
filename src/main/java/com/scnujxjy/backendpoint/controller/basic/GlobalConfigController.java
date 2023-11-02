package com.scnujxjy.backendpoint.controller.basic;


import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.basic.GlobalConfigPO;
import com.scnujxjy.backendpoint.service.basic.GlobalConfigService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 *  全局配置类
 *
 * @author 谢辉龙
 * @since 2023-10-29
 */
@RestController
@RequestMapping("/global-config")
public class GlobalConfigController {
    @Resource
    private MinioService minioService;

    @Resource
    private GlobalConfigService globalConfigService;

    /**
     * 获取全局配置中的指定 key 的模板文件
     * @param fileKey
     * @return
     */
    @PostMapping("/get-file")
    public SaResult getFile(@RequestParam("fileKey") String fileKey) {
        try {
            // 从 globalService 获取 minioUrl
            GlobalConfigPO globalConfigPO = globalConfigService.getBaseMapper().selectOne(
                    new LambdaQueryWrapper<GlobalConfigPO>().eq(GlobalConfigPO::getConfigKey, fileKey)
            );
            String minioUrl = globalConfigPO.getConfigValue();

            if (minioUrl == null) {
                return SaResult.error("Minio URL not found for the specified key");
            }

            // 从 MinioService 获取文件
            byte[] fileData = minioService.getFileFromMinio(minioUrl);

            // 返回成功的 SaResult
            return SaResult.ok().setData(fileData);

        } catch (Exception e) {
            // 返回失败的 SaResult
            return SaResult.error("An error occurred while fetching the file: " + e.getMessage());
        }
    }
}

