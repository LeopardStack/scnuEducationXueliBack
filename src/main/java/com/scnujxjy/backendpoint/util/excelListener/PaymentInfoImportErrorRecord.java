package com.scnujxjy.backendpoint.util.excelListener;

import com.alibaba.excel.annotation.ExcelProperty;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoImportRO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInfoImportErrorRecord extends PaymentInfoImportRO {
    /**
     * 导入结果
     */
    @ExcelProperty(index = 11)
    private String result;
}
