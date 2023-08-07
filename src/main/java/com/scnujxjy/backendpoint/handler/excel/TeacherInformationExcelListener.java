package com.scnujxjy.backendpoint.handler.excel;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.scnujxjy.backendpoint.model.bo.TeacherInformationExcelBO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class TeacherInformationExcelListener implements ReadListener<TeacherInformationExcelBO> {

    private List<TeacherInformationExcelBO> dataList = ListUtil.toList();

    @Override
    public void invoke(TeacherInformationExcelBO teacherInformationExcelBO, AnalysisContext analysisContext) {
        log.info("解析数据：{}", teacherInformationExcelBO);
        dataList.add(teacherInformationExcelBO);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("所有数据解析完成，共{}条数据", dataList.size());
        // TODO 补充插入数据库的逻辑
    }

    public List<TeacherInformationExcelBO> getDataList() {
        return this.dataList;
    }
}
