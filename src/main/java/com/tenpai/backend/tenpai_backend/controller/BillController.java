package com.tenpai.backend.tenpai_backend.controller;

import com.tenpai.backend.tenpai_backend.common.R;
import com.tenpai.backend.tenpai_backend.mapper.UserGameRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账单/统计 Controller
 */
@RestController
@RequestMapping("/api/bill")
@RequiredArgsConstructor
public class BillController {

    private final UserGameRecordMapper userGameRecordMapper;

    /**
     * 获取账单统计
     * @param userId 用户ID
     * @param type day | month
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate 结束日期 yyyy-MM-dd
     */
    @GetMapping("/stats")
    public R<Map<String, Object>> getStats(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "month") String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ISO_DATE);
        }
        if (endDate == null) {
            endDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE);
        }

        // 补全时分秒，确保查询范围正确
        String startDateTime = startDate + " 00:00:00";
        String endDateTime = endDate + " 23:59:59";

        List<Map<String, Object>> list;
        if ("day".equals(type)) {
            list = userGameRecordMapper.statsByDay(userId, startDateTime, endDateTime);
        } else {
            list = userGameRecordMapper.statsByMonth(userId, startDateTime, endDateTime);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        
        // 计算总计
        long totalScore = 0;
        long totalCount = 0;
        for (Map<String, Object> item : list) {
            if (item.get("totalScore") != null) {
                totalScore += Long.parseLong(item.get("totalScore").toString());
            }
            if (item.get("roundCount") != null) {
                totalCount += Long.parseLong(item.get("roundCount").toString());
            }
        }
        
        result.put("totalScore", totalScore);
        result.put("totalCount", totalCount);

        return R.ok(result);
    }
}
