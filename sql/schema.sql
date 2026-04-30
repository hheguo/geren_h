-- =============================================
-- TenpAI 麻将记分 - 数据库初始化脚本
-- Database: tenpai
-- =============================================

CREATE DATABASE IF NOT EXISTS `tingpai` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `tingpai`;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `openid` varchar(64) NOT NULL UNIQUE COMMENT '微信OpenID',
  `nickname` varchar(64) COMMENT '昵称',
  `avatar_url` varchar(500) COMMENT '头像',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 房间表
CREATE TABLE IF NOT EXISTS `game_room` (
  `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `room_code` varchar(64) NOT NULL UNIQUE COMMENT '房间邀请码(默认6位数字)',
  `owner_id` bigint NOT NULL COMMENT '房主ID',
  `status` tinyint DEFAULT 0 COMMENT '0:进行中, 1:已结束',
  `score_mode` tinyint DEFAULT 0 COMMENT '记分模式: 0-普通, 1-台版',
  `rules` json COMMENT '规则配置(底分等)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_active_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后活跃时间(用于超时自动结束)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间表';

-- 3. 战绩流水表
CREATE TABLE IF NOT EXISTS `game_record` (
  `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `room_id` bigint NOT NULL,
  `round_number` int NOT NULL COMMENT '第几局',
  `scores` json NOT NULL COMMENT '分数变动JSON',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='战绩流水表';

-- 4. 反馈表
CREATE TABLE IF NOT EXISTS `feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` bigint COMMENT '用户ID',
  `type` varchar(20) DEFAULT 'other' COMMENT 'bug/suggestion/other',
  `content` text NOT NULL COMMENT '反馈内容',
  `contact` varchar(200) COMMENT '联系方式',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈表';

-- 5. 用户战绩明细表 (账单)
CREATE TABLE IF NOT EXISTS `user_game_record` (
  `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `game_record_id` bigint NOT NULL COMMENT '关联的大局记录ID',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `round_number` int NOT NULL COMMENT '局数',
  `score_change` int NOT NULL COMMENT '分数变动',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_time (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户战绩明细表';

-- 索引
CREATE INDEX idx_game_record_room_id ON `game_record`(`room_id`);
CREATE INDEX idx_game_room_owner_id ON `game_room`(`owner_id`);
CREATE INDEX idx_game_room_status_active_time ON `game_room`(`status`, `last_active_time`);
