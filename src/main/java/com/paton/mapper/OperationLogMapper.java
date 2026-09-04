package com.paton.mapper;

import com.paton.domain.OperationLog;
import com.paton.domain.OperationLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OperationLogMapper {
    int countByExample(OperationLogExample example);

    int deleteByExample(OperationLogExample example);

    int deleteByPrimaryKey(Integer logId);

    int insert(OperationLog record);

    int insertSelective(OperationLog record);

    List<OperationLog> selectByExample(OperationLogExample example);

    OperationLog selectByPrimaryKey(Integer logId);

    int updateByExampleSelective(@Param("record") OperationLog record, @Param("example") OperationLogExample example);

    int updateByExample(@Param("record") OperationLog record, @Param("example") OperationLogExample example);

    int updateByPrimaryKeySelective(OperationLog record);

    int updateByPrimaryKey(OperationLog record);

    // 自定义方法：根据管理员ID查询操作日志
    List<OperationLog> selectByAdminId(Integer adminId);

    // 自定义方法：根据时间范围查询操作日志
    List<OperationLog> selectByTimeRange(@Param("startTime") String startTime, @Param("endTime") String endTime);

    // 自定义方法：查询最新的操作日志
    List<OperationLog> selectLatestLogs(@Param("limit") Integer limit);
}