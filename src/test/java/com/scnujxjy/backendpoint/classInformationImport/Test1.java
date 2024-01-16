package com.scnujxjy.backendpoint.classInformationImport;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.NewStudentImport.AdmissionInformationListener;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.MajorInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointInformationMapper;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationConfirmRO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationOldSystemImportVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationOldSystemImportVO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.excelListener.ClassInformationListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private MajorInformationMapper majorInformationMapper;

    @Resource
    private AdmissionInformationMapper admissionInformationMapper;

    @Resource
    private CollegeInformationMapper collegeInformationMapper;

    @Resource
    private TeachingPointInformationMapper teachingPointInformationMapper;

    @Resource
    private MinioService minioService;


    @Test
    public void test1(){
        String fileName = "D:\\ScnuWork\\xueli\\xueliBackEnd\\src\\main\\resources\\data\\开班分班\\2023年成教开班情况统计表（2024级）.xlsx";
        int headRowNumber = 2;  // 根据你的 Excel 调整这个值
        // 使用ExcelReaderBuilder注册自定义的日期转换器
        ClassInformationListener classInformationListener = new ClassInformationListener(admissionInformationMapper,
                majorInformationMapper, collegeInformationMapper, teachingPointInformationMapper);

        ExcelReaderBuilder readerBuilder = EasyExcel.read(fileName, ClassInformationConfirmRO.class,classInformationListener);

        // 继续你的读取操作
        readerBuilder.sheet().headRowNumber(headRowNumber).doRead();
    }

    /**
     * 获取新生数据
     */
    @Test
    public void test(){
        List<AdmissionInformationPO> admissionInformationPOS = admissionInformationMapper.selectList(new LambdaQueryWrapper<AdmissionInformationPO>()
                .eq(AdmissionInformationPO::getGrade, "2024"));
        log.info("获取到了 2024级新生 " + admissionInformationPOS.size());
        List<AdmissionInformationOldSystemImportVO> admissionInformationOldSystemImportVOList = new ArrayList<>();

        for(AdmissionInformationPO admissionInformationPO : admissionInformationPOS){
            AdmissionInformationOldSystemImportVO admissionInformationOldSystemImportVO = new AdmissionInformationOldSystemImportVO();
            admissionInformationOldSystemImportVO.setShortStudentNumber(admissionInformationPO.getShortStudentNumber());
            admissionInformationOldSystemImportVO.setName(admissionInformationPO.getName());
            admissionInformationOldSystemImportVO.setGender(admissionInformationPO.getGender());
            admissionInformationOldSystemImportVO.setTotalScore(String.valueOf(admissionInformationPO.getTotalScore()));
            admissionInformationOldSystemImportVO.setMajorCode(admissionInformationPO.getMajorCode());
            admissionInformationOldSystemImportVO.setMajorName(admissionInformationPO.getMajorName());
            admissionInformationOldSystemImportVO.setLevel(admissionInformationPO.getLevel());
            admissionInformationOldSystemImportVO.setStudyForm(admissionInformationPO.getStudyForm());
            admissionInformationOldSystemImportVO.setGraduationSchool(admissionInformationPO.getGraduationSchool());

            Date graduationDate = admissionInformationPO.getGraduationDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            admissionInformationOldSystemImportVO.setGraduationDate(formatter.format(graduationDate));

            admissionInformationOldSystemImportVO.setStudentPhoneNumber(admissionInformationPO.getPhoneNumber());
            admissionInformationOldSystemImportVO.setIdCardNumber(admissionInformationPO.getIdCardNumber());
            admissionInformationOldSystemImportVO.setBirthDate(formatter.format(admissionInformationPO.getBirthDate()));
            admissionInformationOldSystemImportVO.setStudentAddress(admissionInformationPO.getAddress());
            admissionInformationOldSystemImportVO.setPostalCode(admissionInformationPO.getPostalCode());
            admissionInformationOldSystemImportVO.setOriginalEducation(admissionInformationPO.getOriginalEducation());
            admissionInformationOldSystemImportVO.setEthnicity(admissionInformationPO.getEthnicity());
            admissionInformationOldSystemImportVO.setPoliticalStatus(admissionInformationPO.getPoliticalStatus());
            admissionInformationOldSystemImportVO.setAdmissionNumber(admissionInformationPO.getAdmissionNumber());

            admissionInformationOldSystemImportVOList.add(admissionInformationOldSystemImportVO);

            // 获取照片 存到指定目录
            String entrancePhotoUrl = admissionInformationPO.getEntrancePhotoUrl();
            try {
                byte[] fileFromMinio = minioService.getFileFromMinio(entrancePhotoUrl);
                byte[] resizedImage = resizeImage(fileFromMinio, 180, 240); // 调整图片尺寸
                String photoNewName = "Z" + admissionInformationPO.getShortStudentNumber() + ".JPG";
                savePhoto(resizedImage, "D:\\ScnuWork\\xueli\\新生数据导入\\2024新生入学照片", photoNewName);
            } catch (IOException e) {
                log.error("处理学生入学照片失败: " + e.getMessage());
                // 可以选择是否继续处理下一个学生
            }
        }

        if(!admissionInformationOldSystemImportVOList.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("Asia/Shanghai"));

            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/旧系统导入所需数据";
            String errorFileName = "2024" + "_新生导入数据.xlsx";

            // 检查目录是否存在，不存在则创建
            File directory = new File(relativePath);
            if (!directory.exists()) {
                directory.mkdirs(); // 这将创建所需的所有目录，即使中间目录不存在
            }

            EasyExcel.write(relativePath + "/" + errorFileName, AdmissionInformationOldSystemImportVO.class).sheet("Sheet1").doWrite(admissionInformationOldSystemImportVOList);
        }else{
            log.info("未拿到任何开班数据");
        }
    }

    /**
     * 保存照片到指定目录
     * @param photoBytes 照片的字节流
     * @param directoryPath 目录路径
     * @param fileName 文件名
     * @throws IOException 如果发生IO异常
     */
    private void savePhoto(byte[] photoBytes, String directoryPath, String fileName) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (!isCreated) {
                log.error("创建目录失败: " + directoryPath);
                return;
            }
        }
        File photoFile = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(photoFile)) {
            fos.write(photoBytes);
        }
    }

    public static byte[] resizeImage(byte[] imageBytes, int width, int height) throws IOException {
        InputStream is = new ByteArrayInputStream(imageBytes);
        BufferedImage originalImage = ImageIO.read(is);

        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "JPG", baos);
        return baos.toByteArray();
    }
}
