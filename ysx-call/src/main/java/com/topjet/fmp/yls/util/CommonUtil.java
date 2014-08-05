package com.topjet.fmp.yls.util;


import org.springframework.util.StringUtils;



import com.topjet.fmp.user.domain.YslMember;


public class CommonUtil {
	
	  private static String hexStr =  "0123456789ABCDEF";   

	// 如果是login方法，jsess取默认的，否则取sessionid,jcookie 是客户端传过来的请求头信息
	public static String pass_single(String jcookie, String jsess) {
		
		int count=jcookie.length();
		
		if(count<16){
			while(15-count>=0){
				jcookie="0"+jcookie;
				count++;
			}
		}

		byte[] b =HexStringToBinary(jcookie);
		byte[] c = HexStringToBinary(jsess);
		byte[] d = new byte[b.length];
		for (int i = 0; i < b.length; i++) {
			d[i] = (byte) (b[i] ^ c[i]);

		}
		return  BinaryToHexString(d);
		// return new String(d);

		// String loginProf = null;
		//
		// StringBuffer sb = new StringBuffer();
		// for (int i = 0; i < jcookie.length(); i++) {
		// sb.append((char) (jcookie.charAt(i) ^ jsess.charAt(i)));
		//
		// }
		// loginProf = sb.toString();
		//
		// return loginProf;

	}

	public static void main(String[] args) {
		System.out.println(pass_single( "E4CDB76DF4A2089","9E3472FD30293C2E"));

//		System.out.println(pass_single("1520F6FCDB4D82C0", "E3472FD30293C2EC"));
//		//
//		// System.out.println(pass_single("51DA084EDFF4167D",
//		// pass_single("1520F6FCDB4D82C0", "E3472FD30293C2EC")));
//		//
//		// byte[] d = "1520F6FCDB4D82C0".getBytes();
//		//
//		// String str = new String(d);
//		// System.out.println(str);
//
//		String jcookie = "1520F6FCDB4D82C0";
//		String jsess = "E3472FD30293C2EC";
//		byte[] b = jcookie.getBytes();
//		byte[] c = jsess.getBytes();
//		byte[] d = new byte[b.length];
//		for (int i = 0; i < b.length; i++) {
//			d[i] = (byte) (b[i] ^ c[i]);
//
//		}
//
//		System.out.println(Bytes2HexString(d));

		// String jcookie = "1520F6FCDB4D82C0";
		// char[] cha = jcookie.toCharArray();
		// System.out.println(Integer.toHexString(cha[2]));
		
//		
//		 String str = "1520F6FCDB4D82C0";   
//		 String jsess = "E3472FD30293C2EC";        
//		 
//			byte[] b = HexStringToBinary(str);
//			byte[] c = HexStringToBinary(jsess);
//			byte[] d = new byte[b.length];
//			for (int i = 0; i < b.length; i++) {
//			d[i] = (byte) (b[i] ^ c[i]);
//
//		}
//
//		      String hexString = BinaryToHexString(d);   
//		         System.out.println("str转换为十六进制：\n"+hexString);  
		         
		
//	
////		  0000 0011 0110 0001
////		  1111 1100 1001 1110
//		Integer id=865;
//		
//		//sssss
//		Integer xx=~id;
////		
////		
////		Interger xx_16=id.valueOf(xx, 4);
//		String x =Integer.toHexString(xx);
//		while(x.startsWith("f")||x.startsWith("F")){
//			x=x.substring(1);
//		}
//		//xx^0xffff
//	    	
//		System.out.println(x);
//		Long zz =Long.parseLong(x, 16);
//		System.out.println(x+";;"+zz);
////	
////	
////		
////		
//		CRC32 crc32 = new CRC32();
//		crc32.update("5840BF3C6C419C643499444CECFA70C6".getBytes());
//		
//		//sesssonid
//		Long sessionid =crc32.getValue();
//		String x2 =Long.toHexString(sessionid);
//		Long zz2 =Long.parseLong(x2, 16);
//		System.out.println(x2+";;;;"+zz2);
//
//		System.out.println(Long.toHexString(zz+zz2));
//		
		
//		String ss = Integer.toBinaryString(865);
//		  System.out.println("str转换为十六进制：\n"+ss);  
//		StringBuffer  sbf = new StringBuffer();
//			  while(ss.length()<16){
//				  ss="0"+ss;
//			  }
//			  System.out.println(ss);  
//			 for(int i=0;i<ss.length();i++){
//				
//			 }
//			 System.out.println("str取反：\n"+sbf.toString());  
//			  String hexString = BinaryToHexString(c);
//			  System.out.println("str转换为十六进制：\n"+hexString);    
//	

//		String ss ="865";
	    
	
//		  String hexString = BinaryToHexString(c);   
			
		
//	
//		 String hexString = BinaryToHexString(b);   
//		    System.out.println("str转换为十六进制：\n"+hexString);        
	}

	public static String Bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}
	
	
	public static String  getMobiles(YslMember member){
		
		StringBuffer str = new StringBuffer();
		
		String mobile1 = member.getMOBILE1();
		if(StringUtils.hasText(mobile1)){
			str.append(mobile1);
		}
//		String mobile2 = member.getMOBILE2();
//		if(StringUtils.hasText(mobile2)){
//			if(StringUtils.hasText(str)){
//				str.append(Constants.STRING_SPLIT);
//			}
//			str.append(mobile2);
//		}
//		
//		String mobile3 =member.getMOBILE3();
//		if(StringUtils.hasText(mobile3)){
//			if(StringUtils.hasText(str)){
//				str.append(Constants.STRING_SPLIT);
//			}
//			str.append(mobile3);
//		}
//		String mobile4 = member.getMOBILE4();
//		
//		if(StringUtils.hasText(mobile4)){
//			if(StringUtils.hasText(str)){
//				str.append(Constants.STRING_SPLIT);
//			}
//			str.append(mobile4);
//		}
		return str.toString();
	}
	
	
	public static String getTels(YslMember member){
		
		StringBuffer str = new StringBuffer();
		
		String tel1 = member.getTEL1();
		if(StringUtils.hasText(tel1)){
			str.append(tel1);
		}
		
	

		if(!StringUtils.hasText(tel1)&&StringUtils.hasText(member.getTEL2())){
			str.append(member.getTEL2());
		}
//		
	
		return str.toString();
	}
	
	

	
	
	//将十六进制转换为字节数组 
	  public static byte[] HexStringToBinary(String hexString){   
		        //hexString的长度对2取整，作为bytes的长度   
		         int len = hexString.length()/2;   
		       byte[] bytes = new byte[len];   
	       byte high = 0;//字节高四位   
		       byte low = 0;//字节低四位   
		
		       for(int i=0;i<len;i++){   
		            //右移四位得到高位   
		           high = (byte)((hexStr.indexOf(hexString.charAt(2*i)))<<4);   
	            low = (byte)hexStr.indexOf(hexString.charAt(2*i+1));   
		              bytes[i] = (byte) (high|low);//高地位做或运算   
		        }   
		       return bytes;   
	   }   


	  /**  
	       *   
	       * @param bytes  
	       * @return 将二进制转换为十六进制字符输出  
	       */  
	    public static String BinaryToHexString(byte[] bytes){   
	             
	          String result = "";   
	          String hex = "";   
	          for(int i=0;i<bytes.length;i++){   
	             //字节高4位   
	             hex = String.valueOf(hexStr.charAt((bytes[i]&0xF0)>>4));   
	             //字节低4位   
	             hex += String.valueOf(hexStr.charAt(bytes[i]&0x0F));   
	             result +=hex;   
	         }   
	         return result;   
	    }   


}
