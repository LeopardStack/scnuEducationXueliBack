package com.scnujxjy.backendpoint.oldSysDataExport;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.oa.MajorChangeRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.oa.MajorChangeRecordMapper;
import com.scnujxjy.backendpoint.model.vo.oa.MajorChangeRecordExcelVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getStudentLuqus;

@SpringBootTest
@Slf4j
public class TestNewStudentMajorChange {

    @Resource
    private MajorChangeRecordMapper majorChangeRecordMapper;

    @Test
    public void test1(){
        String remark = "新生转专业";
        int delete = majorChangeRecordMapper.delete(new LambdaQueryWrapper<MajorChangeRecordPO>()
                .eq(MajorChangeRecordPO::getRemark, remark));
        log.info("清除所有的新生转专业信息 " + delete);
        log.info("是否清除了 " + (majorChangeRecordMapper.selectCount(new LambdaQueryWrapper<MajorChangeRecordPO>()
                .eq(MajorChangeRecordPO::getRemark, remark)) == 0));

        // 初始化一个 HashMap 来存储每一年的转专业人数
        HashMap<String, Integer> majorChangeCountByYear = new HashMap<>();


        for(int insertGrade = 2023; insertGrade >= 2010; insertGrade--){
            String grade = String.valueOf(insertGrade-1);
            if(insertGrade == 2023){
                grade = "-1";
            }
            ArrayList<HashMap<String, String>> studentLuqus = getStudentLuqus(Integer.parseInt(grade));
//            log.info(studentLuqus.toString());

            // 使用 stream 过滤 CHANGEZYMC 为 null 的数据
            ArrayList<HashMap<String, String>> filteredList = studentLuqus.stream()
                    .filter(map -> (map.get("CHANGEZYMC") != null && !map.get("CHANGEZYMC").equalsIgnoreCase("NULL")) ||
                            (map.get("CHANGETIME") != null && !map.get("CHANGETIME").equalsIgnoreCase("NULL")) ||
                            (map.get("CHANGEXXXS") != null && !map.get("CHANGEXXXS").equalsIgnoreCase("NULL")))
                    .collect(Collectors.toCollection(ArrayList::new));

//            log.info("过滤后的列表: " + filteredList.toString());

            majorChangeCountByYear.putIfAbsent(String.valueOf(insertGrade), filteredList.size());

            for(HashMap<String, String> hashMap : filteredList){
                MajorChangeRecordPO majorChangeRecordPO = new MajorChangeRecordPO();
                majorChangeRecordPO.setRemark("新生转专业");
                majorChangeRecordPO.setSerialNumber(Integer.valueOf(hashMap.get("CHANGENUM")));
                majorChangeRecordPO.setNewMajorName(hashMap.get("CHANGEZYMC"));
                majorChangeRecordPO.setNewGrade(String.valueOf(insertGrade));
                majorChangeRecordPO.setNewStudyForm(hashMap.get("CHANGEXXXS"));
                majorChangeRecordPO.setIdNumber(hashMap.get("SFZH"));
                majorChangeRecordPO.setExamRegistrationNumber(hashMap.get("ZKZH"));
                majorChangeRecordPO.setStudentName(hashMap.get("XM"));
                majorChangeRecordPO.setOldGrade(String.valueOf(insertGrade));
                majorChangeRecordPO.setOldMajorName(hashMap.get("ZYMC"));
                majorChangeRecordPO.setOldStudyForm(hashMap.get("XXXS"));
                majorChangeRecordPO.setLevel(hashMap.get("PYCC"));

                try{
                    // 假设你从 hashMap 中获取到的日期字符串是 "20230308"
                    String originalDateStr = hashMap.get("CHANGETIME");

                    // 解析日期
                    DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    LocalDate date = LocalDate.parse(originalDateStr, originalFormatter);

                    // 添加时间部分 - 这里我设为午夜00:00:00，你可以根据需要调整
                    LocalDateTime dateTime = date.atTime(9, 40, 17); // 或者使用 LocalDateTime.now() 获取当前时间

                    // 格式化为新的格式
                    DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    String formattedDate = dateTime.format(newFormatter);

                    // 设置到 PO 对象
                    majorChangeRecordPO.setApprovalDate(formattedDate);
                    majorChangeRecordPO.setCurrentYear(String.valueOf(insertGrade));

                    majorChangeRecordPO.setApprovalDate(hashMap.get("CHANGETIME"));
                    majorChangeRecordPO.setCurrentYear(String.valueOf(insertGrade));

                    Integer i = majorChangeRecordMapper.selectCount(new LambdaQueryWrapper<MajorChangeRecordPO>()
                            .eq(MajorChangeRecordPO::getOldGrade, String.valueOf(insertGrade))
                            .eq(MajorChangeRecordPO::getIdNumber, majorChangeRecordPO.getIdNumber())
                            .eq(MajorChangeRecordPO::getRemark, "新生转专业")
                    );
                    if(i > 0){
                        log.error("该新生转专业记录已经存在了 ");
                    }else{
                        int insert = majorChangeRecordMapper.insert(majorChangeRecordPO);
                        if(insert > 0){
//                        log.info("插入成功");
                        }else{
                            log.error("新生转专业插入失败 " + insertGrade);
                        }
                    }
                }catch (Exception e){
                    log.error("解析日期失败 " + hashMap + "\n" + e);
                }
            }

            // 在循环外部打印每年的转专业人数统计
            majorChangeCountByYear.forEach((year, count) -> {
                log.info("Year " + year + ": " + count + " major changes");
            });

        }

    }


    @Test
    public void test2(){
        int insertGrade = 2023;
        String grade = String.valueOf(insertGrade-1);
        if(insertGrade == 2023){
            grade = "-1";
        }
        ArrayList<HashMap<String, String>> studentLuqus = getStudentLuqus(Integer.parseInt(grade));
//            log.info(studentLuqus.toString());

        // 使用 stream 过滤 CHANGEZYMC 为 null 的数据
        ArrayList<HashMap<String, String>> filteredList = studentLuqus.stream()
                .filter(map -> (map.get("CHANGEZYMC") != null && !map.get("CHANGEZYMC").equalsIgnoreCase("NULL")) ||
                        (map.get("CHANGETIME") != null && !map.get("CHANGETIME").equalsIgnoreCase("NULL")) ||
                        (map.get("CHANGEXXXS") != null && !map.get("CHANGEXXXS").equalsIgnoreCase("NULL")))
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<MajorChangeRecordExcelVO> majorChangeRecordList = new ArrayList<>();

        for(HashMap<String, String> hashMap : filteredList) {
            MajorChangeRecordExcelVO majorChangeRecordPO = new MajorChangeRecordExcelVO();
            majorChangeRecordPO.setRemark("新生转专业");
            majorChangeRecordPO.setSerialNumber(Integer.valueOf(hashMap.get("CHANGENUM")));
            majorChangeRecordPO.setNewMajorName(hashMap.get("CHANGEZYMC"));
            majorChangeRecordPO.setNewGrade(String.valueOf(insertGrade));
            majorChangeRecordPO.setNewStudyForm(hashMap.get("CHANGEXXXS"));
            majorChangeRecordPO.setIdNumber(hashMap.get("SFZH"));
            majorChangeRecordPO.setExamRegistrationNumber(hashMap.get("ZKZH"));
            majorChangeRecordPO.setStudentName(hashMap.get("XM"));
            majorChangeRecordPO.setOldGrade(String.valueOf(insertGrade));
            majorChangeRecordPO.setOldMajorName(hashMap.get("ZYMC"));
            majorChangeRecordPO.setOldStudyForm(hashMap.get("XXXS"));
            majorChangeRecordPO.setLevel(hashMap.get("PYCC"));

            try {
                // 假设你从 hashMap 中获取到的日期字符串是 "20230308"
                String originalDateStr = hashMap.get("CHANGETIME");

                // 解析日期
                DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate date = LocalDate.parse(originalDateStr, originalFormatter);

                // 添加时间部分 - 这里我设为午夜00:00:00，你可以根据需要调整
                LocalDateTime dateTime = date.atTime(9, 40, 17); // 或者使用 LocalDateTime.now() 获取当前时间

                // 格式化为新的格式
                DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                String formattedDate = dateTime.format(newFormatter);

                // 设置到 PO 对象
                majorChangeRecordPO.setApprovalDate(formattedDate);
                majorChangeRecordPO.setCurrentYear(String.valueOf(insertGrade));

                majorChangeRecordPO.setApprovalDate(hashMap.get("CHANGETIME"));
                majorChangeRecordPO.setCurrentYear(String.valueOf(insertGrade));

                majorChangeRecordList.add(majorChangeRecordPO);
            } catch (Exception e) {
                log.error("出现异常 " + e);
            }
        }

        // 确定文件路径
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String relativePath = "data_import_error_excel/majorChangeData/";
        String fileName = "MajorChangeRecord_" + currentDateTime + ".xlsx";
        String fullPath = relativePath + fileName;

        // 创建目录
        File directory = new File(relativePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 使用EasyExcel写入数据
        try {
            EasyExcel.write(fullPath, MajorChangeRecordExcelVO.class).sheet("Sheet1").doWrite(majorChangeRecordList);
            System.out.println("Excel写入成功，路径：" + fullPath);
        } catch (Exception e) {
            log.error("写入Excel时出现异常", e);
        }
    }
}
