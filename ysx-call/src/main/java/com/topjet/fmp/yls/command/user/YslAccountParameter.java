package com.topjet.fmp.yls.command.user;

public class YslAccountParameter {
	
	
	private String AccountRemainStr;

	private float AccountRemain;

	private Long activityNumber;

	private Long credit;

	public float getAccountRemain() {
		return AccountRemain;
	}

	public void setAccountRemain(float accountRemain) {
		AccountRemain = accountRemain;
	}

	public Long getActivityNumber() {
		return activityNumber;
	}

	public void setActivityNumber(Long activityNumber) {
		this.activityNumber = activityNumber;
	}

	public Long getCredit() {
		return credit;
	}

	public void setCredit(Long credit) {
		this.credit = credit;
	}

	public String getAccountRemainStr() {
		return AccountRemainStr;
	}

	public void setAccountRemainStr(String accountRemainStr) {
		AccountRemainStr = accountRemainStr;
	}

}
