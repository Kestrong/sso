CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `del_flg` tinyint(4) DEFAULT '1',
  `avatar` varchar(256) DEFAULT NULL,
  `open_id` varchar(128) NOT NULL,
  `username` varchar(32) NOT NULL,
  `password_hash` varchar(256) NOT NULL,
  `salt` varchar(128) NOT NULL,
  `first_name` varchar(32) CHARACTER SET utf8mb4 DEFAULT NULL,
  `last_name` varchar(32) CHARACTER SET utf8mb4 DEFAULT NULL,
  `telephone` varchar(16) DEFAULT NULL,
  `mail` varchar(64) DEFAULT NULL,
  `birthday` datetime DEFAULT NULL,
  `sex` varchar(8) DEFAULT 'unknown',
  `zip_code` varchar(16) DEFAULT NULL,
  `country` varchar(32) DEFAULT NULL,
  `province` varchar(32) DEFAULT NULL,
  `city` varchar(32) DEFAULT NULL,
  `zone` varchar(32) DEFAULT NULL,
  `address` varchar(256) DEFAULT NULL,
  `id_card` varchar(64) DEFAULT NULL,
  `system_code` varchar(16) DEFAULT NULL,
  `status` varchar(16) DEFAULT 'normal',
  `job` varchar(32) DEFAULT NULL,
  `description` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openId` (`open_id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_mail` (`mail`) USING BTREE,
  UNIQUE KEY `uk_telephone` (`telephone`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

# admin 123456
INSERT INTO `user` (`id`, `create_time`, `update_time`, `del_flg`, `open_id`, `username`, `password_hash`, `salt`, `first_name`, `last_name`, `telephone`, `mail`, `birthday`, `sex`, `zip_code`, `country`, `province`, `city`, `zone`, `address`, `id_card`, `system_code`, `status`) VALUES ('1', '2019-05-25 21:06:39', '2019-05-25 21:06:39', '1', '1', 'admin', '8ejEHySQT6Fln9JdjIFRJlgkVWxhxl5q', 'C75R9kDBVOYoXkReCPFxlvNOGggLp0VaxvR97rfBSmM=', NULL, NULL, NULL, NULL, NULL, 'unknown', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'normal');

CREATE TABLE `application` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `application_name` varchar(32) NOT NULL,
  `app_id` varchar(128) NOT NULL,
  `app_key` varchar(256) NOT NULL,
  `description` varchar(128) DEFAULT NULL,
  `status` varchar(16) DEFAULT 'blocked',
  `scope` varchar(256) DEFAULT NULL,
  `redirect_uri` varchar(256) DEFAULT NULL,
  `grand_type` varchar(128) DEFAULT NULL,
  `del_flg` int(11) DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_id` (`app_id`),
  UNIQUE KEY `uk_app_name` (`application_name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

INSERT INTO `application` (`id`, `application_name`, `app_id`, `app_key`, `description`, `status`,`scope`,`redirect_uri`,`grand_type`, `del_flg`, `create_time`, `update_time`) VALUES ('1', 'test', '123456', 'abcdefg', NULL, 'normal','user_info','http://localhost:8081','authorization_code', '1', '2019-06-13 16:24:03', '2019-06-13 16:24:03');
