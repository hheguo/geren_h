package com.tenpai.backend.tenpai_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tenpai.backend.tenpai_backend.entity.GameRecord;
import com.tenpai.backend.tenpai_backend.entity.GameRoom;
import com.tenpai.backend.tenpai_backend.mapper.GameRecordMapper;
import com.tenpai.backend.tenpai_backend.mapper.GameRoomMapper;
import com.tenpai.backend.tenpai_backend.service.GameRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 战绩 Service 实现
 */
@Service
@RequiredArgsConstructor
public class GameRecordServiceImpl extends ServiceImpl<GameRecordMapper, GameRecord> implements GameRecordService {

    private final GameRecordMapper gameRecordMapper;
    private final GameRoomMapper gameRoomMapper;
    private final com.tenpai.backend.tenpai_backend.service.UserGameRecordService userGameRecordService;

    @Override
    public GameRecord addRecord(Long roomId, Integer roundNumber, String scores, Long currentUserId, Integer userScore) {
        GameRecord record = new GameRecord();
        record.setRoomId(roomId);
        record.setRoundNumber(roundNumber);
        record.setScores(scores);
        gameRecordMapper.insert(record);

        // 刷新房间活跃时间：有新对局即视为活跃
        GameRoom room = new GameRoom();
        room.setId(roomId);
        room.setLastActiveTime(java.time.LocalDateTime.now());
        gameRoomMapper.updateById(room);

        // 如果是当前用户记录的，且有分数变动，则记录到该用户的账单中
        if (currentUserId != null && userScore != null) {
            com.tenpai.backend.tenpai_backend.entity.UserGameRecord ugr = new com.tenpai.backend.tenpai_backend.entity.UserGameRecord();
            ugr.setGameRecordId(record.getId());
            ugr.setRoomId(roomId);
            ugr.setUserId(currentUserId);
            ugr.setRoundNumber(roundNumber);
            ugr.setScoreChange(userScore);
            ugr.setCreateTime(java.time.LocalDateTime.now());
            userGameRecordService.save(ugr);
        }

        return record;
    }

    @Override
    public List<GameRecord> listRecords(Long roomId) {
        LambdaQueryWrapper<GameRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GameRecord::getRoomId, roomId)
               .orderByAsc(GameRecord::getRoundNumber);
        return gameRecordMapper.selectList(wrapper);
    }
}
