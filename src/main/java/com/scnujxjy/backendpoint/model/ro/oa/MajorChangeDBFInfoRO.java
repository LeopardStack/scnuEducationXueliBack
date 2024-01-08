package com.scnujxjy.backendpoint.model.ro.oa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生成转专业 DBF 所需的其他额外信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MajorChangeDBFInfoRO extends MajorChangeRecordRO{
    /**
     * 文号
     */
    String documentNumber;

    /**
     * 生成的文件类型 eg. dbf、excel
     */
    String exportFormat;
}
