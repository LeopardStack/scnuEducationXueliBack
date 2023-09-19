package com.scnujxjy.backendpoint.collegeFriendTest;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public class OriginalDataListener implements ReadListener<OriginalData> {
    // 使用一个列表来存储数据，也可以在这里进行其他处理，比如写入数据库
    public List<OriginalData> dataList = new ArrayList<>();

    @Override
    public void invoke(OriginalData originalData, AnalysisContext analysisContext) {
        dataList.add(originalData);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 这里可以进行数据的后处理，如果不需要可以留空
    }

    public List<OriginalData> getDataList() {
        return dataList;
    }
}
