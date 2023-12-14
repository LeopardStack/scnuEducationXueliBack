package com.scnujxjy.backendpoint.NewStudentImport;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.CourseScheduleTest.CourseScheduleListener;
import com.scnujxjy.backendpoint.CourseScheduleTest.CustomDateConverter;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleExcelImportVO;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    private AdmissionInformationMapper admissionInformationMapper;

    @Resource
    private PersonalInfoMapper personalInfoMapper;
    @Resource
    private PlatformUserMapper platformUserMapper;
    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private MinioClient minioClient;


    public static String extractContentFromFileName(String fileName) {
        // 正则表达式，匹配括号里的内容
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(fileName);

        if (matcher.find()) {
            return matcher.group(1);  // 返回括号里的内容
        }
        return null;  // 如果没有匹配到任何内容，返回null
    }

    /**
     * 使用 easyExcel 读取教学计划
     */
    @Test
    public  void test1(){

        String fileName = "D:\\ScnuWork\\xueli\\xueliBackEnd\\src\\main\\resources\\data\\新生录取数据\\2024级录取数据16410人.xlsx";
        String collegeName = extractContentFromFileName(fileName);
        int headRowNumber = 1;  // 根据你的 Excel 调整这个值
        // 使用ExcelReaderBuilder注册自定义的日期转换器
        AdmissionInformationListener admissionInformationListener = new AdmissionInformationListener(admissionInformationMapper,
                personalInfoMapper, platformUserMapper, collegeName);

        ExcelReaderBuilder readerBuilder = EasyExcel.read(fileName, AdmissionInformationRO.class,admissionInformationListener);

        // 继续你的读取操作
        readerBuilder.sheet().headRowNumber(headRowNumber).doRead();
    }


    /**
     * 清除新生录取信息
     */
    @Test
    public void test1_1(){
        String grade = "2024";
        List<AdmissionInformationPO> admissionInformationPOS = admissionInformationMapper.selectList(new LambdaQueryWrapper<AdmissionInformationPO>()
                .eq(AdmissionInformationPO::getGrade, grade));
        for(AdmissionInformationPO admissionInformationPO: admissionInformationPOS){
            int delete = personalInfoMapper.delete(new LambdaQueryWrapper<PersonalInfoPO>()
                    .eq(PersonalInfoPO::getIdNumber, admissionInformationPO.getIdCardNumber())
                    .eq(PersonalInfoPO::getGrade, grade)
            );
            List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectList(new LambdaQueryWrapper<StudentStatusPO>()
                    .eq(StudentStatusPO::getIdNumber, admissionInformationPO.getIdCardNumber())
            );
            if(studentStatusPOS.isEmpty()){
                int delete1 = platformUserMapper.delete(new LambdaQueryWrapper<PlatformUserPO>()
                        .eq(PlatformUserPO::getUsername, admissionInformationPO.getIdCardNumber()));
            }
        }
    }

    private void uploadFile(String bucketName, File file) throws Exception {
        String objectName = file.getAbsolutePath().substring(rootDirPath.length() + 1).replace("\\", "/");
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(file.getAbsolutePath())
                        .build());
        System.out.println("Uploaded " + file.getPath() + " to " + objectName);
    }

    private void uploadDirectory(String bucketName, File dir) throws Exception {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    uploadDirectory(bucketName, file);
                } else {
                    uploadFile(bucketName, file);
                }
            }
        }
    }

    private static String rootDirPath;
    /**
     * 上传学历教育学生照片
     */
    @Test
    public void testUploadPictures(){
        String bucketName = "xuelistudentpictures";
        String projectRootPath ="D:\\ScnuWork\\xueli\\测试数据\\新生录取\\基础数据";
        File rootDir = new File(projectRootPath, "xuelistudentpictures");
        rootDirPath = rootDir.getAbsolutePath();

        try {
            uploadDirectory(bucketName, rootDir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test2(){
        // 源文件夹路径
        String sourceFolderPath = "D:\\华师工作\\2023年系统建设\\学历教育\\新生录取模板\\2023年成高录取相片（16410人）\\temp";
        // 目标文件夹路径
        String destinationFolderPath = "D:\\华师工作\\2023年系统建设\\学历教育\\新生录取模板\\2023年成高录取相片（16410人）\\xuelistudentpictures\\2024\\import";

        try {
            File sourceFolder = new File(sourceFolderPath);
            File destinationFolder = new File(destinationFolderPath);

            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs();
            }

            // 遍历源文件夹
            traverseAndCopyPhotos(sourceFolder, destinationFolder, admissionInformationMapper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void traverseAndCopyPhotos(File sourceFolder, File destinationFolder, AdmissionInformationMapper admissionInformationMapper) throws IOException {
        File[] files = sourceFolder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归处理子文件夹
                    traverseAndCopyPhotos(file, destinationFolder, admissionInformationMapper);
                } else {
                    // 处理照片文件
                    if (isPhotoFile(file)) {
                        // 生成新的文件名
                        String newFileName = generateNewFileName(file, admissionInformationMapper);
                        // 目标文件路径
                        Path destinationPath = new File(destinationFolder, newFileName).toPath();

                        // 复制文件
                        Files.copy(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                        System.out.println("文件复制成功：" + destinationPath);
                    }
                }
            }
        }
    }

    private static void traverseAndRenamePhotos(File sourceFolder, File destinationFolder, AdmissionInformationMapper admissionInformationMapper) throws IOException {
        File[] files = sourceFolder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归处理子文件夹
                    traverseAndRenamePhotos(file, destinationFolder, admissionInformationMapper);
                } else {
                    // 处理照片文件
                    if (isPhotoFile(file)) {
                        // 生成新的文件名
                        String newFileName = generateNewFileName(file, admissionInformationMapper);
                        // 目标文件路径
                        Path destinationPath = new File(destinationFolder, newFileName).toPath();

                        // 移动并重命名文件
                        Files.move(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                        System.out.println("文件移动成功：" + destinationPath);
                    }
                }
            }
        }
    }

    private static boolean isPhotoFile(File file) {
        // 判断文件是否为照片文件，这里简单地以.jpg和.png为例
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".png");
    }

    private static String getFileNameWithoutExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            // 去掉文件后缀
            return fileName.substring(0, lastDotIndex);
        } else {
            // 如果文件没有后缀，返回整个文件名
            return fileName;
        }
    }

    private static String generateNewFileName(File file, AdmissionInformationMapper admissionInformationMapper) {
        // 根据照片的考生号短号来获取其准考证号 从而插入到系统
        String fileNameWithoutExtension = getFileNameWithoutExtension(file).replace("Z", "");
        List<AdmissionInformationPO> admissionInformationPOs = admissionInformationMapper.selectList(new LambdaQueryWrapper<AdmissionInformationPO>()
                .eq(AdmissionInformationPO::getShortStudentNumber, fileNameWithoutExtension)
                .eq(AdmissionInformationPO::getGrade, "2024")
        );
        if(admissionInformationPOs.size() > 1){
            log.info("存在多份录取数据");
        }else if(admissionInformationPOs.size() == 1){
            AdmissionInformationPO admissionInformationPO = admissionInformationPOs.get(0);

            if(admissionInformationPO != null){
                return admissionInformationPO.getAdmissionNumber() + ".JPG";
            }
        }

        return file.getName();
    }
}
