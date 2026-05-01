package com.tenpai.backend.tenpai_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tenpai.backend.tenpai_backend.entity.GameRoom;
import com.tenpai.backend.tenpai_backend.mapper.GameRoomMapper;
import com.tenpai.backend.tenpai_backend.service.GameRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 房间 Service 实现
 */
@Service
@RequiredArgsConstructor
public class GameRoomServiceImpl extends ServiceImpl<GameRoomMapper, GameRoom> implements GameRoomService {

    private final GameRoomMapper gameRoomMapper;
    private final com.tenpai.backend.tenpai_backend.mapper.GameRecordMapper gameRecordMapper;

    @Override
    public GameRoom createRoom(Long ownerId, String rules, Integer scoreMode) {
        autoEndExpiredRooms();
        ensureNoActiveRoomForUser(ownerId);

        GameRoom room = new GameRoom();
        room.setOwnerId(ownerId);
        room.setRules(rules);
        room.setScoreMode(scoreMode != null ? scoreMode : 0);
        room.setStatus(0);
        room.setLastActiveTime(LocalDateTime.now());

        // 使用 6 位邀请码作为房间号，便于输入和分享
        String roomCode = generateUniqueRoomCode();
        room.setRoomCode(roomCode);
        
        gameRoomMapper.insert(room);
        return room;
    }

    @Override
    public java.util.List<GameRoom> listRooms(Long userId) {
        autoEndExpiredRooms();
        // 房主 + 曾加入房间（players JSON 中含该用户 id）均可看到自己的房间列表
        String userIdStr = String.valueOf(userId);
        // 数字 id 在 JSON 里无引号，避免 LIKE "%\"id\":1%" 误匹配 "id":12
        java.util.List<GameRoom> rooms = gameRoomMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<GameRoom>()
                        .nested(w -> w.eq("owner_id", userId)
                                .or()
                                .like("players", "\"id\":\"" + userIdStr + "\"")
                                .or()
                                .like("players", "\"id\":" + userIdStr + "}")
                                .or()
                                .like("players", "\"id\":" + userIdStr + ",")
                                .or()
                                .like("players", ",\"id\":" + userIdStr + "}")
                                .or()
                                .like("players", ",\"id\":" + userIdStr + ","))
                        .orderByDesc("create_time"));
        
        // Calculate score summary for each room
        for (GameRoom room : rooms) {
            try {
                // 1. Parse player names first
                java.util.Map<String, String> playerNames = new java.util.HashMap<>();
                if (cn.hutool.core.util.StrUtil.isNotEmpty(room.getPlayers())) {
                    cn.hutool.json.JSONArray playersArray = cn.hutool.json.JSONUtil.parseArray(room.getPlayers());
                    for (Object p : playersArray) {
                        cn.hutool.json.JSONObject player = (cn.hutool.json.JSONObject) p;
                        playerNames.put(player.getStr("id"), player.getStr("name"));
                    }
                }

                // 2. Fetch records
                java.util.List<com.tenpai.backend.tenpai_backend.entity.GameRecord> records = gameRecordMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.tenpai.backend.tenpai_backend.entity.GameRecord>()
                        .eq("room_id", room.getId())
                );
                
                // 3. Aggregate scores
                java.util.Map<String, Integer> totalScores = new java.util.HashMap<>();
                if (!records.isEmpty()) {
                    for (com.tenpai.backend.tenpai_backend.entity.GameRecord record : records) {
                        cn.hutool.json.JSONObject scoreMap = cn.hutool.json.JSONUtil.parseObj(record.getScores());
                        for (String key : scoreMap.keySet()) {
                            totalScores.put(key, totalScores.getOrDefault(key, 0) + scoreMap.getInt(key));
                        }
                    }
                }
                
                // 4. Build detailed stats list (Include ALL players, even if score is 0)
                java.util.List<java.util.Map<String, Object>> statsList = new java.util.ArrayList<>();
                StringBuilder summary = new StringBuilder();

                // Use playerNames keys to ensure all players are listed
                // If records exist, we might have IDs not in current players list? (Deleted players)
                // But generally satisfied by iterating playerNames or union of both.
                // Let's use playerNames as base, plus any extras from totalScores.
                java.util.Set<String> allPlayerIds = new java.util.HashSet<>(playerNames.keySet());
                allPlayerIds.addAll(totalScores.keySet());

                for (String uid : allPlayerIds) {
                    int score = totalScores.getOrDefault(uid, 0);
                    String name = playerNames.getOrDefault(uid, "玩家" + uid); 
                    
                    if (summary.length() > 0) summary.append(", ");
                    summary.append(name).append(score >= 0 ? "+" : "").append(score);

                    java.util.Map<String, Object> stat = new java.util.HashMap<>();
                    stat.put("name", name);
                    stat.put("score", score);
                    stat.put("text", (score >= 0 ? "+" : "") + score);
                    stat.put("isPositive", score >= 0);
                    stat.put("color", score > 0 ? "#ef4444" : (score < 0 ? "#4ade80" : "#999999")); 
                    statsList.add(stat);
                }
                
                room.setScoreSummary(summary.length() > 0 ? summary.toString() : "暂无战绩");
                room.setPlayerStats(statsList);
                
            } catch (Exception e) {
                e.printStackTrace();
                room.setScoreSummary("数据解析错误");
            }
        }
        
        return rooms;
    }

    @Override
    public GameRoom joinRoom(String roomCode) {
        autoEndExpiredRooms();
        GameRoom room = gameRoomMapper.selectByRoomCode(roomCode);
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        if (room.getStatus() == 1) {
            throw new IllegalArgumentException("房间已结束");
        }
        return room;
    }

    @Override
    public void endRoom(Long roomId) {
        autoEndExpiredRooms();
        GameRoom room = gameRoomMapper.selectById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        room.setStatus(1);
        gameRoomMapper.updateById(room);
    }

    @Override
    public GameRoom getRoomByCode(String roomCode) {
        autoEndExpiredRooms();
        GameRoom room = gameRoomMapper.selectByRoomCode(roomCode);
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        return room;
    }

    @Override
    public void updateRoom(GameRoom room) {
        autoEndExpiredRooms();
        gameRoomMapper.updateById(room);
    }

    @Override
    public java.util.List<com.tenpai.backend.tenpai_backend.entity.GameRecord> getRoomRecords(Long roomId) {
        return gameRecordMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.tenpai.backend.tenpai_backend.entity.GameRecord>()
                .eq("room_id", roomId)
                .orderByAsc("round_number")
        );
    }

    // Helper getter for controller if needed, but we use interface method now
    public com.tenpai.backend.tenpai_backend.mapper.GameRecordMapper getGameRecordMapper() {
        return gameRecordMapper;
    }

    private String generateUniqueRoomCode() {
        for (int i = 0; i < 20; i++) {
            String code = String.valueOf(cn.hutool.core.util.RandomUtil.randomInt(100000, 1000000));
            if (gameRoomMapper.selectByRoomCode(code) == null) {
                return code;
            }
        }
        throw new RuntimeException("房间创建失败，请稍后再试");
    }

    private void autoEndExpiredRooms() {
        // 12小时内无新增对局（以 last_active_time 为准）则自动结束
        LocalDateTime cutoff = LocalDateTime.now().minusHours(12);
        gameRoomMapper.autoEndInactiveRooms(cutoff);
    }

    private void ensureNoActiveRoomForUser(Long userId) {
        String uid = String.valueOf(userId);
        String quotedPattern = "%\"id\":\"" + uid + "\"%";
        String plainPattern = "%\"id\":" + uid + "%";
        int activeCount = gameRoomMapper.countActiveRoomsByOwnerOrPlayer(userId, quotedPattern, plainPattern);
        if (activeCount > 0) {
            throw new IllegalArgumentException("你已有进行中的房间，请先继续或结束当前房间");
        }
    }
}
