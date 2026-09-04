package com.paton.domain;

import java.util.Date;

public class OperationLog {
    private Integer logId;
    private Integer adminId;
    private String adminName;
    private String operationType; // 操作类型：新增、修改、删除、查询等
    private String operationModule; // 操作模块：用户管理、图书管理、分类管理等
    private String operationDescription; // 操作描述
    private String operationTarget; // 操作目标（如用户名、图书名等）
    private String operationIp; // 操作IP地址
    private Date operationTime; // 操作时间
    private String operationResult; // 操作结果：成功/失败
    private String operationDetails; // 操作详情（JSON格式存储详细信息）

    // 构造方法
    public OperationLog() {}

    public OperationLog(Integer adminId, String adminName, String operationType,
                        String operationModule, String operationDescription) {
        this.adminId = adminId;
        this.adminName = adminName;
        this.operationType = operationType;
        this.operationModule = operationModule;
        this.operationDescription = operationDescription;
        this.operationTime = new Date();
        this.operationResult = "成功";
    }

    // Getter和Setter方法
    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationModule() {
        return operationModule;
    }

    public void setOperationModule(String operationModule) {
        this.operationModule = operationModule;
    }

    public String getOperationDescription() {
        return operationDescription;
    }

    public void setOperationDescription(String operationDescription) {
        this.operationDescription = operationDescription;
    }

    public String getOperationTarget() {
        return operationTarget;
    }

    public void setOperationTarget(String operationTarget) {
        this.operationTarget = operationTarget;
    }

    public String getOperationIp() {
        return operationIp;
    }

    public void setOperationIp(String operationIp) {
        this.operationIp = operationIp;
    }

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    public String getOperationResult() {
        return operationResult;
    }

    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }

    public String getOperationDetails() {
        return operationDetails;
    }

    public void setOperationDetails(String operationDetails) {
        this.operationDetails = operationDetails;
    }
}