DROP ALL OBJECTS DELETE FILES;
drop schema if exists `test`;
create schema `test`;
use `test`;
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`
(
    `id`   int(10) unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(64)    NOT NULL,
    `gender`       varchar(20)       NOT NULL,
    `age`          int(3)           NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;