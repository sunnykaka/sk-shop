/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2015-4-21 16:20:29                           */
/*==============================================================*/


drop table if exists bar;

drop table if exists conf;

drop table if exists invoice;

drop table if exists order_base;

drop table if exists order_item;

drop table if exists test_object;

drop table if exists test_object_item;

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
   description          varchar(512) default NULL comment '˵��',
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
   receiver_name        varchar(128) comment '�ջ�������',
   receiver_phone       varchar(128) default NULL comment '�ջ����ֻ���(���ջ��˵绰��ҵ���ϱ������һ��)',
   receiver_mobile      varchar(128) default NULL comment '�ջ��˵绰(���ջ����ֻ�����ҵ���ϱ������һ��)',
   receiver_zip         varchar(128) default NULL comment '�ջ����ʱ�',
   receiver_state       varchar(64) comment '�ջ���ʡ��',
   receiver_city        varchar(64) comment '�ջ��˳���',
   receiver_district    varchar(64) default NULL comment '�ջ��˵���',
   receiver_address     varchar(256) comment '������ʡ��������ϸ��ַ',
   shipping_no          varchar(128) default NULL comment '�������',
   shipping_comp        varchar(64) default NULL comment '������˾',
   primary key (id)
);

/*==============================================================*/
/* Table: order_base                                            */
/*==============================================================*/
create table order_base
(
   id                   int(11) not null auto_increment,
   order_no             varchar(20) not null comment '�������',
   type                 varchar(15) not null comment '��������',
   status               varchar(20) not null comment '����״̬',
   shared_discount_fee  bigint(20) comment 'ϵͳ�Żݽ��',
   post_fee             bigint(20) comment '�ʷ�',
   actual_fee           bigint(20) comment 'ʵ�����',
   buyer_id             varchar(128) comment '���Id(�Ա���)',
   buyer_message        text comment '�������',
   remark               text comment '�ͷ���ע',
   repo_id              int(11) comment '�ⷿid',
   buy_time             datetime default '0000-00-00 00:00:00' comment '�µ�ʱ��',
   pay_time             datetime default '0000-00-00 00:00:00' comment '֧��ʱ��',
   need_receipt         tinyint(1) default NULL comment '�Ƿ���Ҫ��Ʊ',
   receipt_title        varchar(100) default NULL comment '��Ʊ̧ͷ',
   receipt_content      varchar(512) default NULL comment '��Ʊ����',
   platform_type        varchar(10) not null comment '�ⲿƽ̨����(��è���Ǿ���)',
   create_time          datetime default '0000-00-00 00:00:00' comment '����ʱ��',
   update_time          datetime default '0000-00-00 00:00:00' comment '����ʱ��',
   operator_id          int(11),
   invoice_id           int(11) comment '��������ϢID',
   primary key (id)
);

/*==============================================================*/
/* Table: order_item                                            */
/*==============================================================*/
create table order_item
(
   id                   int(11) not null auto_increment,
   platform_type        varchar(20) not null comment '�ⲿƽ̨����(��è���Ǿ���)',
   product_id           int(11) not null comment '��Ʒid',
   product_code         varchar(32) comment '��Ʒ����',
   product_sku          varchar(32) not null comment '��Ʒ������',
   product_name         varchar(32) comment '��Ʒ����',
   order_id             int(11) not null comment '����ID',
   status               varchar(20) comment '������״̬',
   type                 varchar(20) not null comment '����������(��Ʒ, �ײ�, ��Ʒ...)',
   price                bigint(20) comment '��Ʒ����',
   discount_fee         bigint(20) comment '������',
   buy_count            int(11) comment '��������',
   shared_discount_fee  bigint(20) comment '��̯�Żݽ��',
   actual_fee           bigint(20) comment 'ʵ���ܽ��',
   primary key (id)
);

/*==============================================================*/
/* Table: test_object                                           */
/*==============================================================*/
create table test_object
(
   id                   int(11) not null auto_increment,
   status               varchar(20) not null comment '����״̬',
   actual_fee           bigint(20) comment 'ʵ�����',
   buyer_id             varchar(128) comment '���Id(�Ա���)',
   buy_time             datetime default '0000-00-00 00:00:00' comment '�µ�ʱ��',
   platform_type        varchar(10) not null comment '�ⲿƽ̨����(��è���Ǿ���)',
   create_time          datetime default '0000-00-00 00:00:00' comment '����ʱ��',
   update_time          datetime default '0000-00-00 00:00:00' comment '����ʱ��',
   operator_id          int(11),
   order_no             varchar(20) not null comment '�������',
   primary key (id)
);

/*==============================================================*/
/* Table: test_object_item                                      */
/*==============================================================*/
create table test_object_item
(
   id                   int(11) not null auto_increment,
   product_sku          varchar(32) not null comment '��Ʒ������',
   product_id           int(11) not null comment '��Ʒid',
   test_object_id       int(11) not null comment '����ID',
   status               varchar(20) comment '������״̬',
   price                bigint(20) comment '��Ʒ����',
   primary key (id)
);

alter table order_base add constraint FK_Reference_2 foreign key (invoice_id)
      references invoice (id) on delete restrict on update restrict;

alter table order_item add constraint FK_Reference_1 foreign key (order_id)
      references order_base (id) on delete restrict on update restrict;

alter table test_object_item add constraint FK_Reference_3 foreign key (test_object_id)
      references test_object (id) on delete restrict on update restrict;

