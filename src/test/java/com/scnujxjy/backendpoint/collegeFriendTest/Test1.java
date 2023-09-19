package com.scnujxjy.backendpoint.collegeFriendTest;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Data
class TransformedData {
    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("性别")
    private String gender;

    @ExcelProperty("身份证")
    private String idCard;

    @ExcelProperty("电话")
    private String phone;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("学院")
    private String college;

    @ExcelProperty("专业")
    private String major;

    @ExcelProperty("学历")
    private String educationLevel;

    @ExcelProperty("学习类型")
    private String studyType;

    @ExcelProperty("入学年份")
    private String admissionYear;

    @ExcelProperty("毕业年份")
    private String graduationYear;

    @ExcelProperty("通讯地址")
    private String mailingAddress;

    @ExcelProperty("居住城市")
    private String cityOfResidence;

    @ExcelProperty("工作单位")
    private String workplace;
}

@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private AdmissionInformationMapper admissionInformationMapper;

    @Test
    public void test1(){
        String path = getClass().getClassLoader().getResource("data/collegeFriend/11.xlsx").getPath();
        List<OriginalData> dataList = EasyExcel.read(path, OriginalData.class, new OriginalDataListener()).sheet().doReadSync();

        List<TransformedData> transformedList = new ArrayList<>();
        for (OriginalData data : dataList) {
            String grade = data.getNJ();
            String sfzh = data.getSFZH();

            List<AdmissionInformationPO> admissionInformationPOS = admissionInformationMapper.selectInfoByGradeAndIdNumber(grade, sfzh);


            TransformedData transformed = new TransformedData();
            transformed.setName(data.getXM());
            transformed.setGender(data.getXB());
            transformed.setIdCard(data.getSFZH());
            transformed.setPhone(data.getLXDH());
            transformed.setEmail("");  // 空
            transformed.setCollege(data.getXSH());
            transformed.setMajor(data.getZYMC());
            transformed.setEducationLevel(data.getCC());
            transformed.setStudyType(data.getXXXS());
            transformed.setAdmissionYear(data.getRXRQ());
            transformed.setGraduationYear(data.getBYRQ());
            if(admissionInformationPOS.size() > 0){
                transformed.setMailingAddress(admissionInformationPOS.get(0).getAddress()); // 空
            }

            transformed.setCityOfResidence(""); // 空
            transformed.setWorkplace(""); // 空

            transformedList.add(transformed);
        }

        // If you want to write the transformed data back to Excel
         String outputPath = "D:\\MyProject\\xueliJYPlatform2\\xueliBackEnd\\src\\main\\resources\\data\\collegeFriend\\transformedData.xlsx";
         EasyExcel.write(outputPath, TransformedData.class).sheet().doWrite(transformedList);
    }

    // Please make sure to define the OriginalDataListener as well.
}


