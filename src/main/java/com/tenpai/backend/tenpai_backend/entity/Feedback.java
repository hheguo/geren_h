package com.tenpai.backend.tenpai_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈表
 */
@Data
@TableName("feedback")
public class Feedback {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID (可选) */
    private Long userId;

    /** 反馈类型: bug/suggestion/other */
    private String type;

    /** 反馈内容 */
    private String content;

    /** 联系方式 (可选) */
    private String contact;

    /** 创建时间 */
    private LocalDateTime createTime;
}
