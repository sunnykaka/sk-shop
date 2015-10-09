use boss;

drop table if exists message_job;

/*==============================================================*/
/* Table: message_job                                           */
/*==============================================================*/
create table message_job
(
   id                   int(10) unsigned not null auto_increment,
   contact              varchar(1023),
   content              text,
   processed            boolean default false,
   type                 varchar(31),
   createTime           datetime,
   updateTime           datetime,
   title                varchar(255),
   targetTime           datetime,
   processInfo          text,
   source               varchar(63),
   primary key (id)
);

/* 商品列表页，给前后台类目关联表增加主键，解决JPA映射不能没有主键的问题  */
alter table categoryassociation add column id int(11)  auto_increment ,add primary key(id);

/* 给设计师表增加优先级字段，默认值1000，方便向前，后调顺序的自由度 */
alter table customer add column priority int(11) DEFAULT 1000;