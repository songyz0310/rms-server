package org.geekpower.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 人员表实体
 * 
 * @author songyz
 * @createTime 2020-05-30 11:39:08
 */
@Entity
@Table(name = "tb_user")
public class UserPO {

    @Id
    @Column(name = "user_id", columnDefinition = "int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '人员ID'")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "account", columnDefinition = "varchar(32) NOT NULL COMMENT '账号'")
    private String account;

    @Column(name = "user_name", columnDefinition = "varchar(64) NOT NULL COMMENT '用户名'")
    private String userName;

    @Column(name = "password", columnDefinition = "varchar(64) NOT NULL COMMENT '密码'")
    private String password;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
