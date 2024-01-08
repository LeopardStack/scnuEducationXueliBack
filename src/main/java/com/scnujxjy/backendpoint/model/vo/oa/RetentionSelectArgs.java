package com.scnujxjy.backendpoint.model.vo.oa;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RetentionSelectArgs implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 留级前的年级
     */
    private List<String> oldGrades;

    /**
     * 留级后的年级
     */
    private List<String> newGrades;
}
