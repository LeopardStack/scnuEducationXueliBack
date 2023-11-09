package com.scnujxjy.backendpoint.inverter.core_data;

import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoRO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PaymentInfoInverter {
    @Mappings({})
    public abstract PaymentInfoVO po2VO(PaymentInfoPO paymentInfo);

    @Mappings({})
    public abstract PaymentInfoPO ro2PO(PaymentInfoRO paymentInfoRO);

    @Mappings({})
    public abstract List<PaymentInfoVO> po2VO(List<PaymentInfoPO> paymentInfoPOS);

    @Mappings({
            @Mapping(target = "id", source = "studentStatusVO.id"),
            @Mapping(target = "studentNumber", source = "studentStatusVO.studentNumber"),
            @Mapping(target = "admissionNumber", source = "studentStatusVO.admissionNumber"),
            @Mapping(target = "name", source = "studentStatusVO.name"),
            @Mapping(target = "grade", source = "studentStatusVO.grade")
    })
    public abstract PaymentInfoVO po2VO(StudentStatusVO studentStatusVO, PaymentInfoPO paymentInfoPO);
}
