CREATE TABLE `task_queue` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `status` int(4) NOT NULL COMMENT '0:waiting,1:running,2:failed,3:success',
  `attributes` VARCHAR(512) DEFAULT NULL COMMENT '扩展属性',
  `type` int(11) NOT NULL COMMENT '类型',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本',
  `unique_key` varchar(64) NOT NULL COMMENT '唯一key',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `timeout` datetime DEFAULT NULL COMMENT '超时时间',
  `execute_timeout` datetime  NOT NULL COMMENT '单次执行超时时间，超过会被重置状态',
  `msg` VARCHAR(128) DEFAULT NULL COMMENT '执行信息',
  `ip` VARCHAR(128) DEFAULT NULL COMMENT '执行机器',
  `remain_retry_count` int(11) NOT NULL COMMENT '剩余重试次数',
  `orig_retry_count` int(11) NOT NULL COMMENT '最多重试次数',
  `execute_interval_seconds` int(8) DEFAULT NULL COMMENT '执行间隔',
  `alarm_threshold` int(8) NOT NULL DEFAULT '0' COMMENT '报警阈值',
  `env` VARCHAR(8) DEFAULT NULL COMMENT '环境',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父ID',
  `children_count` int(8) NOT NULL DEFAULT '0' COMMENT '父节点数量',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uk` (`type`,`unique_key`),
  KEY `idx_status` (`status`,`remain_retry_count`,`start_time`,`env`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='任务队列'
;


CREATE TABLE `sequence` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(64) NOT NULL,
    `value` BIGINT NOT NULL,
    `gmt_create` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` TIMESTAMP NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_unique_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='sequence'
;


CREATE TABLE `user` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(64) NOT NULL,
    `gmt_create` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` TIMESTAMP NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_unique_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='user'
;

CREATE TABLE `user_authority` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `password` VARCHAR(64) NOT NULL,
    `salt` VARCHAR(64) NOT NULL,
    `gmt_create` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` TIMESTAMP NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_unique_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='user_authority'
;

CREATE TABLE `user_relation` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `ref_type` INT(11) NOT NULL comment '类型',
  `ref_id` BIGINT UNSIGNED NOT NULL comment '关联ID',
  `attributes` VARCHAR(256) DEFAULT NULL,
  `status` TINYINT NOT NULL comment '0:无效,1:有效',
  `gmt_create` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_unique_name` (`ref_id`,`ref_type`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='user_relation'
;

CREATE TABLE `task` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `gmt_create` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` TIMESTAMP NOT NULL,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `task_id` VARCHAR(64) NOT NULL,
    `type` int(11) NOT NULL COMMENT '类型',
    `status` int(11) NOT NULL COMMENT '0:无效,3:结束,4:失败',
    `attributes` VARCHAR(4096) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_unique_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='user_authority'
;