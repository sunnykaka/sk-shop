ALTER TABLE limitedtimediscount
ADD COLUMN `discountName` VARCHAR(45) NULL COMMENT '' ,
ADD COLUMN `handleStatus` INT NULL COMMENT '' ;

ALTER TABLE valuation
ADD COLUMN `replyUserId` int(11) DEFAULT '0',
ADD COLUMN `replyUserName` varchar(128) DEFAULT NULL;

DROP TABLE IF EXISTS `voucher_activity`;
CREATE TABLE `voucher_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uniqueNo` varchar(128) NOT NULL,
  `activityId` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `createTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='活动与代金券关联';