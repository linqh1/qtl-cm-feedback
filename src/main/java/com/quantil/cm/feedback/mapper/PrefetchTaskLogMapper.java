package com.quantil.cm.feedback.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.quantil.cm.feedback.domain.PrefetchTaskLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PrefetchTaskLogMapper extends BaseMapper<PrefetchTaskLog> {
    List<PrefetchTaskLog> selectByTaskId(@Param("taskIds") List<String> ids);
}
