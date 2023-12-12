package com.scnujxjy.backendpoint.dao.entity.teaching_process;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 共享资料表
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("shared_materials")
public class SharedMaterialsPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 班级标识符
     */
    private String classIdentifier;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 上传时间
     */
    private Date uploadTime;

    /**
     * 上传用户ID
     */
    private Integer uploaderId;

    /**
     * 是否可见
     */
    private Boolean isVisible;


}
