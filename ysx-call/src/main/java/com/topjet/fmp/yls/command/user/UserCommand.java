package com.topjet.fmp.yls.command.user;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sun.xml.internal.rngom.digested.DPatternVisitor;
import com.topjet.fmp.aa.Credential;
import com.topjet.fmp.aa.CredentialFactory;
import com.topjet.fmp.aa.domain.Account;
import com.topjet.fmp.aa.domain.AccountLog;
import com.topjet.fmp.aa.domain.AccountTemp;
import com.topjet.fmp.aa.domain.Session;
import com.topjet.fmp.aa.service.AAService;
import com.topjet.fmp.aa.service.AccountLogService;
import com.topjet.fmp.aa.service.AccountService;
import com.topjet.fmp.aa.service.AccountTempService;
import com.topjet.fmp.app.domain.WeizhangReq;
import com.topjet.fmp.app.domain.WeizhangReqBO;
import com.topjet.fmp.app.domain.WeizhangReqCriteria;
import com.topjet.fmp.banyan.domain.TIdentityCard;
import com.topjet.fmp.banyan.response.ServiceResponse;
import com.topjet.fmp.banyan.service.SMSService;
import com.topjet.fmp.banyan.service.TAuthService;
import com.topjet.fmp.banyan.service.exception.NetworkException;
import com.topjet.fmp.banyan.service.exception.TAuthException;
import com.topjet.fmp.base.DC;
import com.topjet.fmp.base.domain.Dict;
import com.topjet.fmp.base.domain.PaginatedList;
import com.topjet.fmp.biz.tk.domain.MemberTkOnline;
import com.topjet.fmp.biz.tk.domain.TkOnline;
import com.topjet.fmp.biz.tk.domain.TkOnlineTemp;
import com.topjet.fmp.biz.tk.service.TkOnlineService;
import com.topjet.fmp.biz.tk.service.TruckOnlineTempService;
import com.topjet.fmp.exception.AAException;
import com.topjet.fmp.exception.AccountException;
import com.topjet.fmp.exception.FMPException;
import com.topjet.fmp.exception.FinanceException;
import com.topjet.fmp.exception.MemberException;
import com.topjet.fmp.exception.SVCException;
import com.topjet.fmp.fin.domain.FinAccountBO;
import com.topjet.fmp.fin.domain.Recommend;
import com.topjet.fmp.fin.service.FinAccountService;
import com.topjet.fmp.fin.service.RecommendService;
import com.topjet.fmp.stat.domain.UserVisitedStatInfoBO;
import com.topjet.fmp.stat.service.UserAreaStatInfoService;
import com.topjet.fmp.svc.domain.SPCriteria;
import com.topjet.fmp.svc.domain.SVCCriteria;
import com.topjet.fmp.svc.domain.ServiceBO;
import com.topjet.fmp.svc.domain.ServiceOrder;
import com.topjet.fmp.svc.domain.ServicePriceBO;
import com.topjet.fmp.svc.service.SVCPriceService;
import com.topjet.fmp.svc.service.SVCService;
import com.topjet.fmp.svc.service.VerificationService;
import com.topjet.fmp.sys.domain.NoticeBO;
import com.topjet.fmp.sys.domain.NoticeCriteria;
import com.topjet.fmp.sys.service.DictService;
import com.topjet.fmp.sys.service.NoticeService;
import com.topjet.fmp.user.domain.Agent;
import com.topjet.fmp.user.domain.ContactTemp;
import com.topjet.fmp.user.domain.IDNORECORD;
import com.topjet.fmp.user.domain.IDNORECORDCriteria;
import com.topjet.fmp.user.domain.MemberGpsCurrent;
import com.topjet.fmp.user.domain.UserAccessLog;
import com.topjet.fmp.user.domain.UserDpDetailCriteria;
import com.topjet.fmp.user.domain.UserDpDetailDomain;
import com.topjet.fmp.user.domain.UserDpDetailDomainBO;
import com.topjet.fmp.user.domain.UserLocationReqBO;
import com.topjet.fmp.user.domain.UserLocationReqCriteria;
import com.topjet.fmp.user.domain.YslAccount;
import com.topjet.fmp.user.domain.YslMember;
import com.topjet.fmp.user.domain.YslMemberBO;
import com.topjet.fmp.user.domain.YslMemberCriteria;
import com.topjet.fmp.user.domain.YslMemberMoble;
import com.topjet.fmp.user.domain.YslMessageBO;
import com.topjet.fmp.user.service.AgentService;
import com.topjet.fmp.user.service.ContactTempService;
import com.topjet.fmp.user.service.IDnoRecordService;
import com.topjet.fmp.user.service.MemberGpsCurrentService;
import com.topjet.fmp.user.service.UserAccessLogService;
import com.topjet.fmp.user.service.UserDpDetailService;
import com.topjet.fmp.user.service.UserLocationReqService;
import com.topjet.fmp.user.service.YslAccountService;
import com.topjet.fmp.user.service.YslMemberService;
import com.topjet.fmp.user.service.YslMessageService;
import com.topjet.fmp.user.util.MobileConstants;
import com.topjet.fmp.util.CLSBase64;
import com.topjet.fmp.util.CommonUtils;
import com.topjet.fmp.util.Constants;
import com.topjet.fmp.util.DES;
import com.topjet.fmp.util.PWDUtil;
import com.topjet.fmp.util.WebUtil;
import com.topjet.fmp.yls.MobileManager;
import com.topjet.fmp.yls.RemoteResponse;
import com.topjet.fmp.yls.command.BaseCommand;
import com.topjet.fmp.yls.command.Command;
import com.topjet.fmp.yls.util.CommonUtil;
import com.topjet.fmp.yls.util.IDCardUtil;
import com.topjet.fmp.yls.util.IdcardLocation;
import com.topjet.fmp.yls.util.LocationMapUtil;
import com.topjet.fmp.yls.util.PhoneLocation;

/**
 * 
 * <pre>
 * Description
 * Copyright:	Copyright (c)2009  
 * Company:		拓景科技
 * Author:		Dengfp
 * Version:		1.0  
 * Create at:	2012-7-24 上午11:29:08  
 *  
 * 修改历史:
 * 日期    作者    版本  修改描述
 * ------------------------------------------------------------------
 * 
 * </pre>
 */
@Component("user_command")
public class UserCommand extends BaseCommand {

	private Log log = LogFactory.getLog(UserCommand.class);

	@Autowired
	private YslMemberService yslMembertService;

	@Autowired
	private AgentService agentService;

	@Autowired
	private TruckOnlineTempService tkOnlineTempService;

	@Autowired
	private YslMessageService yslMessageService;

	@Autowired
	private TkOnlineService tkOnlineService;

	@Autowired
	private FinAccountService finAccountService;
	@Autowired
	private IDnoRecordService idnoRecordService;

	@Autowired
	private AAService aaService;

	@Autowired
	private AccountTempService accountTempService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private TAuthService tauthService;

	@Autowired
	private SMSService smsService;

	@Autowired
	private NoticeService noticeService;

	@Autowired
	private DictService dictService;

	@Autowired
	private VerificationService verificationService;

	@Autowired
	private AccountLogService accountLogService;

	@Autowired
	private UserLocationReqService userLocationReqService;

	@Autowired
	private SVCPriceService svcPriceService;

	@Autowired
	private ContactTempService contactTempService;

	@Autowired
	private RecommendService recommendService;

	@Autowired
	private MemberGpsCurrentService memberGpsCurrentService;

	@Autowired
	private YslAccountService yslAccountService;
	@Autowired
	private UserDpDetailService userDpDetailService;

	@Autowired
	private SVCService svcService;
	@Autowired
	private UserAreaStatInfoService userAreaStatInfoService;

	@Autowired
	private UserAccessLogService userAccessLogService;

	private static final String MSG_ORDER_SUCC = "订购成功。有效期{0}至{1}";

	public RemoteResponse login(Credential credential, Map<String, Object> map) {
		// System.out.println(map);
		LoginParameter parameter = new LoginParameter();
		HttpServletRequest request = (HttpServletRequest) map
				.get(Command.P_HTTPREQUEST);

		RemoteResponse res = new RemoteResponse();
		// 用户名
		String username = (String) map.get("username");
		// 密码
		String pwd = (String) map.get("pwd");

		Credential cred = null;

		if (!StringUtils.hasText(username) && !StringUtils.hasText(pwd)) {
			res.setSucc(0);
			res.setMsg("账号或密码不能为空");
			return res;
		}

		Long usrid = null;

		// 需要考虑到用户的登录日志,目前已经在accountService中进行考虑进去,具体的如下
		AccountLog accountLog = new AccountLog();
		// 获取IP地址
		accountLog.setALIPADDR((String) map.get(Command.P_IPADDR));
		accountLog.setDCT_ACCAT(DC.ACCAT_CS);
		if (MobileManager.isMobileReq(request)) {
			accountLog.setDCT_ACCAT(DC.ACCAT_MOBILE_YSX);
		} else {
			accountLog.setDCT_ACCAT(DC.ACCAT_PC_YSX);
		}
		accountLog.setDCT_ACCA(DC.ACCA_LOGIN);
		accountLog.setDCT_APP(DC.APP_REMOTE);

		// cred = accountService.logon(request.getSession(false).getId(),
		// username, pwd, accountLog);
		YslMemberBO bo = null;
		try {
			// if(!MobileManager.isMobileReq(request)){
			// bo = yslMembertService.logon(username.trim(), pwd, accountLog);
			// }else{
			bo = yslMembertService.logonByTempPwdFirst(username.trim(), pwd,
					accountLog);
			// }
		} catch (MemberException e) {
			log.error(e.getMessage());
			res.setMsg(e.getMessage());
			res.setSucc(0);
			return res;
		}
		if (bo == null) {
			res.setMsg("您输入的用户名或者密码错误！");
			res.setSucc(0);
			return res;
		}

		if (DC.UC_AGTC.equals(bo.getDCT_UC())
				|| DC.UC_AGTP.contains(bo.getDCT_UC())) {
			res.setMsg("非常抱歉，您的账户类型不能登录客户端！");
			res.setSucc(0);
			return res;
		}

		// 判断是否有登录权限，判断该电脑是否有注册

		// String jcookie = "F72A9D7E18E7A3BC";

		HttpSession session = request.getSession();
		// String sessionid=session.getId();
		// String jsess = sessionid.substring(10, 26);
		// 由于初次登录的时候，客户端不知道jsessionID的内容，所以约定一个固定的密钥（E3472FD30293C2EC），仅用于login方法中,允许两个
		if (!MobileManager.isMobileReq(request)) {
			String jcookie = request.getHeader("jcookieid");
			String jsess = "9E3472FD30293C2E";
			String loginProf = CommonUtil.pass_single(jcookie, jsess);
			boolean isAdd = false;
			if (StringUtils.hasText(bo.getYSLLOGIN())) {
				List<String> listRights = Arrays.asList(bo.getYSLLOGIN().split(
						Constants.STRING_SPLIT));
				if (!listRights.contains(loginProf)) {
					if (listRights.size() < 2) {
						isAdd = true;
					} else {
						res.setSucc(0);
						res.setMsg("_FMP_CLIENT_MACHINE_CHANGED");
						res.setSucc(0);
						return res;
					}

				}
			} else {
				isAdd = true;

			}
			if (isAdd) {
				YslMember member = new YslMember();
				member.setUSRID(bo.getUSRID());
				if (StringUtils.hasText(bo.getYSLLOGIN())) {
					member.setYSLLOGIN(bo.getYSLLOGIN()
							+ Constants.STRING_SPLIT + loginProf);
				} else {
					member.setYSLLOGIN(loginProf);
				}

				yslMembertService.update(member);
			}
		}

		Map<String, Object> values = new HashMap<String, Object>();
		values.put("USRID", bo.getUSRID());
		values.put("DCT_UC", bo.getDCT_UC());
		values.put("SESSIONID", request.getSession(false).getId());
		values.put("USRNAME", username);
		values.put("IPADDR", accountLog.getALIPADDR());
		values.put("DCT_ACCAT", DC.ACCAT_MOBILE_YSX);

		cred = CredentialFactory.getInstance().create(values);

		// 手机端的，，先用用户名去表中取相关session记录，没有才插入数据库中
		// 返回的数据会覆盖原来的
		if (MobileManager.isMobileReq(request)) {
			try {
				Session se = aaService.getYsxMobileSessionByUsrName(username,
						DC.ACCAT_MOBILE_YSX);
				if (se == null) {
					aaService.createSession(cred);
					values.put("USRID", bo.getUSRID());
					values.put("DCT_UC", bo.getDCT_UC());
					values.put("SESSIONID", request.getSession(false).getId());
					values.put("USRNAME", username);
					values.put("IPADDR", accountLog.getALIPADDR());
					values.put("DCT_ACCAT", DC.ACCAT_MOBILE_YSX);
					cred = CredentialFactory.getInstance().create(values);
				} else {
					values.put("USRID", se.getUSRID());
					values.put("DCT_UC", se.getDCT_UC());
					values.put("SESSIONID", se.getSESSIONID());
					values.put("USRNAME", username);
					values.put("IPADDR", accountLog.getALIPADDR());
					values.put("DCT_ACCAT", DC.ACCAT_MOBILE_YSX);
					cred = CredentialFactory.getInstance().create(values);
				}
			} catch (AAException e) {
				log.error(e.getMessage());
				res.setMsg(e.getMessage());
				res.setSucc(0);
				return res;
			}

			// 推荐号码存在，还有是否上传过通讯录，针对手机端
			Recommend mend = recommendService.selectRecommendByRdedMobile(
					credential, username);
			if (mend != null) {
				parameter.setIsrecommend(1);
				parameter.setRecommendMobile(mend.getRDMOBILE());
			}

			// 手机取base64头像
			if (bo.getPHOTOID() != null) {
				parameter.setIdCardPhoto(tauthService.queryYSLPhoneById(
						credential, bo.getPHOTOID()));
				parameter.setPHOTOURL(MobileConstants.YSX_PHOTO + bo.getPHOTOID());
			}

		}

		SimpleDateFormat dateformat1 = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");

		String remark = null;

		String place = bo.getPLACE();

		String idcard = "";
		ServiceOrder service = null;

		// Long pictureID = null;
		// 判断用户身份：会员
		if (DC.UC_MBR.equals(bo.getDCT_UC())) {

			// 取该用户的服务
			SVCCriteria svcCriteria = new SVCCriteria();
			svcCriteria.setUSRID(cred.getUSRID());
			svcCriteria.setSVCCODE(Constants.YSX);

			List<ServiceOrder> list = getSvcOrderService()
					.selectAllValidServiceByCode(credential, svcCriteria);
			if ((list == null || list.size() <= 0)
					&& !MobileManager.isMobileReq(request)) {
				res.setSucc(0);
				res.setMsg("您没有订购服务，无权登录");
				return res;

			}
			boolean isnotLogin = true;
			String allService = "";
			for (int i = 0; i < list.size(); i++) {
				// 可登录的服务，即通过break;
				if (Constants.YSX_ZS.equals(list.get(i).getSVCCODE())
						|| Constants.YSX_SY.equals(list.get(i).getSVCCODE())
						|| Constants.YSX_YP.equals(list.get(i).getSVCCODE())) {
					service = list.get(i);
					isnotLogin = false;
					break;
				}

			}

			if (isnotLogin && !MobileManager.isMobileReq(request)) {
				res.setSucc(0);
				res.setMsg("您订购的服务无法登录运商行客户端软件");
				return res;
			}

			if (service != null) {
				parameter.setService(service.getSVCCODE());
			}
			parameter.setAgentAccount(0);

			remark = bo.getREMARK();

			// 诚信提示信息，針對會員

			if (!StringUtils.hasText(remark)) {
				Dict city = dictService.transDictCode(DC.YSL_REMARK);

				remark = city.getDCTVALUE1();

			}

			parameter.getTrustInfo().setTrustPromptInfo(remark);

			// 当地信用排名，針對會員
			if (StringUtils.hasText(place)) {
				parameter.getTrustInfo().setLocalTrustLevel(
						getYslRegionRank(cred, place, bo.getUSRID(),
								bo.getDCT_UT()));
			}
			// 获得全部订购的服务
			for (int i = 0; i < list.size(); i++) {

				allService += list.get(i).getSVCCODE() + "|";

			}

			if (allService.lastIndexOf("|") > 0) {
				allService = allService.substring(0, allService.length() - 1);
			}
			parameter.setAllService(allService);

		} else {
			// 表示代理点
			parameter.setAgentAccount(1);
		}

		parameter.setAllowOtherViewMyCertInfo(bo.getISVIEWIDNO());

		// MemberBO memberBO = null;
		//
		// memberBO = memberAdminService.queryMemberBO(cred,
		// cred.getUSRID());

		parameter.setUserID(bo.getUSRID());
		parameter.setRealName(bo.getREALNAME());
		// 1男，0女
		if ("1".equals(bo.getSEX())) {
			parameter.setSex(1L);
		} else {
			parameter.setSex(0L);
		}

		if (DC.UCERT_PS.equals(bo.getDCT_UA())) {
			parameter.setIsCert(Constants.TRUE);
		} else {
			parameter.setIsCert(Constants.FALSE);
		}

		parameter.setPhoneNumber(CommonUtil.getTels(bo));
		parameter.setMobilePhoneNumber(CommonUtil.getMobiles(bo));
		parameter.setCompanyName(bo.getCOMPANYNAME());

		parameter.setUserType(bo.getDCT_UT());
		// parameter.setIdCardPhoto(memberBO.getUSRIDNO());
		parameter.setRegisterTime(dateformat1.format(bo.getCREATE_TIME()));
		parameter.setPlace(bo.getPLACE());

		parameter.setBusiAddress(bo.getBUSIADDRESS());
		parameter.setBusiLines(bo.getBUSILINES());
		parameter.setTransportInfo(bo.getTRANSPORTINFO());
		remark = bo.getREMARK();
		// 常驻地，如果不存在，需要取一次做判断，并且更新到数据库
		place = bo.getPLACE();
		if (!StringUtils.hasText(place)) {
			// 如果不存在，则优先取固话所在地注意点，tel1,mobile1是主号码，一定存在一个
			String phone = bo.getTEL1();
			if (!StringUtils.hasText(phone)) {
				phone = bo.getMOBILE1();
			}
			place = super.getPalced(credential, bo.getUSRID(), phone);
			parameter.setPlace(place);
		}

		if (StringUtils.hasText(place)) {
			String city = getCityService().translate(place, true);
			if (city != null) {
				parameter.setPlaceName(city);
			}
		}
		//
		usrid = bo.getUSRID();
		idcard = bo.getIDNO();

		if (StringUtils.hasText(idcard)) {
			parameter.setAge(IDCardUtil.getAgeByIdCard(idcard));
			parameter.setUserHouseHold(IdcardLocation.getLocation(idcard
					.substring(0, 6)));
		}

		parameter.getTrustInfo().setComplaintNumber(bo.getBTSCOUNT());

		parameter.getTrustInfo().setComplaintOtherPeopleNumber(bo.getTSCOUNT());

		// 活跃度,如果代理为默认 0
		if (DC.UC_MBR.equals(bo.getDCT_UC())) {
			parameter.getTrustInfo().setActivityNumber(bo.getYSLACTIVEDEG());
		}
		// 运商联指数
		parameter.getTrustInfo().setCredit(bo.getYSLACCOUNT());

		// 证书编号
		parameter.setCertificationID(bo.getIDCERTIFICATIONID());

		// 设置 session
		request.getSession(false).setAttribute(Constants.KEY_USR_CREDENTIAL,
				cred);
		request.getSession(false).setAttribute(Constants.YSL_SERVICE_CODE,
				service == null ? null : service.getSVCCODE());
		if (MobileManager.isMobileReq(request)) {
			request.getSession(false).setMaxInactiveInterval(-1);
		} else {
			request.getSession(false).setMaxInactiveInterval(30 * 60);
		}

		res.setId(cred.getSESSIONID());
		// log.error("the res jsessionid is:"+res.getId()+",and the request sesssion is:"+request.getSession(false).getId()+"and mobile is:"+username);

		res.setResult(parameter);
		return res;
	}

	public RemoteResponse getUserPhoto(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		Long pictureID = null;
		Integer usrid = (Integer) map.get("UserID");

		YslMember member = yslMembertService.getYslMemberById(Long
				.valueOf(usrid));

		if (null != member) {
			pictureID = member.getPHOTOID();
		}

		if (null == pictureID) {
			res.setSucc(0);
			res.setMsg("没有头像存在！");
			return res;

		}
		String pt = tauthService.queryYSLPhoneById(credential, pictureID);
		Map mapPic = new HashMap();
		mapPic.put("idCardPhoto", pt);
		res.setResult(mapPic);

		return res;

	}

	/**
	 * 1) 把陌生人注册成网站的会员,注册信息的来源主要是电话盒,注册到哪个代理商下面
	 * 
	 * 2)
	 */

	public RemoteResponse orderServiceForMember(Credential credential,
			Map<String, Object> map) {
		// System.out.println(map);
		// credential = testLogin(credential, map);
		JSONObject jsonObject = JSONObject.fromObject(map.get("MemberInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(
				jsonObject, Map.class);

		RemoteResponse res = new RemoteResponse();

		// ID
		Integer MemberID = (Integer) userMap.get("TargetMemberID");

		// MemberBO memberBO = memberAdminService.queryMemberBO(credential,
		// Long.valueOf(MemberID));

		YslMember yslmember = yslMembertService.getYslMemberById(Long
				.valueOf(MemberID));

		if (!DC.UCERT_PS.equals(yslmember.getDCT_UA())) {
			res.setMsg("对不起，该用户没有认证，请认证后再订购");
			res.setSucc(0);
			return res;
		}

		String service = (String) userMap.get("Service");
		String serviceDuration = (String) userMap.get("ServiceDuration");
		// ServicePrice price = null;
		// try {
		// price= svcPriceService.queryPrice(credential,service,
		// serviceDuration);
		// } catch (SVCException e) {
		// e.printStackTrace();
		// res.setMsg(e.getMessage());
		// res.setSucc(0);
		// return res;
		// }
		// // 充值,注意这里取出来的是分
		// Long money = price.getSPPRICE();
		//
		// //
		// if (null != money&&!"0".equals(money.toString())) {
		// try {
		// financeService.transfer(credential, credential.getUSRID(),
		// MemberID, money, "");
		//
		// } catch (FinanceException e) {
		// e.printStackTrace();
		// res.setMsg(e.getMessage());
		// res.setSucc(0);
		// return res;
		//
		// }
		// }

		if (StringUtils.hasText(service)) {
			try {
				orderService(credential, service, serviceDuration, res,
						MemberID);

				// 订购成功后，需要把会员的代理商ID，改成该代理商ID
				YslMember member = new YslMember();
				member.setUSRID(yslmember.getUSRID());
				member.setAGENT_ID(credential.getUSRID());
				// 取场站信息绑定到被订购的用户身上
				YslMember creMember = yslMembertService
						.getYslMemberById(credential.getUSRID());
				member.setTSID(creMember.getTSID());

				getYslMemberService().update(member);

				// 对于首次订购服务的账号，进行密码重置密码操作哦
				if (getSvcOrderService().countOrderWithUser(
						yslmember.getUSRID()) == 1) {
					String pwd = "";
					String phone = "";
					if (StringUtils.hasText(yslmember.getMOBILE1())) {
						phone = yslmember.getMOBILE1();
						pwd = RandomStringUtils.random(6, false, true);
						// 有手机号码的，还必须要发短信
						Account account = new Account();
						account.setUSRID(yslmember.getUSRID());
						account.setUSRPWD(PWDUtil.encrypt(pwd));
						accountService.update(credential, account);
						// 进行发短信操作
						smsService.sendYslSms(credential, phone, pwd, yslmember
								.getUSRID().toString(), getSmsMessage(service));
					} else {
						phone = yslmember.getTEL1();
						pwd = phone.substring(phone.length() - 6);
						Account account = new Account();
						account.setUSRID(yslmember.getUSRID());
						account.setUSRPWD(PWDUtil.encrypt(pwd));
						accountService.update(credential, account);
					}
				}

			} catch (SVCException e) {
				e.printStackTrace();
				res.setMsg(e.getMessage());
				res.setSucc(0);
				log.warn(e.getMessage(), e);
			} catch (FinanceException e) {
				e.printStackTrace();
				res.setMsg(e.getMessage());
				res.setSucc(0);
				log.warn(e.getMessage(), e);
			} catch (Throwable e) {
				e.printStackTrace();
				res.setMsg(e.getMessage());
				res.setSucc(0);
				log.warn(e.getMessage(), e);
			}
		}

		return res;
	}

	private void orderService(Credential credential, String service,
			String serviceDuration, RemoteResponse res, Integer MemberID)
			throws SVCException, FinanceException, MemberException {

		ServiceOrder order = new ServiceOrder();
		order.setSVCCODE(service);
		order.setUSRID(Long.valueOf(MemberID));
		order.setSVCDURATION(serviceDuration);
		order = getSvcOrderService().order(credential, order);

		res.setMsg(MessageFormat.format(MSG_ORDER_SUCC,
				CommonUtils.formatDate(order.getSOSTARTDATE()),
				CommonUtils.formatDate(order.getSOENDDATE())));

	}

	// 在线认证
	public RemoteResponse getPersonIDCardInfo(Credential credential,
			Map<String, Object> map) {

		JSONObject jsonObject = JSONObject.fromObject(map.get("PersonInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(
				jsonObject, Map.class);

		Object memberID = (Object) userMap.get("MemberID");
		String member = null;

		try {
			if (memberID instanceof Integer) {
				member = memberID.toString();

			}
		} catch (ClassCastException e1) {
			e1.printStackTrace();
			member = null;
		}

		String phone = "";

		String idCardNumber = (String) userMap.get("IDCardNumber");
		String name = (String) userMap.get("Name");
		// String name = "王敏慧";

		RemoteResponse res = new RemoteResponse();

		// Long agentID =null;
		// if (DC.UC_AGTR.equals(credential.getDCT_UC())) {
		//
		// agentID =credential.getUSRID();
		// } else {
		// YslMember member =
		// yslMembertService.getYslMemberById(credential.getUSRID());
		// agentID=member.getAGENT_ID();
		// }

		// 在线进行认证，需要扣钱的

		ServiceResponse<Integer> stauts = null;
		try {
			// name为3级代理的ID
			stauts = verificationService.idVerification(credential, name,
					idCardNumber);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res.setMsg(e.getMessage());
			res.setSucc(0);
			return res;
		}

		String result = stauts.getMsg();

		if (3 == stauts.getResponse()) {
			res.setMsg("姓名与身份证号一致，身份真实有效！");

			IDCardParameter pr = new IDCardParameter();
			pr.setName(name);
			pr.setiDCardNumber(idCardNumber);

			// 是否要考虑从省份证中去取，就怕没有啊，这个是个问题，待定
			TIdentityCard card = tauthService.getNativeIdData(idCardNumber);
			if (name.equals(card.getAIDNAME())) {
				pr.setNation(card.getAIDNATION());
				byte[] pic = stauts.getPhone();

				if (pic != null) {
					pr.setiDCardPhoto(CLSBase64.encode(pic));
				}

				pr.setSex(IDCardUtil.getSexByIdCard(card.getAIDNO()));

				pr.setPublishOrg(card.getAIDPOLICE());

				pr.setAddress(card.getAIDADDR());

				Date birthday = card.getAIDBORN();

				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				if (birthday != null) {
					pr.setBirthday(format.format(birthday));

				}

				Date start = card.getAIDSTART();
				Date end = card.getAIDEXPIRED();
				if (null != start && null != end) {
					pr.setValid(format.format(start) + "," + format.format(end));

				}
			}

			if (StringUtils.hasText(member)) {
				try {
					// 能绑就绑，不能绑也没关系
					YslMember temPember = yslMembertService
							.getYslMemberById(Long.valueOf(member));
					if (temPember != null) {
						phone = temPember.getMOBILE1() != null ? temPember
								.getMOBILE1() : temPember.getTEL1();
					}
					if (!DC.UCERT_PS.equals(temPember.getDCT_UA())) {
						yslMembertService.updateMemberByIdcard(credential,
								Long.valueOf(member), idCardNumber, name,
								stauts.getPictureID());
					}
				} catch (MemberException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// 在线认证的需要记录认证历史，出错直接捕捉，这个失败了没关系
			try {
				IDNORECORD record = new IDNORECORD();
				record.setREGIDNO(idCardNumber);
				record.setREGNAME(name);
				record.setREGPHOTOID(stauts.getPictureID());

				// 有关联的从用户列表中取也就是传了ID 过来认证的，没有则根据身份证号码去数据库中查询该条记录
				// if(StringUtils.hasText(phone)){
				// record.setREGPHONE(phone);
				// }else{
				// YslMember temPember =
				// yslMembertService.getYslMemberByidno(idCardNumber);
				// if(temPember!=null){
				// record.setREGPHONE(temPember.getMOBILE1()!=null?temPember.getMOBILE1():temPember.getTEL1());
				// }
				// }
				idnoRecordService.create(credential, record);
			} catch (Exception e) {
				log.error("在线身份证认证，记录用户认证历史出错");
				e.printStackTrace();
			}

			// pr.set(IDCardUtil.getAgeByIdCard(idCardNumber));
			// pr.setSex(IDCardUtil.getSexByIdCard(idCardNumber));
			// parameter.setUserHouseHold(IdcardLocation.getLocation(member.getUSRIDNO().substring(0,
			// 6)));
			Map personInfoMap = new HashMap();
			personInfoMap.put("IDCardInfo", pr);

			res.setResult(personInfoMap);

		} else if (2 == stauts.getResponse()) {
			res.setMsg("姓名与身份证号不一致！");
			res.setSucc(0);
			return res;
		} else if (1 == stauts.getResponse()) {
			res.setMsg("库中无此身份证号，请检查身份证号输入是否正确！");
			res.setSucc(0);
			return res;
		}

		return res;
	}

	// 身份证认证历史记录详细信息
	public RemoteResponse getIDCardCertHistory(Credential credential,
			Map<String, Object> map) {
		RemoteResponse res = new RemoteResponse();
		Integer historyID = (Integer) map.get("TargetHistoryID");

		IDNORECORD record = idnoRecordService.query(Long.valueOf(historyID));

		if (record == null) {
			res.setMsg("认证记录不存在！");
			res.setSucc(0);
			return res;
		}

		IDCardParameter pr = new IDCardParameter();
		TIdentityCard card = tauthService.getNativeIdData(record.getREGIDNO(),
				record.getREGNAME());
		if (card == null) {
			res.setMsg("身份证认证记录不存在！");
			res.setSucc(0);
			return res;
		}
		pr.setNation(card.getAIDNATION());
		pr.setName(card.getAIDNAME());
		pr.setiDCardNumber(card.getAIDNO());

		if (card.getBDAIDPT() != null) {
			String pt = tauthService.queryYSLPhoneById(credential,
					card.getBDAIDPT());
			pr.setiDCardPhoto(pt);
		}

		pr.setSex(IDCardUtil.getSexByIdCard(card.getAIDNO()));

		pr.setPublishOrg(card.getAIDPOLICE());

		pr.setAddress(card.getAIDADDR());

		Date birthday = card.getAIDBORN();

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (birthday != null) {
			pr.setBirthday(format.format(birthday));

		}

		Date start = card.getAIDSTART();
		Date end = card.getAIDEXPIRED();
		if (null != start && null != end) {
			pr.setValid(format.format(start) + "," + format.format(end));

		}

		// Map personInfoMap = new HashMap();
		HistoryIDcard hisCard = new HistoryIDcard();
		hisCard.setiDCard(pr);
		// personInfoMap.put("IDCard", pr);

		res.setResult(hisCard);

		return res;

	}

	// 身份证认证取历史记录
	public RemoteResponse getIDCardCertHistoryList(Credential credential,
			Map<String, Object> map) {

		Integer usrid = (Integer) map.get("UserID");
		RemoteResponse res = new RemoteResponse();

		String startDate = (String) map.get("StartDate");
		String endDate = (String) map.get("EndDate");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date sDate = null;
		Date eDate = null;

		try {
			if (StringUtils.hasText(startDate)) {
				sDate = format.parse(startDate);
			}
			if (StringUtils.hasText(endDate)) {
				eDate = format.parse(endDate);
			}
			Calendar cal_end = Calendar.getInstance();
			cal_end.setTime(eDate);
			cal_end.add(Calendar.DATE, 1);

			IDNORECORDCriteria criteria = new IDNORECORDCriteria();
			criteria.setCREATE_BY(credential.getUSRID());
			criteria.setStartDay(sDate);
			criteria.setEndDay(cal_end.getTime());
			criteria.setPagesize(0);
			List<IDNORECORD> records = idnoRecordService.list(criteria);
			if (records == null || records.size() == 0) {
				res.setMsg("身份证认证历史记录为空！");
				res.setSucc(0);
				return res;
			}
			List<CertHistoryInfo> hisList = new ArrayList<CertHistoryInfo>();
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = 0; i < records.size(); i++) {
				CertHistoryInfo cord = new CertHistoryInfo();
				cord.setRealName(records.get(i).getREGNAME());
				cord.setiDCardNumber(records.get(i).getREGIDNO());
				if (null != records.get(i).getCREATE_DATE()) {
					cord.setCertTime(format.format(records.get(i)
							.getCREATE_DATE()));
				}
				cord.setHistoryID(records.get(i).getREGID());
				hisList.add(cord);
			}
			Map mapParameter = new HashMap();
			mapParameter.put("CertHistoryInfo", hisList);
			res.setResult(mapParameter);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;

	}

	// private void updateMemberByIdcard(Credential credential, Integer
	// memberID,
	// String idCardNumber, String name, Long pictureId) {
	// YslMember member = new YslMember();
	//
	// member.setPHOTOID(pictureId);
	//
	// member.setUSRID(Long.valueOf(memberID));
	// member.setDCT_UA(DC.UCERT_PS);
	// member.setREALNAME(name);
	// member.setIDNO(idCardNumber);
	// try {
	// getYslMemberService().update( member);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	@SuppressWarnings("unchecked")
	public RemoteResponse getPersonIDCardInfoByPartNumbers(
			Credential credential, Map<String, Object> map) {

		JSONObject jsonObject = JSONObject.fromObject(map.get("PersonInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(
				jsonObject, Map.class);

		RemoteResponse res = new RemoteResponse();

		Integer memberID = (Integer) userMap.get("MemberID");

		String iDCardNumber = (String) userMap.get("PartIDCardNumber");

		// 用户信息
		YslMember yslmember = yslMembertService.getYslMemberById(Long
				.valueOf(memberID));

		if (yslmember == null) {
			res.setSucc(0);
			res.setMsg("该会员不存在，请重新确认！");
			return res;
		}

		if (Constants.FALSE == yslmember.getISVIEWIDNO()) {
			res.setSucc(0);
			res.setMsg("对不起，该用户没有开放查询权限！");
			return res;
		}
		String no = yslmember.getIDNO();
		if (no == null || "".equals(no)) {
			res.setSucc(0);
			res.setMsg("该用户没有认证过！");
			return res;
		}

		if (!no.substring(no.length() - 6, no.length()).equals(iDCardNumber)) {
			res.setSucc(0);
			res.setMsg("对不起，您无权限查看该用户！");
			return res;
		}

		// 身份证信息
		TIdentityCard card = tauthService.getNativeIdData(no);

		if (card == null) {
			res.setSucc(0);
			res.setMsg("该会员不是我们的用户，无法查询！");
			return res;
		}
		if (!card.getAIDNAME().equals(yslmember.getREALNAME())) {
			res.setSucc(0);
			res.setMsg("您输入的身份证信息有误，请您重新输入！");
			return res;
		}

		// 身份证照片信息
		Long pitid = yslmember.getPHOTOID();
		String picture = "";
		if (pitid != null) {
			picture = tauthService.queryYSLPhoneById(credential, pitid);
		}

		IDCardParameter pr = new IDCardParameter();
		pr.setName(yslmember.getREALNAME());
		pr.setiDCardNumber(yslmember.getIDNO());

		pr.setSex(IDCardUtil.getSexByIdCard(yslmember.getIDNO()));

		pr.setNation(card.getAIDNATION());
		pr.setiDCardPhoto(picture);
		pr.setPublishOrg(card.getAIDPOLICE());

		pr.setAddress(card.getAIDADDR());

		Date birthday = card.getAIDBORN();

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (birthday != null) {
			pr.setBirthday(format.format(birthday));

		}
		// format = new SimpleDateFormat("yyyy-MM-dd");
		Date start = card.getAIDSTART();
		Date end = card.getAIDEXPIRED();
		if (null != start && null != end) {
			pr.setValid(format.format(start) + "," + format.format(end));

		}

		Map personInfoMap = new HashMap();
		personInfoMap.put("IDCardInfo", pr);

		res.setResult(personInfoMap);

		return res;
	}

	// 保存完整身份证信息接口
	@SuppressWarnings("unchecked")
	public RemoteResponse saveIDCardInfo(Credential credential,
			Map<String, Object> map) {
		RemoteResponse res = new RemoteResponse();
		Integer memberID = (Integer) map.get("TargetMemberID");
		JSONObject jsonObject = JSONObject.fromObject(map.get("IDCard"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(
				jsonObject, Map.class);
		// / Integer UserID = (Integer) userMap.get("UserID");
		String name = (String) userMap.get("Name");
		String idCardNumber = (String) userMap.get("IDCardNumber");
		Integer sex = (Integer) userMap.get("Sex");
		String nation = (String) userMap.get("Nation");
		String idCardPhoto = (String) userMap.get("IDCardPhoto");

		String birthday = (String) userMap.get("BirthDay");
		String publishOrg = (String) userMap.get("PubOrgnization");
		String address = (String) userMap.get("Address");
		String valid = (String) userMap.get("Valid");

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		TIdentityCard card = new TIdentityCard();
		card.setAIDADDR(address);
		card.setAIDNO(idCardNumber);
		card.setAIDNAME(name);
		card.setAIDNATION(nation);
		card.setAIDSEX(String.valueOf(sex));
		card.setAIDPOLICE(publishOrg);
		try {

			card.setAIDBORN(format.parse(birthday));

			String[] val = valid.split(",");
			// format = new SimpleDateFormat("yyyy-MM-dd");
			card.setAIDSTART(format.parse(val[0]));
			card.setAIDEXPIRED(format.parse(val[1]));
		} catch (Exception e) {

			e.printStackTrace();
		}

		idCardPhoto = idCardPhoto.replaceAll(" ", "+");
		Long pictureID = tauthService
				.saveYSLIdno(credential, idCardPhoto, card);

		if (memberID != null) {
			try {
				YslMember bo = getYslMemberService().getYslMemberById(
						Long.valueOf(memberID));
				if (!DC.UCERT_PS.equals(bo.getDCT_UA())) {
					yslMembertService.updateMemberByIdcard(credential,
							Long.valueOf(memberID), idCardNumber, name,
							pictureID);
				}
			} catch (MemberException e) {
				e.printStackTrace();
				res.setSucc(0);
				res.setMsg("_FMP_IDCARD_ALREADY_BIND_TO_USER");
				return res;
			}
		}

		return res;
	}

	public RemoteResponse execute(Credential credential,
			Map<String, Object> args) {

		// TODO Auto-generated method stub
		return null;
	}

	// 公告
	public RemoteResponse getAnnounceMents(Credential credential,
			Map<String, Object> map) {

		Integer memberID = (Integer) map.get("UserID");
		RemoteResponse res = new RemoteResponse();
		NoticeCriteria criteria = new NoticeCriteria();
		criteria.setMV_NTCPOS(Constants.MV_NTCPOS_32);

		PaginatedList<NoticeBO> nList = noticeService
				.list(credential, criteria);
		if (null == nList.getResult() || nList.getResult().size() == 0) {
			res.setMsg("暂时没有公告信息！");
			res.setResult(0);
			return res;
		}

		List<NoticeParameter> advList = new ArrayList<NoticeParameter>();
		for (NoticeBO noticeBO : nList.getResult()) {
			NoticeParameter np = new NoticeParameter(noticeBO);
			advList.add(np);
		}
		//
		Map advMap = new HashMap();
		advMap.put("AnnounceMent", advList);
		res.setResult(advMap);
		return res;

	}

	public RemoteResponse getMemberAccountState(Credential credential,
			Map<String, Object> map) {

		Integer usrid = (Integer) map.get("UserID");
		RemoteResponse res = new RemoteResponse();
		// Finalaccount account = accountService.query(credential,
		// credential.getUSRID());
		FinAccountBO account = finAccountService.query(credential);
		if (account == null) {
			res.setMsg("账号不存在，请联系客服确认");
			res.setSucc(0);
			return res;
		}
		YslAccount yslAccount = yslAccountService.query(credential,
				credential.getUSRID());
		if (yslAccount == null) {
			res.setMsg("运商联账号不存在，请联系客服确认");
			res.setSucc(0);
			return res;
		}
		YslAccountParameter parameter = new YslAccountParameter();
		parameter.setAccountRemain(Float.valueOf(CommonUtils
				.changeToYuan(account.getUABALANCE())));
		parameter.setAccountRemainStr(CommonUtils.changeToYuan(account
				.getUABALANCE()));
		parameter.setActivityNumber(yslAccount.getYSLACTIVEDEG());
		parameter.setCredit(yslAccount.getYSLACCOUNT());

		Map personInfoMap = new HashMap();
		personInfoMap.put("accountState", parameter);

		res.setResult(personInfoMap);

		return res;

	}

	@SuppressWarnings("unchecked")
	public RemoteResponse modifyUserPassword(Credential credential,
			Map<String, Object> map) {

		JSONObject jsonObject = JSONObject.fromObject(map.get("PasswordInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(
				jsonObject, Map.class);

		RemoteResponse res = new RemoteResponse();

		String oldPwd = (String) userMap.get("OldPwd");

		String newPwd = (String) userMap.get("NewPwd");

		AccountLog accountLog = new AccountLog();
		accountLog.setUSRLOGONNAME(String.valueOf(credential.getUSRID()));
		accountLog.setALDATE(new Date());
		accountLog.setDCT_ACCA(DC.ACCA_CPSW);
		accountLog.setDCT_ACCAT(DC.ACCAT_PC);
		accountLog.setALIPADDR((String) map.get(Command.P_IPADDR));
		try {
			accountService.changePwd(credential, oldPwd, newPwd);
			accountLog.setALSUCC(Constants.TRUE);
			accountLogService.create(accountLog);

		} catch (AccountException e) {
			log.error(e.getMessage());
			accountLog.setALSUCC(Constants.FALSE);
			accountLog.setALFAILREASON(e.getMessage());
			accountLogService.create(accountLog);
			res.setSucc(0);
			res.setMsg(e.getMessage());

		}
		res.setMsg("修改密码成功！");
		return res;

	}

	public RemoteResponse allowOtherViewMyCertInfo(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();

		Integer boolAllow = (Integer) map.get("boolAllow");

		YslMember member = new YslMember();
		member.setUSRID(credential.getUSRID());
		// 0/1 0是不允许，1是允许
		member.setISVIEWIDNO(boolAllow == 1 ? Constants.TRUE : Constants.FALSE);
		yslMembertService.update(member);

		res.setMsg("设置成功！");
		return res;

	}

	public RemoteResponse getServiceList(Credential tail,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();

		SPCriteria criteria = new SPCriteria();
		// criteria.set
		// 按时间SVCTYPE_PTCF DCT_SVCTYPE
		// 服务是否正常 SVCSTS_NORMAL" DCT_SVCSTS
		// 不是默认开通的 为1 SVCISDEFAULT

		//
		// 取登录用户的省代理usrname
		String username = null;

		Long id = tail.getUSRID();

		if (DC.UC_AGTP.equals(tail.getDCT_UC())) {
			username = tail.getUSRNAME();
		} else {
			if (DC.UC_AGTR.equals(tail.getDCT_UC())) {
				Agent agent = agentService.query(tail, id);
				id = agent.getAGTSUPERVISION();
			}
			Agent agent = agentService.query(tail, id);
			id = agent.getAGTSUPERVISION();
			agent = agentService.query(tail, id);
			username = agent.getUSRNAME();
		}

		criteria.setDCT_SVCTYPE(DC.SVCTYPE_PTCF);
		criteria.setDCT_SVCSTS(DC.SVCSTS_NORMAL);
		criteria.setUSRNAME(username);
		criteria.setSVCISDEFAULT("0");
		criteria.setSVCCODE(Constants.YSX_TP);
		PaginatedList<ServicePriceBO> list = svcPriceService.list(tail,
				criteria);
		if (list == null || list.getResult() == null) {
			res.setSucc(0);
			res.setMsg("没有服务！");
			return res;
		}
		List<ServicePriceBO> bolist = list.getResult();
		ServiceInfoParameter[] parameters = new ServiceInfoParameter[bolist
				.size()];
		for (int i = 0; i < bolist.size(); i++) {
			parameters[i] = new ServiceInfoParameter();
			parameters[i].setServiceCode(bolist.get(i).getSVCCODE());
			parameters[i].setServiceName(bolist.get(i).getSVCNAME());
			parameters[i].setServicePrice(CommonUtils.changeToYuan(bolist
					.get(i).getSPPRICE()));
			parameters[i].setServicePeriod(bolist.get(i).getSVCDURATION());

		}
		Map reMap = new HashMap();
		reMap.put("ServiceInfo", parameters);

		res.setResult(reMap);
		return res;

	}

	public RemoteResponse updateContactSingleInfo(Credential credential,
			Map<String, Object> map) {

		// "ContactInfo" : {
		// "DataType" : "COMPANYNAME/BUSIADDRESS/BUSILINES/TRANSPORTINFO",
		// //需要进行更新的目标信息类型(目前是这4种)，公司名称等
		// "NewDataValue" : "新的值"
		// },
		// "TargetMemberID":434,//当前需要操作的目标会员的ID。
		// "UserID":2342,//执行此操作的当前会员的ID

		JSONObject jsonObject = JSONObject.fromObject(map.get("ContactInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(
				jsonObject, Map.class);

		RemoteResponse res = new RemoteResponse();

		Integer UserID = (Integer) map.get("TargetMemberID");

		String dataType = (String) userMap.get("DataType");

		String value = (String) userMap.get("NewDataValue");

		YslMember bo = getYslMemberService().getYslMemberById(
				Long.valueOf(UserID));

		// if(DC.UCERT_PS.equals(bo.getDCT_UA())){
		// res.setSucc(0);
		// res.setMsg("该会员经过认证，不允许修改！");
		// return res;
		//
		// }

		YslMember tempMember = new YslMember();
		tempMember.setUSRID(bo.getUSRID());
		if ("COMPANYNAME".equals(dataType)) {
			tempMember.setCOMPANYNAME(value);

		} else if ("BUSIADDRESS".equals(dataType)) {
			tempMember.setBUSIADDRESS(value);

		} else if ("BUSILINES".equals(dataType)) {
			tempMember.setBUSILINES(value);
		} else if ("TRANSPORTINFO".equals(dataType)) {
			tempMember.setTRANSPORTINFO(value);
		} else {
			res.setSucc(0);
			res.setMsg("传入参数有误，请确认！");
			return res;
		}

		getYslMemberService().update(tempMember);

		res.setMsg("设置成功！");
		return res;

	}

	public RemoteResponse getTruckLocationUrl(Credential credential,
			Map<String, Object> map) {
		RemoteResponse res = new RemoteResponse();
		try {
			HttpServletRequest request = (HttpServletRequest) map
					.get(Command.P_HTTPREQUEST);

			Map<String, Object> values = new HashMap<String, Object>();
			values.put("USRID", credential.getUSRID());
			values.put("DCT_UC", credential.getDCT_UC());
			String sessid = request.getSession(false).getId();
			values.put("SESSIONID", sessid);
			values.put("USRNAME", credential.getUSRNAME());
			values.put("IPADDR", (String) map.get(Command.P_IPADDR));
			values.put("DCT_ACCAT", DC.ACCAT_PC_CGJ);
			Credential cred = CredentialFactory.getInstance().create(values);

			// 将cred信息保存到session表里，给ysx_mobile使用

			aaService.createSession(cred);
			Map<String, String> resultMap = new HashMap<String, String>();
			String token = DES.encrypt(sessid, "nvs1f23d");
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
			String today = format.format(new Date());
			String url = MobileConstants.SJ_URL;
			Dict dict = dictService.query("URL_CGJ");
			if (dict != null && StringUtils.hasText(dict.getDCTVALUE())) {
				url = dict.getDCTVALUE().trim();
			}

			url += token + "&l=" + new Date().getTime();
			log.error(url);
			resultMap.put("URL", url);
			res.setResult(resultMap);
		} catch (AAException e) {
			res.setSucc(0);
			res.setMsg("手机定位功能异常，请联系客服！");
			e.printStackTrace();
		} catch (Exception e) {
			res.setSucc(0);
			res.setMsg("手机定位功能异常，请稍候访问！");
			e.printStackTrace();
		}

		// Map<String, String> resultMap =new HashMap<String,String>();
		//
		// // String url="http://www.melink.cn/login";
		//
		// // resultMap.put("URL", url);
		// res.setResult(resultMap);
		res.setMsg("设置成功！");
		return res;

	}

	public RemoteResponse getTruckFleetUrl(Credential credential,
			Map<String, Object> map) {
		RemoteResponse res = new RemoteResponse();
		try {
			HttpServletRequest request = (HttpServletRequest) map
					.get(Command.P_HTTPREQUEST);

			Map<String, Object> values = new HashMap<String, Object>();
			values.put("USRID", credential.getUSRID());
			values.put("DCT_UC", credential.getDCT_UC());
			String sessid = request.getSession(false).getId();
			values.put("SESSIONID", sessid);
			values.put("USRNAME", credential.getUSRNAME());
			values.put("IPADDR", (String) map.get(Command.P_IPADDR));
			values.put("DCT_ACCAT", DC.ACCAT_PC_LSCD);
			Credential cred = CredentialFactory.getInstance().create(values);

			// 将cred信息保存到session表里，给ysx_mobile使用

			aaService.createSession(cred);
			Map<String, String> resultMap = new HashMap<String, String>();
			String token = DES.encrypt(sessid, "nvs1f23d");
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
			String today = format.format(new Date());
			String url = MobileConstants.YSX_TRUCK_LOGIN_URL + token;
			log.error(url);
			resultMap.put("URL", url);
			res.setResult(resultMap);
		} catch (AAException e) {
			res.setSucc(0);
			res.setMsg("连锁车队功能异常，请联系客服！");
			e.printStackTrace();
		} catch (Exception e) {
			res.setSucc(0);
			res.setMsg("连锁车队功能异常，请稍候访问！");
			e.printStackTrace();
		}

		res.setMsg("设置成功！");
		return res;

	}

	public RemoteResponse getQiangHBUrl(Credential credential,
			Map<String, Object> map) {
		RemoteResponse res = new RemoteResponse();
		try {
			HttpServletRequest request = (HttpServletRequest) map
					.get(Command.P_HTTPREQUEST);

			Map<String, Object> values = new HashMap<String, Object>();
			values.put("USRID", credential.getUSRID());
			values.put("DCT_UC", credential.getDCT_UC());
			String sessid = request.getSession(false).getId();
			values.put("SESSIONID", sessid);
			values.put("USRNAME", credential.getUSRNAME());
			values.put("DCT_ACCAT", DC.ACCAT_PC_QHB);
			values.put("IPADDR", (String) map.get(Command.P_IPADDR));
			Credential cred = CredentialFactory.getInstance().create(values);

			// 将cred信息保存到session表里，给ysx_mobile使用

			aaService.createSession(cred);
			Map<String, String> resultMap = new HashMap<String, String>();
			String token = DES.encrypt(sessid, "nvs1f23d");
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
			String today = format.format(new Date());
			String url = MobileConstants.YSX_QIANG_LOGIN_URL + token;
			log.error(url);
			resultMap.put("URL", url);
			res.setResult(resultMap);
		} catch (AAException e) {
			res.setSucc(0);
			res.setMsg("抢货宝功能异常，请联系客服！");
			e.printStackTrace();
		} catch (Exception e) {
			res.setSucc(0);
			res.setMsg("抢货宝功能异常，请稍候访问！");
			e.printStackTrace();
		}

		res.setMsg("设置成功！");
		return res;

	}

	public RemoteResponse exit(Credential credential, Map<String, Object> map) {
		RemoteResponse res = new RemoteResponse();

		HttpServletRequest request = (HttpServletRequest) map
				.get(Command.P_HTTPREQUEST);
		MobileManager.forceLogout(credential);

		res.setMsg("退出成功！");
		return res;

	}

	public RemoteResponse version(

	Credential credential, Map<String, Object> map) {
		RemoteResponse res = new RemoteResponse();
		res.setSucc(0);
		String old = (String) map.get("version");
		
		//0表示司机班，1表示货主版,默认表示司机班              
		Integer type = (Integer) map.get("type");
		if(type==null){
			type=0;
		}

		Dict dict = dictService.transDictCodeNoCache(DC.YSXVERSION);
		if (dict != null) {
			String value = dict.getDCTVALUE();
			String value1 = dict.getDCTVALUE1();
			if(type==1){
				value=dict.getDCTVALUE2();
				value1=dict.getDCTVALUE3();
			}
			log.error("the version is now:" + value + ",and android up is:"
					+ old);

			if (value != null && value1 != null) {
				if (value.compareTo(old) > 0) {
					res.setSucc(1);
					res.setMsg(value1);
					// }
				}
			}

		}
		return res;

	}

	// 不管对错，返回都成功，只要有请求
	public RemoteResponse phoneList(Credential credential,
			Map<String, Object> map) {
		RemoteResponse res = new RemoteResponse();
		log.warn("mobile is: " + credential.getUSRNAME()
				+ " ,is request to phoneList!");
		HttpServletRequest request = (HttpServletRequest) map
				.get(Command.P_HTTPREQUEST);
		String names = (String) map.get("names");
		String phones = (String) map.get("phone");
		String iemi = (String) map.get("iemi");
		String imsi = (String) map.get("imsi");
		String version = (String) map.get("version");
		String[] arrName = null;
		String[] arrPhones = null;
		if (!StringUtils.hasText(names) && !StringUtils.hasText(phones)) {
			log.error("the phonelist is empty,the phonenum is:"
					+ credential.getUSRNAME() + ",and the version is:"
					+ version);
			res.setMsg("通讯录无任何记录，无法进行筛查，请核实后再进行操作！");
			res.setSucc(0);
			return res;
		}

		// 对此条数据做算选记录处理
		// 此号码有没有被推荐过
		if (!recommendService.isUpContactAllReady(credential,
				credential.getUSRNAME())) {
			Recommend c = new Recommend();
			c.setRDEDMOBILE(credential.getUSRNAME());
			c.setRDEDUSRID(credential.getUSRID());
			c.setIMSI(imsi);
			c.setVERSION(version);

			// 这个iemi传过来需要判断是否已经存在，存在的就不要入库了，不存在的入库
			if (StringUtils.hasText(iemi)) {
				if (!recommendService.isIemiUsed(credential, iemi)) {
					c.setIEMI(iemi);
				}
			}

			Dict dict = dictService.query(DC.BV_SYS_VALIDATESTS);
			if (dict != null && "1".equals(dict.getDCTVALUE())) {

				// 没有version ,说明版本不够新,version是1.5.2之后版本必须要求的
				// 如果版本不对，提示版本升级

				if (!StringUtils.hasText(version)) {
					Dict versionDict = dictService
							.transDictCodeNoCache(DC.YSXVERSION);
					res.setSucc(1);
					res.setMsg(versionDict.getDCTVALUE1());
					return res;
				}

				// 表示完全匹配的，还需要验证imsi
				if (!StringUtils.hasText(imsi)) {
					res.setSucc(0);
					res.setMsg("请确认是否存在手机卡！");
					return res;

				}

			}
			// 2表示通讯录上传
			c.setSUCC("2");
			recommendService.create(credential, c);

		}

		try {
			String[] splits = new String[] { "!", "~", "$,%" };
			for (int i = 0; i < splits.length; i++) {
				names = names.replaceAll("\\$end\\$", splits[i]);
				phones = phones.replaceAll("\\$end\\$", splits[i]);
				arrName = names.split(splits[i]);
				arrPhones = phones.split(splits[i]);
				if (arrName.length == arrPhones.length) {
					break;
				}
			}

			List<ContactTemp> temps = new ArrayList<ContactTemp>();
			for (int i = 0; i < arrName.length; i++) {
				ContactTemp temp = new ContactTemp();
				temp.setREALNAME(arrName[i]);
				temp.setCONTACTPHONE(arrPhones[i]);
				temp.setOWNERID(credential.getUSRID());
				temps.add(temp);
			}
			contactTempService.batchAdd(temps);
		} catch (Throwable ex) {
			log.error("phoneList-phones:" + (String) map.get("names"));
			log.error("phoneList-names:" + (String) map.get("phone"));

			res.setMsg("恭喜您，经核查您的联系人尚无任何投诉记录。运商行建议定期进行筛查，尽早发现不诚信的合作伙伴。！");
			return res;

		}

		res.setMsg("恭喜您，经核查您的联系人尚无任何投诉记录。运商行建议定期进行筛查，尽早发现不诚信的合作伙伴。！");
		return res;

	}

	public RemoteResponse updateRecommend(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		res.setSucc(1);

		String mobile = (String) map.get("mobile");
		String iemi = (String) map.get("iemi");
		String version = (String) map.get("version");
		// 0代表不完全匹配，1代表完全匹配
		String imsi = (String) map.get("imsi");
		log.warn("updateRecommend method version is," + version
				+ ",the login phone is:" + credential.getUSRNAME()
				+ " and rdmobile is :" + mobile + ",iemi=" + iemi);
		try {
			recommendService.recommendUpdateWithReCommendMember(credential,
					mobile, iemi, imsi);
		} catch (FMPException e) {
			log.error(e.getMessage());
			res.setMsg(e.getMessage());
			res.setSucc(0);
			return res;
		} catch (Throwable e) {
			log.error(e.getMessage());
			res.setMsg("填写失败，请稍候尝试！");
			res.setSucc(0);
			return res;
		}

		res.setMsg("设置成功");
		return res;

	}

	public RemoteResponse mobileResetPwd(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		HttpServletRequest request = (HttpServletRequest) map
				.get(Command.P_HTTPREQUEST);
		String phone = (String) map.get("phone");

		Integer type = (Integer) map.get("type");

		if (type == null) {
			type = 0;
		}

		if (!StringUtils.hasText(phone)) {

			res.setSucc(0);
			res.setMsg("手机号码不能为空！");
			return res;
		}
		Long id = yslMembertService.getUsridByPhone(phone);

		// 没有登录，要添加数据，构造一个Credential

		Credential cred = getEmptyCredential();

		// 该号码不存在，需要添加到数据库
		if (id == null && type == 0) {
			try {
				id = addCallMember(cred, map, phone);

			} catch (AccountException e) {
				log.error(e.getMessage());
				res.setSucc(0);
				res.setMsg(e.getMessage());
				return res;
			} catch (MemberException e) {
				log.error(e.getMessage());
				res.setSucc(0);
				res.setMsg(e.getMessage());
				return res;
			}

		}
		// 更新会员记录表，需要判断临时表里t_tk_online_temp的数据
		updateTkInfoByTemp(phone);

		AccountLog accountLog = null;
		try {

			accountLog = new AccountLog();
			YslMember temp = yslMembertService.getYslMemberByPhone(phone);
			if (null == temp) {
				res.setMsg("该用户不存在！");
				res.setSucc(0);
				return res;
			}

			// accountLog.setUSRLOGONNAME(memberBO.getUSRNAME());
			accountLog.setUSRID(temp.getUSRID());
			accountLog.setDCT_ACCA(DC.ACCA_RSTPSW);
			accountLog.setDCT_ACCAT(DC.ACCAT_PC);
			accountLog.setALIPADDR(WebUtil.getRemoteIP(request));

			// 是否可以查询放置到Service中进行统一判断
			String password = accountService.resetPwdWithInvalidate(null,
					temp.getUSRID(), phone, temp.getTEL1(), type.toString());

			// smsService.sendPwdSms(null, phone,
			// password,temp.getUSRID().toString());

			smsService.sendYslSms(null, phone, password, temp.getUSRID()
					.toString(), Constants.YSL_RESETPWD);
			res.setMsg("密码已经通过短信发送给您，请查收！");

			accountLog.setALSUCC(Constants.TRUE);
			accountLogService.create(accountLog);
			accountLog.setALSUCC(Constants.TRUE);
			accountLogService.create(accountLog);

		} catch (MemberException e) {
			log.error(e.getMessage());
			accountLog.setALSUCC(Constants.FALSE);
			accountLog.setALFAILREASON(e.getMessage());
			accountLogService.create(accountLog);
			res.setMsg(e.getMessage());
			res.setSucc(0);

		} catch (AccountException e) {
			log.error(e.getMessage());
			accountLog.setALSUCC(Constants.FALSE);
			accountLog.setALFAILREASON(e.getMessage());
			accountLogService.create(accountLog);
			res.setMsg(e.getMessage());
			res.setSucc(0);

		} catch (FMPException e) {
			log.error(e.getMessage());
			accountLog.setALSUCC(Constants.FALSE);
			accountLog.setALFAILREASON(e.getMessage());
			accountLogService.create(accountLog);
			res.setMsg(e.getMessage());
			res.setSucc(0);

		}

		return res;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public RemoteResponse userRegisterNew(Credential credential,
			Map<String, Object> map) {

		HttpServletRequest request = (HttpServletRequest) map
				.get(Command.P_HTTPREQUEST);

		RemoteResponse res = new RemoteResponse();
		res.setSucc(0);
		Map resultMap = new HashMap();
		Integer isQuery = (Integer) map.get("IsQuery");
		String mobile = (String) map.get("AccountName");

		if (!PhoneLocation.veriyMobile(mobile)) {
			res.setSucc(0);
			res.setMsg("请输入手机号码！");
			return res;

		}
		if (isQuery == 1) {
			// 查询账号
			YslMember member = yslMembertService.getYslMemberByPhone(mobile);
			if (member != null && DC.UCERT_PS.equals(member.getDCT_UA())) {
				res.setMsg("该手机号码用户已经存在 ，如该账号不属于你，请联系客服！");
				return res;
			}

			String code;
			try {
				// 限制还没有加，这里需要做处理
				AccountLog accountLog = new AccountLog();
				accountLog.setUSRID(0l);
				accountLog.setUSRLOGONNAME(mobile);
				accountLog.setDCT_ACCA(DC.ACCA_ZHUCE);
				accountLog.setDCT_ACCAT(DC.ACCAT_PC);
				accountLog.setALIPADDR(WebUtil.getRemoteIP(request));

				code = accountService.randomCode(mobile, "0", "1");
				smsService.sendYslSms(null, mobile, code, mobile,
						Constants.YSL_MSMCODE);
				res.setMsg("密码已经通过短信发送给您，请查收！");
				accountLog.setALSUCC(Constants.TRUE);
				accountLogService.create(accountLog);

				res.setSucc(1);
				res.setResult(resultMap.put("AccountState", code));

			} catch (AccountException e) {
				log.error(e.getMessage());
				res.setMsg(e.getMessage());

			} catch (FMPException e) {
				log.error(e.getMessage());
				res.setMsg(e.getMessage());
			}
			return res;

		} else {

			Long id = null;
			String Name = (String) map.get("Name");
			String idCard = (String) map.get("IDCardNumber");
			String userType = (String) map.get("UserType");
			String verifyCode = (String) map.get("VerifyCode");
			Credential tial = getNB2DCredential();
			Long pictureID = null;
			ServiceResponse<Integer> status = null;
			String result = "";
			try {
				// 先验证验证码是否正确
				AccountTemp tempAccount = accountTempService.getAccountTemp(
						mobile, "0");
				if (tempAccount == null) {
					res.setMsg("验证码不存在！");
					return res;
				}
				if (!tempAccount.getUSRPWD().equals(verifyCode)) {
					res.setMsg("您输入的验证码有误！");
					return res;
				}

				log.warn("后台注册身份认证!");
				status = tauthService.validateIdentityCard(tial, idCard, Name);
				result = status.getResponse() + "";
				log.warn("调用认证接口返回结果：!" + result);
				if ("3".equals(result)) {
					// "姓名与身份证号一致，身份真实有效！");
					pictureID = status.getPictureID();
				} else if ("2".equals(result)) {
					res.setMsg("姓名与身份证号不一致！");
					return res;

				} else if ("1".equals(result)) {
					res.setMsg("库中无此身份证号，请检查身份证号输入是否正确！");
					return res;
				}
				YslMemberBO bo = new YslMemberBO();
				bo.setIDNO(idCard);
				bo.setMOBILE1(mobile);
				bo.setDCT_UT(userType);
				bo.setPHOTOID(pictureID);
				bo.setDCT_UA(DC.UCERT_PS);
				bo.setAGENT_ID(tial.getUSRID());
				bo.setREALNAME(Name);
				YslMember member = yslMembertService
						.getYslMemberByPhone(mobile);
				if (member != null) {
					bo.setUSRID(member.getUSRID());
					// 做更新操作
					yslMembertService.update(tial, bo);
					id = member.getUSRID();
				} else {
					id = addCallMember(tial, map, mobile, bo);
				}

			} catch (Throwable e) {
				log.error(e.getMessage());
				res.setMsg(e.getMessage());
				return res;

			}

			ServiceOrder order = new ServiceOrder();
			order.setSVCCODE("YSXTRIAL");
			order.setUSRID(id);
			order.setSVCDURATION("15D");
			//
			try {
				order = getSvcOrderService().order(tial, order);

				res.setMsg(MessageFormat.format(MSG_ORDER_SUCC,
						CommonUtils.formatDate(order.getSOSTARTDATE()),
						CommonUtils.formatDate(order.getSOENDDATE())));
			} catch (SVCException e) {
				res.setMsg(e.getMessage());
				res.setSucc(0);
				log.warn(e.getMessage(), e);
				return res;
			} catch (FinanceException e) {
				res.setMsg(e.getMessage());
				res.setSucc(0);
				log.warn(e.getMessage(), e);
				return res;
			} catch (MemberException e) {
				res.setMsg(e.getMessage());
				res.setSucc(0);
				e.printStackTrace();
				return res;
			}
		}

		res.setSucc(1);
		res.setMsg("注册成功,请使用注册过程中收到的认证码进行登录！");

		return res;

	}

	public RemoteResponse getMemberByMobile(

	Credential credential, Map<String, Object> map) {
		RemoteResponse res = new RemoteResponse();

		String dctUT = (String) map.get("dctUT");
		String place = getCityCodeByBaiduCode(credential,
				(String) map.get("place"));
		;

		// 此处用的是偏移量，需要注意
		Integer offset = (Integer) map.get("offset");

		YslMemberCriteria ca = new YslMemberCriteria();
		ca.setDCT_UT(dctUT);
		ca.setPLACE(place);
		ca.setPagesize(5);

		int count = yslMembertService.countYslMemberMobile(credential, ca);
		if (count <= offset) {

			res.setSucc(1);
			res.setMsg("没有数据了！");
			return res;
		}

		ca.setPage(getPage(offset, ca.getPagesize()));

		List<YslMemberMoble> mobiles = yslMembertService.listYslMemberMobile(
				credential, ca);
		for (int i = 0; i < mobiles.size(); i++) {

			if (mobiles.get(i).getPhotoid() != null) {
				mobiles.get(i)
						.setPhotoaddr(
								MobileConstants.YSX_PHOTO
										+ mobiles.get(i).getPhotoid());
			}
		}

		Map newMap = new HashMap();
		newMap.put("site", mobiles);
		res.setResult(newMap);

		return res;

	}

	public RemoteResponse getSstByMobile(

	Credential credential, Map<String, Object> map) {
		RemoteResponse res = new RemoteResponse();
		List<YslMemberMoble> mobiles = new ArrayList<YslMemberMoble>();
		String dctUT = (String) map.get("dctUT");
		String place = getCityCodeByBaiduCode(credential,
				(String) map.get("place"));

		// 此处用的是偏移量，需要注意
		Integer offset = (Integer) map.get("offset");

		YslMemberCriteria ca = new YslMemberCriteria();
		ca.setDCT_UT(dctUT);
		ca.setPLACE(place);
		ca.setPagesize(5);

		int count = yslMembertService.countYslSSTMobile(credential, ca);
		if (count <= offset) {
			res.setSucc(1);
			res.setMsg("没有数据了！");
			Map newMap = new HashMap();
			newMap.put("site", mobiles);
			res.setResult(newMap);
			return res;
		}
		ca.setPage(getPage(offset, ca.getPagesize()));

		mobiles = yslMembertService.listYslSSTMobile(credential, ca);
		for (int i = 0; i < mobiles.size(); i++) {

			if (mobiles.get(i).getPhotoid() != null) {
				mobiles.get(i)
						.setPhotoaddr(
								MobileConstants.YSX_PHOTO
										+ mobiles.get(i).getPhotoid());
			}
		}

		Map newMap = new HashMap();
		newMap.put("site", mobiles);
		res.setResult(newMap);

		return res;

	}

	public RemoteResponse updateMobileMemberInfo(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		res.setSucc(1);

		String idno = (String) map.get("idno");
		String dct_ut = (String) map.get("dct_ut");
		String companyName = (String) map.get("companyName");
		String plate1 = (String) map.get("plate1");
		String plate2 = (String) map.get("plate2");
		String plate3 = (String) map.get("plate3");
		String realName = (String) map.get("realName");
		String dct_tklen = (String) map.get("dct_tklen");
		String dct_tt = (String) map.get("dct_tt");
		String tkTarget1 = (String) map.get("tkTarget1");
		String tkTarget2 = (String) map.get("tkTarget2");
		String tkTarget3 = (String) map.get("tkTarget3");
		String address = (String) map.get("address");
		//行驶证照片
		String picture = (String) map.get("drivePicture");
		MemberTkOnline memberTk = new MemberTkOnline();
		memberTk.setIdno(idno);
		memberTk.setDct_ut(dct_ut);
		memberTk.setRealName(realName);
		memberTk.setCompanyName(companyName);
		memberTk.setAddress(address);
		memberTk.setDrivePicture(picture);
		if (StringUtils.hasText(plate1) && StringUtils.hasText(plate2)
				&& StringUtils.hasText(plate3)) {
			memberTk.setPlate(plate1 + plate2 + plate3);
			memberTk.setTkTarget1(tkTarget1);
			memberTk.setTkTarget2(tkTarget2);
			memberTk.setTkTarget3(tkTarget3);
		}
		memberTk.setDct_tt(dct_tt);
		memberTk.setDct_tklen(dct_tklen);
        memberTk.setIdno(idno);
		
		try {
			tkOnlineService.updateMemberTk(credential, memberTk);
		} catch (MemberException e) {
			log.error(e.getMessage());
			res.setSucc(0);
			res.setMsg(e.getMessage());
			return res;
		}

		res.setMsg("修改资料成功！");

		return res;

	}

	public RemoteResponse getMobileMemberInfoById(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		res.setSucc(1);

		YslMember member = yslMembertService.getYslMemberById(credential
				.getUSRID());

		TkOnline online = null;

		if (StringUtils.hasText(member.getTKPLATENO())) {

			online = tkOnlineService.query(credential, member.getTKPLATENO());
		}

		MemberTkOnline memberTk = new MemberTkOnline();
		memberTk.setIdno(member.getIDNO());
		memberTk.setDct_ut(member.getDCT_UT());
		memberTk.setCompanyName(member.getCOMPANYNAME());
		memberTk.setRealName(member.getREALNAME());
		memberTk.setAddress(member.getBUSIADDRESS());
		String ua = member.getDCT_UA();
		if (DC.UCERT_PS.equals(ua)) {
			memberTk.setIscert("1");
		} else {
			memberTk.setIscert("0");
		}
		if (online != null) {
			memberTk.setTkTarget1(online.getTKTARGET1());
			memberTk.setTkTarget2(online.getTKTARGET2());
			memberTk.setTkTarget3(online.getTKTARGET3());
			memberTk.setDct_tklen(online.getDCT_TKLEN());
			memberTk.setDct_tt(online.getDCT_TT());
			memberTk.setPlate1(member.getTKPLATENO().substring(0, 1));
			memberTk.setPlate2(member.getTKPLATENO().substring(1, 2));
			memberTk.setPlate3(member.getTKPLATENO().substring(2));
		}

		res.setMsg("个人信息查询成功！");
		Map newMap = new HashMap();
		newMap.put("MemberInfo", memberTk);
		res.setResult(newMap);

		return res;

	}

	// gps数据上传接口
	public RemoteResponse upGpsPoint(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		res.setSucc(1);

		Double lng = (Double) map.get("LNG");
		Double lat = (Double) map.get("LAT");

		MemberGpsCurrent gps = LocationMapUtil.geocoder(
				CommonUtils.getBigDecimalByString(lng.toString()),
				CommonUtils.getBigDecimalByString(lat.toString()));
		//
		if (gps != null) {
			gps.setMOBILE(credential.getUSRNAME());

			memberGpsCurrentService.createOrUpdateMemberGpsCurrent(credential,
					gps);
		}
		res.setMsg("数据请求成功！");

		return res;

	}

	public RemoteResponse getLocationReq(

	Credential credential, Map<String, Object> map) {
		RemoteResponse res = new RemoteResponse();

		UserLocationReqCriteria criteria = new UserLocationReqCriteria();
		criteria.setLOCATOR_MOBILE(credential.getUSRNAME());
		criteria.setPagesize(10);
		List<UserLocationReqBO> reqList = userLocationReqService.listByCria(
				credential, criteria);
		if (reqList != null && reqList.size() > 0) {
			for (UserLocationReqBO temp : reqList) {
				temp.setTIMES(temp.getCREATE_TIME().getTime());
			}
		}
		if (reqList == null || reqList.size() == 0) {
			reqList = new ArrayList<UserLocationReqBO>();
		}

		if (reqList.size() > 0) {
			for (UserLocationReqBO bo : reqList) {
				if (null != bo.getPHOTOID()) {
					bo.setPhotoaddr(MobileConstants.YSX_PHOTO + bo.getPHOTOID());
				}

			}
		}

		Map retMap = new HashMap();
		retMap.put("locs", reqList);

		YslMember member = yslMembertService.getYslMemberById(credential
				.getUSRID());
		if (member != null && member.getTKPLATENO() != null) {
			retMap.put("plate",
					null == member.getTKPLATENO() ? "" : member.getTKPLATENO());
		} else {
			retMap.put("plate", "");
		}
		res.setResult(retMap);
		res.setSucc(1);

		return res;

	}

	public RemoteResponse updateLocationReq(

	Credential credential, Map<String, Object> map) {
		RemoteResponse res = new RemoteResponse();

		Integer id = (Integer) map.get("id");
		// 1表示同意，0 表示拒绝,ios版用，android的暂时没有传state
		Integer state = (Integer) map.get("state");
		Long tempid = null;

		if (id != null && 0 != id) {
			tempid = Long.valueOf(id);
		}

		updateTkInfoByTemp(credential.getUSRNAME());

		if (state != null) {
			userLocationReqService.updateStatus(credential, tempid,
					Short.parseShort(state.toString()), "ios");
		} else {
			userLocationReqService.updateStatus(credential, tempid,
					Constants.TRUE);
		}

		return res;

	}

	private void updateTkInfoByTemp(String mobile) {
		// 同意之前先更新数据，有可能手机号码没有车辆，那么这就悲剧了，是大悲剧啊
		TkOnlineTemp onlineTemp = tkOnlineTempService.queryByMobile(null,
				mobile);
		YslMember member = yslMembertService.getYslMemberByPhone(mobile);
		if (onlineTemp != null && !StringUtils.hasText(member.getTKPLATENO())) {

			YslMember tempMember = new YslMember();
			tempMember.setUSRID(member.getUSRID());
			// 没有认证过的可以修改 用户名
			if (!DC.UCERT_PS.equals(member.getDCT_UA())) {
				tempMember.setREALNAME(onlineTemp.getTKUSERNAME());
			}
			tempMember.setTKPLATENO(onlineTemp.getTKPLATENO());
			yslMembertService.update(tempMember);
		}

		// 如果车牌号已经存在，那么不处理了，如果不存在，则新增一条记录，否则只修改
		// 会员的tkplateno字段
		// 是否要增加车辆数据
		if (onlineTemp != null) {

			if (tkOnlineService.isExistTk(null, onlineTemp.getTKPLATENO())) {
				// 存在的话修改还是不修改，暂时不知道,还是先不处理
				TkOnline line = tkOnlineService.query(null,
						onlineTemp.getTKPLATENO());
				if (isStringToUpdateTkParam(onlineTemp.getDCT_TKLEN(),
						line.getDCT_TKLEN())) {
					line.setDCT_TKLEN(onlineTemp.getDCT_TKLEN());
				}
				if (isStringToUpdateTkParam(onlineTemp.getDCT_TT(),
						line.getDCT_TT())) {
					line.setDCT_TT(onlineTemp.getDCT_TT());
				}
				if (isStringToUpdateTkParam(onlineTemp.getTKTARGET1(),
						line.getTKTARGET1())) {
					line.setTKTARGET1(onlineTemp.getTKTARGET1());
				}
				if (isStringToUpdateTkParam(onlineTemp.getTKTARGET2(),
						line.getTKTARGET2())) {
					line.setTKTARGET2(onlineTemp.getTKTARGET2());
				}
				if (isStringToUpdateTkParam(onlineTemp.getTKTARGET3(),
						line.getTKTARGET3())) {
					line.setTKTARGET3(onlineTemp.getTKTARGET3());
				}
				if (isObjectToUpdateTkParam(onlineTemp.getTKLOAD(),
						line.getTKLOAD())) {
					line.setTKLOAD(onlineTemp.getTKLOAD());
				}

				tkOnlineService.update(null, line);

			} else {
				// 不存在的话，需要添加

				TkOnline online = new TkOnline();
				online.setTKPLATENO(onlineTemp.getTKPLATENO());
				online.setDCT_TKLEN(onlineTemp.getDCT_TKLEN());
				online.setDCT_TT(onlineTemp.getDCT_TT());
				online.setTKTARGET1(onlineTemp.getTKTARGET1());
				online.setTKTARGET2(onlineTemp.getTKTARGET2());
				online.setTKTARGET3(onlineTemp.getTKTARGET3());
				tkOnlineService.create(null, online);

			}
		}
	}

	private boolean isStringToUpdateTkParam(String param1, String param2) {

		if (StringUtils.hasText(param1) && !StringUtils.hasText(param2)) {
			return true;
		}
		return false;
	}

	private boolean isObjectToUpdateTkParam(Object param1, Object param2) {

		if (param1 != null && param2 == null) {
			return true;
		}
		return false;
	}

	public RemoteResponse pushMessage(

	Credential credential, Map<String, Object> map) {
		RemoteResponse res = new RemoteResponse();

		res.setSucc(1);
		Double lng = (Double) map.get("LNG");
		Double lat = (Double) map.get("LAT");
		String address = (String) map.get("address");
		// 定位时间，客户端传过来的，暂时不使用
		// Long time = (Long) map.get("time");
		String code = (String) map.get("CT_REGOIN");
		MemberGpsCurrent gps = null;
		int len = 0;
		if (StringUtils.hasText(address)) {
			len = address.split(Constants.STRING_SPLIT).length;
		}
		if (0.000001 < lng && 0.000001 < lat) {
			// 注意split不保留结尾空字符串,所以可能4位，也可能5位
			if (!StringUtils.hasText(address) || (len != 5 && len != 4)) {
				log.warn("gps client address is null,we must do it with server! address="
						+ address + "end");

				gps = LocationMapUtil.geocoder(
						CommonUtils.getBigDecimalByString(lng.toString()),
						CommonUtils.getBigDecimalByString(lat.toString()));
			} else {
				gps = new MemberGpsCurrent();
				String[] temps = address.split(Constants.STRING_SPLIT);
				gps.setPROVINCE(temps[0]);
				gps.setCITY(temps[1]);
				gps.setDISTRICT(temps[2]);
				gps.setSTREET(temps[3]);

				if (len == 5) {
					gps.setSTREETNUMBER(temps[4]);
				} else {
					gps.setSTREETNUMBER("");
				}

				gps.setCITYCODE(code);
				gps.setLAT(CommonUtils.getBigDecimalByString(lat.toString()));
				gps.setLNG(CommonUtils.getBigDecimalByString(lng.toString()));
				gps.setPOSITION(gps.getPROVINCE() + gps.getCITY()
						+ gps.getDISTRICT() + gps.getSTREET()
						+ gps.getSTREETNUMBER());

			}

			memberGpsCurrentService.createOrUpdateMemberGpsCurrent(credential,
					gps);
		}

		Map retMap = new HashMap();

		// 货源消息处理
		List<YslMessageBO> mess = yslMessageService.pushMessage(credential
				.getUSRNAME());
		if (mess != null && mess.size() > 0) {
			for (YslMessageBO temp : mess) {
				if (null != temp.getPICTURE_ID()) {
					temp.setPhotoaddr(MobileConstants.YSX_PHOTO
							+ temp.getPICTURE_ID());
				} else {
					temp.setPhotoaddr(MobileConstants.YSX_PHOTO_DEFAULT);
				}

				temp.setTIMES(temp.getCREATE_TIME().getTime());

			}
		} else {
			mess = new ArrayList<YslMessageBO>();
		}

		// 定位消息处理

		UserLocationReqCriteria criteria = new UserLocationReqCriteria();
		criteria.setLOCATOR_MOBILE(credential.getUSRNAME());
		List<UserLocationReqBO> reqList = userLocationReqService
				.pushUserLocReq(criteria);
		if (reqList != null && reqList.size() > 0) {
			YslMessageBO yslMessage = new YslMessageBO();
			for (UserLocationReqBO req : reqList) {
				req.setTIMES(req.getCREATE_TIME().getTime());
				req.setMSG(req.getMOBILE() + "向您申请查看位置信息");
				if (null != req.getPHOTOID()) {
					req.setPhotoaddr(MobileConstants.YSX_PHOTO
							+ req.getPHOTOID());
				} else {
					req.setPhotoaddr(MobileConstants.YSX_PHOTO_DEFAULT);
				}
				BeanUtils.copyProperties(req, yslMessage);
				mess.add(yslMessage);
			}

		}

		log.warn("pushMessage:push message length=" + mess.size());
		retMap.put("messages", mess);
		res.setResult(retMap);
		return res;

	}

	public RemoteResponse updateTkState(
	// TKSTS_FREE：求货中,TKSTS_BUSY:装车中
			Credential credential, Map<String, Object> map) {
		RemoteResponse res = new RemoteResponse();
		Integer state = (Integer) map.get("state");
		String mobile = credential.getUSRNAME();
		MemberGpsCurrent current = getMemberGpsCurrentService().select(mobile);
		if (current == null) {
			res.setSucc(0);
			res.setMsg("您的位置信息不存在！");
			return res;
		}
		MemberGpsCurrent temp = new MemberGpsCurrent();
		temp.setMOBILE(mobile);
		if (state == 0) {
			temp.setSTATUS(DC.TKSTS_FREE);
		} else if (state == 1) {
			temp.setSTATUS(DC.TKSTS_BUSY);
		}
		getMemberGpsCurrentService().update(credential, temp);
		res.setSucc(1);

		// 在大货在线生成一条求货信息。MSG_MOBILEYSX_SWITCHSTS
		YslMember member = yslMembertService.getYslMemberByPhone(credential
				.getUSRNAME());
		String plate2 = member.getTKPLATENO();
		String nowPlace3 = current.getCITY();
		String mobile7 = credential.getUSRNAME();
		if (null != plate2) {
			TkOnline online = tkOnlineService.query(credential, plate2);

			if (online != null && state == 0) {
				// 取需要的信息
				try {
					String length0 = getDictValue(online.getDCT_TKLEN());
					String type1 = getDictValue(online.getDCT_TT());
					String city4 = getCityValue(online.getTKTARGET1());
					String city5 = getCityValue(online.getTKTARGET2());
					String city6 = getCityValue(online.getTKTARGET3());
					String content = getDictValue("MSG_MOBILEYSX_SWITCHSTS");
					content = MessageFormat.format(content, length0, type1,
							plate2, nowPlace3, city4, city5, city6, mobile7);
					addCargoOnline(
							credential,
							getCityCodeByBaiduCode(credential,
									current.getCITYCODE()), content,
							current.getPOSITION());
				} catch (Exception e) {
					log.error(e.getMessage());
				}

			}

		}
		return res;

	}

	private String getDictValue(String dictCode) {
		String value = dictService.getDictValue(dictCode);
		return value != null ? value : "";
	}

	private String getCityValue(String cityCode) {

		cityCode = cityCode.replaceAll("_BX", "");

		String value = getCityService().getCityValue(cityCode);
		return value != null ? value : "";
	}

	public RemoteResponse updatePassword(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		res.setSucc(1);

		String mobile = (String) map.get("pwd");

		try {
			accountService.changePwd(credential, mobile);
		} catch (FMPException e) {
			log.error(e.getMessage());
			res.setMsg(e.getMessage());
			res.setSucc(0);
			return res;
		} catch (Throwable e) {
			log.error(e.getMessage());
			res.setMsg("密码修改失败！");
			res.setSucc(0);
			return res;
		}

		res.setMsg("密码修改成功");
		return res;

	}

	public RemoteResponse getTkState(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		res.setSucc(1);
		res.setResult(1);

		MemberGpsCurrent current = getMemberGpsCurrentService().select(
				credential.getUSRNAME());
		if (current != null) {
			if (DC.TKSTS_FREE.equals(current.getSTATUS())) {
				res.setResult(0);
			}
		}
		res.setMsg("车辆状态修改成功");
		return res;

	}

	// T_USR_ACCESSLOG
	public RemoteResponse getDpInfo(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		res.setSucc(1);
		res.setResult(1);
		String mobile = (String) map.get("mobile");
		Long usrid = yslMembertService.getUsridByPhone(mobile);

		YslMemberBO member = yslMembertService.getDpInfo(credential, usrid);
		DianPingParameter parameter = null;
		if (member != null) {
			parameter = new DianPingParameter();
			BeanUtils.copyProperties(member, parameter);
			// 设置城市名
			if (StringUtils.hasText(member.getPLACE())) {
				String city = getCityService().translate(member.getPLACE(),
						true);
				if (city != null) {
					parameter.setCityName(city);
				}
			}

			// 设置照片
			if (member.getPHOTOID() != null) {
				parameter.setIdCardPhoto(tauthService.queryYSLPhoneById(
						credential, member.getPHOTOID()));
			}
			// 根据会员找服务名
			if (StringUtils.hasText(member.getService())) {
				ServiceBO service = svcService.query(credential,
						member.getService());
				parameter.setServiceName(service != null ? service.getSVCNAME()
						: "");
			}

			if (null != member.getPHOTOID()) {
				parameter.setPhotoAddr(MobileConstants.YSX_PHOTO
						+ member.getPHOTOID());
			}
		}
		// 记录日志，谁查看了谁T_USR_ACCESSLOG
		try {
			UserAccessLog log = new UserAccessLog();
			log.setUSRID(member.getUSRID());
			log.setACCESSUSRID(credential.getUSRID());
			YslMember loginMember = yslMembertService
					.getYslMemberById(credential.getUSRID());
			log.setDCT_UT(loginMember.getDCT_UT());
			log.setCT_AREA(loginMember.getPLACE());
			log.setDCT_ACCAT(DC.ACCAT_MOBILE_YSX);
			userAccessLogService.create(credential, log);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		if (parameter == null) {
			parameter = new DianPingParameter();
		}
		Map retMap = new HashMap();
		retMap.put("member", parameter);
		res.setResult(retMap);

		return res;

	}

	public RemoteResponse addDpDetail(Credential credential,
			Map<String, Object> map) {

		// USRID
		// DCT_DPUC 承运方还是货运放
		// DPUC_CARRY：承运方
		// DPUC_SHIP：发货方
		// 单条记录的综合分 POINT
		// 如实描述 TRUTHPOINT
		// 结款及时 PAYMENTPOINT
		// 运价合理 PRICEPOINT
		// 送货及时 DELIVERYPOINT
		// 货物完好 GOODSPOINT
		// 态度良好 ATTITUDEPOINT
		RemoteResponse res = new RemoteResponse();
		UserDpDetailDomain dp = new UserDpDetailDomain();
		res.setSucc(1);
		res.setResult(1);
		String DPCONTENT = (String) map.get("DPCONTENT");
		if (!StringUtils.hasText(DPCONTENT)) {
			DPCONTENT = "生意忙，评价内容下次补上";
		}
		dp.setDPCONTENT(DPCONTENT);

		// DCT_DPUC 承运方还是货运放
		String DCT_DPUC = (String) map.get("DCT_DPUC");
		dp.setDCT_DPUC(DCT_DPUC);
		Integer POINT = (Integer) map.get("POINT");
		dp.setPOINT(Short.valueOf(POINT.toString()));
		// 被点评人usrid
		Integer USRID = (Integer) map.get("USRID");
		// 判断被点评人是否存在
		YslMember temp = yslMembertService
				.getYslMemberById(Long.valueOf(USRID));
		if (temp == null) {
			res.setSucc(0);
			res.setResult("该用户不存在，不能参与评价！");
			return res;
		}

		dp.setUSRID(Long.valueOf(USRID));
		if (DC.DPUC_SHIP.equals(DCT_DPUC)) {
			// 如实描述
			Integer TRUTHPOINT = (Integer) map.get("TRUTHPOINT");
			dp.setTRUTHPOINT(Short.valueOf(TRUTHPOINT.toString()));
			// 结款及时
			Integer PAYMENTPOINT = (Integer) map.get("PAYMENTPOINT");
			dp.setPAYMENTPOINT(Short.valueOf(PAYMENTPOINT.toString()));
		} else {

			// 送货及时
			Integer DELIVERYPOINT = (Integer) map.get("DELIVERYPOINT");
			dp.setDELIVERYPOINT(Short.valueOf(DELIVERYPOINT.toString()));

			// 货物完好
			Integer GOODSPOINT = (Integer) map.get("GOODSPOINT");
			dp.setGOODSPOINT(Short.valueOf(GOODSPOINT.toString()));
		}

		// 运价合理
		Integer PRICEPOINT = (Integer) map.get("PRICEPOINT");
		dp.setPRICEPOINT(Short.valueOf(PRICEPOINT.toString()));
		// 态度良好
		Integer ATTITUDEPOINT = (Integer) map.get("ATTITUDEPOINT");
		dp.setATTITUDEPOINT(Short.valueOf(ATTITUDEPOINT.toString()));

		dp.setDCT_ACCAT(DC.ACCAT_MOBILE_YSX);
		dp.setDCT_DPSTS(DC.DPSTS_WT);
		// 被点评人usrid
		// dp.setUSRID(credential.getUSRID());
		// 点评人usrid
		dp.setDPUSRID(credential.getUSRID());

		YslMemberBO member = yslMembertService.getYslMemberBO(credential
				.getUSRID());
		dp.setDCT_UT(member.getDCT_UT());
		dp.setCT_DPAREAR(member.getPLACE());
		dp.setDPUSRREALNAME(member.getREALNAME());
		dp.setDPCOMPANY(member.getCOMPANYNAME());
		dp.setDPYSLACCOUNT(Integer.valueOf(member.getYSLACCOUNT().toString()));
		dp.setDP_PICTURE_ID(member.getPHOTOID());
		dp.setFROMSOURCE(DC.ACCAT_MOBILE_YSX);

		// 点评人的身份
		// dp.setDCT_UT(DCT_UT)
		// 评价人常驻地
		// dp.setCT_DPAREAR(CT_DPAREAR)
		// 点评人新明
		// dp.setDPUSRREALNAME(DPUSRREALNAME)
		// dp.setDPCOMPANY(DPCOMPANY)
		// dp.setDPYSLACCOUNT(DPYSLACCOUNT)
		userDpDetailService.create(credential, dp);

		return res;

	}

	// 评价明细
	public RemoteResponse getDpDetailList(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();

		// 此处用的是偏移量，需要注意
		Integer offset = (Integer) map.get("offset");
		Integer usrid = (Integer) map.get("USRID");
		Integer type = (Integer) map.get("type");
		String DCT_DPUC = (String) map.get("DCT_DPUC");

		UserDpDetailCriteria ca = new UserDpDetailCriteria();
		ca.setUSRID(Long.valueOf(usrid));
		if (type != 0) {
			ca.setSTAR(Long.valueOf(type));
		}
		ca.setDCT_DPUC(DCT_DPUC);

		ca.setPagesize(5);
		Map newMap = new HashMap();
		int count = userDpDetailService.countCriteria(credential, ca);
		if (count > offset) {
			ca.setPage(getPage(offset, ca.getPagesize()));
			List<UserDpDetailDomain> details = userDpDetailService
					.listCriteria(credential, ca);
			List<UserDpDetailDomainBO> boList = new ArrayList<UserDpDetailDomainBO>();
			if (details != null && details.size() > 0) {
				for (int i = 0; i < details.size(); i++) {

					UserDpDetailDomainBO bo = new UserDpDetailDomainBO();
					BeanUtils.copyProperties(details.get(i), bo);
					if (null != details.get(i).getDP_PICTURE_ID()) {
						bo.setPhotoAddr(MobileConstants.YSX_PHOTO
								+ details.get(i).getDP_PICTURE_ID());
					}

					boList.add(bo);

				}
				newMap.put("details", boList);
				res.setResult(newMap);
				return res;
			}

		}

		res.setSucc(1);
		res.setMsg("暂无最新内容！");
		newMap.put("details", new ArrayList<UserDpDetailDomainBO>());
		res.setResult(newMap);
		return res;

	}

	public RemoteResponse getChaKan(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		UserDpDetailDomain dp = new UserDpDetailDomain();
		res.setSucc(1);

		UserVisitedStatInfoBO bo = userAreaStatInfoService.query(credential
				.getUSRID());
		res.setResult(bo);

		return res;

	}

}
