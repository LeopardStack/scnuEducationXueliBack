package com.scnujxjy.backendpoint.service.oa;

import com.scnujxjy.backendpoint.dao.mongoEntity.OAApplicationForm;
import com.scnujxjy.backendpoint.dao.mongoEntity.StudentTransferApplication;

/**
 * 该抽象类 实现 OA 的申请创建
 */
public abstract class OATaskExecutorService {
    /**
     * 根据前端表单 构建申请表单
     * @param application 申请表单
     */
    protected abstract boolean application(OAApplicationForm application);

    /**
     * 处理表单
     * @param applicationId 表单 ID
     */
    protected abstract void process(String applicationId);

    /**
     * 表单审核通过后的处理函数
     * @param applicationId 表单 ID
     */
    protected abstract void success(String applicationId);

    /**
     * 表单审核不通过后的处理函数
     * @param applicationId 表单 ID
     */
    protected abstract void failed(String applicationId);

}
