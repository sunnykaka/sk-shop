ALTER TABLE logistics CHANGE COLUMN `zipCode` `zipCode` VARCHAR(10) NULL COMMENT '邮编' ;

ALTER TABLE address CHANGE COLUMN `zipCode` `zipCode` VARCHAR(10) NULL COMMENT '邮编';

