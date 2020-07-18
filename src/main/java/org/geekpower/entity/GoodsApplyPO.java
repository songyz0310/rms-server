package org.geekpower.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 商品申请表
 * 
 * @author songyz
 * @createTime 2020-05-30 11:39:20
 */
@Entity
@Table(name = "tb_goods_apply")
public class GoodsApplyPO {

    @Id
    @Column(name = "apply_id", columnDefinition = "int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '申请ID'")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applyId;

    @Column(name = "goods_id", updatable = false, columnDefinition = "int(10) unsigned NOT NULL COMMENT '商品ID'")
    private Integer goodsId;

    @Column(name = "instance_id", columnDefinition = "varchar(64) COMMENT '流程实例ID'")
    private String instanceId;

    @Column(name = "task_id", columnDefinition = "varchar(64) COMMENT '当前任务ID'")
    private String taskId;

    @Column(name = "task_name", columnDefinition = "varchar(64) COMMENT '当前任务名称'")
    private String taskName;

    @Column(name = "role_id", columnDefinition = "int(10) COMMENT '当前任务审核角色ID'")
    private int roleId;

    @Column(name = "status", columnDefinition = "tinyint unsigned DEFAULT 0 NOT NULL COMMENT '状态 : 0，进行中，1，已通过，2已驳回'")
    private byte status;

    @Column(name = "create_time", updatable = false, columnDefinition = "datetime NOT NULL COMMENT '创建时间'")
    private Date createTime;

    @Column(name = "update_time", columnDefinition = "datetime NOT NULL COMMENT '更新时间'")
    private Date updateTime;

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

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
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

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

}
