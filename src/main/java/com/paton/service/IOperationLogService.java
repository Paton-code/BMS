package com.paton.service;

import com.paton.domain.OperationLog;
import com.paton.utils.page.Page;

import java.util.List;

public interface IOperationLogService {

    /**
     * 记录操作日志
     * @param log 操作日志对象
     * @return 是否记录成功
     */
    boolean recordOperationLog(OperationLog log);

    /**
     * 记录操作日志（简化版）
     * @param adminId 管理员ID
     * @param adminName 管理员名称
     * @param operationType 操作类型
     * @param operationModule 操作模块
     * @param operationDescription 操作描述
     * @return 是否记录成功
     */
    boolean recordOperationLog(Integer adminId, String adminName, String operationType,
                               String operationModule, String operationDescription);

    /**
     * 根据管理员ID查询操作日志
     * @param adminId 管理员ID
     * @return 操作日志列表
     */
    List<OperationLog> getLogsByAdminId(Integer adminId);

    /**
     * 根据时间范围查询操作日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作日志列表
     */
    List<OperationLog> getLogsByTimeRange(String startTime, String endTime);

    /**
     * 分页查询操作日志
     * @param pageNum 页码
     * @return 分页结果
     */
    Page<OperationLog> getLogsByPage(Integer pageNum);

    /**
     * 获取最新的操作日志
     * @param limit 限制数量
     * @return 最新的操作日志列表
     */
    List<OperationLog> getLatestLogs(Integer limit);

    /**
     * 根据操作类型查询日志
     * @param operationType 操作类型
     * @param pageNum 页码
     * @return 分页结果
     */
    Page<OperationLog> getLogsByOperationType(String operationType, Integer pageNum);

    /**
     * 根据操作类型和操作模块组合查询日志
     * @param operationType 操作类型（可为空）
     * @param operationModule 操作模块（可为空）
     * @param pageNum 页码
     * @return 分页结果
     */
    Page<OperationLog> getLogsByCondition(String operationType, String operationModule, Integer pageNum);
}