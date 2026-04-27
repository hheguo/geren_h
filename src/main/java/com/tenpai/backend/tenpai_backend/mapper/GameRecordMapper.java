package com.tenpai.backend.tenpai_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tenpai.backend.tenpai_backend.entity.GameRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 战绩 Mapper
 */
@Mapper
public interface GameRecordMapper extends BaseMapper<GameRecord> {
}
