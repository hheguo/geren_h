package com.tenpai.backend.tenpai_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tenpai.backend.tenpai_backend.entity.UserGameRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserGameRecordMapper extends BaseMapper<UserGameRecord> {

    // 按日统计
    @Select("SELECT DATE(create_time) as date, SUM(score_change) as totalScore, COUNT(*) as roundCount " +
            "FROM user_game_record " +
            "WHERE user_id = #{userId} AND create_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(create_time) ORDER BY date DESC")
    List<Map<String, Object>> statsByDay(@Param("userId") Long userId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    // 按月统计
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') as date, SUM(score_change) as totalScore, COUNT(*) as roundCount " +
            "FROM user_game_record " +
            "WHERE user_id = #{userId} AND create_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m') ORDER BY date DESC")
    List<Map<String, Object>> statsByMonth(@Param("userId") Long userId, @Param("startDate") String startDate, @Param("endDate") String endDate);
}
