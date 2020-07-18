package org.geekpower.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 商品表
 * 
 * @author songyz
 * @createTime 2020-05-30 11:39:20
 */
@Entity
@Table(name = "tb_goods")
public class GoodsPO {

    @Id
    @Column(name = "goods_id", columnDefinition = "int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '商品ID'")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer goodsId;

    @Column(name = "goods_title", columnDefinition = "varchar(255) NOT NULL COMMENT '商品标题'")
    private String goodsTitle;

    @Column(name = "goods_price", columnDefinition = "decimal(10,2) NOT NULL COMMENT '商品价格'")
    private BigDecimal goodsPrice;

    @Column(name = "status", columnDefinition = "tinyint unsigned DEFAULT 0 NOT NULL COMMENT '状态 : 0，已创建，1，已申请，2已驳回，3已发布'")
    private byte status;

    @Column(name = "create_time", updatable = false, columnDefinition = "datetime NOT NULL COMMENT '创建时间'")
    private Date createTime;

    @Column(name = "update_time", columnDefinition = "datetime NOT NULL COMMENT '更新时间'")
    private Date updateTime;

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
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

}
