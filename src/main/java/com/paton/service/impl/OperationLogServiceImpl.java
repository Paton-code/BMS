package com.paton.service.impl;

import com.paton.domain.OperationLog;
import com.paton.domain.OperationLogExample;
import com.paton.mapper.OperationLogMapper;
import com.paton.service.IOperationLogService;
import com.paton.utils.page.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OperationLogServiceImpl implements IOperationLogService {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Override
    public boolean recordOperationLog(OperationLog log) {
        try {
            int result = operationLogMapper.insert(log);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean recordOperationLog(Integer adminId, String adminName, String operationType,
                                      String operationModule, String operationDescription) {
        OperationLog log = new OperationLog(adminId, adminName, operationType, operationModule, operationDescription);
        return recordOperationLog(log);
    }

    @Override
    public List<OperationLog> getLogsByAdminId(Integer adminId) {
        return operationLogMapper.selectByAdminId(adminId);
    }

    @Override
    public List<OperationLog> getLogsByTimeRange(String startTime, String endTime) {
        return operationLogMapper.selectByTimeRange(startTime, endTime);
    }

    @Override
    public Page<OperationLog> getLogsByPage(Integer pageNum) {
        Page<OperationLog> page = new Page<>();
        page.setPageNum(pageNum);
        page.setPageSize(10); // 每页10条记录

        OperationLogExample example = new OperationLogExample();
        example.setOrderByClause("operation_time DESC");

        int totalCount = operationLogMapper.countByExample(example);
        page.setTotalCount(totalCount);

        example.setLimitStart((pageNum - 1) * page.getPageSize());
        example.setLimitSize(page.getPageSize());

        List<OperationLog> logs = operationLogMapper.selectByExample(example);
        page.setList(logs);

        return page;
    }

    @Override
    public List<OperationLog> getLatestLogs(Integer limit) {
        return operationLogMapper.selectLatestLogs(limit);
    }

    @Override
    public Page<OperationLog> getLogsByOperationType(String operationType, Integer pageNum) {
        Page<OperationLog> page = new Page<>();
        page.setPageNum(pageNum);
        page.setPageSize(10);

        OperationLogExample example = new OperationLogExample();
        OperationLogExample.Criteria criteria = example.createCriteria();
        criteria.andOperationTypeEqualTo(operationType);
        example.setOrderByClause("operation_time DESC");

        int totalCount = operationLogMapper.countByExample(example);
        page.setTotalCount(totalCount);

        example.setLimitStart((pageNum - 1) * page.getPageSize());
        example.setLimitSize(page.getPageSize());

        List<OperationLog> logs = operationLogMapper.selectByExample(example);
        page.setList(logs);

        return page;
    }

    @Override
    public Page<OperationLog> getLogsByCondition(String operationType, String operationModule, Integer pageNum) {
        Page<OperationLog> page = new Page<>();
        page.setPageNum(pageNum);
        page.setPageSize(10);

        OperationLogExample example = new OperationLogExample();
        OperationLogExample.Criteria criteria = example.createCriteria();
        if (operationType != null && !operationType.isEmpty()) {
            criteria.andOperationTypeEqualTo(operationType);
        }
        if (operationModule != null && !operationModule.isEmpty()) {
            criteria.andOperationModuleEqualTo(operationModule);
        }
        example.setOrderByClause("operation_time DESC");

        int totalCount = operationLogMapper.countByExample(example);
        page.setTotalCount(totalCount);

        example.setLimitStart((pageNum - 1) * page.getPageSize());
        example.setLimitSize(page.getPageSize());

        List<OperationLog> logs = operationLogMapper.selectByExample(example);
        page.setList(logs);

        return page;
    }
}