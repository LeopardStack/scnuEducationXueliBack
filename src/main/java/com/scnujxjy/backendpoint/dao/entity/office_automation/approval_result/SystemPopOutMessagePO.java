package com.scnujxjy.backendpoint.dao.entity.oa;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统消息表，用于存储系统发送的消息
 */
@ApiModel(description="系统消息表，用于存储系统发送的消息")
@Builder
public class SystemPopOutMessagePO implements Serializable {
    /**
    * 消息ID，自增长
    */
    @ApiModelProperty(value="消息ID，自增长")
    private Integer id;

    /**
    * 用户ID，不能为空
    */
    @ApiModelProperty(value="用户ID，不能为空")
    private String userId;

    /**
    * 标题，不能为空
    */
    @ApiModelProperty(value="标题，不能为空")
    private String title;

    /**
    * 内容，不能为空
    */
    @ApiModelProperty(value="内容，不能为空")
    private String content;

    /**
    * 消息类型
    */
    @ApiModelProperty(value="消息类型")
    private String messageType;

    /**
    * 创建时间，不能为空
    */
    @ApiModelProperty(value="创建时间，不能为空")
    private Date createdAt;

    /**
    * 优先级
    */
    @ApiModelProperty(value="优先级")
    private Integer priority;

    /**
    * 创建者
    */
    @ApiModelProperty(value="创建者")
    private String createdBy;

    /**
    * 目标群体
    */
    @ApiModelProperty(value="目标群体")
    private String targetGroup;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        SystemPopOutMessagePO other = (SystemPopOutMessagePO) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getMessageType() == null ? other.getMessageType() == null : this.getMessageType().equals(other.getMessageType()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getPriority() == null ? other.getPriority() == null : this.getPriority().equals(other.getPriority()))
            && (this.getCreatedBy() == null ? other.getCreatedBy() == null : this.getCreatedBy().equals(other.getCreatedBy()))
            && (this.getTargetGroup() == null ? other.getTargetGroup() == null : this.getTargetGroup().equals(other.getTargetGroup()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getMessageType() == null) ? 0 : getMessageType().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        result = prime * result + ((getPriority() == null) ? 0 : getPriority().hashCode());
        result = prime * result + ((getCreatedBy() == null) ? 0 : getCreatedBy().hashCode());
        result = prime * result + ((getTargetGroup() == null) ? 0 : getTargetGroup().hashCode());
        return result;
    }
}