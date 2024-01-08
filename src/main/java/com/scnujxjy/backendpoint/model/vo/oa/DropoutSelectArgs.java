package com.scnujxjy.backendpoint.model.vo.oa;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DropoutSelectArgs implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 年级
     */
    private List<String> grades;

}
