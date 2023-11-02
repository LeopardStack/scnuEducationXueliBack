package com.scnujxjy.backendpoint.model.vo.platform_message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUploadsVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 上传文件的用户 ID
     */
    private Long userId;

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
