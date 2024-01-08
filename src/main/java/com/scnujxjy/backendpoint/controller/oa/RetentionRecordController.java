package com.scnujxjy.backendpoint.controller.oa;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.oa.DropoutRecordRO;
import com.scnujxjy.backendpoint.model.ro.oa.RetentionRecordRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.oa.DropoutRecordVO;
import com.scnujxjy.backendpoint.model.vo.oa.DropoutSelectArgs;
import com.scnujxjy.backendpoint.model.vo.oa.RetentionRecordVO;
import com.scnujxjy.backendpoint.model.vo.oa.RetentionSelectArgs;
import com.scnujxjy.backendpoint.service.oa.DropoutRecordService;
import com.scnujxjy.backendpoint.service.oa.RetentionRecordService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;

/**
 * 留级 Rest 接口
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@RestController
@RequestMapping("/retention-record")
public class RetentionRecordController {
    @Resource
    private RetentionRecordService retentionRecordService;

    /**
     * 分页查询学生留级审核信息
     *
     * @param retentionRecordROPageRO 学生留级审核分页查询参数
     * @return 学生留级分页信息
     */
    @PostMapping("/page")
    public SaResult pageQueryDropoutInformation(@RequestBody PageRO<RetentionRecordRO> retentionRecordROPageRO) {
        // 参数校验
        if (Objects.isNull(retentionRecordROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(retentionRecordROPageRO.getEntity())) {
            retentionRecordROPageRO.setEntity(new RetentionRecordRO());
        }
        // 查询数据
        PageVO<RetentionRecordVO> dropoutRecordVOPageVO = retentionRecordService.getRetentionInfos(retentionRecordROPageRO);
//        // 校验数据
        if (Objects.isNull(dropoutRecordVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(dropoutRecordVOPageVO).setCode(200);
    }

    /**
     * 查询学生留级审核筛选信息
     *
     * @param retentionRecordROPageRO 学生留级审核分页查询参数
     * @return 学生留级分页信息
     */
    @PostMapping("/query_retention_select_args")
    public SaResult queryDropoutInformationSelectArgs(@RequestBody PageRO<RetentionRecordRO> retentionRecordROPageRO) {
        // 参数校验
        if (Objects.isNull(retentionRecordROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(retentionRecordROPageRO.getEntity())) {
            retentionRecordROPageRO.setEntity(new RetentionRecordRO());
        }
        // 查询数据
        RetentionSelectArgs retentionInfosSelectArgs = retentionRecordService.getRetentionInfosSelectArgs(retentionRecordROPageRO.getEntity());
//        // 校验数据
        if (Objects.isNull(retentionInfosSelectArgs)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(retentionInfosSelectArgs).setCode(200);
    }
}

