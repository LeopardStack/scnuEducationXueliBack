package com.scnujxjy.backendpoint.controller.oa;


import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.oa.SuspensionRecordPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.oa.RetentionRecordRO;
import com.scnujxjy.backendpoint.model.ro.oa.SuspensionRecordRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.oa.*;
import com.scnujxjy.backendpoint.service.oa.RetentionRecordService;
import com.scnujxjy.backendpoint.service.oa.SuspensionRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.apache.tika.utils.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;

/**
 * 休学 Rest 接口
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@RestController
@RequestMapping("/suspension-record")
@Slf4j
public class SuspensionRecordController {
    @Resource
    private SuspensionRecordService suspensionRecordService;

    /**
     * 分页查询学生休学审核信息
     *
     * @param suspensionRecordROPageRO 学生休学审核分页查询参数
     * @return 学生休学分页信息
     */
    @PostMapping("/page")
    public SaResult pageQuerySuspensionInformation(@RequestBody PageRO<SuspensionRecordRO> suspensionRecordROPageRO) {
        // 参数校验
        if (Objects.isNull(suspensionRecordROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(suspensionRecordROPageRO.getEntity())) {
            suspensionRecordROPageRO.setEntity(new SuspensionRecordRO());
        }
        // 查询数据
        PageVO<SuspensionRecordVO> suspensionInfos = suspensionRecordService.getSuspensionInfos(suspensionRecordROPageRO);
//        // 校验数据
        if (Objects.isNull(suspensionInfos)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(suspensionInfos).setCode(200);
    }

    /**
     * 查询学生休学审核筛选信息
     *
     * @param suspensionRecordROPageRO 学生休学审核分页查询参数
     * @return 学生休学分页信息
     */
    @PostMapping("/query_suspension_select_args")
    public SaResult querySuspensionInformationSelectArgs(@RequestBody PageRO<SuspensionRecordRO> suspensionRecordROPageRO) {
        // 参数校验
        if (Objects.isNull(suspensionRecordROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(suspensionRecordROPageRO.getEntity())) {
            suspensionRecordROPageRO.setEntity(new SuspensionRecordRO());
        }
        // 查询数据
        SuspensionSelectArgs suspensionInfosSelectArgs = suspensionRecordService.getSuspensionInfosSelectArgs(suspensionRecordROPageRO.getEntity());
//        // 校验数据
        if (Objects.isNull(suspensionInfosSelectArgs)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(suspensionInfosSelectArgs).setCode(200);
    }

    /**
     * 根据休学的复学 ID 不为空 获取复学信息
     */
    @PostMapping("/get_resumption")
    public SaResult getResumptionInfo(@RequestBody SuspensionRecordRO suspensionRecordRO){
        SuspensionWithResumptionVO resumptionInfo = suspensionRecordService.getResumptionInfo(suspensionRecordRO);
        return SaResult.ok().setData(resumptionInfo);
    }
}

