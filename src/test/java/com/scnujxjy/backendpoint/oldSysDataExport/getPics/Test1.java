package com.scnujxjy.backendpoint.oldSysDataExport.getPics;

import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.GraduationInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.GraduationInfoVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.PersonalInfoVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private PersonalInfoMapper personalInfoMapper;

    @Resource
    private GraduationInfoMapper graduationInfoMapper;

    @Resource
    private MinioService minioService;

    @Test
    public void test1(){
        boolean cover = false;  // 不覆盖，也就是说如果照片存在 则不上传覆盖

        PersonalInfoVO personalInfoVOS = personalInfoMapper.
                selectInfoByGradeAndIdNumberOne("2015", "440281199102013825");
        GraduationInfoVO graduationInfoVOS = graduationInfoMapper.selectInfoByGradeAndIdNumberOne("2015", "440281199102013825");

        if(personalInfoVOS != null){
            log.info(personalInfoVOS.toString());
            // 判断入学照片是否在 文件系统中
            boolean isExist = false;
            String entrancePhoto = personalInfoVOS.getEntrancePhoto();
            try {
                byte[] fileFromMinio = minioService.getFileFromMinio(entrancePhoto);
                if (fileFromMinio != null && fileFromMinio.length > 0) {
                    log.info("成功获取到了入学照片");
                    isExist = true;
                }else{
                    log.error("未能获取到入学照片 \n");
                }
            }catch (Exception e){
                log.error("未能获取到入学照片 \n" + e.toString());
            }

            if(isExist && cover){
                try {
                    minioService.uploadFileToMinio(entrancePhoto);
                    log.info("成功上传本地入学照片");
                }catch (Exception e){
                    log.error(e.toString());
                }
            }else if(!isExist){
                try {
                    minioService.uploadFileToMinio(entrancePhoto);
                    log.info("成功上传本地入学照片");
                }catch (Exception e){
                    log.error(e.toString());
                }
            }
            if(graduationInfoVOS != null){
                log.info(graduationInfoVOS.toString());


                // 判断毕业照片是否在系统中
                isExist = false;
                String graduationPhoto = graduationInfoVOS.getGraduationPhoto();
                try {
                    byte[] fileFromMinio = minioService.getFileFromMinio(graduationPhoto);
                    if (fileFromMinio != null && fileFromMinio.length > 0) {
                        log.info("成功获取到了毕业照片");
                        isExist = true;
                    }else{
                        log.error("未能获取到毕业照片 \n");
                    }
                }catch (Exception e){
                    log.error("未能获取到毕业照片 \n" + e.toString());
                }

                if(isExist && cover){
                    try {
                        minioService.uploadFileToMinio(graduationPhoto);
                        log.info("成功上传本地毕业照片");
                    }catch (Exception e){
                        log.error(e.toString());
                    }
                }else if(!isExist){
                    try {
                        minioService.uploadFileToMinio(graduationPhoto);
                        log.info("成功上传本地毕业照片");
                    }catch (Exception e){
                        log.error(e.toString());
                    }
                }


            }
        }

    }

    @Test
    public void test2(){

        String bucketName = minioService.getBucketName();
        PersonalInfoVO personalInfoVOS = personalInfoMapper.
                selectInfoByGradeAndIdNumberOne("2015", "440281199102013825");
        GraduationInfoVO graduationInfoVOS = graduationInfoMapper.selectInfoByGradeAndIdNumberOne("2015", "440281199102013825");

        if(personalInfoVOS != null) {
            log.info(personalInfoVOS.toString());
            if (graduationInfoVOS != null) {
                log.info(graduationInfoVOS.toString());

                // 判断入学照片是否在 文件系统中
                boolean isExist = false;
                String entrancePhoto = personalInfoVOS.getEntrancePhoto();

                try {
                    byte[] fileFromMinio = minioService.getFileFromMinio(entrancePhoto);
                    if (fileFromMinio != null && fileFromMinio.length > 0) {
                        log.info("成功获取到了入学照片1");
                        isExist = true;
                    }else{
                        log.error("未能获取到入学照片1 \n");
                    }
                }catch (Exception e){
                    log.error("未能获取到入学照片1 \n" + e.toString());
                }
                // 去掉桶名
                entrancePhoto = entrancePhoto.replace("./" + bucketName + "/", "");
                byte[] imageAsBytes = minioService.getImageAsBytes(entrancePhoto);
                if(imageAsBytes == null){
                    log.info("未能读到入学照片");
                }else{
                    log.info("读到了入学照片 ");
                }
            }
        }


    }
}
