package com.scnujxjy.backendpoint.mapperTest;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class AdminDataListener implements ReadListener<ExcelAdminData> {
    private final CollegeAdminInformationMapper mapper;
    private final CollegeInformationMapper collegeInformationMapper;

    @Override
    public void invoke(ExcelAdminData data, AnalysisContext context) {
        // Convert ExcelAdminData to CollegeAdminInformationPO
        CollegeAdminInformationPO po = new CollegeAdminInformationPO();
        po.setName(data.getName());
        String collegeName = data.getCollegeName();
        List<CollegeInformationPO> collegeInformationPOS = collegeInformationMapper.selectByCollegeName(collegeName);
        if(collegeInformationPOS.size() > 0){
            CollegeInformationPO collegeInformationPO = collegeInformationPOS.get(0);
            String collegeId = collegeInformationPO.getCollegeId();
            // TODO: 这里需要逻辑将collegeName转换为collegeId
            po.setCollegeId(collegeId);
        }

        po.setPhone(data.getPhone());
        po.setWorkNumber(data.getWorkNumber());
        po.setIdNumber(data.getIdNumber());

        // Save to DB
        mapper.insert(po);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("All data parsed and saved.");
    }
}

