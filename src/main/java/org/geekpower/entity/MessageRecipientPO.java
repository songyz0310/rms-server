package org.geekpower.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 消息接收方实体
 * 
 * @author songyz
 * @createTime 2020-05-30 11:39:48
 */
@Entity
@Table(name = "tb_message_recipient")
public class MessageRecipientPO {

    @Id
    @Column(name = "mr_id", columnDefinition = "int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键'")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mrId;

    @Column(name = "message_id", updatable = false, columnDefinition = "int(10) unsigned COMMENT '消息ID'")
    private int messageId;

    @Column(name = "recipient", columnDefinition = "int(10) unsigned NOT NULL COMMENT '接收人'")
    private int recipient;

    @Column(name = "is_delete", columnDefinition = "tinyint unsigned DEFAULT 0 NOT NULL COMMENT '是否已删除 : 0，未删除，1，已删除（回收站），2，已彻底删除'")
    private byte isDelete;

    @Column(name = "is_rubbish", columnDefinition = "tinyint unsigned DEFAULT 0 NOT NULL COMMENT '是否是垃圾 : 0，正常邮件，1，垃圾邮件'")
    private byte isRubbish;

    @Column(name = "is_read", columnDefinition = "tinyint unsigned DEFAULT 0 NOT NULL COMMENT '是否已读 : 0，未读，1，已读'")
    private byte isRead;

    @Column(name = "read_time", columnDefinition = "datetime COMMENT '读取时间'")
    private Date readTime;

    @Column(name = "update_time", columnDefinition = "datetime NOT NULL COMMENT '更新时间'")
    private Date updateTime;

    public int getMrId() {
        return mrId;
    }

    public void setMrId(int mrId) {
        this.mrId = mrId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getRecipient() {
        return recipient;
    }

    public void setRecipient(int recipient) {
        this.recipient = recipient;
    }

    public byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(byte isDelete) {
        this.isDelete = isDelete;
    }

    public byte getIsRubbish() {
        return isRubbish;
    }

    public void setIsRubbish(byte isRubbish) {
        this.isRubbish = isRubbish;
    }

    public byte getIsRead() {
        return isRead;
    }

    public void setIsRead(byte isRead) {
        this.isRead = isRead;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
