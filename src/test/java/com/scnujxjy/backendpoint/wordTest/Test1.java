package com.scnujxjy.backendpoint.wordTest;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
public class Test1 {

    @Test
    public void test1() throws IOException {
        FileInputStream fis = new FileInputStream(new File("D:\\我的资料\\华师项目开发\\华师学历教育系统\\系统数据导入\\21-23级人培方案\\21年拟招生专业培养方案（2022级）\\城市文化学院\\" +
                "120210 城市文化学院  专起本  3年制  函授  文化产业管理  培养方案.doc"));

        HWPFDocument document = new HWPFDocument(fis);
        WordExtractor extractor = new WordExtractor(document);

        String[] paragraphs = extractor.getParagraphText();
        for (String paragraph : paragraphs) {
            System.out.println(paragraph);
        }

        // 注意：HWPF 提供的对表格的支持不如 XWPF 完善。
        // 如果你需要处理表格，可能需要进一步地处理。

        fis.close();
    }
}
