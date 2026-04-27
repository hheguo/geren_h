package com.tenpai.backend.tenpai_backend.controller;

import com.tenpai.backend.tenpai_backend.common.R;
import com.tenpai.backend.tenpai_backend.entity.GameRecord;
import com.tenpai.backend.tenpai_backend.service.GameRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 战绩记录 Controller
 */
@RestController
@RequestMapping("/api/record")
@RequiredArgsConstructor
public class GameRecordController {

    private final GameRecordService gameRecordService;

    /**
     * 添加一局记录
     * 请求体: { "roomId": 1, "roundNumber": 1, "scores": "{\"1\":20,\"2\":-20}" }
     */
    @PostMapping("/add")
    public R<GameRecord> add(@RequestBody Map<String, Object> params) {
        Long roomId = Long.valueOf(params.get("roomId").toString());
        Integer roundNumber = Integer.valueOf(params.get("roundNumber").toString());
        String scores = params.get("scores").toString();
        
        Long currentUserId = params.containsKey("userId") ? Long.valueOf(params.get("userId").toString()) : null;
        Integer userScore = params.containsKey("userScore") ? Integer.valueOf(params.get("userScore").toString()) : null;

        GameRecord record = gameRecordService.addRecord(roomId, roundNumber, scores, currentUserId, userScore);
        return R.ok(record);
    }

    /**
     * 查询房间所有记录
     */
    @GetMapping("/list/{roomId}")
    public R<List<GameRecord>> list(@PathVariable Long roomId) {
        List<GameRecord> records = gameRecordService.listRecords(roomId);
        return R.ok(records);
    }
}
