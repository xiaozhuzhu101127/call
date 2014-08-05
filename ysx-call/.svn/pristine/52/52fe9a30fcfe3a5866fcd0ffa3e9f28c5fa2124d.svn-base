package com.topjet.fmp.yls;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.topjet.fmp.aa.Credential;
import com.topjet.fmp.aa.CredentialFactory;
import com.topjet.fmp.aa.domain.Resource;
import com.topjet.fmp.aa.domain.SearchResourceCriteria;
import com.topjet.fmp.aa.domain.Session;
import com.topjet.fmp.aa.service.AAService;
import com.topjet.fmp.aa.service.AccountService;
import com.topjet.fmp.base.DC;
import com.topjet.fmp.exception.AAException;
import com.topjet.fmp.util.AppContext;
import com.topjet.fmp.util.Constants;

public class MobileManager {

	private static Logger log = LoggerFactory.getLogger(MobileManager.class);

	/**
	 * 需要权限控制的action
	 */
	private static Map<String, String> resources = null;

	/**
	 * 公共action,不需要登录即可访问的
	 */
	private static Map<String, String> publicResources = null;

	private static AAService aaService;

	private static String CURRENT_APP = null;

	private MobileManager() {

	}

	static void destroy() {
		resources = null;
		publicResources = null;
	}

	static void init(FilterConfig config) throws Exception {
		log.info("Initializing AuthManager...");

		aaService = AppContext.getBean(AAService.class);

		CURRENT_APP = config.getInitParameter("APP");

		initPrivateResources();
		initPublicResources();

		log.info("AuthManager initialized");
	}

	private static void initPrivateResources() throws Exception {
		resources = new HashMap<String, String>();

		try {

			log.info("Initializing protected resources...");

			// 启动时初始化受权限控制管理的资源
			SearchResourceCriteria criteria = new SearchResourceCriteria();
			criteria.setRESISPUBLIC(Constants.FALSE);
			criteria.setDCT_APP(CURRENT_APP);

			List<Resource> pvg = aaService.listResources(null, criteria);

			if (null == pvg)
				return;

			Resource privilege = null;

			for (int i = 0; i < pvg.size(); i++) {

				privilege = (Resource) pvg.get(i);

				String resvalue = privilege.getRESVALUE().trim();

				resources.put(resvalue, privilege.getRESNAME());
			}

		} catch (Exception e) {
			log.error("Error occured while initializing AuthManager!!!", e);
			throw e;
		}
	}

	private static void initPublicResources() {
		log.info("Initializing public resources...");

		publicResources = new HashMap<String, String>();

		SearchResourceCriteria criteria = new SearchResourceCriteria();
		criteria.setRESISPUBLIC(Constants.TRUE);
		criteria.setDCT_APP(CURRENT_APP);

		List<Resource> publicRes = aaService.listResources(null, criteria);

		if (null == publicRes)
			return;

		Resource publicDict = null;

		for (int i = 0; i < publicRes.size(); i++) {

			publicDict = (Resource) publicRes.get(i);

			String value = publicDict.getRESVALUE().trim();

			publicResources.put(value, "Y");

		}

	}

	public static boolean hasPermission(String action, Credential credential, HttpSession session) {
		if (null == resources || 0 == resources.size())
			return true;

		// 查看此资源是否是私有资源
		Object obj = resources.get(action);

		if (null == obj)
			return true;

		if (null == credential) {
			return false;
		}

		// 获取当前用户的所有权限 并将URL缓存起来
		Map<String, Resource> usrResources = loadResource(credential);

		return (null != usrResources.get(action));
	}

	/**
	 * 加载用户所拥有的资源
	 * 
	 * @param credential
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Resource> loadResource(Credential credential) {

		Ehcache cache = (Ehcache) AppContext.getBean("userAACache");

		Element element = cache.get(Constants.CACHE_KEY_USR_AA + Constants.SYMBOL_UNDERSCORE + credential.getUSRID());

		// 缓存中存在直接返回
		if (null != element) {
			log.debug("Found user resources in cache, just return");
			return (Map<String, Resource>) element.getValue();
		}

		List<Resource> resourceList = aaService.listResources(null, credential.getUSRID(), DC.RT_URL);
		Map<String, Resource> usrResources = new HashMap<String, Resource>();

		for (Resource res : resourceList) {
			usrResources.put(res.getRESVALUE(), res);
		}

		element = new Element(Constants.CACHE_KEY_USR_AA + Constants.SYMBOL_UNDERSCORE + credential.getUSRID(),
				usrResources);

		cache.put(element);

		return usrResources;
	}

	public static boolean isPublic(String action) {

		if (null == publicResources)
			return false;

		return (null != publicResources.get(action));

	}

	/**
	 * Validate if session is valid, throws an exception when invalid, the
	 * caller should invalidate the httpsession as well
	 * 
	 * @param id
	 */
	public static void validateSession(String id) throws AAException {
		AAService aaService = AppContext.getBean(AAService.class);
		aaService.validateSession(id);
	}

	static Credential restoreCredential(String id) throws AAException {
		AAService aaService = AppContext.getBean(AAService.class);
		Session session = aaService.querySession(id);

		if (null == session) {
			throw new AAException("Credential restore error,Session is not alive");
		}

		return CredentialFactory.getInstance().restore(session);
	}

	public static void forceLogout(Credential credential) {
		AccountService accountService = AppContext.getBean(AccountService.class);
		accountService.logout(credential);// 记录日志，表示被踢出
	}

	static void registerSession(Credential credential) throws AAException {
		aaService.createSession(credential);// XXX 几个整理到一起去
	}

	public static void invalidateSession(String sessionId) {
		AAService aaService = AppContext.getBean(AAService.class);
		aaService.invalidateSession(sessionId);
	}
	
	public static void deleteSession(String sessionId) {
		aaService.deleteSession(sessionId);
	}
	
	public static boolean isMobileReq(HttpServletRequest req) {
		
		String reqSource = req.getHeader("reqSource");
		 if("mobile".equals(reqSource)){
			 return true;
		 }
		 return false;
	}

}
