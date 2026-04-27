package com.tenpai.backend.tenpai_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tenpai.backend.tenpai_backend.entity.GameRecord;

import java.util.List;

/**
 * 战绩 Service
 */
public interface GameRecordService extends IService<GameRecord> {

    /**
     * 添加一局记录
     *
     * @param roomId      房间ID
     * @param roundNumber 第几局
     * @param scores      分数变动JSON
     * @return 记录
     */
    GameRecord addRecord(Long roomId, Integer roundNumber, String scores, Long currentUserId, Integer userScore);

    /**
     * 查询房间所有记录
     */
    List<GameRecord> listRecords(Long roomId);
}
