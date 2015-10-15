use boss;

drop table if exists bulletin;
CREATE TABLE bulletin (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  content varchar(800) NULL DEFAULT NULL COMMENT '内容',
  effective bit NOT NULL DEFAULT 0 COMMENT '是否有效，默认有效',
  createTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  createBy int not null comment '创建人id',
  updateTime timestamp NULL DEFAULT NULL COMMENT '更新时间',
  updateBy int NULL DEFAULT NULL comment '更新者id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='公告表';

drop table if exists feedback;
CREATE TABLE feedback (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  advice varchar(800) NOT NULL COMMENT '反馈意见',
  contact varchar(100) NOT NULL COMMENT '联系方式',
  process bit NOT NULL DEFAULT 0 COMMENT '是否处理，默认没有',
  createTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  createBy int NULL DEFAULT NULL comment '创建人id',
  updateTime timestamp NULL DEFAULT NULL COMMENT '更新时间',
  updateBy int NULL DEFAULT NULL comment '更新者id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='意见反馈表';

drop table if exists links;
CREATE TABLE links (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  name varchar(100) NOT NULL COMMENT '名称',
  link varchar(100) NOT NULL COMMENT '链接',
  effective bit NOT NULL DEFAULT 1 COMMENT '是否有效，默认有效',
  createTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  createBy int not null comment '创建人id',
  updateTime timestamp NULL DEFAULT NULL COMMENT '更新时间',
  updateBy int NULL DEFAULT NULL comment '更新者id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='友情链接';