package com.topjet.fmp.yls.command.user;

public class LoginParameter {

	private Long userID;

	private String realName;

	private Long sex;

	// private String age;

	private String phoneNumber;

	private String mobilePhoneNumber;
	
	private Short allowOtherViewMyCertInfo;

	
//��Ա����
	private String userType;

	//�Ƿ���֤����
	private Short isCert;

   private String idCardPhoto;

	//ע��ʱ��
	private String registerTime;

	private String place; // ��ס��
	
	
	private String placeName;

	//�Ƿ��Ǵ������˺�
	private Integer agentAccount;

	//��������ʾ��Ϣ
	private String comment;

	//��˾����
	private String companyName;

	//��������
	private Long workedYears;

	// ��Ӫ��ַ
	private String busiAddress;

	// "��Ӫ·��
	private String busiLines;

	// �û��������ڵ�
	private String userHouseHold;

	// ��������
	private String transportInfo;
	


	private Integer age;

	//����ID
	private String service;
	
	
	private String allService;
	
	
	private String recommendMobile;
	
	//1��ʾ����
	private Integer isrecommend=0;
	
	
    
    private String   PHOTOURL;

	

	
	public Integer getIsrecommend() {
		return isrecommend;
	}

	public void setIsrecommend(Integer isrecommend) {
		this.isrecommend = isrecommend;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public Short getAllowOtherViewMyCertInfo() {
		return allowOtherViewMyCertInfo;
	}

	public void setAllowOtherViewMyCertInfo(Short allowOtherViewMyCertInfo) {
		this.allowOtherViewMyCertInfo = allowOtherViewMyCertInfo;
	}

	//֤����
	private String certificationID;

	public String getCertificationID() {
		return certificationID;
	}

	public void setCertificationID(String certificationID) {
		this.certificationID = certificationID;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	private TrustInfoParameter trustInfo = new TrustInfoParameter();

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}

	public String getRealName() {
		return realName;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Short getIsCert() {
		return isCert;
	}

	public void setIsCert(Short isCert) {
		this.isCert = isCert;
	}

	public Integer getAgentAccount() {
		return agentAccount;
	}

	public String getRecommendMobile() {
		return recommendMobile;
	}

	public void setRecommendMobile(String recommendMobile) {
		this.recommendMobile = recommendMobile;
	}

	public void setAgentAccount(Integer agentAccount) {
		this.agentAccount = agentAccount;
	}

	public Long getSex() {
		return sex;
	}

	public void setSex(Long sex) {
		this.sex = sex;
	}

	public String getBusiAddress() {
		return busiAddress;
	}

	public String getTransportInfo() {
		return transportInfo;
	}

	public void setTransportInfo(String transportInfo) {
		this.transportInfo = transportInfo;
	}

	public void setBusiAddress(String busiAddress) {
		this.busiAddress = busiAddress;
	}

	public String getBusiLines() {
		return busiLines;
	}

	public void setBusiLines(String busiLines) {
		this.busiLines = busiLines;
	}

	public String getUserHouseHold() {
		return userHouseHold;
	}

	public void setUserHouseHold(String userHouseHold) {
		this.userHouseHold = userHouseHold;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMobilePhoneNumber() {
		return mobilePhoneNumber;
	}

	public void setMobilePhoneNumber(String mobilePhoneNumber) {
		this.mobilePhoneNumber = mobilePhoneNumber;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}

	public String getAllService() {
		return allService;
	}

	public void setAllService(String allService) {
		this.allService = allService;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public TrustInfoParameter getTrustInfo() {
		return trustInfo;
	}

	public void setTrustInfo(TrustInfoParameter trustInfo) {
		this.trustInfo = trustInfo;
	}

	public String getComment() {
		return comment;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Long getWorkedYears() {
		return workedYears;
	}

	public void setWorkedYears(Long workedYears) {
		this.workedYears = workedYears;
	}


	public String getIdCardPhoto() {
		return idCardPhoto;
	}

	public void setIdCardPhoto(String idCardPhoto) {
		this.idCardPhoto = idCardPhoto;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPHOTOURL() {
		return PHOTOURL;
	}

	public void setPHOTOURL(String pHOTOURL) {
		PHOTOURL = pHOTOURL;
	}
	
	
}