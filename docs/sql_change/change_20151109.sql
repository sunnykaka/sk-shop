DROP TABLE IF EXISTS `homenewproduct`;
CREATE TABLE `homenewproduct` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `productId` int(11) NOT NULL,
  `priority` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='app首页新品';


DROP TABLE IF EXISTS `homefocus`;
CREATE TABLE `homefocus` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `picUrl` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `content` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `priority` int(11) DEFAULT '0',
  `pageType` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `createTime` datetime DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='app首页焦点图';


ALTER TABLE product ADD COLUMN `onboardInfo` VARCHAR(300) NULL DEFAULT NULL COMMENT '';
