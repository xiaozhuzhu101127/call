package com.topjet.fmp.yls.command.parameter;

import java.math.BigDecimal;

public class CurrentGPS {
	//设备号
    private String MOBILE;

  //经度
    private BigDecimal LNG;

  //维度
    private BigDecimal LAT;
   
   //时间yyyy-MM-dd hh:mm:ss
    private String POSTIME;
    
    private String POSITION;
    
    private String realName;
    
    //诚信值
    private Long yslAccount;
    
    //历史成交
    private int historyCount;
    
    //承运好品读
    private Long CARRYPOINT;
    
    //评价次数
    private Long BCARRYCOUNT;
    
    private String companyName;
    
    private String mobile;
    

    private String   PHOTOURL;

	public String getMOBILE() {
		return MOBILE;
	}

	public void setMOBILE(String mOBILE) {
		MOBILE = mOBILE;
	}

	public BigDecimal getLNG() {
		return LNG;
	}

	public void setLNG(BigDecimal lNG) {
		LNG = lNG;
	}

	public BigDecimal getLAT() {
		return LAT;
	}

	public void setLAT(BigDecimal lAT) {
		LAT = lAT;
	}

	public String getPOSTIME() {
		return POSTIME;
	}

	public void setPOSTIME(String pOSTIME) {
		POSTIME = pOSTIME;
	}

	public String getPOSITION() {
		return POSITION;
	}

	public void setPOSITION(String pOSITION) {
		POSITION = pOSITION;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Long getYslAccount() {
		return yslAccount;
	}

	public void setYslAccount(Long yslAccount) {
		this.yslAccount = yslAccount;
	}

	public int getHistoryCount() {
		return historyCount;
	}

	public void setHistoryCount(int historyCount) {
		this.historyCount = historyCount;
	}


	public Long getCARRYPOINT() {
		return CARRYPOINT;
	}

	public void setCARRYPOINT(Long cARRYPOINT) {
		CARRYPOINT = cARRYPOINT;
	}

	

	public Long getBCARRYCOUNT() {
		return BCARRYCOUNT;
	}

	public void setBCARRYCOUNT(Long bCARRYCOUNT) {
		BCARRYCOUNT = bCARRYCOUNT;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPHOTOURL() {
		return PHOTOURL;
	}

	public void setPHOTOURL(String pHOTOURL) {
		PHOTOURL = pHOTOURL;
	}
  


}
