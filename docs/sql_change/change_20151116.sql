ALTER TABLE limitedtimediscount
ADD COLUMN `discountName` VARCHAR(45) NULL COMMENT '' ,
ADD COLUMN `handleStatus` INT NULL COMMENT '' ;

ALTER TABLE valuation
ADD COLUMN `replyUserId` int(11) DEFAULT '0',
ADD COLUMN `replyUserName` varchar(128) DEFAULT NULL;