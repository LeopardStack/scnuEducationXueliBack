package com.scnujxjy.backendpoint.controller.home;

import lombok.Data;

import java.util.List;

@Data
public class ChartOptions {

    private Title title;
    private EmptyObject tooltip = new EmptyObject();
    private Legend legend;
    private XAxis xAxis;
    private EmptyObject yAxis = new EmptyObject();
    private List<Series> series;

    @Data
    public static class EmptyObject {
        // 这是一个空类，仅用于解决序列化问题
        int id;
    }

    // 其他的DTOs定义
    @Data
    public static class Title {
        private String text;

        // getters and setters
    }

    @Data
    public static class Legend {
        private List<String> data;

        // getters and setters
    }

    @Data
    public static class XAxis {
        private List<String> data;

        // getters and setters
    }

    @Data
    public static class Series {
        private String name;
        private String type;
        private List<Long> data;

        // getters and setters
    }

    // ChartOptions 的 getters 和 setters
}

