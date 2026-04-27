package com.tenpai.backend.tenpai_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 微信OpenID */
    private String openid;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatarUrl;

    /** 创建时间 */
    private LocalDateTime createTime;
}
