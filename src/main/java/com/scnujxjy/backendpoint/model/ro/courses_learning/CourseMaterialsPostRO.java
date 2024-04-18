package com.scnujxjy.backendpoint.model.ro.courses_learning;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseMaterialsPostRO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @ApiModelProperty(value = "课程资料主键 ID")
    private Long id;

    /**
     * 课程ID
     */
    @ApiModelProperty(value = "课程ID")
    private Long courseId;

    /**
     * 课程资料
     */
    @ApiModelProperty(value = "课程资料")
    private List<MultipartFile> postMaterials;

    /**
     * 权限信息 1 代表可读 2 代表 可下载 0 代表可读可下载 -1 表示不可读 不可下载
     */
    @ApiModelProperty(value = "权限")
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
