ALTER TABLE stockkeepingunit ADD COLUMN defaultSku boolean DEFAULT false;

update productpicture set skuId = concat(',',skuId,',');
alter table productpicture change column skuId skuId varchar(100) default ',0,';