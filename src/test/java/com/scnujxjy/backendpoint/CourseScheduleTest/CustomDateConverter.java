package com.scnujxjy.backendpoint.CourseScheduleTest;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateConverter implements Converter<Date> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return Date.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Date convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (cellData.getStringValue() == null) {
            return null;
        }
        try {
            // Try parsing with the first format
            return new SimpleDateFormat("yyyy-MM-dd").parse(cellData.getStringValue());
        } catch (ParseException e) {
            try {
                // Try parsing with the second format
                return new SimpleDateFormat("yyyy/M/d").parse(cellData.getStringValue());
            } catch (ParseException ex) {
                throw new ExcelDataConvertException(cellData.getRowIndex(), cellData.getColumnIndex(), cellData, contentProperty, ex.toString());
            }
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(Date value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        // Handle the export logic if required
        return new WriteCellData<>(new SimpleDateFormat("yyyy-MM-dd").format(value));
    }
}

