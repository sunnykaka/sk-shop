use boss;

drop table if exists product_spec;

/*==============================================================*/
/* Table: product_spec                                          */
/*==============================================================*/
create table product_spec
(
   id                   int(10) unsigned not null auto_increment,
   name                 varchar(255),
   value                varchar(255),
   productId            text,
   primary key (id)
);
