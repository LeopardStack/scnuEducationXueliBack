package com.scnujxjy.backendpoint.model.vo.teaching_process;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDataVO<T> {
    /**
     * 信息
     */
    List<T> data;
    /**
     * 总数据
     */
    long total;
}

