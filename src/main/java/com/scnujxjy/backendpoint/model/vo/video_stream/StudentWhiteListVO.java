package com.scnujxjy.backendpoint.model.vo.video_stream;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 学生白名单表
 * </p>
 *
 * @author leopard
 * @since 2023-08-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class StudentWhiteListVO {

private static final String describe = "白名单导入模板\n" +
        "说明：\n" +
        "1、会员码填写在第一列，昵称填写在第二列。\n" +
        "2、会员码不可为空，不可重复，不可为频道号、嘉宾号、助教号。会员码不区分大小写。\n" +
        "3、其他信息1、2仅用于坐席功能中的公司/职位(30字内)、介绍(100字内)。\n";

    @ExcelProperty(value = {describe,"会员码"})
    private String code;

    @ExcelProperty(value = {describe,"昵称（备注）"})
    private String name;

    @ExcelProperty(value = {describe,"其他信息1（选填）"})
    private String information;

}