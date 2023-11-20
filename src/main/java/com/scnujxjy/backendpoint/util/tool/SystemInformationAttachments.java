package com.scnujxjy.backendpoint.util.tool;


import com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 谢辉龙
 */
@Component
@Slf4j
public class SystemInformationAttachments {
    /**
     * 引入这个类 将 List<T> 转为 excel 写入指定的桶
     */
    @Resource
    private OldDataSynchronize oldDataSynchronize;
}
