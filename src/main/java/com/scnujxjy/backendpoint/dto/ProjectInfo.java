package com.scnujxjy.backendpoint.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProjectInfo {
    private Long id;

    private String name;

    private String description;

    private Date createdAt;

    private Date deadline;

    private Integer creditHours;

    private String resources;
}
