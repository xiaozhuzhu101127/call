package com.topjet.fmp.yls.util;



import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;


public class PhoneAreaUtil {

	protected static String K780 = "http://api.k780.com/?app=phone.get&phone=#mobile#&appkey=10019&sign=9b1d7e05f95dba18ca2fb65adfa014e1&format=json";

	// longju longju
	public static final String BAIFUBAO = "https://www.baifubao.com/callback?cmd=1059&callback=phone&phone=15850781443";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getAreaLocation("13075075222");
	}

	public static String getAreaLocation(String mobile) {

		mobile = K780.replace("#mobile#", mobile);
		String area = null;
		try {
			HttpClient httpClient = new HttpClient();
			GetMethod method = new GetMethod(mobile);

			httpClient.executeMethod(method);
			String result = method.getResponseBodyAsString().trim();
			JSONObject object = JSONObject.fromObject(result);
			JSONObject reusltObject = object.getJSONObject("result"); 
            area=reusltObject.getString("area");			
		} catch (Throwable e) {
			System.out.println(e.getMessage());
		}

		return area;

	}

}
