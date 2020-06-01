SET SESSION FOREIGN_KEY_CHECKS=0;

/* Drop Tables */

DROP TABLE IF EXISTS tb_message_recipient;
DROP TABLE IF EXISTS tb_message;
DROP TABLE IF EXISTS tb_user;




/* Create Tables */

-- 站内消息表
CREATE TABLE tb_message
(
	message_id int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '消息ID',
	message_title varchar(255) NOT NULL COMMENT '消息标题',
	rich_content text NOT NULL COMMENT '消息内容（富文本）',
	simple_content text NOT NULL COMMENT '消息内容（纯文本）',
	-- 0，草稿
	-- 1，已生效
	-- 
	status tinyint unsigned DEFAULT 0 NOT NULL COMMENT '状态 : 0，草稿
1，已生效
',
	-- 0，未删除
	-- 1，已删除（回收站）
	-- 2，已彻底删除
	is_delete tinyint DEFAULT 0 NOT NULL COMMENT '是否已删除 : 0，未删除
1，已删除（回收站）
2，已彻底删除',
	create_time datetime NOT NULL COMMENT '创建时间',
	update_time datetime NOT NULL COMMENT '更新时间',
	send_time datetime COMMENT '发送时间',
	sender int(10) unsigned NOT NULL COMMENT '发送者',
	ref_message_id int(10) unsigned COMMENT '关联消息ID',
	PRIMARY KEY (message_id)
) COMMENT = '站内消息表';


-- 消息接收方
CREATE TABLE tb_message_recipient
(
	mr_id int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
	message_id int(10) unsigned NOT NULL COMMENT '消息ID',
	recipient int(10) unsigned NOT NULL COMMENT '接收人',
	-- 0，未删除
	-- 1，已删除（回收站）
	-- 2，已彻底删除
	is_delete tinyint unsigned NOT NULL COMMENT '是否已删除 : 0，未删除
1，已删除（回收站）
2，已彻底删除',
	-- 0，正常邮件
	-- 1，垃圾邮件
	is_rubbish tinyint unsigned DEFAULT 0 NOT NULL COMMENT '是否是垃圾 : 0，正常邮件
1，垃圾邮件',
	-- 0，未读
	-- 1，已读
	is_read tinyint unsigned DEFAULT 0 NOT NULL COMMENT '是否已读 : 0，未读
1，已读',
	read_time datetime COMMENT '读取时间',
	update_time datetime NOT NULL COMMENT '更新时间',
	PRIMARY KEY (mr_id)
) COMMENT = '消息接收方';


-- 人员表
CREATE TABLE tb_user
(
	user_id int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '人员ID',
	account varchar(32) NOT NULL COMMENT '账号',
	user_name varchar(84) NOT NULL COMMENT '人员名称',
	password varchar(64) NOT NULL COMMENT '密码',
	PRIMARY KEY (user_id)
) COMMENT = '人员表';



