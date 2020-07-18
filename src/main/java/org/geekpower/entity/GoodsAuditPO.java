package org.geekpower.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 商品申请审核表
 * 
 * @author songyz
 * @createTime 2020-05-30 11:39:20
 */
@Entity
@Table(name = "tb_goods_audit")
public class GoodsAuditPO {

    @Id
    @Column(name = "audit_id", columnDefinition = "int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '审核ID'")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer auditId;

    @Column(name = "apply_id", updatable = false, columnDefinition = "int(10) unsigned NOT NULL COMMENT '申请ID'")
    private Integer applyId;

    @Column(name = "task_id", updatable = false, columnDefinition = "varchar(64) NOT NULL COMMENT '当前任务ID'")
    private String taskId;

    @Column(name = "task_name", updatable = false, columnDefinition = "varchar(64) NOT NULL COMMENT '当前任务名称'")
    private String taskName;

    @Column(name = "status", updatable = false, columnDefinition = "tinyint unsigned DEFAULT 0 NOT NULL COMMENT '状态 : 1，已通过，2已驳回'")
    private byte status;

    @Column(name = "audit_user", updatable = false, columnDefinition = "int(10) unsigned NOT NULL COMMENT '审核人ID'")
    private int auditUser;

    @Column(name = "audit_time", updatable = false, columnDefinition = "datetime NOT NULL COMMENT '审核时间'")
    private Date auditTime;

    @Column(name = "audit_advice", updatable = false, columnDefinition = "varchar(64) NOT NULL COMMENT '审核意见'")
    private String auditAdvice;

    public Integer getAuditId() {
        return auditId;
    }

    public void setAuditId(Integer auditId) {
        this.auditId = auditId;
    }

    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
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

    public int getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(int auditUser) {
        this.auditUser = auditUser;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditAdvice() {
        return auditAdvice;
    }

    public void setAuditAdvice(String auditAdvice) {
        this.auditAdvice = auditAdvice;
    }

}
