package com.scnujxjy.backendpoint.dao.entity.courses_learning;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 课程资料表
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("course_materials")
public class CourseMaterialsPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 上传者用户名
     */
    private String username;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * Minio存储地址
     */
    private String minioStorageUrl;

    /**
     * 权限信息 1 代表可读 2 代表 可下载 0 代表可读可下载 -1 表示不可读 不可下载
     */
    private Integer permissionInfo;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;


}
