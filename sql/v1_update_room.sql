-- 更新 game_room 表结构以支持新功能

-- 1. 添加 score_mode 字段 (记分模式: 0-普通, 1-台版)
ALTER TABLE `game_room` ADD COLUMN `score_mode` TINYINT DEFAULT 0 COMMENT '记分模式: 0-普通, 1-台版';

-- 2. 修改 room_code 字段长度，兼容邀请码长度演进 (原 varchar(10))
ALTER TABLE `game_room` MODIFY COLUMN `room_code` VARCHAR(64) NOT NULL COMMENT '房间邀请码(默认6位数字)';

-- 3. 添加 rules 字段 (存储房间规则JSON)
ALTER TABLE `game_room` ADD COLUMN `rules` TEXT COMMENT '房间规则JSON';

-- 4. 添加 players 字段 (存储玩家列表JSON: [{"id":1,"name":"xx"},...])
ALTER TABLE `game_room` ADD COLUMN `players` TEXT COMMENT '玩家列表JSON';

-- 5. 添加 last_active_time 字段 (用于12小时无对局自动结束)
ALTER TABLE `game_room` ADD COLUMN `last_active_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后活跃时间(用于超时自动结束)';

-- 6. 初始化历史数据的 last_active_time（优先取最后战绩时间，无则用创建时间）
UPDATE `game_room` r
LEFT JOIN (
  SELECT room_id, MAX(create_time) AS max_record_time
  FROM game_record
  GROUP BY room_id
) gr ON r.id = gr.room_id
SET r.last_active_time = COALESCE(gr.max_record_time, r.create_time);

-- 7. 添加查询索引
CREATE INDEX idx_game_room_status_active_time ON `game_room`(`status`, `last_active_time`);
