package com.quantil.cm.feedback.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.quantil.cm.feedback.domain.PurgeTaskLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PurgeTaskLogMapper extends BaseMapper<PurgeTaskLog> {
    List<PurgeTaskLog> selectByTaskId(@Param("taskId") String taskId);

    List<PurgeTaskLog> selectByMultiTaskId(@Param("purgeTaskIds") List<String> ids);
}
