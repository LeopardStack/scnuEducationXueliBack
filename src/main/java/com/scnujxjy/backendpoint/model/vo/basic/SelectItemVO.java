package com.scnujxjy.backendpoint.model.vo.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectItemVO {
    /**
     * key 值
     */
    private String label;

    /**
     * 下拉框实际上要展示的值
     */
    private String value;
}
