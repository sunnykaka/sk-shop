drop table if exists theme_collect;

/*==============================================================*/
/* Table: theme_collect                                         */
/*==============================================================*/
create table theme_collect
(
   id                   int(11) not null auto_increment,
   userId               int(11),
   themeNo              int(11),
   createDate           timestamp,
   updateDate           timestamp,
   isDelete             tinyint default 0,
   collectTime          timestamp,
   primary key (id)
);


drop table if exists AppTheme;

/*==============================================================*/
/* Table: AppTheme                                              */
/*==============================================================*/
create table AppTheme
(
   id                   int(11) not null auto_increment,
   themeNo              int not null,
   name                 varchar(100) not null,
   startTime            timestamp not null,
   baseNum              int,
   picUrl               varchar(200) not null,
   createTime           timestamp,
   updateTime           timestamp,
   operator             varchar(100),
   products             varchar(500),
   isDelete             tinyint(1) not null default 0,
   online               tinyint(1) not null default 0,
   digest               varchar(500) not null,
   primary key (id)
);


drop table if exists AppThemeContent;

/*==============================================================*/
/* Table: AppThemeContent                                       */
/*==============================================================*/
create table AppThemeContent
(
   id                   int(11) not null auto_increment,
   themeId              int,
   content              text,
   createTime           timestamp,
   updateTime           timestamp,
   operator             varchar(100),
   primary key (id)
);