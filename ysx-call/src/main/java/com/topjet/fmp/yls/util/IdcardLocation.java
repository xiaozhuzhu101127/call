package com.topjet.fmp.yls.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdcardLocation {

	private final static Logger logger = LoggerFactory.getLogger(IdcardLocation.class);

	private static final String URL = "http://www.youdao.com/smartresult-xml/search.s?type=mobile&q=";

	public final static String UNKNOW = "δ֪����";

	public final static String UNKNOW_DEFAULT = "ZJ_HZ";

	static Map<String, String> location = new HashMap<String, String>();

	static {
		Properties props = new Properties();
		try {
			props.load(PhoneLocation.class.getClassLoader().getResourceAsStream("idcardlocation.properties"));
			Enumeration e = props.propertyNames();
			String value = null;
			String key = null;
			while (e.hasMoreElements()) {
				key = (String) e.nextElement();
				value = (String) props.getProperty(key).trim();
				location.put(key, value);
			}
		} catch (IOException e1) {
			logger.error(e1.getMessage());
		}

	}

	public static String getLocation(String card) {

		StringBuffer sbf = new StringBuffer();
		String provice = location.get(card.substring(0, 2) + "0000");
		sbf.append(provice + " ");
		String city = location.get(card.substring(0, 4) + "00");
		sbf.append(city + " ");
		String town = location.get(card);
		sbf.append(town);

		return sbf.toString();

	}

}
