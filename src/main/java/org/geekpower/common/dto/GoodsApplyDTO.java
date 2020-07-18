package org.geekpower.common.dto;

import java.util.Date;

public class GoodsApplyDTO {

    private Integer applyId;
    private Integer goodsId;
    private String taskId;
    private String taskName;
    private byte status;
    private Date createTime;
    private Date updateTime;

    /**************************************/
    private GoodsDTO goodsObj;

    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
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

    public GoodsDTO getGoodsObj() {
        return goodsObj;
    }

    public void setGoodsObj(GoodsDTO goodsObj) {
        this.goodsObj = goodsObj;
    }

}
