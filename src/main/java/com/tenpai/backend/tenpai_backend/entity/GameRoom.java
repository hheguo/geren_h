package com.tenpai.backend.tenpai_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 房间表
 */
@Data
@TableName("game_room")
public class GameRoom {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 4位数字房间号 */
    private String roomCode;

    /** 房主ID */
    private Long ownerId;

    /** 状态: 0-进行中, 1-已结束 */
    private Integer status;

    /** 规则配置(底分等) JSON */
    private String rules;

    /** 记分模式: 0-普通, 1-台版 */
    private Integer scoreMode;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 玩家列表 JSON */
    private String players;

    /** 临时字段：分数汇总文本 (e.g. "张三:+20 李四:-10") */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String scoreSummary;

    /** 临时字段：结构化玩家分数 (for UI coloring) */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private java.util.List<java.util.Map<String, Object>> playerStats;
}
