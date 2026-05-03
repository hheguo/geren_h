package com.tenpai.backend.tenpai_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tenpai.backend.tenpai_backend.entity.GameRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 房间 Mapper
 */
@Mapper
public interface GameRoomMapper extends BaseMapper<GameRoom> {

    /**
     * 根据房间号查询房间
     */
    @Select("SELECT * FROM game_room WHERE room_code = #{roomCode}")
    GameRoom selectByRoomCode(String roomCode);

    @Update("UPDATE game_room " +
            "SET status = 1 " +
            "WHERE status = 0 " +
            "AND COALESCE(last_active_time, create_time) < #{cutoff}")
    int autoEndInactiveRooms(@Param("cutoff") java.time.LocalDateTime cutoff);

    @Select("SELECT COUNT(1) FROM game_room " +
            "WHERE status = 0 " +
            "AND (owner_id = #{userId} OR players LIKE #{quotedPattern} OR players LIKE #{plainPattern})")
    int countActiveRoomsByOwnerOrPlayer(@Param("userId") Long userId,
                                        @Param("quotedPattern") String quotedPattern,
                                        @Param("plainPattern") String plainPattern);

    /**
     * 当前用户作为房主或玩家参与的一个进行中房间（最近活跃优先）
     */
    @Select("SELECT * FROM game_room " +
            "WHERE status = 0 " +
            "AND (owner_id = #{userId} OR players LIKE #{quotedPattern} OR players LIKE #{plainPattern}) " +
            "ORDER BY COALESCE(last_active_time, create_time) DESC LIMIT 1")
    GameRoom selectActiveRoomForUser(@Param("userId") Long userId,
                                     @Param("quotedPattern") String quotedPattern,
                                     @Param("plainPattern") String plainPattern);
}
