package com.tenpai.backend.tenpai_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tenpai.backend.tenpai_backend.entity.GameRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
