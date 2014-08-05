package com.topjet.fmp.yls;


import java.io.File;
import java.io.IOException;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


import com.topjet.fmp.aa.Credential;
import com.topjet.fmp.base.DC;
import com.topjet.fmp.exception.AAException;
import com.topjet.fmp.exception.FMPException;
import com.topjet.fmp.svc.domain.SVCCriteria;
import com.topjet.fmp.svc.domain.ServiceOrder;
import com.topjet.fmp.svc.service.SVCOrderService;
import com.topjet.fmp.user.domain.SystemOperate;
import com.topjet.fmp.user.domain.YslSystemOperateCriteria;

import com.topjet.fmp.user.domain.YslMember;



import com.topjet.fmp.user.service.YslMemberService;
import com.topjet.fmp.user.service.YslOperateService;
import com.topjet.fmp.user.service.YslSystemOperateService;
import com.topjet.fmp.util.AjaxResponse;
import com.topjet.fmp.util.AppContext;
import com.topjet.fmp.util.Constants;
import com.topjet.fmp.util.WebUtil;
import com.topjet.fmp.util.Base64.InputStream;
import com.topjet.fmp.yls.command.Command;
import com.topjet.fmp.yls.util.CommonUtil;

/**
 * Servlet implementation class RemoteServlet
 */
public class RemoteServlet extends GenericServlet {

	private static final long serialVersionUID = -771048352365805530L;

	private Logger log = LoggerFactory.getLogger(RemoteServlet.class);

	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
//       System.out.println(req.getCharacterEncoding());
		res.setContentType("application/json;charset=UTF-8");
		res.setCharacterEncoding("UTF-8");


//		
//		java.io.InputStream  stream =req.getInputStream();
//		BufferedReader  reader = new BufferedReader(new InputStreamReader(stream));
//		String out =null;
//		while((out=reader.readLine())!=null){
//			System.out.println(out);
//		}
		
//		Map  re=req.getParameterMap();
//		Iterator it =re.entrySet().iterator();
//	
//		   while(it.hasNext()){ 
//			    Map.Entry m=(Map.Entry)it.next(); 
//			   System.out.println("name-" + m.getKey() + ":" + m.getValue()); 
//			   } 
//
//        
		
		// get parameter
	    String jessionid=req.getRequestedSessionId();
		String parameter = req.getParameter("p");
		log.warn("the jessionid is="+jessionid);

		RemoteResponse remoteResponse = new RemoteResponse();

		Map<String, Object> arguments = null;
		Map<String, Object> command_params = null;

		try {
			arguments = getArgumentsMap(parameter);
			command_params = getArgumentsMap(arguments.get("params"));

			// TODO:XXX 需要判断是否有SQL注入攻击,如果有则直接抛出异常

			// TODO: 放置IP地址,继续待测试,本地测试的时候需要进行注意
			command_params.put(Command.P_IPADDR, WebUtil.getRemoteIP((HttpServletRequest) request));
			command_params.put(Command.P_HTTPREQUEST, req);
		} catch (Exception e) {
			log.warn("Error getting command arguments: p={}", parameter);
			log.warn(e.getMessage(), e);
			remoteResponse.setSucc(0);
			remoteResponse.setMsg("参数错误");

			// return response
			res.getWriter().print(remoteResponse.toJSON());// XXX 配置GZIP,减少传输

			res.getWriter().flush();

			return;
		}

		// get and execute command
		String clazz = (String) arguments.get("className");

		// get and execute method
		String cmd = (String) arguments.get("cmd");

		if (null == clazz || cmd == null) {
			log.warn("Error getting command arguments: p={}", parameter);
			remoteResponse.setSucc(0);
			remoteResponse.setMsg("参数错误");

			// return response
			res.getWriter().print(remoteResponse.toJSON());// XXX 配置GZIP,减少传输
			res.getWriter().flush();
			return;
		}
		
	
		
		 

		// XXX 因为remote url都是一样的，用当前的AuthFilter做不到公共资源和私有资源的判别，
		// 以后要实现app自己的AuthManager，暂时hardcode
		Credential credential = null;
		if (!public_commands.contains(clazz + "_" + cmd)) {

			boolean isValid = validate(req, res);
//			   log.error("the method is"+cmd+",and the ip is "+WebUtil.getRemoteIP((HttpServletRequest) request));

			if (!isValid) {

				gotoLogon(req, res);

				return;
			}
			credential= (Credential) req.getSession().getAttribute(Constants.KEY_USR_CREDENTIAL);
		}

	
		
		if("login".equals(cmd)&&MobileManager.isMobileReq(req)){
			jessionid=null;
		}
		
		
	     //不需要登录的不绑，还有手机登录的不绑定
		 if (!public_commands.contains(clazz + "_" + cmd)&&!MobileManager.isMobileReq(req)) {
			 // 这里引入限制，只能在注册的那台电脑上展示。,login在具体的方法里边做
			 String jcookie = req.getHeader("jcookieid");
			
//			 String jcookie = "F72A9D7E18E7A3BC";
			
			 HttpSession session = req.getSession();
			 String jsess = credential.getSESSIONID().substring(10, 26);
//			 Long usrid = Long.valueOf((Integer) command_params.get("UserID"));
			 String loginProf = CommonUtil.pass_single(jcookie, jsess);
			
	
			 
			 YslMemberService  yslMemberService=(YslMemberService) AppContext.getBean("yslMemberServiceImpl");
					
			 YslMember member = yslMemberService.getYslMemberById(credential.getUSRID());
			
			 List<String> listRights =Arrays.asList(member.getYSLLOGIN().split(Constants.STRING_SPLIT));			
			 if(!listRights.contains(loginProf)){
				 remoteResponse.setSucc(0);
				 remoteResponse.setMsg("_FMP_CLIENT_MACHINE_CHANGED");
				 res.getWriter().print(remoteResponse.toJSON());
				 res.getWriter().flush();
				 return;
			 }
			
	   }
		
		//
		 if (!public_commands.contains(clazz + "_" + cmd))  {
			// 这里还需要做一次处理，不允许操作太频繁。

			// 这里有个zhu
			 YslSystemOperateService yslOperateService = (YslSystemOperateService) AppContext.getBean("yslSystemOperateService");
			String SVCCODE = null;

			if (DC.UC_MBR.equals(credential.getDCT_UC())) {

				SVCOrderService svcOrderService = (SVCOrderService) AppContext.getBean("SVCOrderServiceImpl");
				// 取服务。
				SVCCriteria svcCriteria = new SVCCriteria();
				svcCriteria.setUSRID(credential.getUSRID());
				svcCriteria.setSVCCODE(Constants.YSX);

				ServiceOrder service = svcOrderService.selectValidServiceByCode(credential, svcCriteria);

				
				List<ServiceOrder> list = svcOrderService.selectAllValidServiceByCode(credential,
						svcCriteria);
				if ((list == null||list.size()<=0)&&!MobileManager.isMobileReq(req)) {
					remoteResponse.setSucc(0);
					remoteResponse.setMsg("您没有订购服务，不能访问！");
					res.getWriter().print(remoteResponse.toJSON());
					res.getWriter().flush();
					return;

				}
				boolean isnotLogin =true;
				for(int i=0;i<list.size();i++){
					//可登录的服务，即通过break;
					if ("YSXZS".equals(list.get(i).getSVCCODE())||Constants.YSX_SY.equals(list.get(i).getSVCCODE())||Constants.YSX_YP.equals(list.get(i).getSVCCODE())) {
						service=list.get(i);
						isnotLogin=false;
						break;
					}		

				}
				if(isnotLogin&&!MobileManager.isMobileReq(req)){				
					remoteResponse.setSucc(0);
					remoteResponse.setMsg("您订购的服务无法登录运商行客户端软件！");
					res.getWriter().print(remoteResponse.toJSON());
					res.getWriter().flush();
					return ;
				}
				if(service!=null){
				  SVCCODE = service.getSVCCODE();
				}
			}

			//传入第二个参数，是历史遗留结果，看后期需求，可去掉
//			YslRule yslRule = yslOperateService.getYslRule( cmd, Constants.TRUE);
//			if (yslRule != null) {
				// 该用户今天此方法的访问次数，
				YslSystemOperateCriteria criteria = new YslSystemOperateCriteria();
				criteria.setCmd(cmd);
				criteria.setUsrid(credential.getUSRID());
				int methodCount=0;
				try{
					methodCount = yslOperateService.countYslOperate(criteria);
				}catch(FMPException e){
					remoteResponse.setSucc(0);
					remoteResponse
							.setMsg(e.getMessage());
					res.getWriter().print(remoteResponse.toJSON());
					res.getWriter().flush();
					return ;
					
				}
//				if (methodCount >= yslRule.getOPERATE_COUNT()) {
//					remoteResponse.setSucc(0);
//					remoteResponse
//							.setMsg("十分抱歉！由于您使用次数大大超过正常值，为保证其它用户的正常使用，本功能暂停服务，明天即可恢复使用！如确实因业务需要，请致电我们的客服：4008656315。不便之处，敬请谅解！");
//					res.getWriter().print(remoteResponse.toJSON());
//					res.getWriter().flush();
//					return ;
//				}

			
//			}
			
			SystemOperate oper = new SystemOperate();
			if(MobileManager.isMobileReq(req)){
				oper.setDCT_SYSTEM(DC.ACCAT_MOBILE_YSX);
			}else{
				oper.setDCT_SYSTEM(DC.ACCAT_PC_YSX);
			}
			oper.setUSERMETHOD(cmd);
			oper.setUSRID(credential.getUSRID());
			oper.setLOGINNAME(credential.getUSRNAME());
			oper.setIPADDR((String) command_params.get(Command.P_IPADDR));
			yslOperateService.create(oper);

		}
		

		// 验证结束

		try {
			Command command = getCommand(clazz);
			// remoteResponse = command.execute(credential, method,
			// command_params);
			Method mod = command.getClass().getMethod(cmd, Credential.class, Map.class);
			remoteResponse = (RemoteResponse) mod.invoke(command, credential, command_params);

		} catch (Exception e) {
			e.printStackTrace();
			log.warn("Error executing command: p={}, credential={}", parameter, credential);
			log.warn(e.getMessage(), e);
			remoteResponse.setSucc(0);
			remoteResponse.setMsg(e.getMessage());
		}

		// TODO:XXX return response,这个信息需要进行进一步的加密,口子暂时留在这里
		if(StringUtils.hasText(jessionid)){
			remoteResponse.setId(jessionid);
		}
		
//		log.error("the login jsessionid is:"+remoteResponse.getId()+",receive jessionid is:"+jessionid+"and mobile is:"+(String)command_params.get("username"));

		res.getWriter().print(remoteResponse.toJSON());// XXX 配置GZIP,减少传输

		res.getWriter().flush();
	}



	private Command getCommand(String cmd) {
		return (Command) AppContext.getBean(cmd + "_command");
	}

	@SuppressWarnings({ "unchecked" })
	private Map<String, Object> getArgumentsMap(Object parameter) {
		// 参数都应该是MAP格式
		JSONObject json = JSONObject.fromObject(parameter);

		return (Map<String, Object>) JSONObject.toBean(json, Map.class);
	}

	// XXX 这个方法是从AuthFilter抄过来的，后期再整合起来
	private boolean validate(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();

		Credential credential=null;
		
		//如果是手机发送的请求
       if(MobileManager.isMobileReq(request)){
    	   String jessionid =request.getRequestedSessionId();
//    	   log.error("the jessionid now is:"+jessionid);
    	   String	token = session.getId();
    	   try {
    		   credential = MobileManager.restoreCredential(jessionid!=null?jessionid:token);
    		
    		   session.setAttribute(Constants.KEY_USR_CREDENTIAL, credential);
    		   
		} catch (AAException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
    	   //从数据库中还远session信息
       }else{
    	   
    	    credential = (Credential) session.getAttribute(Constants.KEY_USR_CREDENTIAL);  		
		
       }
       
	   	if (null == credential) {
			return false;
		}

		// validate db session


	

//			AuthManager.validateSession(token);

			return true;
	

	
	}

	private void gotoLogon(HttpServletRequest request, HttpServletResponse response) throws IOException {

		AjaxResponse res = new AjaxResponse();
		res.setSucc(0);
		res.setMsg("_FMP_SESSION_TIMEOUT");

		response.setContentType("application/json;charset=GBK");
		response.setCharacterEncoding("GBK");

		response.getWriter().write(res.toJSON());
		response.getWriter().flush();
		
	}

	private List<String> public_commands = new ArrayList<String>();

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		ClassLoader oClassLoader = Thread.currentThread().getContextClassLoader();
		URL url = oClassLoader.getResource("public_commands.txt");

		File f = new File(url.getFile());

		List<String> list;

		try {
			list = FileUtils.readLines(f);
		} catch (IOException e) {
			throw new ServletException(e);
		}

		for (String s : list) {
			if (!"".equals(s) && !s.startsWith("#")) {
				public_commands.add(s.trim());
			}
		}
	}

}
