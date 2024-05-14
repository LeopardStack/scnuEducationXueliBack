package com.scnujxjy.backendpoint.service.office_automation.approval_result;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval_result.DropoutRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.approval_result.DropoutRecordMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.oa.DropoutRecordRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.oa.DropoutRecordWithClassInfoVO;
import com.scnujxjy.backendpoint.model.vo.oa.DropoutSelectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * <p>
 * 退学记录表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@Service
@Slf4j
public class DropoutRecordService extends ServiceImpl<DropoutRecordMapper, DropoutRecordPO> implements IService<DropoutRecordPO> {

    /**
     * 获取退学信息
     * @param dropoutRecordROPageRO
     * @return
     */
    public PageVO<DropoutRecordWithClassInfoVO> getDropoutInfos(PageRO<DropoutRecordRO> dropoutRecordROPageRO) {
        List<DropoutRecordWithClassInfoVO> dropoutRecordVOS = getBaseMapper().getDropoutInfos(dropoutRecordROPageRO.getEntity(),
                (dropoutRecordROPageRO.getPageNumber()-1) * dropoutRecordROPageRO.getPageSize(),
                dropoutRecordROPageRO.getPageSize());
        long count = getBaseMapper().getDropoutInfosCount(dropoutRecordROPageRO.getEntity());
        PageVO<DropoutRecordWithClassInfoVO> pageVO = new PageVO<DropoutRecordWithClassInfoVO>();
        pageVO.setRecords(dropoutRecordVOS);
        pageVO.setTotal(count);
        pageVO.setCurrent(dropoutRecordROPageRO.getPageNumber());
        return pageVO;
    }

    public DropoutSelectArgs getDropoutInfosSelectArgs(DropoutRecordRO entity) {
        DropoutSelectArgs dropoutSelectArgs = new DropoutSelectArgs();

        ExecutorService executor = Executors.newFixedThreadPool(1); // 1 代表你有1个查询

        Future<List<String>> distinctGradesFuture = executor.submit(() -> getBaseMapper().getDistinctGrades(entity));

        try {
            dropoutSelectArgs.setGrades(distinctGradesFuture.get());

        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return dropoutSelectArgs;
    }

    /**
     * 获取单个退学信息
     * @param dropoutRecordROPageRO
     * @return
     */
    public List<DropoutRecordWithClassInfoVO> getSingleDropoutInfos(DropoutRecordRO dropoutRecordROPageRO) {
        return getBaseMapper().getSingleDropoutInfos(dropoutRecordROPageRO);
    }
}
