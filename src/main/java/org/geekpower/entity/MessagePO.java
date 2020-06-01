package org.geekpower.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 站内消息表实体
 * 
 * @author songyz
 * @createTime 2020-05-30 11:39:20
 */
@Entity
@Table(name = "tb_message")
public class MessagePO {

    @Id
    @Column(name = "message_id", columnDefinition = "int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '消息ID'")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @Column(name = "message_title", columnDefinition = "varchar(255) NOT NULL COMMENT '消息标题'")
    private String messageTitle;

    @Column(name = "rich_content", columnDefinition = "TEXT NOT NULL COMMENT '消息内容（富文本）'")
    private String richContent;

    @Column(name = "simple_content", columnDefinition = "TEXT NOT NULL COMMENT '消息内容（纯文本）'")
    private String simpleContent;

    @Column(name = "status", columnDefinition = "tinyint unsigned DEFAULT 0 NOT NULL COMMENT '状态 : 0，草稿，1，已生效'")
    private short status;

    @Column(name = "is_delete", columnDefinition = "tinyint unsigned DEFAULT 0 NOT NULL COMMENT '是否已删除 : 0，未删除，1，已删除（回收站），2，已彻底删除'")
    private short isDelete;

    @Column(name = "sender", updatable = false, columnDefinition = "int(10) unsigned NOT NULL COMMENT '发送者'")
    private int sender;

    @Column(name = "create_time", updatable = false, columnDefinition = "datetime NOT NULL COMMENT '创建时间'")
    private Date createTime;

    @Column(name = "update_time", columnDefinition = "datetime NOT NULL COMMENT '更新时间'")
    private Date updateTime;

    @Column(name = "send_time", columnDefinition = "datetime COMMENT '发送时间'")
    private Date sendTime;

    @Column(name = "ref_message_id", columnDefinition = "int(10) unsigned COMMENT '关联消息ID'")
    private Integer refMessageId;

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getRichContent() {
        return richContent;
    }

    public void setRichContent(String richContent) {
        this.richContent = richContent;
    }

    public String getSimpleContent() {
        return simpleContent;
    }

    public void setSimpleContent(String simpleContent) {
        this.simpleContent = simpleContent;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public short getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(short isDelete) {
        this.isDelete = isDelete;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Integer getRefMessageId() {
        return refMessageId;
    }

    public void setRefMessageId(Integer refMessageId) {
        this.refMessageId = refMessageId;
    }

}
