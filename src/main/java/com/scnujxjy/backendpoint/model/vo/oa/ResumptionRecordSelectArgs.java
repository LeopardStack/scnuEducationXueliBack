package com.scnujxjy.backendpoint.model.vo.oa;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ResumptionRecordSelectArgs implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 复学时的年份
     */
    private List<String> grades;
}
