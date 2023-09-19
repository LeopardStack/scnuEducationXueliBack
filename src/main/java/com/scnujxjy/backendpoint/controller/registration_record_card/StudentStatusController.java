package com.scnujxjy.backendpoint.controller.registration_record_card;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentAllStatusInfoVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 学籍信息表
 *
 * @author leopard
 * @since 2023-08-04
 */
@RestController
@RequestMapping("/student-status")
public class StudentStatusController {

    @Resource
    private StudentStatusService studentStatusService;

    @Resource
    private MinioService minioService;

    /**
     * 根据id查询学籍信息
     *
     * @param id 学籍信息id
     * @return 学籍信息
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        // 校验参数
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查询
        StudentStatusVO studentStatusVO = studentStatusService.detailById(id);
        if (Objects.isNull(studentStatusVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(studentStatusVO);
    }

    /**
     * 分页查询学籍信息
     *
     * @param studentStatusROPageRO 分页参数
     * @return 学籍信息列表
     */
    @PostMapping("/page")
    public SaResult pageQueryStudentStatus(@RequestBody PageRO<StudentStatusRO> studentStatusROPageRO) {
        // 校验参数
        if (Objects.isNull(studentStatusROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(studentStatusROPageRO.getEntity())) {
            studentStatusROPageRO.setEntity(new StudentStatusRO());
        }
        // 查询
        PageVO<StudentStatusVO> studentStatusVOPageVO = studentStatusService.pageQueryStudentStatus(studentStatusROPageRO);
        if (Objects.isNull(studentStatusVOPageVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(studentStatusVOPageVO);
    }

    /**
     * 更新学籍信息
     *
     * @param studentStatusRO 学籍信息
     * @return 更新后的学籍信息
     */
    @PutMapping("/edit")
    public SaResult editById(StudentStatusRO studentStatusRO) {
        // 校验参数
        if (Objects.isNull(studentStatusRO) || Objects.isNull(studentStatusRO.getId())) {
            throw dataMissError();
        }
        // 更新学籍信息
        StudentStatusVO studentStatusVO = studentStatusService.editById(studentStatusRO);
        if (Objects.isNull(studentStatusVO)) {
            throw dataUpdateError();
        }
        return SaResult.data(studentStatusVO);
    }

    /**
     * 删除学籍信息
     *
     * @param id 学籍信息id
     * @return 删除学籍信息的数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(Long id) {
        // 校验参数
        if (Objects.isNull(id)) {
            throw dataDeleteError();
        }
        // 删除学籍信息
        int count = studentStatusService.deleteById(id);
        if (count <= 0) {
            throw dataDeleteError();
        }
        return SaResult.data(count);
    }

    /**
     * 根据学生用户 id 查询自己的学籍信息
     *
     * @return 学籍信息
     */
    @GetMapping("/query_student_status_information")
    public SaResult queryStudentStatusInformation() {
        Object loginId = StpUtil.getLoginId();
        String studentId = null;
        // 校验参数
        if (Objects.isNull(loginId)) {
            throw dataMissError();
        }else{
            try{
                studentId = (String)loginId;
            }catch (Exception e){
                throw e;
            }
        }
        // 查询
        List<StudentAllStatusInfoVO> studentAllStatusInfoVOS = studentStatusService.statusInfoByIdNumber(studentId);
        if (Objects.isNull(studentAllStatusInfoVOS)) {
            throw dataNotFoundError();
        }
        return SaResult.data(studentAllStatusInfoVOS);
    }

    /**
     * 根据 用户名查询学籍信息
     *
     * @return 学籍信息
     */
    @GetMapping("/detail_username")
    public SaResult detailByUserName() {
        String loginId = (String)StpUtil.getLoginId();
        // 校验参数
        if (Objects.isNull(loginId) || loginId.length() == 0) {
            throw dataMissError();
        }
        // 查询
        List<StudentStatusVO> studentStatusVOs = studentStatusService.getBaseMapper().
                selectStudentByidNumber(loginId);
        if (Objects.isNull(studentStatusVOs) || studentStatusVOs.size() == 0) {
            throw dataNotFoundError();
        }

        // 使用流操作找到最大的 grade 值
        Optional<StudentStatusVO> maxGradeStudent = studentStatusVOs.stream()
                .max(Comparator.comparing(StudentStatusVO::getGrade));

        // 检查是否找到最大 grade 值的对象
        StudentStatusVO result = null;
        result = maxGradeStudent.orElseGet(() -> studentStatusVOs.get(0));

        return SaResult.data(result);
    }

    /**
     * 获取入学照片
     * @param grade 年级
     * @return
     */
    @GetMapping("/searchImportPhoto/{grade}")
    public ResponseEntity<byte[]> getImportPhoto(@PathVariable String grade) {
        byte[] photoBytes = studentStatusService.getImportPhoto(grade);

        if (photoBytes != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(photoBytes.length)
                    .body(photoBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取毕业照片
     * @param grade 年级
     * @return
     */
    @GetMapping("/searchExportPhoto/{grade}")
    public ResponseEntity<byte[]> getExportPhoto(@PathVariable String grade) {
        byte[] photoBytes = studentStatusService.getExportPhoto(grade);

        if (photoBytes != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(photoBytes.length)
                    .body(photoBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取学位照片
     * @param payload 学位照片的URL
     * @return
     */
    @PostMapping("/searchDegreePhoto")
    public ResponseEntity<byte[]> getDegreePhoto(@RequestBody Map<String, String> payload) {
        String degreePhotoUrl = payload.get("degreePhotoUrl");

        // 根据 degreePhotoUrl 获取学位照片的字节数据
        byte[] photoBytes = studentStatusService.getDegreePhotoByURL(degreePhotoUrl);

        if (photoBytes != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(photoBytes.length)
                    .body(photoBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

