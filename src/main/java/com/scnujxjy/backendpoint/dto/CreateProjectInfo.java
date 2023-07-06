package com.scnujxjy.backendpoint.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author leopard
 */
@Data
public class CreateProjectInfo {
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private String hours;
    private boolean discussionClosed;
    private boolean likesEnabled;
    private boolean commentsClosed;
    private boolean achievementsClosed;
    private boolean livePermissionsClosed;
    private boolean vodPermissionsClosed;
}
