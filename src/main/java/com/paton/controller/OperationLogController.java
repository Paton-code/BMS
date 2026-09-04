package com.paton.controller;

import com.paton.domain.OperationLog;
import com.paton.service.IOperationLogService;
import com.paton.utils.page.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OperationLogController {

    @Resource
    private IOperationLogService operationLogService;

    /**
     * 返回操作日志查询页面
     * @param model Model对象
     * @param pageNum 页码
     * @return 操作日志页面
     */
    @RequestMapping("/operationLogPage")
    public String operationLogPage(Model model, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<OperationLog> page = operationLogService.getLogsByPage(pageNum);
        model.addAttribute("page", page);
        model.addAttribute("operationType", null);
        model.addAttribute("operationModule", null);
        return "admin/operationLog";
    }

    /**
     * 根据操作类型和操作模块查询日志
     * @param operationType 操作类型
     * @param operationModule 操作模块
     * @param pageNum 页码
     * @return 操作日志页面
     */
    @RequestMapping("/operationLogByType")
    public String operationLogByType(@RequestParam(value = "operationType", required = false) String operationType,
                                     @RequestParam(value = "operationModule", required = false) String operationModule,
                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                     Model model) {
        Page<OperationLog> page = operationLogService.getLogsByCondition(operationType, operationModule, pageNum);
        model.addAttribute("page", page);
        model.addAttribute("operationType", operationType);
        model.addAttribute("operationModule", operationModule);
        return "admin/operationLog";
    }

    /**
     * 获取最新的操作日志（API接口）
     * @param limit 限制数量
     * @return 最新的操作日志列表
     */
    @RequestMapping("/getLatestOperationLogs")
    @ResponseBody
    public Map<String, Object> getLatestOperationLogs(@RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<OperationLog> logs = operationLogService.getLatestLogs(limit);
            result.put("success", true);
            result.put("data", logs);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取操作日志失败");
        }
        return result;
    }

    /**
     * 记录操作日志（API接口）
     * @param log 操作日志对象
     * @return 记录结果
     */
    @RequestMapping("/recordOperationLog")
    @ResponseBody
    public Map<String, Object> recordOperationLog(OperationLog log, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 设置操作IP
            String ip = getClientIpAddress(request);
            log.setOperationIp(ip);

            boolean success = operationLogService.recordOperationLog(log);
            result.put("success", success);
            if (!success) {
                result.put("message", "记录操作日志失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "记录操作日志异常");
        }
        return result;
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
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