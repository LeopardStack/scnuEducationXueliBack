package com.scnujxjy.backendpoint.dbfTest;

import com.linuxense.javadbf.*;
import com.scnujxjy.backendpoint.util.dbf.AdmissionRetentionRecord;
import com.scnujxjy.backendpoint.util.dbf.DbfRecord;
import com.scnujxjy.backendpoint.util.dbf.DbfWriterUtil;
import com.scnujxjy.backendpoint.util.dbf.StudentStatusChangeRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ExportDbfExample {

    /**
     * 生成学籍异动 dbf 包含 休学、复学、转专业、留级
     */
    @Test
    public void test1() {
        // 定义 DBF 文件的字段
        DBFField[] fields = {
                createField("KSH", DBFDataType.CHARACTER, 18),
                createField("XM", DBFDataType.CHARACTER, 40),
                createField("YDLX", DBFDataType.CHARACTER, 8),
                createField("PZRQ", DBFDataType.CHARACTER, 8),
                createField("WH", DBFDataType.CHARACTER, 50),
                createField("YY", DBFDataType.CHARACTER, 50),
                createField("SM", DBFDataType.CHARACTER, 200),
                createField("ZYDM", DBFDataType.CHARACTER, 8),
                createField("ZYMC", DBFDataType.CHARACTER, 50),
                createField("XZ", DBFDataType.CHARACTER, 3),
                createField("DQSZJ", DBFDataType.CHARACTER, 4),
                createField("XH", DBFDataType.CHARACTER, 15),
                createField("FY", DBFDataType.CHARACTER, 24),
                createField("XSH", DBFDataType.CHARACTER, 24),
                createField("BH", DBFDataType.CHARACTER, 24),
                createField("YJBYRQ", DBFDataType.CHARACTER, 8)
        };

        // 准备数据
        List<DbfRecord> records = new ArrayList<>();
        records.add(new StudentStatusChangeRecord("123456789012345678", "张三", "类型1",
                "20220101", "文号1", "原因1", "说明1", "专业代码1",
                "专业名称1", "3", "2022", "学号1", "分院1",
                "系所1", "班号1", "20230601"));

        // 你可以添加更多的记录，或者从数据库中查询数据并填充到 records 列表中

        // 写入 DBF 文件
        DbfWriterUtil.writeDbf("./dbfResult/xuejiyidong.dbf", fields, records);
    }

    private static DBFField createField(String name, DBFDataType type, int length) {
        DBFField field = new DBFField();
        field.setName(name);
        field.setType(type);
        field.setLength(length);
        return field;
    }

    @Test
    public void test2(){
        // 定义 DBF 文件的字段
        DBFField[] fields = {
                createField("KSH", DBFDataType.CHARACTER, 18),
                createField("XM", DBFDataType.CHARACTER, 40),
                createField("YDYY", DBFDataType.CHARACTER, 50)
        };

        // 准备数据
        List<DbfRecord> records = new ArrayList<>();
        records.add(new AdmissionRetentionRecord("123456789012345678", "李四", "个人原因"));

        // 写入 DBF 文件
        DbfWriterUtil.writeDbf("./dbfResult/baoliuruxue.dbf", fields, records);
    }
}

