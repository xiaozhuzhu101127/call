package com.topjet.fmp.yls.command.user;

import java.text.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.topjet.fmp.aa.Credential;
import com.topjet.fmp.aa.domain.Account;
import com.topjet.fmp.aa.domain.AccountLog;
import com.topjet.fmp.aa.service.AccountService;


import com.topjet.fmp.user.service.YslContactService;
import com.topjet.fmp.banyan.service.TAuthService;
import com.topjet.fmp.base.DC;

import com.topjet.fmp.base.domain.PaginatedList;

import com.topjet.fmp.exception.AccountException;
import com.topjet.fmp.exception.MemberException;


import com.topjet.fmp.svc.domain.YslCall;
import com.topjet.fmp.svc.domain.YslCallBO;
import com.topjet.fmp.svc.domain.YslCallCriteria;
import com.topjet.fmp.svc.service.SVCOrderService;
import com.topjet.fmp.svc.service.YslCallService;


import com.topjet.fmp.user.domain.YslContact;

import com.topjet.fmp.user.domain.YslMemberBO;

import com.topjet.fmp.user.service.MemberAdminService;

import com.topjet.fmp.user.service.PointRecordService;
import com.topjet.fmp.user.service.UserComplainService;
import com.topjet.fmp.user.service.YslAccountService;

import com.topjet.fmp.user.service.YslMemberService;

import com.topjet.fmp.util.Constants;
import com.topjet.fmp.yls.RemoteResponse;
import com.topjet.fmp.yls.command.BaseCommand;
import com.topjet.fmp.yls.command.Command;
import com.topjet.fmp.yls.util.CommonUtil;
import com.topjet.fmp.yls.util.IDCardUtil;
import com.topjet.fmp.yls.util.IdcardLocation;
import com.topjet.fmp.yls.util.IpUtil;
import com.topjet.fmp.yls.util.PhoneAreaUtil;
import com.topjet.fmp.yls.util.PhoneLocation;

/**
 * 
 * <pre>
 * Description
 * Copyright:	Copyright (c)2009  
 * Company:		�ؾ��Ƽ�
 * Author:		Dengfp
 * Version:		1.0  
 * Create at:	2012-8-13 ����11:25:24  
 *  
 * �޸���ʷ:
 * ����    ����    �汾  �޸�����
 * ------------------------------------------------------------------
 * 
 * </pre>
 */
@Component("call_command")
public class CallCommand extends BaseCommand {

	private Log log = LogFactory.getLog(CallCommand.class);

	@Autowired
	   private PointRecordService pointRecordService;


	@Autowired
	private AccountService accountService;


	@Autowired
	private YslCallService  yslCallService;



	@Autowired
	private YslAccountService yslAccountService;

;

	
	@Autowired
	private YslMemberService yslMemberService;
	
	@Autowired
	private YslContactService yslContactService;

	// �жϺ������������Ƿ���ͬ
	public boolean isLocationCode(String area, String tel) {
		String areaCode = null;
		//����һ�����Žӿ�
		if(PhoneLocation.veriyMobile(tel)){
			tel=PhoneLocation.formatMobile(tel);
			areaCode=PhoneAreaUtil.getAreaLocation(tel);
			if(areaCode!=null){
				if(areaCode.trim().equals(area.trim())){
					return true;
				}
				
			}
		}
		
		String telCode = PhoneLocation.getLocaleByTel(tel);		
		// ���ŵõ���ַ
		 areaCode = PhoneLocation.getLocaleByArea(area);
		if (areaCode.equals(telCode)) {
			return true;
		}

		return false;

	}

	// ��������,ȥ����Ϣ
	// "CallInOrOut":0/1, //0�Ǻ���绰(����)��1�Ǻ����绰(����) ����绰��ʱ��������粻�ǻ�Ա����Ҫע��ɻ�Ա��
	public RemoteResponse addCallingInfo(Credential credential, Map<String, Object> map) {

		JSONObject jsonObject = JSONObject.fromObject(map.get("CallingInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(jsonObject, Map.class);

		RemoteResponse res = new RemoteResponse();
		YslCall  call =new YslCall();

		Integer usr_id = (Integer) map.get("UserID");

		String phoneNumber = (String) userMap.get("PhoneNumber");
		
		
		if(!StringUtils.hasText(phoneNumber)){
				
				res.setSucc(0);
				res.setMsg("�ֻ����벻��Ϊ�գ�");
				return res;
			}

		

		String startTime = (String) userMap.get("StartTime");
//��0,
		Integer callInOrOut = (Integer) userMap.get("CallInOrOut");
		// ��ע
		String affair = (String) userMap.get("Affair");

		// ͨ���ߵ�ID
		Long otherID = null;

		// �ڽ�����ز���ǰ���Ƚ���һ���жϣ�������ֻ����룬ǰ���0�ģ����Ƚ���ȥ0 ������������Ҫע�����
		// ����Ϊ��ϵ�˵�ʱ����ʲô���룬��Ϊʲô���룬�����ֻ���ȥ0����
		String bindPhone = "";

		if (PhoneLocation.veriyMobile(phoneNumber)) {
			// ������ֻ��Ժ���ȥ0����
			bindPhone = PhoneLocation.formatMobile(phoneNumber);

		} else {
			bindPhone = phoneNumber;
		}
	
		
		Long id = yslMemberService.getUsridByPhone(bindPhone);

		// �ж��Ƿ��ǻ�Ա��������ǣ�����Ҫע��ɻ�Ա��ע������ǰ󶨵ģ���Ҫ�ҳ���¼id		

		if (id==null) {
			try {
				id=addCallMember(credential, map, phoneNumber);
				
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
		// Member memberP = memberAdminService.queryMemberByName(credential,
		// bindPhone + Constants.YSL_USRNAME_SUFFIX);
		Account account = accountService.query(credential, id);
		if (account.getUSRID().equals(credential.getUSRID())) {
			res.setSucc(0);
			res.setMsg("���ܲ�ѯ�Լ�");
			return res;
		}
	
		call.setUSRID(account.getUSRID());

		call.setCREATE_BY(credential.getUSRID());
		// ��ϵ���У���Ļ���ԭ���ĺ��룬������ֻ�������ܴ�0��Ҳ���ܲ���0
		call.setCALLNUM(phoneNumber);

		
		//
		if (callInOrOut == 0) {
			call.setCALLIO(Constants.FALSE);
		} else {
			call.setCALLIO(Constants.TRUE);
		}

//		Date startDate = null;
//		try {
//			startDate = DateUtils.parseDate(startTime, new String[] { "yyyy-MM-dd HH:mm:ss" });
//		} catch (ParseException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		call.setCALLREMARK(affair);
//		call.setCALLSTARTDATE(startDate);
		Long callid = yslCallService.create(credential, call);

		//

		//
		// // Ҫ���л��֣�������ָ�����Ӳ����������ǻ�Ծ����10,ָ������1 ��
//		try {
////			yslAccountService.updateActiveDeg(credential, account == null ? null : account.getUSRID());
//			pointRecordService.addPoint(credential,account.getUSRID(),Constants.POINT_TYPE2,"��绰�ͻ���");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		Map mapParam = new HashMap();

		mapParam.put("callingInfoID", callid);

		res.setResult(mapParam);

		return res;
	}



	// �޸���������
	public RemoteResponse updateCallingInfoEndTime(Credential credential, Map<String, Object> map) {

		JSONObject jsonObject = JSONObject.fromObject(map.get("CallingInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(jsonObject, Map.class);
		System.out.println(userMap);
		RemoteResponse res = new RemoteResponse();

		Integer usr_id = (Integer) map.get("UserID");

		Integer callingInfoID = (Integer) userMap.get("CallingInfoID");

		String endTime = (String) userMap.get("EndTime");

//		Date endDate = null;
//		try {
//			endDate = DateUtils.parseDate(endTime, new String[] { "yyyy-MM-dd HH:mm:ss" });
//		} catch (ParseException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		YslCall call = yslCallService.getYslCallById(Long.valueOf(callingInfoID));
		YslCall userCall = new YslCall();
		userCall.setCALLID(Long.valueOf(callingInfoID));
//		userCall.setCALLENDDATE(endDate);
		yslCallService.update(credential, userCall);
		
		
//		try {
////			yslAccountService.updateActiveDeg(credential, account == null ? null : account.getUSRID());
//			pointRecordService.addPoint(credential,call.getUSRID(),Constants.POINT_TYPE2,"��绰�ͻ���");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return res;
	}

	// �޸���������
	public RemoteResponse modifyCallingAffair(Credential credential, Map<String, Object> map) {

		JSONObject jsonObject = JSONObject.fromObject(map.get("CallingInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(jsonObject, Map.class);
		System.out.println(userMap);
		RemoteResponse res = new RemoteResponse();

		Integer usr_id = (Integer) map.get("UserID");

		String affair = (String) userMap.get("Affair");

		Integer call_id = (Integer) userMap.get("CallingInfoID");

		yslCallService.updateRemark(credential, Long.valueOf(call_id), affair);

		return res;
	}

	// ɾ������
	public RemoteResponse removeCallingInfo(Credential credential, Map<String, Object> map) {

		JSONObject jsonObject = JSONObject.fromObject(map.get("CallingInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(jsonObject, Map.class);
	
		Integer usr_id = (Integer) map.get("UserID");
		RemoteResponse res = new RemoteResponse();
		JSONArray jsonArray = JSONArray.fromObject(userMap.get("CallingInfoID"));
		Object obj[] = jsonArray.toArray();

		long[] ids = new long[obj.length];
		for (int i = 0; i < obj.length; i++) {
			ids[i] = Long.valueOf(obj[i].toString());
		}
		yslCallService.delete(credential, ids);

		return res;
	}

	// ��ȡ������Ϣ(��ȡ��ǰ�û���ָ��ʱ�䷶Χ�ڵ�����������Ϣ)
	public RemoteResponse getCallingInfo(Credential credential, Map<String, Object> map) {

		Integer usrid = (Integer) map.get("UserID");
		RemoteResponse res = new RemoteResponse();
		JSONObject jsonObject = JSONObject.fromObject(map.get("CallingInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(jsonObject, Map.class);

		String startDate = (String) userMap.get("StartDate");
		String endDate = (String) userMap.get("EndDate");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		YslCallCriteria criteria = new YslCallCriteria();
		criteria.setCREATE_BY(credential.getUSRID());
		try {
			if (StringUtils.hasText(startDate)) {
				criteria.setStartDate(format.parse(startDate));
			}
			if (StringUtils.hasText(endDate)) {
				criteria.setEndDate(format.parse(endDate));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		criteria.setPagesize(0);
		PaginatedList<YslCallBO> list = yslCallService.list(credential, criteria);
		if (list == null || list.getResult() == null || list.getResult().size() == 0) {
			res.setMsg("û�����ĵ绰��¼��Ϣ");
			res.setSucc(0);
			return res;
		}
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<CallParameter> callList = new ArrayList<CallParameter>();
		List<YslCallBO> tempList = list.getResult();
		for (int i = 0; i < tempList.size(); i++) {
			CallParameter cp = new CallParameter();

			cp.setPhoneNumber(tempList.get(i).getCALLNUM());
			cp.setPhonePlace(tempList.get(i).getPlace());
			cp.setCallInfoID(tempList.get(i).getCALLID());
			cp.setCallInOrOut(tempList.get(i).getCALLIO());
			cp.setCallerName(tempList.get(i).getREALNAME());
			cp.setCallerID(tempList.get(i).getUSRID());
			cp.setCallerUserType(tempList.get(i).getDCT_UT());
			if (null != tempList.get(i).getCALLSTARTDATE()) {
				cp.setStartTime(format.format(tempList.get(i).getCALLSTARTDATE()));
			}

			if (null != tempList.get(i).getCALLENDDATE()) {
				cp.setEndTime(format.format(tempList.get(i).getCALLENDDATE()));
			}
			if (DC.UCERT_PS.equals(tempList.get(i).getDCT_UA())) {
				cp.setIsCert(Constants.TRUE);
			} else {
				cp.setIsCert(Constants.FALSE);
			}
			cp.setCallcomment(tempList.get(i).getCALLREMARK());
			callList.add(cp);

		}
		Map mapParameter = new HashMap();
		mapParameter.put("callingInfo", callList);
		res.setResult(mapParameter);
		return res;
	}

	public RemoteResponse getTrustInfoByPhone(Credential credential, Map<String, Object> map) {

		String phoneNumber = (String) map.get("PhoneNumber");
		Integer USRID = (Integer) map.get("UserID");
		RemoteResponse res = new RemoteResponse();

		if (PhoneLocation.veriyMobile(phoneNumber)) {
			phoneNumber = PhoneLocation.formatMobile(phoneNumber);
		}

		// �����������ȷ�ϵ�¼���룬����ǰ󶨺��룬��ȡ�ð󶨺���ĵ�¼�˺�
		Long id = yslMemberService.getUsridByPhone(phoneNumber);
           LoginParameter parameter = new LoginParameter();

		if (null == id) {
			res.setSucc(0);
			res.setMsg("�û�Ա������");
			return res;
		}
	    YslMemberBO  bo = yslMemberService.getYslMemberBO(id);
		Long usrid = null;

		String idcard = null;
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		if (DC.UC_MBR.equals(account.getDCT_UC())) {
	
			parameter.setUserID(bo.getUSRID());
			parameter.setRealName(bo.getREALNAME());			

			parameter.setPhoneNumber(CommonUtil.getTels(bo));
			parameter.setMobilePhoneNumber(CommonUtil.getMobiles(bo));
			parameter.setUserType(bo.getDCT_UT());
			// parameter.setIdCardPhoto(memberBO.getUSRIDNO());

			parameter.setRegisterTime(dateformat.format(bo.getCREATE_TIME()));
			parameter.setPlace(bo.getPLACE());
	
			parameter.setCompanyName(bo.getCOMPANYNAME());
			parameter.setBusiAddress(bo.getBUSIADDRESS());
			parameter.setBusiLines(bo.getBUSILINES());
			parameter.setTransportInfo(bo.getTRANSPORTINFO());
			if (DC.UCERT_PS.equals(bo.getDCT_UA())) {
				parameter.setIsCert(Constants.TRUE);
			} else {
				parameter.setIsCert(Constants.FALSE);
			}
			parameter.setAgentAccount(0);
			res.setResult(parameter);
			if (StringUtils.hasText(bo.getIDNO())) {
				parameter.setAge(IDCardUtil.getAgeByIdCard(bo.getIDNO()));
			}

			usrid = bo.getUSRID();
			String place =bo.getPLACE();
			if (!StringUtils.hasText(place)) {
				// ��������ڣ�������ȡ�̻����ڵ�
				String phone = bo.getMOBILE1();
				if (StringUtils.hasText(bo.getTEL1())) {
					phone = bo.getTEL1();
				}
				place = super.getPalced(credential, bo.getUSRID(), phone);
			}

			idcard = bo.getIDNO();

			// ��ȡ��ϵ����Ϣ
			YslContact tact = yslContactService.getContactByUserID(credential, bo.getUSRID(), credential.getUSRID());
			if (tact != null) {
				parameter.setComment(tact.getCONTREMARK());
			}

			// ������ʾ��Ϣ
			String remark = bo.getREMARK();

			// ȡ����,���ܴ��ڶ�����������שʯ��ô��չʾשʯ����Ȼ����ͭ�ƣ�������
		
			String serviceCode =getServiceOrderCode(credential, bo.getUSRID());
			if (serviceCode != null) {
				parameter.setService(serviceCode);
			}

			parameter.getTrustInfo().setTrustPromptInfo(getRemark(credential,remark, serviceCode==null ? false:true));
			
			parameter.setPlace(place);

	

			// ������������
			if (StringUtils.hasText(place)) {				
				parameter.getTrustInfo().setLocalTrustLevel(getYslRegionRank(credential, place, usrid,bo.getDCT_UT()));
			}

	

		// ȡ�������ڵ�
		if (StringUtils.hasText(idcard)) {

			parameter.setUserHouseHold(IdcardLocation.getLocation(idcard.substring(0, 6)));
		}

		// // Ͷ�����˴���

		parameter.getTrustInfo().setComplaintNumber(bo.getBTSCOUNT());
		
		
		// ��Ծ��+������ָ��
		parameter.getTrustInfo().setComplaintOtherPeopleNumber(bo.getTSCOUNT());


	
		if (DC.UC_MBR.equals(bo.getDCT_UC())) {
		  parameter.getTrustInfo().setActivityNumber(bo.getYSLACTIVEDEG());
		}
		parameter.getTrustInfo().setCredit(bo.getYSLACCOUNT());

		Map mapParameter = new HashMap();
		mapParameter.put("memberInfo", parameter);

		res.setResult(mapParameter);
		return res;
	}

	// ���ؽ����Ϊ1���غ��롣Ϊ0����غ���
	public RemoteResponse isLocalMobileNumber(Credential credential, Map<String, Object> map) {

		JSONObject jsonObject = JSONObject.fromObject(map.get("Numbers"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(jsonObject, Map.class);

		String mobilePhone = (String) userMap.get("MobilePhone");
		String localAreaNumber = (String) userMap.get("LocalAreaNumber");

		RemoteResponse res = new RemoteResponse();

		if (isLocationCode(localAreaNumber, mobilePhone)) {
			res.setResult(1);
			return res;
		}
		res.setResult(0);

		return res;

	}

	public RemoteResponse execute(Credential credential, Map<String, Object> args) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 1) ������Ϣ��չʾ,�����������Ҫ��ԭ��ȷ��֮��,�Ż���
	 * 
	 * 2) ֻ�е绰�����ֻ�����󶨵������,�ſ��Բ�ѯ�ĵ�,������Զֻ��һ����¼
	 * 
	 * 
	 * 3) ��Ҫ����һЩ��Ϣ����İ�ȫ�� (ǰ����Ҫ���п�����?����ֱ����?��ͼƬ�ķ�ʽ���д���,ǰ���һ�����(��Base64����м���))
	 */
	public RemoteResponse query(Map<String, Object> args) {
		return null;
	}

}