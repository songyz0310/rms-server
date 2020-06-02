package org.geekpower.common.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MessageRecipientDTO {

    private int mrId;
    private int messageId;
    private int recipient;
    private byte isRubbish;
    private byte isRead;
    private Date readTime;
    private Date updateTime;

    /*********************************************/
    private String messageTitle;
    private String richContent;
    private String simpleContent;
    private short status;
    private int sender;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;
    private Integer refMessageId;
    private UserDTO sendUser;
    private List<UserDTO> recipientUsers;
    private MessageDTO refMessage;

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

    public List<UserDTO> getRecipientUsers() {
        return recipientUsers;
    }

    public void setRecipientUsers(List<UserDTO> recipientUsers) {
        this.recipientUsers = recipientUsers;
    }

    public MessageDTO getRefMessage() {
        return refMessage;
    }

    public void setRefMessage(MessageDTO refMessage) {
        this.refMessage = refMessage;
    }

}
