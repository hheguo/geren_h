package com.tenpai.backend.tenpai_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户战绩明细表 - 用于账单统计
 */
@Data
@TableName("user_game_record")
public class UserGameRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联的大局记录ID */
    private Long gameRecordId;

    /** 房间ID */
    private Long roomId;

    /** 用户ID */
    private Long userId;

    /** 局数 */
    private Integer roundNumber;

    /** 分数变动 (正负) */
    private Integer scoreChange;

    /** 记录时间 */
    private LocalDateTime createTime;
}
