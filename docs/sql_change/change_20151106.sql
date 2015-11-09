alter table ordertable add column voucherFee bigint(20) default 0;

drop table if exists voucher_batch;

/*==============================================================*/
/* Table: voucher_batch                                         */
/*==============================================================*/
create table voucher_batch
(
   id                   int(11) not null auto_increment,
   status               varchar(20) not null comment '代金券状态',
   type                 varchar(20) not null comment '分发类型',
   amount               bigint(20) default 0 comment '面额',
   uniqueNo             varchar(128) not null comment '唯一编号',
   startTime            datetime default '0000-00-00 00:00:00' comment '开始时间',
   endTime              datetime default '0000-00-00 00:00:00' comment '结束时间',
   deadline             datetime default '0000-00-00 00:00:00' comment '代金券截止时间',
   periodDay            int(11) comment '代金券有效期',
   minOrderAmount       bigint(20) default 0 comment '最小订单金额',
   maxQuantity          int(11) comment '代金券最大数量',
   createTime           datetime default '0000-00-00 00:00:00' comment '创建时间',
   remark               varchar(512) comment '备注',
   primary key (id),
   unique key AK_Key_2 (uniqueNo)
);

alter table voucher_batch comment 'voucher_batch';


drop table if exists voucher;

/*==============================================================*/
/* Table: voucher                                               */
/*==============================================================*/
create table voucher
(
   id                   int(11) not null auto_increment,
   status               varchar(20) not null comment '代金券状态',
   type                 varchar(20) not null comment '分发类型',
   amount               bigint(20) default 0 comment '面额',
   uniqueNo             varchar(128) not null comment '唯一编号',
   deadline             datetime default '0000-00-00 00:00:00' comment '代金券截止时间',
   minOrderAmount       bigint(20) default 0 comment '最小订单金额',
   createTime           datetime default '0000-00-00 00:00:00' comment '创建时间',
   useTime              datetime default '0000-00-00 00:00:00' comment '使用时间',
   userId               bigint(20) comment '关联用户',
   batchId              bigint(20),
   primary key (id),
   unique key AK_Key_2 (uniqueNo)
);

alter table voucher comment 'voucher';


drop table if exists voucher_use;

/*==============================================================*/
/* Table: voucher_use                                           */
/*==============================================================*/
create table voucher_use
(
   id                   int(11) not null auto_increment,
   createTime           datetime default '0000-00-00 00:00:00' comment '创建时间',
   orderId              bigint(20),
   voucherId            bigint(20),
   primary key (id)
);

alter table voucher_use comment 'voucher_use';


