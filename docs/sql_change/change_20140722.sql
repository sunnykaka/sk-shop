use boss;

drop table if exists sms_history;

/*==============================================================*/
/* Table: sms_history                                           */
/*==============================================================*/
create table sms_history
(
   id                   int(10) unsigned not null auto_increment,
   phones               varchar(1023),
   content              text,
   resp                 text,
   count                int default 0,
   createTime           datetime default '0000-00-00 00:00:00',
   result               varchar(127),
   primary key (id)
);