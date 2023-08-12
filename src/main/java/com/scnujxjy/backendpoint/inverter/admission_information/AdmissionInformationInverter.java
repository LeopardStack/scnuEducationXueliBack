package com.scnujxjy.backendpoint.inverter.admission_information;

import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class AdmissionInformationInverter {

    @Mappings({})
    public abstract AdmissionInformationVO po2VO(AdmissionInformationPO admissionInformationPO);

    @Mappings({})
    public abstract List<AdmissionInformationVO> po2VO(List<AdmissionInformationPO> admissionInformationPOS);

    @Mappings({})
    public abstract AdmissionInformationPO ro2PO(AdmissionInformationRO admissionInformationRO);

}
