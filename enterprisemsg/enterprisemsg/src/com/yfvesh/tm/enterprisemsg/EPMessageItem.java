package com.yfvesh.tm.enterprisemsg;

import java.io.Serializable;
import java.util.Date;

public class EPMessageItem implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private int type;
	private int protocol;
	private String from;
	private String body;
	private Date time;
	private int read = 0; // 0 UNREAD 1 READ

	public EPMessageItem() {
	}

	public EPMessageItem(String from, String body, Date time, boolean bread) {
		this.from = from;
		this.body = body;
		this.time = time;
		if (bread) {
			this.read = 1;
		} else {
			read = 0;
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public String getPhone() {
		return from;
	}

	public void setPhone(String phone) {
		this.from = phone;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getTime() {
		return this.time;
	}

	public void setTime(Date date) {
		this.time = date;
	}

	public boolean getUsrRead() {
		return (this.read == 0) ? false : true;
	}

	public int getUsrReadInt() {
		return this.read;
	}

	public void setUsrRead(boolean bread) {
		if (bread) {
			this.read = 1;
		} else {
			this.read = 0;
		}
	}

	public void setUsrRead(int read) {
		this.read = read;
	}

	public String toString() {
		return "yfve enterprise message: from --" + from + "\n content--"
				+ body;
	}
}
