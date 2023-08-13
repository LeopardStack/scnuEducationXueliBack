package com.scnujxjy.backendpoint.oldSysDataExport;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.DegreeInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.DegreeInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getDegreedatas;

@SpringBootTest
@Slf4j
public class TestGetAllDegreedatas {
    @Autowired(required = false)
    private DegreeInfoMapper degreeInfoMapper;


    @Test
    public void test1() {
        // 关闭 mybatis-plus 的 SQL 日志控制台输出
        LoggingSystem.get(ClassLoader.getSystemClassLoader()).setLogLevel("org.mybatis", LogLevel.OFF);


        ArrayList<HashMap<String, String>> degreedatas = getDegreedatas();
        log.info("总共多少数据 " + degreedatas.size());
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMM");

        for (HashMap<String, String> degreeData : degreedatas) {
            DegreeInfoPO degreeInfoPO = new DegreeInfoPO();
            String pyxs = degreeData.get("PYXS");
            if (pyxs != null) {
                if (pyxs.contains("自考")) {
                    degreeInfoPO.setAdmissionNumber(degreeData.get("XH"));
                } else {
                    degreeInfoPO.setStudentNumber(degreeData.get("XH"));
                }
            } else {
                log.error("培养形式为空 " + degreeData);
            }

            degreeInfoPO.setName(degreeData.get("XM"));
            degreeInfoPO.setNamePinyin(degreeData.get("XMPY"));
            degreeInfoPO.setGender(degreeData.get("XB"));
            degreeInfoPO.setEthnicity(degreeData.get("MZ"));

            String birthDateString = degreeData.get("CSRQ");
            if (birthDateString != null) {
                try {
                    Date birthDate = null;
                    birthDate = dateFormat1.parse(birthDateString);
                    degreeInfoPO.setBirthDate(birthDate);
                } catch (ParseException e) {
                    log.error("解析出生日期失败 " + degreeData.get("XH"));
                }
            }

            degreeInfoPO.setIdType(degreeData.get("ZJLX"));
            degreeInfoPO.setIdNumber(degreeData.get("SFZH"));
            degreeInfoPO.setPrincipalName(degreeData.get("XZXM"));
            degreeInfoPO.setCertificateName(degreeData.get("ZXXM"));
            degreeInfoPO.setMajorName(degreeData.get("ZYMC"));

            String admissionDateString = degreeData.get("RXSJ");
            if (admissionDateString != null) {
                try {
                    Date admissionDate = null;
                    admissionDate = dateFormat2.parse(admissionDateString);
                    degreeInfoPO.setAdmissionDate(admissionDate);
                } catch (ParseException e) {
                    log.error("解析入学日期失败 " + degreeData.get("XH"));
                }
            }

            String graduationDateString = degreeData.get("BYSJ");
            if (graduationDateString != null) {
                try {
                    Date graduationDate = null;
                    graduationDate = dateFormat2.parse(graduationDateString);
                    degreeInfoPO.setGraduationDate(graduationDate);
                } catch (ParseException e) {
                    log.error("解析毕业日期失败 " + degreeData.get("XH"));
                }
            }

            degreeInfoPO.setStudyPeriod(degreeData.get("XZ"));
            degreeInfoPO.setStudyForm(degreeData.get("PYXS"));
            degreeInfoPO.setDegreeCertificateNumber(degreeData.get("XWZSBH"));

            String degreeDateString = degreeData.get("FZRQ");
            if (degreeDateString != null) {
                try {
                    Date degreeDate = null;
                    degreeDate = dateFormat1.parse(degreeDateString);
                    degreeInfoPO.setDegreeDate(degreeDate);
                } catch (ParseException e) {
                    log.error("解析学位授予日期失败 " + degreeData.get("XH"));
                }
            }

            degreeInfoPO.setDegreeType(degreeData.get("XKML"));
            degreeInfoPO.setDegreeProcessNumber(degreeData.get("JYBH"));
            degreeInfoPO.setDegreeForeignLanguagePassNumber(degreeData.get("HGZH"));
            degreeInfoPO.setCollege(degreeData.get("YX"));
            degreeInfoPO.setGraduationCertificateNumber(degreeData.get("BYZH"));

            String avg = degreeData.get("PJF");
            if (avg != null) {
                // 使用正则表达式检查是否包含非数字字符
                if (!avg.matches("[0-9.]*")) {
                    log.error("异常值: " + avg + " ksh: " + degreeData.get("XH"));
                }
                // 使用正则表达式只保留数字部分
                String cleanedAvg = avg.replaceAll("[^0-9.]", "");
                degreeInfoPO.setAverageScore(BigDecimal.valueOf(Double.parseDouble(cleanedAvg)));
            }

            degreeInfoPO.setAwardingCollege(degreeData.get("BELONGTO"));

            String degreeForeignLanguagePassDateString = degreeData.get("WYKSNY");
            if (degreeForeignLanguagePassDateString != null) {
                try {
                    Date degreeForeignLanguagePassDate = null;
                    degreeForeignLanguagePassDate = dateFormat2.parse(degreeForeignLanguagePassDateString);
                    degreeInfoPO.setDegreeForeignLanguagePassDate(degreeForeignLanguagePassDate);
                } catch (ParseException e) {
                    log.error("解析外语通过日期失败 " + degreeData.get("XH"));
                }
            }

            degreeInfoPO.setDegreePhotoUrl(degreeData.get("XWPIC"));

            int insert = degreeInfoMapper.insert(degreeInfoPO);
        }
    }
}

