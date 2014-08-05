package com.topjet.fmp.yls.command.user;

public class CertHistoryInfo {
	
//    "HistoryID":2342,//历史记录的ID
//    "RealName":"姓名",
//    "PhoneNumber":"05712732983",//固话 ,若无值就返回空字符串 
//    "MobilePhoneNumber":"13312341234"//手机 ,若无值就返回空字符串
//    "IDCardNumber":"身份证号码",
//    "CertTime":"进行身份证验证的时间(服务器上的标准北京时间)",//格式为"1982-8-8 21:20:45"
	
	private  Long historyID;
	
	private String realName;
	
	private String iDCardNumber;
	
	private String certTime;
	
	private String mobilePhoneNumber="";
	
	private String phoneNumber="";

	public Long getHistoryID() {
		return historyID;
	}

	public void setHistoryID(Long historyID) {
		this.historyID = historyID;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getiDCardNumber() {
		return iDCardNumber;
	}

	public void setiDCardNumber(String iDCardNumber) {
		this.iDCardNumber = iDCardNumber;
	}

	public String getCertTime() {
		return certTime;
	}

	public void setCertTime(String certTime) {
		this.certTime = certTime;
		
	}

	public String getMobilePhoneNumber() {
		return mobilePhoneNumber;
	}

	public void setMobilePhoneNumber(String mobilePhoneNumber) {
		this.mobilePhoneNumber = mobilePhoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	

	

}
