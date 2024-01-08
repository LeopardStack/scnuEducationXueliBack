package com.scnujxjy.backendpoint.model.vo.oa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumptionWithSuspensionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ResumptionRecordWithClassInfoVO resumptionRecordVO;

    private List<SuspensionRecordWithClassInfoVO> suspensionRecordVOList;

}
