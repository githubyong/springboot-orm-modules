DROP ALL OBJECTS DELETE FILES;

DROP TABLE IF EXISTS "Student";
CREATE TABLE "Student"
(
    `id`   int(10) unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(64)    NOT NULL,
    `gender`       varchar(20)       NOT NULL,
    `age`          int(3)           NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS "Score";

CREATE TABLE "Score"
(
    `id`   int(10) unsigned NOT NULL AUTO_INCREMENT,
    `stu_id` int(10)    NOT NULL,
    `course_name`       varchar(20)       NOT NULL,
    `score_point`           decimal(10, 2)           NOT NULL,
    `year`          varchar(20)           NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;