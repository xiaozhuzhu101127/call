package com.topjet.fmp.yls.command.user;

public class Carrier {
	
	 private Long  USRID;//:“1234560”,

     private String CARRIERNAME   ;

     private String  CARRIERCOMPANY;

    private String  CARRIERADDR;

    private String  CARRIERTEL ;

    private String   CARRIERMOBILE;

    private String   PHOTOURL;

    private int  HISTORYSUBMIT;  //8,//历史成交的货源数

    private Long  CARRYPOINT;//4;//承运方好评度

   private Long   CARRYCOUNT;//:523//作为承运方被评价次数  

public Long getUSRID() {
	return USRID;
}

public void setUSRID(Long uSRID) {
	USRID = uSRID;
}

public String getCARRIERNAME() {
	return CARRIERNAME;
}

public void setCARRIERNAME(String cARRIERNAME) {
	CARRIERNAME = cARRIERNAME;
}

public String getCARRIERCOMPANY() {
	return CARRIERCOMPANY;
}

public void setCARRIERCOMPANY(String cARRIERCOMPANY) {
	CARRIERCOMPANY = cARRIERCOMPANY;
}

public String getCARRIERADDR() {
	return CARRIERADDR;
}

public void setCARRIERADDR(String cARRIERADDR) {
	CARRIERADDR = cARRIERADDR;
}

public String getCARRIERTEL() {
	return CARRIERTEL;
}

public void setCARRIERTEL(String cARRIERTEL) {
	CARRIERTEL = cARRIERTEL;
}

public String getCARRIERMOBILE() {
	return CARRIERMOBILE;
}

public void setCARRIERMOBILE(String cARRIERMOBILE) {
	CARRIERMOBILE = cARRIERMOBILE;
}

public String getPHOTOURL() {
	return PHOTOURL;
}

public void setPHOTOURL(String pHOTOURL) {
	PHOTOURL = pHOTOURL;
}

public int getHISTORYSUBMIT() {
	return HISTORYSUBMIT;
}

public void setHISTORYSUBMIT(int hISTORYSUBMIT) {
	HISTORYSUBMIT = hISTORYSUBMIT;
}

public Long getCARRYPOINT() {
	return CARRYPOINT;
}

public void setCARRYPOINT(Long cARRYPOINT) {
	CARRYPOINT = cARRYPOINT;
}

public Long getCARRYCOUNT() {
	return CARRYCOUNT;
}

public void setCARRYCOUNT(Long cARRYCOUNT) {
	CARRYCOUNT = cARRYCOUNT;
}
   
   

}
