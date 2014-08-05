package com.topjet.fmp.yls.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.topjet.fmp.user.domain.MemberGps;
import com.topjet.fmp.user.domain.MemberGpsCurrent;




public class LocationMapUtil {
	private static final Logger log = LoggerFactory.getLogger(LocationMapUtil.class);
	
	private static final String MAP_CONVERT_API = "http://api.map.baidu.com/ag/coord/convert";
	
	private static final String MAP_GEOCODER_API = "http://api.map.baidu.com/geocoder";
	
	
	private static final String MAP_CONVERT_API_RESULT_STATE = "0";
	
	private static final String MAP_GEOCODER_API_RESULT_STATE = "OK";
	
	private static final String MAP_GEOCODER_KEY = "";
	
	public static void main(String []args){
//		offset(new BigDecimal("120.123889"), new BigDecimal("30.272778"));
		
		geocoder(new BigDecimal("120.123889"), new BigDecimal("30.272778"));
		
		//MTIwLjEzNTEwNDc1ODY3
		//MzAuMjc2ODI5NTU0ODU5
	}


	public static String getConvertURL(BigDecimal x,BigDecimal y) {
	   
		 StringBuffer  sb = new StringBuffer();
      
		sb.append(MAP_CONVERT_API).append("?from=0&to=4&mode=1&x=");
		sb.append(x).append("&y=").append(y);

		log.debug("CONVERT URL: " + sb.toString());
		return sb.toString();
	}
	
	public static URL getGeocoderURL(BigDecimal x,BigDecimal y) throws MalformedURLException {
		StringBuilder sb = new StringBuilder();
		sb.append(MAP_GEOCODER_API).append("?location=").append(y).append(",").append(x);
		sb.append("&key=").append(MAP_GEOCODER_KEY);
		sb.append("&output=json");

		log.debug("GEOCODER URL: " + sb.toString());
		return new URL(sb.toString());
	}


	public static final String offset(BigDecimal x,BigDecimal y) {
		
		try {
			String jsonText = readContent(new URL(getConvertURL(x,y)));
	
			
			
//			[{"error":0,"x":"MTIwLjEzNTEwNDc1ODY3","y":"MzAuMjc2ODI5NTU0ODU5"}]
			JSONArray  jsonArray = JSONArray.fromObject(jsonText);
			JSONObject json = jsonArray.getJSONObject(0);
			String state = json.getString("error");
			if (MAP_CONVERT_API_RESULT_STATE.equals(state)) {
				String lngString = json.getString("x");
				String latString = json.getString("y");
				if(StringUtils.hasText(lngString)&&StringUtils.hasText(latString)){

				lngString = new String(Base64.decodeBase64(lngString.getBytes()));
				latString = new String(Base64.decodeBase64(latString.getBytes()));
				
				return lngString+","+latString;
				}
				
			}
	
			
				
				
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	
	public static MemberGpsCurrent geocoder(BigDecimal x,BigDecimal y) {
		MemberGpsCurrent geoBAIDU=null;
		
		try {
			String jsonText = readContent(getGeocoderURL(x,y));
			JSONObject jsonObject = JSONObject.fromObject(jsonText);
			String status = jsonObject.getString("status");
			if (MAP_GEOCODER_API_RESULT_STATE.equals(status)) {
				JSONObject result = jsonObject.getJSONObject("result");	
				geoBAIDU=new MemberGpsCurrent();
				geoBAIDU.setLAT(y);
				geoBAIDU.setLNG(x);
				geoBAIDU.setPOSITION(result.getString("formatted_address"));
				geoBAIDU.setCITYCODE(result.getString("cityCode"));
				JSONObject address = result.getJSONObject("addressComponent");
				geoBAIDU.setPROVINCE(address.getString("province"));
				geoBAIDU.setCITY(address.getString("city"));
				geoBAIDU.setDISTRICT(address.getString("district"));				
				geoBAIDU.setSTREET(address.getString("street"));
				geoBAIDU.setSTREETNUMBER(address.getString("street_number"));
				
			}
			
			
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return geoBAIDU;
	}




	private static final int DEFAULT_SECOND = 3 * 1000; 

	public static String readContent(URL u) throws IOException {
		String tmp = null;
		BufferedReader reader = null;
		StringBuilder builder = new StringBuilder();

		HttpURLConnection httpConnect = (HttpURLConnection) u.openConnection();
		
		/* set timeout / Millisecond */
		httpConnect.setReadTimeout(DEFAULT_SECOND);

		/* if response state is not 200, get out exception */
		int state = httpConnect.getResponseCode();
		if (state != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("the response failed!");
		}

		reader = new BufferedReader(new InputStreamReader(httpConnect.getInputStream(), "utf-8"));
		while ((tmp = reader.readLine()) != null) {
			builder.append(tmp);
		}

		if (reader != null)
			reader.close();

		if (httpConnect != null)
			httpConnect.disconnect();

		log.debug(builder.toString());
		return builder.toString();
	}

}
