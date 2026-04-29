-- 更新 game_room 表结构以支持新功能

-- 1. 添加 score_mode 字段 (记分模式: 0-普通, 1-台版)
ALTER TABLE `game_room` ADD COLUMN `score_mode` TINYINT DEFAULT 0 COMMENT '记分模式: 0-普通, 1-台版';

-- 2. 修改 room_code 字段长度，兼容邀请码长度演进 (原 varchar(10))
ALTER TABLE `game_room` MODIFY COLUMN `room_code` VARCHAR(64) NOT NULL COMMENT '房间邀请码(默认6位数字)';

-- 3. 添加 rules 字段 (存储房间规则JSON)
ALTER TABLE `game_room` ADD COLUMN `rules` TEXT COMMENT '房间规则JSON';

-- 4. 添加 players 字段 (存储玩家列表JSON: [{"id":1,"name":"xx"},...])
ALTER TABLE `game_room` ADD COLUMN `players` TEXT COMMENT '玩家列表JSON';
