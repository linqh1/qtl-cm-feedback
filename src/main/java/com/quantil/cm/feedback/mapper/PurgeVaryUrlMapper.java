package com.quantil.cm.feedback.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.quantil.cm.feedback.domain.PrefetchTaskLog;
import com.quantil.cm.feedback.domain.PurgeVaryUrl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PurgeVaryUrlMapper extends BaseMapper<PurgeVaryUrl> {
    List<PurgeVaryUrl> selectByTaskId(@Param("taskIds") List<String> ids);
}
