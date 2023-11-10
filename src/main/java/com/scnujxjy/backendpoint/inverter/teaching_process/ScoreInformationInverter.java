package com.scnujxjy.backendpoint.inverter.teaching_process;

import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.ScoreInformationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ScoreInformationInverter {
    @Mappings({})
    public abstract ScoreInformationVO po2VO(ScoreInformationPO scoreInformationPO);

    @Mappings({})
    public abstract List<ScoreInformationVO> po2VO(List<ScoreInformationPO> scoreInformationPOS);
}
