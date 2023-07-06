package com.scnujxjy.backendpoint.entity.project_manage;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author leopard
 * @since 2023-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("TrainingSummary")
public class TrainingSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String fileName;

    private String fileType;

    private String filePath;

    private Long uploader;

    private Date uploadedAt;

    private Long fileSize;

    private Long projectId;


}
