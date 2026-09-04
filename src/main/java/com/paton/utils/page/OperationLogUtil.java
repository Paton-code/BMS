package com.paton.utils.page;

import com.paton.domain.OperationLog;
import com.paton.service.IOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class OperationLogUtil {

    private static IOperationLogService operationLogService;

    @Autowired
    public void setOperationLogService(IOperationLogService operationLogService) {
        OperationLogUtil.operationLogService = operationLogService;
    }

    /**
     * 记录操作日志
     * @param adminId 管理员ID
     * @param adminName 管理员名称
     * @param operationType 操作类型
     * @param operationModule 操作模块
     * @param operationDescription 操作描述
     * @param request HTTP请求对象
     */
    public static void recordLog(Integer adminId, String adminName, String operationType,
                                 String operationModule, String operationDescription,
                                 HttpServletRequest request) {
        try {
            OperationLog log = new OperationLog(adminId, adminName, operationType,
                    operationModule, operationDescription);

            // 设置操作IP
            String ip = getClientIpAddress(request);
            log.setOperationIp(ip);

            operationLogService.recordOperationLog(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录操作日志（带操作目标）
     */
    public static void recordLogWithTarget(Integer adminId, String adminName, String operationType,
                                           String operationModule, String operationDescription,
                                           String operationTarget, HttpServletRequest request) {
        try {
            OperationLog log = new OperationLog(adminId, adminName, operationType,
                    operationModule, operationDescription);
            log.setOperationTarget(operationTarget);

            String ip = getClientIpAddress(request);
            log.setOperationIp(ip);

            operationLogService.recordOperationLog(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录操作日志（带详细信息）
     */
    public static void recordLogWithDetails(Integer adminId, String adminName, String operationType,
                                            String operationModule, String operationDescription,
                                            String operationTarget, String operationDetails,
                                            HttpServletRequest request) {
        try {
            OperationLog log = new OperationLog(adminId, adminName, operationType,
                    operationModule, operationDescription);
            log.setOperationTarget(operationTarget);
            log.setOperationDetails(operationDetails);

            String ip = getClientIpAddress(request);
            log.setOperationIp(ip);

            operationLogService.recordOperationLog(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取客户端IP地址
     */
    private static String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
