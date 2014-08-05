package com.topjet.fmp.yls.util;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;

import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class PhoneLocation {

	private final static Logger logger = LoggerFactory.getLogger(PhoneLocation.class);

	private static final String URL = "http://apis.juhe.cn/mobile/get?key=cf7381ed5aca866a4e75e2528cb58c70&phone=";

	public final static String UNKNOW = "未知号码";

	public final static String UNKNOW_DEFAULT = "ZJ_HZ";

	static Map<String, String> location = new HashMap<String, String>();

	static {
		Properties props = new Properties();
		try {
			props.load(PhoneLocation.class.getResourceAsStream("location.properties"));
			Enumeration e = props.propertyNames();
			String value = null;
			String key = null;
			while (e.hasMoreElements()) {
				key = (String) e.nextElement();
				value = (String) props.getProperty(key);
				location.put(key,value);
			}
		} catch (IOException e1) {
			logger.error(e1.getMessage());
		}

	}

	public static String getLocaleByTel(String tel) {
		if (!StringUtils.hasText(tel)) {
			return UNKNOW;
		}
		if (veriyMobile(tel)) {

			return getMobileLocation(formatMobile(tel));
		} else {
			String result = location.get(tel.substring(0, 4));
			if (!StringUtils.hasText(result))
				result = location.get(tel.substring(0, 3));
			result = StringUtils.hasText(result) ? result : UNKNOW;
			return result;
		}
	}

	public static String getLocaleByArea(String area) {

		return location.get(area);
	}


	private static String getMobileLocation(String mobile) {
		String result = UNKNOW;
		Document doc = null;
		SAXReader reader = null;
		// 编码的时候需要使用服务器的编码

		try {
			HttpClient httpClient = new HttpClient();
			GetMethod method = new GetMethod(URL + mobile);
			int code = httpClient.executeMethod(method);
			String response =method.getResponseBodyAsString();
			if (200 == code) {
			
					
					JSONObject object =JSONObject.fromObject(response);
					System.out.println(object);
					if("200".equals((String)object.get("resultcode"))){
						JSONObject sult=(JSONObject)object.get("result");
						String province =sult.getString("province");
						String city =sult.getString("city");
						return province+" "+city;
						
					}else{
						logger.error("phone place is error,the case  resultcode is:"+(String)object.get("resultcode"));
					}
			
			}else{
			   logger.error(response.toString());
			}

		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}  catch (NullPointerException e) {
			logger.error(e.getMessage());
		} finally {
			doc = null;
			reader = null;
		}
		return result;
	}

	public static boolean veriyMobile(String mobile) {
		if (mobile.indexOf("0") == 0) {
			if (mobile.indexOf("010") == 0)
				return false;
			else
				mobile = mobile.substring(1, mobile.length());
		}
		return mobile.matches("(?is)(^1[3|4|5|8][0-9]\\d{4,8}$)");
	}

	public static String formatMobile(String mobile) {
		if (mobile.indexOf("0") == 0) {
			return mobile.substring(1, mobile.length());
		} else {
			return mobile;
		}
	}

	public static String getKey(String ct_region) {
		for (Map.Entry<String, String> entry : location.entrySet()) {
			String value = entry.getValue();
			if (value.indexOf(ct_region) >= 0) {
				return entry.getKey();
			}

		}

		return null;

	}

	public static void main(String[] args) {
		// System.out.println(getLocaleByTel("18616808097"));
		// // System.out.println(getLocaleByTel("02187876767"));
		// System.out.println(getLocaleByTel("15320349996"));
		 System.out.println(getLocaleByTel("13335716731"));
		//System.out.println(getLocaleByTel("13085286827"));

	}

}
