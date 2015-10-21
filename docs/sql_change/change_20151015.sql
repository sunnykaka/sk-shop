ALTER TABLE apptheme CHANGE COLUMN `startTime` `startTime` datetime default NULL;

UPDATE apptheme SET startTime = '2015-10-09 10:00:00' WHERE themeNo = 1;
UPDATE apptheme SET startTime = '2015-10-12 10:00:00' WHERE themeNo = 2;
UPDATE apptheme SET startTime = '2015-10-13 10:00:00' WHERE themeNo = 3;
UPDATE apptheme SET startTime = '2015-10-14 10:00:00' WHERE themeNo = 4;

ALTER TABLE theme_collect ADD COLUMN deviceId VARCHAR(200) DEFAULT NULL;

ALTER TABLE trade ADD COLUMN version int(11) DEFAULT NULL;