# 数据库初始化

-- 创建库
create database if not exists lack;

-- 切换库
use lack;

# 用户表
create table user
(
    username     varchar(256)                       null comment '用户昵称',
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    profile      varchar(512)                       null comment '个人简介',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '状态 0 - 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户角色 0 - 普通用户 1 - 管理员',
    planetCode   varchar(512)                       null comment '人员编号'
)
    comment '用户';

# 导入示例用户
INSERT INTO uni.user (username, userAccount, avatarUrl, gender,profile, userPassword, phone, email, userStatus, createTime, updateTime, isDelete, userRole, planetCode) VALUES ('二哈', 'erha', null, null,  null,'b0dd3697a192885d7c055db46155b26a', null, null, 0, '2023-11-06 14:14:22', '2023-11-06 14:39:37', 0, 1, '1');


-- 队伍表
create table team
(
    id bigint auto_increment comment 'id'
        primary key,
    name varchar(256) not null comment '队伍名称',
    description varchar(1024) null comment '描述',
    maxNum int default 1 not null comment '最大人数',
    expireTime datetime null comment '过期时间',
    userId bigint comment '用户id',
    status int default 0 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password varchar(512) null comment '密码',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete tinyint default 0 not null comment '是否删除'
)
    comment '队伍';

-- 用户队伍关系表
create table user_team
(
    id bigint auto_increment comment 'id'
        primary key,
    userId bigint comment '用户id',
    teamId bigint comment '队伍id',
    joinTime datetime null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete tinyint default 0 not null comment '是否删除'
)
    comment '用户队伍关系';

