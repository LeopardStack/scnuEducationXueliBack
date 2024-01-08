package com.scnujxjy.backendpoint.model.vo.oa;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SuspensionSelectArgs implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 休学时的年份
     */
    private List<String> grades;
}
