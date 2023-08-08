package com.scnujxjy.backendpoint.dao.entity.basic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class PlatformRole implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 角色id
     */
    @TableId(type = IdType.AUTO)
    private Long roleId;

    /**
     * 角色名
     */
    private String roleName;


}
