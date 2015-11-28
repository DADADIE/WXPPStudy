package com.supersoft.comm.utils;

public class AccessToken {

	private String accessToken;
	private long expireTime;

	public AccessToken(String accessToken, long expireTime) {
		this.accessToken = accessToken;
		this.expireTime = expireTime;
	}

	public AccessToken() {
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public boolean isExpired() {
		if (accessToken == null) {
			return true;
		}
		long currentTime = System.currentTimeMillis();
		return currentTime > expireTime;
	}
}
