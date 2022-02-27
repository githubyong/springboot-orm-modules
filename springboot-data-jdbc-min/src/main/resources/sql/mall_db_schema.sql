DROP ALL OBJECTS DELETE FILES;
drop schema if exists `test`;
create schema `test`;
use `test`;

create table "user_info"
(
    `id`          bigint    NOT NULL AUTO_INCREMENT,
    `login_name`  varchar(64) COMMENT '登录账号',
    `pasword`     varchar(64) COMMENT '密码',
    `nick_name`   varchar(64) COMMENT '昵称',
    `email`       varchar(64),
    `telephone`   varchar(11),
    `create_time` datetime,
    `update_time` timestamp not null,
    PRIMARY key (`id`)
)ENGINE = InnoDB DEFAULT CHARSET = utf8;

create table "user_address"
(
    `id`                      bigint NOT NULL AUTO_INCREMENT,
    `user_id`                 int,
    `receiver_name`           varchar(64) COMMENT '收货人姓名',
    `receiver_phone`          varchar(32) COMMENT '收货人电话',
    `receiver_detail_address` varchar(200) COMMENT '收货人详细地址',
    `user_tag`                varchar(32) COMMENT '收货人标签',
    `create_time`             datetime,
    `update_time`             timestamp,
    PRIMARY key (`id`)
)ENGINE = InnoDB DEFAULT CHARSET = utf8;
