/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2015-4-2 16:03:33                            */
/*==============================================================*/


drop table if exists bar;

drop table if exists conf;

drop table if exists invoice;

drop table if exists order_base;

drop table if exists order_item;

/*==============================================================*/
/* Table: bar                                                   */
/*==============================================================*/
create table bar
(
   id                   int(11) not null auto_increment,
   name                 varchar(64) not null,
   primary key (id)
);

/*==============================================================*/
/* Table: conf                                                  */
/*==============================================================*/
create table conf
(
   id                   int(11) not null auto_increment,
   name                 varchar(64) not null comment 'key',
   value                varchar(128) not null comment 'value',
   description          varchar(512) default NULL comment '说明',
   create_time          datetime default '0000-00-00 00:00:00',
   update_time          datetime default '0000-00-00 00:00:00',
   operator_id          int(11),
   primary key (id),
   unique key config_key (name)
);

/*==============================================================*/
/* Table: invoice                                               */
/*==============================================================*/
create table invoice
(
   id                   int(11) not null auto_increment,
   receiver_name        varchar(128) comment '收货人姓名',
   receiver_phone       varchar(128) default NULL comment '收货人手机号(与收货人电话在业务上必须存在一个)',
   receiver_mobile      varchar(128) default NULL comment '收货人电话(与收货人手机号在业务上必须存在一个)',
   receiver_zip         varchar(128) default NULL comment '收货人邮编',
   receiver_state       varchar(64) comment '收货人省份',
   receiver_city        varchar(64) comment '收货人城市',
   receiver_district    varchar(64) default NULL comment '收货人地区',
   receiver_address     varchar(256) comment '不包含省市区的详细地址',
   shipping_no          varchar(128) default NULL comment '物流编号',
   shipping_comp        varchar(64) default NULL comment '物流公司',
   primary key (id)
);

/*==============================================================*/
/* Table: order_base                                            */
/*==============================================================*/
create table order_base
(
   id                   int(11) not null auto_increment,
   order_no             varchar(20) not null comment '订单编号',
   type                 varchar(15) not null comment '订单类型',
   status               varchar(20) not null comment '订单状态',
   shared_discount_fee  bigint(20) comment '系统优惠金额',
   post_fee             bigint(20) comment '邮费',
   actual_fee           bigint(20) comment '实付金额',
   buyer_id             varchar(128) comment '买家Id(淘宝号)',
   buyer_message        text comment '买家留言',
   remark               text comment '客服备注',
   repo_id              int(11) comment '库房id',
   buy_time             timestamp default '0000-00-00 00:00:00' comment '下单时间',
   pay_time             timestamp default '0000-00-00 00:00:00' comment '支付时间',
   need_receipt         tinyint(1) default NULL comment '是否需要发票',
   receipt_title        varchar(100) default NULL comment '发票抬头',
   receipt_content      varchar(512) default NULL comment '发票内容',
   platform_type        varchar(10) not null comment '外部平台类型(天猫还是京东)',
   create_time          timestamp default '0000-00-00 00:00:00' comment '创建时间',
   update_time          timestamp default '0000-00-00 00:00:00' comment '更新时间',
   operator_id          int(11),
   invoice_id           int(11) comment '发货单信息ID',
   primary key (id)
);

/*==============================================================*/
/* Table: order_item                                            */
/*==============================================================*/
create table order_item
(
   id                   int(11) not null auto_increment,
   platform_type        varchar(20) not null comment '外部平台类型(天猫还是京东)',
   product_id           int(11) not null comment '商品id',
   product_code         varchar(32) comment '商品编码',
   product_sku          varchar(32) not null comment '商品条形码',
   product_name         varchar(32) comment '商品名称',
   order_id             int(11) not null comment '订单ID',
   status               varchar(20) comment '订单项状态',
   type                 varchar(20) not null comment '订单项类型(商品, 套餐, 赠品...)',
   price                bigint(20) comment '商品单价',
   discount_fee         bigint(20) comment '促销价',
   buy_count            int(11) comment '购买数量',
   shared_discount_fee  bigint(20) comment '分摊优惠金额',
   actual_fee           bigint(20) comment '实付总金额',
   primary key (id)
);

alter table order_base add constraint FK_Reference_2 foreign key (invoice_id)
      references invoice (id) on delete restrict on update restrict;

alter table order_item add constraint FK_Reference_1 foreign key (order_id)
      references order_base (id) on delete restrict on update restrict;

