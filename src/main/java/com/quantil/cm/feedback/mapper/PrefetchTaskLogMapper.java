package com.quantil.cm.feedback.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.quantil.cm.feedback.domain.PrefetchTaskLog;
import com.quantil.cm.feedback.domain.PurgeTaskLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PrefetchTaskLogMapper extends BaseMapper<PrefetchTaskLog> {
    List<PurgeTaskLog> selectByTaskId(@Param("taskIds") List<String> ids);
}
