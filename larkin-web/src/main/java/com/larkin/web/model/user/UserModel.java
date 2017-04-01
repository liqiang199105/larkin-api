package com.larkin.web.model.user;

import java.io.Serializable;
import java.util.Date;

public class UserModel implements Serializable {
	private String userId;
	private String nick;
	private String avatar;
	private String mail;
	private String phone;
	private String token;
	private String verifyCode;
	private Date lastModified;

	public UserModel(String userId, String nick, String avatar, String mail, String phone, String verifyCode, Date lastModified) {
		this.userId = userId;
		this.nick = nick;
		this.avatar = avatar;
		this.mail = mail;
		this.phone = phone;
		this.verifyCode = verifyCode;
		this.lastModified = lastModified;
	}

	public UserModel() {
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}


	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
