package org.geekpower.common.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageRecipientDTO extends MessageDTO {

    private int mrId;
    private int recipient;
    private byte isRubbish;
    private byte isRead;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date readTime;

    /*********************************************/
    private UserDTO recipientUser;

    public int getMrId() {
        return mrId;
    }

    public void setMrId(int mrId) {
        this.mrId = mrId;
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

    public UserDTO getRecipientUser() {
        return recipientUser;
    }

    public void setRecipientUser(UserDTO recipientUser) {
        this.recipientUser = recipientUser;
    }

}
