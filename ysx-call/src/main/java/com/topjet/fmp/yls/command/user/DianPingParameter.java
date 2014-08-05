package com.topjet.fmp.yls.command.user;

import java.util.Date;

public class DianPingParameter {
	
	
	//名字
	private String REALNAME;
	
	private String DCT_UA;
	
	//公司名
	private String COMPANYNAME;
	
	//承运方综合分
		private Long CARRYPOINT;
		
		//发货方综合分
		private Long SHIPPOINT;
	
	//运商行提示信息
	private String comment ; 
	
	//手机号码
	private String MOBILE1  ;
	
	//固话号码
	private String TEL1;
	
	//当地排名
	private Integer localTrustLevel  ;
	
	//会员类型
	private String DCT_UT ;
	
	//会员级别
	private String serviceName ;
	
	//注册时间
	private Date CREATE_TIME;
	
	// 经营路线
	private String BUSILINES;
	
	// 经营地址
	private String BUSIADDRESS;
	
	//  用户ID	
	private Long USRID;
	
	// 指数
	private Long YSLACCOUNT;
	
	//cityName  城市名称
	private String cityName;
	
	// base64照片
	private String idCardPhoto;
	
	// 照片ID
	private Long PHOTOID;
	
	//photoAddr
	private String photoAddr;
	
	//承运方被点评次数
	private Long BCARRYCOUNT;
	
	//发货方被点评次数
	private Long BSHIPCOUNT;
	
	
	private String DCT_DPUC;
	
	private String PLACE;
	
	
	
	
	


	public String getPLACE() {
		return PLACE;
	}

	public void setPLACE(String pLACE) {
		PLACE = pLACE;
	}

	public String getREALNAME() {
		return REALNAME;
	}

	public String getDCT_UA() {
		return DCT_UA;
	}

	public void setDCT_UA(String dCT_UA) {
		DCT_UA = dCT_UA;
	}

	public void setREALNAME(String rEALNAME) {
		REALNAME = rEALNAME;
	}

	public String getCOMPANYNAME() {
		return COMPANYNAME;
	}

	public void setCOMPANYNAME(String cOMPANYNAME) {
		COMPANYNAME = cOMPANYNAME;
	}



	
	public String getDCT_DPUC() {
		return DCT_DPUC;
	}

	public void setDCT_DPUC(String dCT_DPUC) {
		DCT_DPUC = dCT_DPUC;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getMOBILE1() {
		return MOBILE1;
	}

	public void setMOBILE1(String mOBILE1) {
		MOBILE1 = mOBILE1;
	}

	public String getTEL1() {
		return TEL1;
	}

	public void setTEL1(String tEL1) {
		TEL1 = tEL1;
	}

	

	public Long getCARRYPOINT() {
		return CARRYPOINT;
	}

	public void setCARRYPOINT(Long cARRYPOINT) {
		CARRYPOINT = cARRYPOINT;
	}

	public Long getSHIPPOINT() {
		return SHIPPOINT;
	}

	public void setSHIPPOINT(Long sHIPPOINT) {
		SHIPPOINT = sHIPPOINT;
	}

	public Integer getLocalTrustLevel() {
		return localTrustLevel;
	}

	public void setLocalTrustLevel(Integer localTrustLevel) {
		this.localTrustLevel = localTrustLevel;
	}

	public String getDCT_UT() {
		return DCT_UT;
	}

	public void setDCT_UT(String dCT_UT) {
		DCT_UT = dCT_UT;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Date getCREATE_TIME() {
		return CREATE_TIME;
	}

	public void setCREATE_TIME(Date cREATE_TIME) {
		CREATE_TIME = cREATE_TIME;
	}

	public String getBUSILINES() {
		return BUSILINES;
	}

	public void setBUSILINES(String bUSILINES) {
		BUSILINES = bUSILINES;
	}

	public String getBUSIADDRESS() {
		return BUSIADDRESS;
	}

	public void setBUSIADDRESS(String bUSIADDRESS) {
		BUSIADDRESS = bUSIADDRESS;
	}

	public Long getUSRID() {
		return USRID;
	}

	public void setUSRID(Long uSRID) {
		USRID = uSRID;
	}

	public Long getYSLACCOUNT() {
		return YSLACCOUNT;
	}

	public void setYSLACCOUNT(Long ySLACCOUNT) {
		YSLACCOUNT = ySLACCOUNT;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getIdCardPhoto() {
		return idCardPhoto;
	}

	public void setIdCardPhoto(String idCardPhoto) {
		this.idCardPhoto = idCardPhoto;
	}

	public Long getPHOTOID() {
		return PHOTOID;
	}

	public void setPHOTOID(Long pHOTOID) {
		PHOTOID = pHOTOID;
	}

	public String getPhotoAddr() {
		return photoAddr;
	}

	public void setPhotoAddr(String photoAddr) {
		this.photoAddr = photoAddr;
	}

	public Long getBCARRYCOUNT() {
		return BCARRYCOUNT;
	}

	public void setBCARRYCOUNT(Long bCARRYCOUNT) {
		BCARRYCOUNT = bCARRYCOUNT;
	}

	public Long getBSHIPCOUNT() {
		return BSHIPCOUNT;
	}

	public void setBSHIPCOUNT(Long bSHIPCOUNT) {
		BSHIPCOUNT = bSHIPCOUNT;
	}
	
	
	
	
	
	
	

}
