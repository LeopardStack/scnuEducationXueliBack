package com.scnujxjy.backendpoint.dao.entity.platform_message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-10-29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("user_upload")
public class UserUploadsPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 上传文件的用户名
     */
    private String userId;

    /**
     * 用户上传的 Minio 地址
     */
    private String fileUrl;

    /**
     * 用户上传的时间
     */
    private Date uploadTime;

    /**
     * 系统处理的结果反馈文件 Minio 地址
     */
    private String resultUrl;

    /**
     * 系统处理的最终结果
     */
    private String resultDesc;

    /**
     * 用户是否已读
     */
    private Boolean isRead;

    /**
     * 上传类型 ：eg.排课表
     */
    private String uploadType;

    /**
     * 系统处理的完成时间
     */
    private Date finishedTime;

}
