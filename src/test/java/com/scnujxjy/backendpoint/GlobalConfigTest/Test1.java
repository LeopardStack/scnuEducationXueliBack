package com.scnujxjy.backendpoint.GlobalConfigTest;

import com.scnujxjy.backendpoint.dao.entity.basic.GlobalConfigPO;
import com.scnujxjy.backendpoint.service.basic.GlobalConfigService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private GlobalConfigService globalConfigService;

    @Resource
    private MinioService minioService;

    @Test
    public void Test1(){
        GlobalConfigPO globalConfigPO = new GlobalConfigPO();
        globalConfigPO.setConfigKey("学历教育排课表导入模板");
        globalConfigPO.setConfigValue("xueli-system-config/学历教育导入模板/排课表信息导入.xlsx");
        globalConfigPO.setDescription("排课表导入模板是为了给二级学院和继续教育学院管理员批量导入二级学院每学期的排课");
        LocalDateTime localDateTime = LocalDateTime.now();

        // 将 LocalDateTime 对象转换为 Instant 对象，使用系统默认时区
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();

        // 将 Instant 对象转换为 java.util.Date 对象
        Date date = Date.from(instant);

        // 打印结果
        log.info(String.valueOf(date));
        globalConfigPO.setUpdatedAt(date);

        int insert = globalConfigService.getBaseMapper().insert(globalConfigPO);
        log.info("插入一条新的配置 \n" + insert);

        List<GlobalConfigPO> globalConfigPOS = globalConfigService.getBaseMapper().selectList(null);
    }
}
