package com.topjet.fmp.yls.util;

import java.util.Calendar;

public class IDCardUtil {

	public static int getAgeByIdCard(String idCard) {
		int iAge = 0;
		// 位的身份证先不管15
		// if (idCard.length() == CHINA_ID_MIN_LENGTH) {
		// idCard = conver15CardTo18(idCard);
		// }
		String year = idCard.substring(6, 10);
		Calendar cal = Calendar.getInstance();
		int iCurrYear = cal.get(Calendar.YEAR);
		iAge = iCurrYear - Integer.valueOf(year);
		return iAge;
	}

	// 获取性别1男，0女
	public static int getSexByIdCard(String idCard) {
		String id17 = idCard.substring(16, 17);
		if (Integer.parseInt(id17) % 2 != 0) {
			return 1;
		} else {
			return 0;
		}
	}

}
