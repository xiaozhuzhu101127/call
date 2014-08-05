package com.topjet.fmp.yls.command.user;

public class TrustInfoParameter {

	private Long activityNumber = 0L;

	private Long credit = 0L;

	private Long complaintOtherPeopleNumber = 0L;

	private Long complaintNumber;

	private Integer localTrustLevel = 0;

	private String trustPromptInfo;

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



	public Integer getLocalTrustLevel() {
		return localTrustLevel;
	}

	public void setLocalTrustLevel(Integer localTrustLevel) {
		this.localTrustLevel = localTrustLevel;
	}

	public String getTrustPromptInfo() {
		return trustPromptInfo;
	}

	public void setTrustPromptInfo(String trustPromptInfo) {
		this.trustPromptInfo = trustPromptInfo;
	}

	public Long getComplaintOtherPeopleNumber() {
		return complaintOtherPeopleNumber;
	}

	public void setComplaintOtherPeopleNumber(Long complaintOtherPeopleNumber) {
		this.complaintOtherPeopleNumber = complaintOtherPeopleNumber;
	}

	public Long getComplaintNumber() {
		return complaintNumber;
	}

	public void setComplaintNumber(Long complaintNumber) {
		this.complaintNumber = complaintNumber;
	}



}
