package com.tenpai.backend.tenpai_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 战绩流水表
 */
@Data
@TableName("game_record")
public class GameRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 房间ID */
    private Long roomId;

    /** 第几局 */
    private Integer roundNumber;

    /** 分数变动JSON: {"uid1":20, "uid2":-20} */
    private String scores;

    /** 创建时间 */
    private LocalDateTime createTime;
}
