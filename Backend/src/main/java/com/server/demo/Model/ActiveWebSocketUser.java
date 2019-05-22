package com.server.demo.Model;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ActiveWebSocketUser {
	@Id
	@GeneratedValue
	private Integer id;

	private String username;

	private String sessionId;

	private Calendar connectionTime;

	public ActiveWebSocketUser() {
	}

	public ActiveWebSocketUser(String username, Calendar connectionTime, String sessionID) {
		this.username = username;
		this.connectionTime = connectionTime;
		this.sessionId = sessionID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Calendar getConnectionTime() {
		return connectionTime;
	}

	public void setConnectionTime(Calendar connectionTime) {
		this.connectionTime = connectionTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}