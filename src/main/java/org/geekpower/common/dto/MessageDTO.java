package org.geekpower.common.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MessageDTO {

    private int messageId;
    private String messageTitle;
    private String richContent;
    private String simpleContent;
    private short status;
    private short isDelete;
    private int sender;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;
    private Integer refMessageId;

    private UserDTO sendUser;
    private UserDTO recipientUser;
    private MessageDTO refMessage;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
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

    public UserDTO getSendUser() {
        return sendUser;
    }

    public void setSendUser(UserDTO sendUser) {
        this.sendUser = sendUser;
    }

    public UserDTO getRecipientUser() {
        return recipientUser;
    }

    public void setRecipientUser(UserDTO recipientUser) {
        this.recipientUser = recipientUser;
    }

    public MessageDTO getRefMessage() {
        return refMessage;
    }

    public void setRefMessage(MessageDTO refMessage) {
        this.refMessage = refMessage;
    }

}
