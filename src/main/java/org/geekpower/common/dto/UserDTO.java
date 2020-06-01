package org.geekpower.common.dto;

public class UserDTO {

	private int userId;
	private String account;
	private String userName;

	public UserDTO() {
	}

	public UserDTO(int userId, String account, String userName) {
		this.userId = userId;
		this.account = account;
		this.userName = userName;
	}

	public UserDTO(int userId, String account) {
		this.userId = userId;
		this.account = account;
	}

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

}
