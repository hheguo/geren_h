package com.tenpai.backend.tenpai_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tenpai.backend.tenpai_backend.entity.GameRecord;
import com.tenpai.backend.tenpai_backend.entity.GameRoom;

/**
 * 房间 Service
 */
public interface GameRoomService extends IService<GameRoom> {

    /**
     * 创建房间
     *
     * @param ownerId   房主ID
     * @param rules     规则JSON
     * @param scoreMode 记分模式
     * @return 房间信息
     */
    GameRoom createRoom(Long ownerId, String rules, Integer scoreMode);

    /**
     * 获取用户房间列表
     *
     * @param userId 用户ID
     * @return 房间列表
     */
    java.util.List<GameRoom> listRooms(Long userId);


    /**
     * 通过房间号加入房间
     *
     * @param roomCode 房间号
     * @return 房间信息
     */
    GameRoom joinRoom(String roomCode);

    /**
     * 结束房间
     *
     * @param roomId 房间ID
     */
    void endRoom(Long roomId);

    /**
     * 通过房间号获取房间详情
     */
    /**
     * 通过房间号获取房间详情
     */
    GameRoom getRoomByCode(String roomCode);

    /**
     * 更新房间信息 (如玩家列表)
     */
    void updateRoom(GameRoom room);

    java.util.List<GameRecord> getRoomRecords(Long roomId);

    /**
     * 用户作为房主或玩家时，当前一个进行中的房间；没有则返回 null
     */
    GameRoom getActiveRoomForUser(Long userId);
}
