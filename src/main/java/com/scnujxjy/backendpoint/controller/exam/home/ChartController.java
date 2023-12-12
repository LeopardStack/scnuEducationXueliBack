package com.scnujxjy.backendpoint.controller.exam.home;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.vo.home.StatisticTableForStudentStatus;
import com.scnujxjy.backendpoint.service.home.DataStatisticService;
import com.scnujxjy.backendpoint.service.home.StatisticTableForGraduation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 后台管理的首页，用于展示常用的数据
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/home")
public class ChartController {

    @Resource
    private DataStatisticService dataStatisticService;

    @GetMapping("/show_student_status1")
    public SaResult getChartData() {
        ChartOptions chartOptions = new ChartOptions();

        List<Map<String, StatisticTableForStudentStatus>> table1Data = dataStatisticService.getTable1Data(10);

        ChartOptions.Title title = new ChartOptions.Title();
        title.setText("学生数量统计");
        chartOptions.setTitle(title);

        ChartOptions.Legend legend = new ChartOptions.Legend();
        legend.setData(Arrays.asList("在籍学生数量", "毕业学生数量", "获取学位学生数量"));
        chartOptions.setLegend(legend);

        // 从table1Data中提取年份（grade）作为xAxis的数据
        ChartOptions.XAxis xAxis = new ChartOptions.XAxis();
        xAxis.setData(
                table1Data.stream()
                        .map(data -> {
                            String Year = String.valueOf(data.get("grade"));
                            return (Year != null) ? Year.toString() : null;
                        })
                        .filter(Objects::nonNull)  // 过滤掉 null 值
                        .collect(Collectors.toList())
        );
        chartOptions.setXAxis(xAxis);

        // 提取在籍学生数量数据为series
        ChartOptions.Series studentCountSeries = new ChartOptions.Series();
        studentCountSeries.setName("在籍学生数量");
        studentCountSeries.setType("bar");
        studentCountSeries.setData(table1Data.stream().map(map -> Long.parseLong(String.valueOf(map.get("student_count")))).collect(Collectors.toList()));

        // 提取毕业学生数量数据为series
        ChartOptions.Series graduationCountSeries = new ChartOptions.Series();
        graduationCountSeries.setName("毕业学生数量");
        graduationCountSeries.setType("bar");
        graduationCountSeries.setData(table1Data.stream().map(map -> Long.parseLong(String.valueOf(map.get("graduation_count"))))
                .collect(Collectors.toList()));

        // 提取获取学位学生数量数据为series
        ChartOptions.Series degreeCountSeries = new ChartOptions.Series();
        degreeCountSeries.setName("获取学位学生数量");
        degreeCountSeries.setType("bar");
        degreeCountSeries.setData(table1Data.stream().map(map -> Long.parseLong(String.valueOf(map.get("degree_count")))).collect(Collectors.toList()));

        chartOptions.setSeries(Arrays.asList(studentCountSeries, graduationCountSeries, degreeCountSeries));

        return SaResult.data(chartOptions);
    }

    @GetMapping("/show_graduation_data")
    public SaResult getGraduationData() {
        List<Map<String, StatisticTableForGraduation>> graduationData = dataStatisticService.getGraduationDataInRange(10);

        // 封装返回的数据格式
        ChartOptions chartOptions = new ChartOptions();
        ChartOptions.Title title = new ChartOptions.Title();
        title.setText("毕业统计");
        chartOptions.setTitle(title);

        ChartOptions.Legend legend = new ChartOptions.Legend();
        legend.setData(Arrays.asList("7月毕业人数", "1月毕业人数", "年度总毕业人数"));
        chartOptions.setLegend(legend);

        ChartOptions.XAxis xAxis = new ChartOptions.XAxis();
        xAxis.setData(
                graduationData.stream()
                        .map(data -> {
                            String graduationYear = String.valueOf(data.get("graduation_year"));
                            return (graduationYear != null) ? graduationYear.toString() : null;
                        })
                        .filter(Objects::nonNull)  // 过滤掉 null 值
                        .collect(Collectors.toList())
        );
        chartOptions.setXAxis(xAxis);
        chartOptions.setXAxis(xAxis);

        ChartOptions.Series julyGraduationSeries = new ChartOptions.Series();
        julyGraduationSeries.setName("7月毕业人数");
        julyGraduationSeries.setType("bar");
        julyGraduationSeries.setData(graduationData.stream()
                .map(data -> {
                    Object value = data.get("july_graduation_count");
                    return value != null ? Long.parseLong(String.valueOf(value)) : null;
                })
                .filter(Objects::nonNull) // 过滤掉 null 值
                .collect(Collectors.toList()));

        ChartOptions.Series januaryGraduationSeries = new ChartOptions.Series();
        januaryGraduationSeries.setName("1月毕业人数");
        januaryGraduationSeries.setType("bar");
        januaryGraduationSeries.setData(graduationData.stream().map(data -> Long.parseLong(String.valueOf(data.get("january_graduation_count")))).collect(Collectors.toList()));

        ChartOptions.Series annualGraduationSeries = new ChartOptions.Series();
        annualGraduationSeries.setName("年度总毕业人数");
        annualGraduationSeries.setType("bar");
        annualGraduationSeries.setData(graduationData.stream().map(data -> Long.parseLong(String.valueOf(data.get("annual_graduation_count")))).collect(Collectors.toList()));

        chartOptions.setSeries(Arrays.asList(julyGraduationSeries, januaryGraduationSeries, annualGraduationSeries));

        return SaResult.data(chartOptions);
    }

}

