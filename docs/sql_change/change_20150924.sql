ALTER TABLE ordertable
ADD COLUMN client VARCHAR(30) NULL;


ALTER TABLE trade
ADD COLUMN client VARCHAR(30) NULL;


UPDATE ordertable
SET client = 'Browser';
UPDATE trade
SET client = 'Browser';


DROP TABLE IF EXISTS theme_collect;

/*==============================================================*/
/* Table: theme_collect                                         */
/*==============================================================*/
CREATE TABLE theme_collect
(
  id          INT(11) NOT NULL AUTO_INCREMENT,
  userId      INT(11),
  themeId     INT(11),
  createDate  TIMESTAMP,
  updateDate  TIMESTAMP,
  isDelete    TINYINT          DEFAULT 0,
  collectTime TIMESTAMP,
  PRIMARY KEY (id)
);


DROP TABLE IF EXISTS AppTheme;

/*==============================================================*/
/* Table: AppTheme                                              */
/*==============================================================*/
CREATE TABLE AppTheme
(
  id         INT(11)      NOT NULL AUTO_INCREMENT,
  themeNo    INT          NOT NULL,
  name       VARCHAR(100) NOT NULL,
  startTime  TIMESTAMP    NOT NULL,
  baseNum    INT,
  picUrl     VARCHAR(200) NOT NULL,
  createTime TIMESTAMP,
  updateTime TIMESTAMP,
  operator   VARCHAR(100),
  products   VARCHAR(500),
  isDelete   TINYINT(1)   NOT NULL DEFAULT 0,
  online     TINYINT(1)   NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);


DROP TABLE IF EXISTS AppThemeContent;

/*==============================================================*/
/* Table: AppThemeContent                                       */
/*==============================================================*/
CREATE TABLE AppThemeContent
(
  id         INT(11) NOT NULL AUTO_INCREMENT,
  themeId    INT,
  type       VARCHAR(10),
  content    TEXT,
  priority   INT,
  createTime TIMESTAMP,
  updateTime TIMESTAMP,
  operator   VARCHAR(100),
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS AppHome;

/*==============================================================*/
/* Table: AppHome                                               */
/*==============================================================*/
CREATE TABLE AppHome
(
  id         INT(11) NOT NULL AUTO_INCREMENT,
  createTime TIMESTAMP,
  updateTime TIMESTAMP,
  operator   VARCHAR(100),
  size       VARCHAR(50),
  picUrl     VARCHAR(200),
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS DeviceToken;

/*==============================================================*/
/* Table: DeviceToken                                           */
/*==============================================================*/
CREATE TABLE DeviceToken
(
  id    INT(11) NOT NULL AUTO_INCREMENT,
  token VARCHAR(128),
  badge INT,
  PRIMARY KEY (id)
);


/*==========================================================*/
/*      CMS相关的内容修改                                    */
/*=========================================================*/

ALTER TABLE product ADD COLUMN saleStatus VARCHAR(15) DEFAULT NULL;
UPDATE product
SET saleStatus = 'HOTSELL'
WHERE online = 1;
UPDATE product
SET saleStatus = 'NONE'
WHERE online = 0;


DROP TABLE IF EXISTS `cms_exhibition`;
CREATE TABLE `cms_exhibition` (
  `id`        INT(11) NOT NULL AUTO_INCREMENT,
  `beginTime` DATETIME         DEFAULT NULL,
  `endTime`   DATETIME         DEFAULT NULL,
  `name`      VARCHAR(100)     DEFAULT NULL,
  `type`      VARCHAR(20)      DEFAULT NULL,
  `jobStatus` INT(11)          DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT = 'CMS活动管理';


DROP TABLE IF EXISTS `cms_content`;
CREATE TABLE `cms_content` (
  `id`        INT(11)   NOT NULL AUTO_INCREMENT,
  `title`     VARCHAR(200)       DEFAULT NULL,
  `pic`       VARCHAR(500)       DEFAULT NULL,
  `link`      VARCHAR(300)       DEFAULT NULL,
  `backPic`   VARCHAR(500)       DEFAULT NULL,
  `priority`  INT(11)            DEFAULT NULL,
  `startTime` TIMESTAMP NULL     DEFAULT NULL,
  `endTime`   TIMESTAMP NULL     DEFAULT NULL,
  `moduleId`  INT(11)            DEFAULT NULL,
  `picSize`   INT(11)            DEFAULT '1',
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT = 'CMS内容管理';

DROP TABLE IF EXISTS `cms_module`;
CREATE TABLE `cms_module` (
  `id`          INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(30)               DEFAULT NULL,
  `description` VARCHAR(200)              DEFAULT NULL,
  `priority`    INT(11)                   DEFAULT NULL,
  `itemCount`   INT(11)                   DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



