package com.topjet.fmp.yls.util;

import java.util.Calendar;

public class IDCardUtil {

	public static int getAgeByIdCard(String idCard) {
		int iAge = 0;
		// λ�����֤�Ȳ���15
		// if (idCard.length() == CHINA_ID_MIN_LENGTH) {
		// idCard = conver15CardTo18(idCard);
		// }
		String year = idCard.substring(6, 10);
		Calendar cal = Calendar.getInstance();
		int iCurrYear = cal.get(Calendar.YEAR);
		iAge = iCurrYear - Integer.valueOf(year);
		return iAge;
	}

	// ��ȡ�Ա�1�У�0Ů
	public static int getSexByIdCard(String idCard) {
		String id17 = idCard.substring(16, 17);
		if (Integer.parseInt(id17) % 2 != 0) {
			return 1;
		} else {
			return 0;
		}
	}

}
