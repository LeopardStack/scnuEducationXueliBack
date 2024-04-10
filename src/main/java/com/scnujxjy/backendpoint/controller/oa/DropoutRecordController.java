package com.scnujxjy.backendpoint.controller.oa;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.oa.DropoutRecordRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.oa.DropoutRecordWithClassInfoVO;
import com.scnujxjy.backendpoint.model.vo.oa.DropoutSelectArgs;
import com.scnujxjy.backendpoint.service.oa.DropoutRecordService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;

/**
 * 退学 Rest 接口
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@RestController
@RequestMapping("/dropout-record")
public class DropoutRecordController {
    @Resource
    private DropoutRecordService dropoutRecordService;

    /**
     * 分页查询学生退学审核信息
     *
     * @param dropoutRecordROPageRO 学生退学审核分页查询参数
     * @return 学生退学分页信息
     */
    @PostMapping("/page")
    public SaResult pageQueryDropoutInformation(@RequestBody PageRO<DropoutRecordRO> dropoutRecordROPageRO) {
        // 参数校验
        if (Objects.isNull(dropoutRecordROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(dropoutRecordROPageRO.getEntity())) {
            dropoutRecordROPageRO.setEntity(new DropoutRecordRO());
        }
        // 查询数据
        PageVO<DropoutRecordWithClassInfoVO> dropoutRecordVOPageVO = dropoutRecordService.getDropoutInfos(dropoutRecordROPageRO);

        // 返回数据
        return SaResult.data(dropoutRecordVOPageVO).setCode(200);
    }

    /**
     * 查询学生退学审核筛选信息
     *
     * @param dropoutRecordROPageRO 学生退学审核分页查询参数
     * @return 学生退学分页信息
     */
    @PostMapping("/query_dropout_select_args")
    public SaResult queryDropoutInformationSelectArgs(@RequestBody PageRO<DropoutRecordRO> dropoutRecordROPageRO) {
        // 参数校验
        if (Objects.isNull(dropoutRecordROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(dropoutRecordROPageRO.getEntity())) {
            dropoutRecordROPageRO.setEntity(new DropoutRecordRO());
        }
        // 查询数据
        DropoutSelectArgs dropoutSelectArgs = dropoutRecordService.getDropoutInfosSelectArgs(dropoutRecordROPageRO.getEntity());

        // 返回数据
        return SaResult.data(dropoutSelectArgs).setCode(200);
    }


    /**
     * 查询单个学生退学审核信息
     *
     * @param dropoutRecordROPageRO 学生退学审核分页查询参数
     * @return 学生退学信息
     */
    @PostMapping("/single_drop_info_query")
    public SaResult pageQueryDropoutInformation(@RequestBody DropoutRecordRO dropoutRecordROPageRO) {
        // 参数校验
        if (Objects.isNull(dropoutRecordROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(dropoutRecordROPageRO)) {
            dropoutRecordROPageRO = new DropoutRecordRO();
        }
        // 查询数据
        List<DropoutRecordWithClassInfoVO> dropoutRecordVOPageVO = dropoutRecordService.getSingleDropoutInfos(dropoutRecordROPageRO);

        // 返回数据
        return SaResult.data(dropoutRecordVOPageVO).setCode(200);
    }
}

