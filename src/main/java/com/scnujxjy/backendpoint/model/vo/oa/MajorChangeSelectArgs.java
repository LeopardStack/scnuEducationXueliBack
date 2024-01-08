package com.scnujxjy.backendpoint.model.vo.oa;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MajorChangeSelectArgs implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 年级
     */
    private List<String> grades;

    /**
     * 备注信息筛选，主要是为了筛选各种类型的转专业 比如 转学到校外的
     * 新生转专业的
     */
    private List<String> remarks;
}
