use boss;

drop table if exists login_record;

/*==============================================================*/
/* Table: login_record                                          */
/*==============================================================*/
create table login_record
(
   id                   int(10) unsigned not null auto_increment,
   deviceInfo           varchar(255),
   deviceId             varchar(255),
   channel              varchar(31),
   userId               int,
   createTime           datetime,
   primary key (id)
);