package com.scnujxjy.backendpoint.service.core_data;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.DownloadFileNameEnum;
import com.scnujxjy.backendpoint.constant.enums.MinioBucketEnum;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.DownloadMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.DownloadMessageMapper;
import com.scnujxjy.backendpoint.inverter.core_data.PaymentInfoInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.core_data.*;
import com.scnujxjy.backendpoint.model.vo.teaching_process.FilterDataVO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.platform_message.PlatformMessageService;
import com.scnujxjy.backendpoint.util.filter.AbstractFilter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 缴费信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class PaymentInfoService extends ServiceImpl<PaymentInfoMapper, PaymentInfoPO> implements IService<PaymentInfoPO> {
    @Resource
    private PaymentInfoInverter paymentInfoInverter;

    @Resource
    private MinioService minioService;

    @Resource
    private PlatformMessageService platformMessageService;

    @Resource
    private DownloadMessageMapper downloadMessageMapper;

    private RestTemplate restTemplate;
    @Bean
    public RestTemplate restTemplate() {
        restTemplate = new RestTemplate();

        // 添加 StringHttpMessageConverter 以处理 text/html 响应
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        return restTemplate;
    }


    public ResponseEntity<String> makePayment(){
        String url = "http://gx1.szhtkj.com.cn/micro/payAccept.aspx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("orderDate", "20231123001510");
        params.add("orderNo", "2311220004");
        params.add("amount", "0.01");
        params.add("xmpch", "004-2014050001");
        params.add("return_url", "http://www.test.com/returnPage.htm");
        params.add("notify_url", "http://www.test.com/notifyPage.htm");
        params.add("sign", "8fa93f9eb38bfe6232d3872b0e34b39d");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
//        String response = restTemplate.postForObject(url, requestEntity, String.class);

        // 发送 post 请求
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        return response;
    }

    /**
     * 通过id查询详情
     *
     * @param id 主键id
     * @return 支付信息详情
     */
    public PaymentInfoVO detailById(Long id) {
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        PaymentInfoPO paymentInfoPO = baseMapper.selectById(id);
        return paymentInfoInverter.po2VO(paymentInfoPO);
    }

    /**
     * 分页查询、批量查询支付信息详情列表
     *
     * @param paymentInfoROPageRO 支付信息分页参数
     * @return 支付信息详情分页列表
     */
    public PageVO<PaymentInfoVO> pageQueryPaymentInfo(PageRO<PaymentInfoRO> paymentInfoROPageRO) {
        // 参数校验
        if (Objects.isNull(paymentInfoROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        PaymentInfoRO entity = paymentInfoROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new PaymentInfoRO();
        }
        // 构造查询条件
        LambdaQueryWrapper<PaymentInfoPO> wrapper = Wrappers.<PaymentInfoPO>lambdaQuery()
                .eq(Objects.nonNull(entity.getId()), PaymentInfoPO::getId, entity.getId())
                .eq(StrUtil.isNotBlank(entity.getStudentNumber()), PaymentInfoPO::getStudentNumber, entity.getStudentNumber())
                .eq(StrUtil.isNotBlank(entity.getAdmissionNumber()), PaymentInfoPO::getAdmissionNumber, entity.getAdmissionNumber())
                .like(StrUtil.isNotBlank(entity.getName()), PaymentInfoPO::getName, entity.getName())
                .eq(StrUtil.isNotBlank(entity.getIdCardNumber()), PaymentInfoPO::getIdCardNumber, entity.getIdCardNumber())
                .between(Objects.nonNull(entity.getPaymentBeginDate()) && Objects.nonNull(entity.getPaymentEndDate()),
                        PaymentInfoPO::getPaymentDate, entity.getPaymentBeginDate(), entity.getPaymentEndDate())
                .eq(StrUtil.isNotBlank(entity.getPaymentCategory()), PaymentInfoPO::getPaymentCategory, entity.getPaymentCategory())
                .eq(StrUtil.isNotBlank(entity.getAcademicYear()), PaymentInfoPO::getAcademicYear, entity.getAcademicYear())
                .eq(StrUtil.isNotBlank(entity.getPaymentType()), PaymentInfoPO::getPaymentType, entity.getPaymentType())
                .eq(Objects.nonNull(entity.getAmount()), PaymentInfoPO::getAmount, entity.getAmount())
                .eq(StrUtil.isNotBlank(entity.getIsPaid()), PaymentInfoPO::getIsPaid, entity.getIsPaid())
                .eq(StrUtil.isNotBlank(entity.getPaymentMethod()), PaymentInfoPO::getPaymentMethod, entity.getPaymentMethod())
                .last(StrUtil.isNotBlank(paymentInfoROPageRO.getOrderBy()), paymentInfoROPageRO.lastOrderSql());

        // 根据参数选择是否查询全部还是分页查询，并返回
        if (Objects.equals(true, paymentInfoROPageRO.getIsAll())) {
            List<PaymentInfoPO> paymentInfoPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(paymentInfoInverter.po2VO(paymentInfoPOS));
        } else {
            Page<PaymentInfoPO> paymentInfoPOPage = baseMapper.selectPage(paymentInfoROPageRO.getPage(), wrapper);
            return new PageVO<>(paymentInfoPOPage, paymentInfoInverter.po2VO(paymentInfoPOPage.getRecords()));
        }
    }

    /**
     * 根据id删除支付信息
     *
     * @param id 主键id
     * @return 删除数量
     */
    public Integer deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 删除
        int count = baseMapper.deleteById(id);
        if (count <= 0) {
            log.error("删除失败，id：{}", id);
        }
        return count;
    }

    /**
     * 根据id更新支付信息
     *
     * @param paymentInfoRO 支付信息
     * @return 更新后的支付信息
     */
    public PaymentInfoVO editById(PaymentInfoRO paymentInfoRO) {
        // 参数校验
        if (Objects.isNull(paymentInfoRO) || Objects.isNull(paymentInfoRO.getId())) {
            log.error("参数缺失");
            return null;
        }
        // 更新信息
        PaymentInfoPO paymentInfoPO = paymentInfoInverter.ro2PO(paymentInfoRO);
        int count = baseMapper.updateById(paymentInfoPO);
        if (count <= 0) {
            log.error("更新失败，数据为：{}", paymentInfoPO);
            return null;
        }
        return detailById(paymentInfoRO.getId());
    }


    /**
     * 根据角色筛选器 获取缴费信息
     * @param paymentInfoFilterROPageRO 缴费筛选参数
     * @param filter 角色筛选器
     * @return
     */
    public FilterDataVO allPageQueryPayInfoFilter(PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO,
                                                  @NotNull AbstractFilter filter) {
        return filter.filterPayInfo(paymentInfoFilterROPageRO);
    }

    /**
     * 获取缴费数据的筛选参数
     * @param loginId 登录用户名
     * @param filter 筛选参数（如果是其他用户则需要额外的限制参数，二级学院、教师、教学点）
     * @return
     */
    public PaymentInformationSelectArgs getStudentStatusArgs(String loginId, AbstractFilter filter) {
        return filter.filterPaymentInformationSelectArgs();
    }

    /**
     * 批量导出下载缴费数据
     * @param pageRO
     * @param filter
     * @param userId
     * @param platformMessagePO
     */
    public void generateBatchPaymentData(PageRO<PaymentInfoFilterRO> pageRO, AbstractFilter filter, String userId, PlatformMessagePO platformMessagePO) {
        // 校验参数
        if (Objects.isNull(pageRO)) {
            log.error("导出班级信息数据参数缺失");

        }


        filter.exportPaymentInfoData(pageRO, userId, platformMessagePO);
    }

    /**
     * 获取新生的缴费信息
     * @param paymentInfoFilterROPageRO
     * @param filter
     * @return
     */
    public FilterDataVO allPageQueryNewStudentPayInfoFilter(PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO, AbstractFilter filter) {
        return filter.filterNewStudentPayInfo(paymentInfoFilterROPageRO);
    }

    /**
     * 获取新生缴费数据的筛选参数
     * @param paymentInfoFilterRO
     * @param filter
     * @return
     */
    public PaymentInformationSelectArgs getNewStudentPaymentInfoArgs(PaymentInfoFilterRO paymentInfoFilterRO, AbstractFilter filter) {
        return filter.getNewStudentPaymentInfoArgs(paymentInfoFilterRO);
    }

    /**
     * 批量导出新生缴费数据
     * @param entity
     * @param platformMessagePO
     */
    @Async
    public void exportNewStudentFeeData(PaymentInfoFilterRO entity, PlatformMessagePO platformMessagePO, String loginId) {
        List<NewStudentPaymentInfoExcelVO> paymentInfoVOS = getBaseMapper().exportNewStudentPayInfoByFilter(entity);
        log.info("导出了 " + paymentInfoVOS.size() + " 条新生缴费数据");

        // 为每个StudentStatusAllVO对象设置序号
        for (int i = 0; i < paymentInfoVOS.size(); i++) {
            paymentInfoVOS.get(i).setIndex(i + 1);
        }

        // 获取所有带有@ExcelProperty注解的字段
        List<String> includeColumnFiledNames = Arrays.stream(NewStudentPaymentInfoExcelVO.class.getDeclaredFields())
                .filter(field -> field.getAnnotation(ExcelProperty.class) != null)
                .map(Field::getName)
                .collect(Collectors.toList());

        // 使用 ByteArrayOutputStream 将数据写入到流中
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, NewStudentPaymentInfoExcelVO.class)
                .includeColumnFiledNames(includeColumnFiledNames)  // 只导出带有@ExcelProperty注解的字段
                .sheet("新生缴费数据")
                .doWrite(paymentInfoVOS);

        // 将流转换为 ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // 获取文件大小
        int fileSize = outputStream.size();
        // 获取桶名和子目录
        String bucketName = MinioBucketEnum.DATA_DOWNLOAD_STUDENT_FEES.getBucketName();
        String subDirectory = MinioBucketEnum.DATA_DOWNLOAD_STUDENT_FEES.getSubDirectory();

        // 使用当前日期和时间作为文件名的一部分
        Date generateData = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        String currentDateTime = sdf.format(generateData);

        // 构建文件名
        String fileName = subDirectory + "/" + loginId +
                "_" + currentDateTime + "_新生缴费数据.xlsx";

        // 上传到 Minio
        boolean b = minioService.uploadStreamToMinio(inputStream, fileName, bucketName);

        // 如果上传成功了 则修改数据库中的用户下载消息
        if (b) {
            DownloadMessagePO downloadMessagePO = new DownloadMessagePO();
            downloadMessagePO.setCreatedAt(generateData);
            downloadMessagePO.setFileName(DownloadFileNameEnum.ADMISSION_STUDENTS_PAY_EXPORT_FILE.getFilename());
            downloadMessagePO.setFileMinioUrl(bucketName + "/" + fileName);
            downloadMessagePO.setFileSize((long) fileSize);
            int insert = downloadMessageMapper.insert(downloadMessagePO);
            log.info("下载新生缴费数据、下载文件消息插入 " + insert);

            // 获取自增ID
            platformMessagePO.setRelatedMessageId(downloadMessagePO.getId());
            platformMessageService.getBaseMapper().update(platformMessagePO, new LambdaQueryWrapper<PlatformMessagePO>().
                    eq(PlatformMessagePO::getId, platformMessagePO.getId()));

            log.info("下载新生缴费信息数据完成 " + insert);
        }
    }

    /**
     * 导出未缴费的新生数据
     * @param entity
     * @param platformMessagePO
     */
    public void exportNewStudentNotPayData(PaymentInfoFilterRO entity, PlatformMessagePO platformMessagePO, String loginId) {
        List<NewStudentNotPayExcelVO> paymentInfoVOS = getBaseMapper().exportNewStudentNotPayInfoByFilter(entity);
        log.info("导出了 " + paymentInfoVOS.size() + " 条新生未缴费数据");

        // 为每个StudentStatusAllVO对象设置序号
        for (int i = 0; i < paymentInfoVOS.size(); i++) {
            paymentInfoVOS.get(i).setIndex(i + 1);
        }

        // 获取所有带有@ExcelProperty注解的字段
        List<String> includeColumnFiledNames = Arrays.stream(NewStudentNotPayExcelVO.class.getDeclaredFields())
                .filter(field -> field.getAnnotation(ExcelProperty.class) != null)
                .map(Field::getName)
                .collect(Collectors.toList());

        // 使用 ByteArrayOutputStream 将数据写入到流中
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, NewStudentNotPayExcelVO.class)
                .includeColumnFiledNames(includeColumnFiledNames)  // 只导出带有@ExcelProperty注解的字段
                .sheet("新生未缴费数据")
                .doWrite(paymentInfoVOS);

        // 将流转换为 ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // 获取文件大小
        int fileSize = outputStream.size();
        // 获取桶名和子目录
        String bucketName = MinioBucketEnum.DATA_DOWNLOAD_STUDENT_FEES.getBucketName();
        String subDirectory = MinioBucketEnum.DATA_DOWNLOAD_STUDENT_FEES.getSubDirectory();

        // 使用当前日期和时间作为文件名的一部分
        Date generateData = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        String currentDateTime = sdf.format(generateData);

        // 构建文件名
        String fileName = subDirectory + "/" + loginId +
                "_" + currentDateTime + "_新生未缴费数据.xlsx";

        // 上传到 Minio
        boolean b = minioService.uploadStreamToMinio(inputStream, fileName, bucketName);

        // 如果上传成功了 则修改数据库中的用户下载消息
        if (b) {
            DownloadMessagePO downloadMessagePO = new DownloadMessagePO();
            downloadMessagePO.setCreatedAt(generateData);
            downloadMessagePO.setFileName(DownloadFileNameEnum.ADMISSION_STUDENTS_NOT_PAY_EXPORT_FILE.getFilename());
            downloadMessagePO.setFileMinioUrl(bucketName + "/" + fileName);
            downloadMessagePO.setFileSize((long) fileSize);
            int insert = downloadMessageMapper.insert(downloadMessagePO);
            log.info("下载新生未缴费数据、下载文件消息插入 " + insert);

            // 获取自增ID
            platformMessagePO.setRelatedMessageId(downloadMessagePO.getId());
            platformMessageService.getBaseMapper().update(platformMessagePO, new LambdaQueryWrapper<PlatformMessagePO>().
                    eq(PlatformMessagePO::getId, platformMessagePO.getId()));

            log.info("下载新生未缴费数据完成 " + insert);
        }
    }
}
