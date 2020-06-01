package org.geekpower.form;

import java.util.List;

public class MessageParam {

    private int messageId;
    private String messageTitle;
    private String richContent;
    private String simpleContent;
    private Integer refMessageId;

    private List<Integer> recipients;

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

    public Integer getRefMessageId() {
        return refMessageId;
    }

    public void setRefMessageId(Integer refMessageId) {
        this.refMessageId = refMessageId;
    }

    public List<Integer> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Integer> recipients) {
        this.recipients = recipients;
    }

}
