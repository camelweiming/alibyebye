/******************************************/
/*   表名称 = app   */
/******************************************/
CREATE TABLE `site` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `name` varchar(32) NOT NULL COMMENT '名称',
  `logo` varchar(128) DEFAULT NULL COMMENT '图片',
  `site` int(11) DEFAULT NULL COMMENT '站点ID',
  `h5_url` varchar(128) DEFAULT NULL COMMENT 'h5地址',
  `ios_url` varchar(128) DEFAULT NULL COMMENT 'IOS跳转地址',
  `android_url` varchar(128) DEFAULT NULL COMMENT '安卓跳转地址',
  `attributes` varchar(512) DEFAULT NULL COMMENT '附加属性',
  `status` tinyint(4) NOT NULL COMMENT '1:可用,2:无效',
  `site_key` varchar(32) NOT NULL COMMENT '唯一值',
  `tags` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'bitset, 2:预发,4:线上,8:IOS,16:ANDROID',
  `priority` int(11) NOT NULL COMMENT '权重',
  `categories` varchar(32) DEFAULT NULL COMMENT '分类多个，分割',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_key` (`site_key`)
  UNIQUE KEY `uk_site` (`site`),
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='支持应用'
;


/******************************************/
/*   表名称 = category   */
/******************************************/
CREATE TABLE `category` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `name` varchar(32) NOT NULL COMMENT '名称',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `attributes` varchar(128) DEFAULT NULL COMMENT '附加属性',
  `status` tinyint(4) NOT NULL COMMENT '0:无效，1:有效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10001 DEFAULT CHARSET=utf8mb4 COMMENT='类目表'
;


/******************************************/
/*   表名称 = site_configs   */
/******************************************/
CREATE TABLE `site_configs` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `site` int(11) NOT NULL COMMENT '站点',
  `config_key` varchar(64) NOT NULL COMMENT '配置key',
  `content` text NOT NULL COMMENT '内容',
  `env` varchar(8) NOT NULL COMMENT '环境',
  `status` tinyint(4) NOT NULL COMMENT '0：无效，1：有效',
  `domains` varchar(128) DEFAULT NULL COMMENT '关联域名',
  `attributes` varchar(128) DEFAULT NULL COMMENT '附加属性',
  `name` varchar(128) DEFAULT NULL COMMENT '名称',
  `platform` varchar(32) DEFAULT NULL COMMENT '适用平台',
  PRIMARY KEY (`id`),
  KEY `uk_site` (`site`,`env`,`config_key`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='站点配置'
;


/******************************************/
/*   表名称 = programme   */
/******************************************/
CREATE TABLE `programme` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `title` varchar(128) NOT NULL COMMENT '标题',
  `release_year` int(11) NOT NULL COMMENT '发型年',
  `img` VARCHAR(128) DEFAULT NULL COMMENT '图片',
  `types` VARCHAR(32) DEFAULT NULL COMMENT '类型',
  `directors` VARCHAR(256) DEFAULT NULL COMMENT '导演',
  `performers` VARCHAR(256) DEFAULT NULL COMMENT '演员',
  `languages` VARCHAR(32) DEFAULT NULL COMMENT '语言',
  `alias` VARCHAR(64) DEFAULT NULL COMMENT '别名',
  `seconds` bigint(20)  NOT NULL COMMENT '时长',
  `score` decimal(10,2) NOT NULL COMMENT '评分',
  `imdb` VARCHAR(16) DEFAULT NULL COMMENT 'IMDB',
  `summary` VARCHAR(512) DEFAULT NULL COMMENT '简介',
  `areas` VARCHAR(32) DEFAULT NULL COMMENT '地区',
  `season` int(8) DEFAULT NULL COMMENT '季数',
  `total_episode` int(8) DEFAULT NULL COMMENT '总集数',
  `status` tinyint(4) NOT NULL COMMENT '0：无效，1：有效',
  `tags` VARCHAR(16) DEFAULT NULL COMMENT '标签',
  `attributes` VARCHAR(256) DEFAULT NULL COMMENT '附加属性',
  `unique_key` VARCHAR(64) NOT NULL COMMENT '唯一键',
  `sites` VARCHAR(32) NOT NULL COMMENT '支持站点',
  `keywords` VARCHAR(128) NOT NULL COMMENT '关键字',
  `categories` VARCHAR(16) DEFAULT NULL COMMENT '类目',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key` (`unique_key`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='节目总表'
;


/******************************************/
/*   表名称 = programme_source   */
/******************************************/
CREATE TABLE `programme_source` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `programme_id` bigint(20) DEFAULT NULL COMMENT '基础表ID',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `site` int(11) NOT NULL COMMENT '站点',
  `source_id` VARCHAR(64) NOT NULL COMMENT '源ID',
  `url` varchar(256) NOT NULL COMMENT '页面地址',
  `title` varchar(128) NOT NULL COMMENT '标题',
  `release_year` int(11) NOT NULL COMMENT '发型年',
  `img` VARCHAR(128) DEFAULT NULL COMMENT '图片',
  `types` VARCHAR(32) DEFAULT NULL COMMENT '类型',
  `directors` VARCHAR(512) DEFAULT NULL COMMENT '导演',
  `performers` VARCHAR(512) DEFAULT NULL COMMENT '演员',
  `languages` VARCHAR(32) DEFAULT NULL COMMENT '语言',
  `alias` VARCHAR(128) DEFAULT NULL COMMENT '别名',
  `seconds` bigint(20) unsigned NOT NULL COMMENT '时长',
  `score` decimal(10,2) NOT NULL COMMENT '评分',
  `imdb` VARCHAR(16) DEFAULT NULL COMMENT 'IMDB',
  `summary` VARCHAR(512) DEFAULT NULL COMMENT '简介',
  `areas` VARCHAR(32) DEFAULT NULL COMMENT '地区',
  `show_status` VARCHAR(16) DEFAULT NULL COMMENT '剧集状态',
  `season` int(8) DEFAULT NULL COMMENT '季数',
  `total_episode` int(8) DEFAULT NULL COMMENT '总集数',
  `status` tinyint(4) NOT NULL COMMENT '0：无效，1：有效',
  `attributes` VARCHAR(256) DEFAULT NULL COMMENT '附加属性',
  `category` int(8) DEFAULT NULL COMMENT '类目',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_source` (`site`,`source_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='节目源'
;


CREATE TABLE `proxy` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `host` VARCHAR(128) NOT NULL COMMENT '基础表ID',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `avg_cost` int(11) NOT NULL COMMENT '平均耗时',
  `success_rate` decimal(10,2) NOT NULL COMMENT '平均耗时',
  `source` VARCHAR(128) DEFAULT NULL COMMENT '失败',
  `failed_count` int(8) DEFAULT NULL COMMENT '失败次数',
  `attributes` VARCHAR(512) DEFAULT NULL COMMENT '扩展字段',
  `user_name` VARCHAR(64) DEFAULT NULL COMMENT '用户名',
  `password` VARCHAR(64) DEFAULT NULL COMMENT '密码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_host` (`host`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='代理'
;


CREATE TABLE `task_queue` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `status` int(4) NOT NULL COMMENT '0:waiting,1:running,2:failed,3:success',
  `attributes` VARCHAR(512) DEFAULT NULL COMMENT '扩展属性',
  `type` int(11) NOT NULL COMMENT '类型',
  `version` int(11) NOT NULL COMMENT '版本',
  `unique_key` varchar(64) NOT NULL COMMENT '唯一key',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `timeout` datetime DEFAULT NULL COMMENT '超时时间',
  `execute_timeout` datetime  NOT NULL COMMENT '单次执行超时时间，超过会被重置状态',
  `msg` VARCHAR(128) DEFAULT NULL COMMENT '执行信息',
  `ip` VARCHAR(128) DEFAULT NULL COMMENT '执行机器',
  `retry_count` int(8) NOT NULL COMMENT '重试次数',
  `orig_retry_count` int(8) NOT NULL COMMENT '最多重试次数',
  `execute_interval_seconds` int(8) DEFAULT NULL COMMENT '执行间隔',
  `alarm_threshold` int(8) DEFAULT NULL COMMENT '报警阈值',
  `env` VARCHAR(8) DEFAULT NULL COMMENT '环境',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父ID',
  `children_count` int(8) DEFAULT NULL COMMENT '父节点数量',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uk` (`type`,`unique_key`),
  KEY `idx_status` (`status`,`retry_count`,`start_time`,`timeout`,`env`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='任务队列'
;