package com.scnujxjy.backendpoint.service.office_automation.approval_result;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval_result.RetentionRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.approval_result.RetentionRecordMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.oa.RetentionRecordRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.oa.RetentionRecordVO;
import com.scnujxjy.backendpoint.model.vo.oa.RetentionSelectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * <p>
 * 留级记录表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@Service
@Slf4j
public class RetentionRecordService extends ServiceImpl<RetentionRecordMapper, RetentionRecordPO> implements IService<RetentionRecordPO>  {

    public PageVO<RetentionRecordVO> getRetentionInfos(PageRO<RetentionRecordRO> retentionRecordROPageRO) {
        List<RetentionRecordVO> retentionRecordVOS = getBaseMapper().getRetentionInfos(retentionRecordROPageRO.getEntity(),
                (retentionRecordROPageRO.getPageNumber()-1) * retentionRecordROPageRO.getPageSize(),
                retentionRecordROPageRO.getPageSize());
        long count = getBaseMapper().getRetentionInfosCount(retentionRecordROPageRO.getEntity());
        PageVO<RetentionRecordVO> pageVO = new PageVO<RetentionRecordVO>();
        pageVO.setRecords(retentionRecordVOS);
        pageVO.setTotal(count);
        pageVO.setCurrent(retentionRecordROPageRO.getPageNumber());
        return pageVO;
    }

    public RetentionSelectArgs getRetentionInfosSelectArgs(RetentionRecordRO entity) {
        RetentionSelectArgs retentionSelectArgs = new RetentionSelectArgs();

        ExecutorService executor = Executors.newFixedThreadPool(2); // 1 代表你有1个查询

        Future<List<String>> distinctOldGradesFuture = executor.submit(() ->
                getBaseMapper().getDistinctOldGrades(entity));
        Future<List<String>> distinctNewGradesFuture = executor.submit(() ->
                getBaseMapper().getDistinctNewGrades(entity));

        try {
            retentionSelectArgs.setOldGrades(distinctOldGradesFuture.get());
            retentionSelectArgs.setNewGrades(distinctNewGradesFuture.get());

        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return retentionSelectArgs;
    }
}
