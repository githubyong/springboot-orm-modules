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
);

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
);


create table "order_info"
(
    `id`                      bigint NOT NULL AUTO_INCREMENT,
    `order_sn`                varchar(64) COMMENT '订单编号',
    `member_username`         varchar(64) COMMENT '用户账号',
    `total_amount`            decimal(10, 2) COMMENT '订单总金额',
    `order_type`              int(1) COMMENT '订单类型：0->正常订单；1->秒杀订单',
    `status`                  int(1) COMMENT '订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单',
    `pay_type`                int(1) COMMENT '支付方式：0->未支付；1->支付宝；2->微信',
    `receiver_phone`          varchar(32) COMMENT '收货人电话',
    `receiver_detail_address` varchar(200) COMMENT '详细地址',
    `delivery_sn`             varchar(64) COMMENT '物流编号',
    `create_time`             datetime COMMENT '创建时间',
    PRIMARY KEY (`id`)
);

create table "order_item"
(
    `id`               bigint NOT NULL AUTO_INCREMENT,
    `order_sn`         varchar(64) COMMENT '订单编号',
    `product_id`       bigint COMMENT '商品编号',
    `product_name`     varchar(64) COMMENT '商品名称',
    `product_brand`    varchar(64) COMMENT '商品品牌',
    `product_sn`       varchar(64) COMMENT '商品条码',
    `product_price`    decimal(10, 2) COMMENT '销售价格',
    `product_quantity` int COMMENT '购买数量',
    PRIMARY KEY (`id`)
);
