CREATE TABLE IF NOT EXISTS `ai_diagnosis` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '用户编号',
    `greenhouse_id` bigint DEFAULT NULL COMMENT '大棚编号',
    `image_url` varchar(500) DEFAULT NULL COMMENT '图片URL',
    `disease_name` varchar(100) DEFAULT NULL COMMENT '病害名称',
    `confidence` float DEFAULT NULL COMMENT '置信度',
    `result_json` text COMMENT '诊断结果JSON',
    `is_helpful` tinyint(1) DEFAULT NULL COMMENT '是否有帮助: 1有用 0无用',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_greenhouse_id` (`greenhouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='番茄病害诊断记录表';