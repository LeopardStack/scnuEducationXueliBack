package com.scnujxjy.backendpoint.office_automation;

import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalRecordMapper;
import com.scnujxjy.backendpoint.service.office_automation.OfficeAutomationService;
import com.scnujxjy.backendpoint.service.office_automation.StudentTransferMajorOAHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationStepStatus.SUCCESS;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
public class TestOfficeAutomation {

    @Autowired
    private StudentTransferMajorOAHandler handler;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OfficeAutomationService officeAutomationService;

    @Autowired
    private ApprovalRecordMapper approvalRecordMapper;

/*    @Test
    void testCreateRecord() {
*//*        Boolean created = handler.createApprovalRecord(ApprovalRecordPO.builder()
                .approvalTypeId(1L)
                .build());
        log.info("测试结果：{}", created);*//*
        ApprovalRecordPO approvalRecordPO = ApprovalRecordPO.builder()
                .userWatchSet(ListUtil.of(123L, 456L))
                .build();
        approvalRecordMapper.insert(approvalRecordPO);
        approvalRecordPO = approvalRecordMapper.selectById(approvalRecordPO.getId());
*//*        List<ApprovalRecordPO> approvalRecordPOS = approvalRecordMapper.selectList(Wrappers.<ApprovalRecordPO>lambdaQuery()
                .eq(ApprovalRecordPO::getUserWatchSet, "[123, 456]"));
        approvalRecordPOS.forEach(approvalRecordPO -> log.info("查询结果是：{}, 其中的集合是：{}", approvalRecordPO, approvalRecordPO.getUserWatchSet()));*//*
        log.info("查询结果是：{}, 其中的集合是：{}", approvalRecordPO, approvalRecordPO.getUserWatchSet());
    }*/

    @Test
    void testProcess() {
/*        Boolean processed = handler.process(ApprovalStepRecordPO.builder()
                .comment("审批成功")
                .status(SUCCESS.getStatus())
                .stepId(3L)
                .id(25L)
                .build());*/
        Boolean processed = handler.process(ApprovalStepRecordPO.builder()
                .comment("审批成功")
                .status(SUCCESS.getStatus())
                .approvalId(3L)
                .stepId(6L)
                .id(28L)
//                .nextStepId(3L)
                .build());
        log.info("测试结果：{}", processed);
    }
}
