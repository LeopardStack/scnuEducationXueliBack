package com.scnujxjy.backendpoint.model.vo.oa;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuspensionWithResumptionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private SuspensionRecordWithClassInfoVO suspensionRecordVO;

    private List<ResumptionRecordWithClassInfoVO> resumptionRecordVOList;

}
