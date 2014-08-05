package com.topjet.fmp.yls.command.user;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.topjet.fmp.aa.Credential;

import com.topjet.fmp.aa.domain.Account;
import com.topjet.fmp.aa.domain.AccountLog;
import com.topjet.fmp.aa.service.AccountService;


import com.topjet.fmp.user.domain.ContactCriteria;


import com.topjet.fmp.user.service.YslContactService;
import com.topjet.fmp.base.DC;

import com.topjet.fmp.base.domain.City;
import com.topjet.fmp.base.domain.PaginatedList;

import com.topjet.fmp.exception.AccountException;
import com.topjet.fmp.exception.MemberException;

import com.topjet.fmp.svc.domain.YslCall;
import com.topjet.fmp.svc.service.SVCOrderService;
import com.topjet.fmp.svc.service.YslCallService;
import com.topjet.fmp.sys.service.CityService;


import com.topjet.fmp.user.domain.MisUser;



import com.topjet.fmp.user.domain.YslContact;
import com.topjet.fmp.user.domain.YslMember;
import com.topjet.fmp.user.domain.YslMemberBO;

import com.topjet.fmp.user.service.MisUserService;

import com.topjet.fmp.user.service.PointRecordService;
import com.topjet.fmp.user.service.YslAccountService;

import com.topjet.fmp.user.service.YslMemberService;
import com.topjet.fmp.util.Constants;
import com.topjet.fmp.yls.RemoteResponse;
import com.topjet.fmp.yls.command.BaseCommand;
import com.topjet.fmp.yls.command.Command;
import com.topjet.fmp.yls.util.CommonUtil;
import com.topjet.fmp.yls.util.IdcardLocation;
import com.topjet.fmp.yls.util.PhoneLocation;


/**
 * 
 * <pre>
 * Description
 * Copyright:	Copyright (c)2009  
 * Company:		拓景科技
 * Author:		Dengfp
 * Version:		1.0  
 * Create at:	2012-8-13 上午11:29:44  
 *  
 * 修改历史:
 * 日期    作者    版本  修改描述
 * ------------------------------------------------------------------
 * 
 * </pre>
 */

@Component("contact_command")
public class ContactCommand extends BaseCommand {

	private Log log = LogFactory.getLog(ContactCommand.class);

	@Autowired
   private PointRecordService pointRecordService;

	@Autowired
	private MisUserService misUserService;

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private YslCallService  yslCallService;



	@Autowired
	private YslMemberService yslMemberService;

	@Autowired
	private YslContactService yslContactService;
	
	

	/**
	 * 添加联系人信息
	 * 
	 * 注意: 在添加联系人的同时,可以将其添加到会员中去
	 * 
	 * @param args
	 * @return
	 */
	public RemoteResponse addContact(Credential credential,
			Map<String, Object> map) {

		JSONObject jsonObject = JSONObject.fromObject(map.get("ContactInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(
				jsonObject, Map.class);

		RemoteResponse res = new RemoteResponse();
		// 固话
		String phoneNumber = (String) userMap.get("PhoneNumber");

		// 手机号码可能带0，可能不带0
		String mobilePhoneNumber = (String) userMap.get("MobilePhoneNumber");

		if (!StringUtils.hasText(phoneNumber)
				&& !StringUtils.hasText(mobilePhoneNumber)) {
			res.setMsg("手机号码和固定号码不能同时为空");
			res.setSucc(0);
			return res;
		}

		// 手机号码前边有0，需去掉
		if (StringUtils.hasText(mobilePhoneNumber)) {
			mobilePhoneNumber = PhoneLocation.formatMobile(mobilePhoneNumber);
		}

		String contPhone = "";
		if (StringUtils.hasText(phoneNumber)) {
			contPhone = phoneNumber;
		} else {
			contPhone = mobilePhoneNumber;
		}
		// 联系人真实姓名
		String realName = (String) userMap.get("RealName");
		String usrType = (String) userMap.get("UserType");
		// 备注
		String comment = (String) userMap.get("Comment");
		
		if(StringUtils.hasText(comment)&&comment.length()>=200){
			res.setSucc(0);
			res.setMsg("备注不能超过200！");
			return res;
		}
		
		String companyName = (String) userMap.get("CompanyName");
		
	
		
		String busiAddress = (String) userMap.get("BusiAddress");
		
		String busiLines = (String) userMap.get("BusiLines");
		
		String transportInfo = (String) userMap.get("TransportInfo");

		Long usrid = yslMemberService.getUsridByPhone(contPhone);

		YslContact contact = new YslContact();

		if (usrid != null && usrid.equals(credential.getUSRID())) {
			res.setSucc(0);
			res.setMsg("不能添加自己为联系人！");
			return res;
		}
		if (usrid == null) {
			// 不存在，需添加
			YslMemberBO member = new YslMemberBO();
			member.setREALNAME(realName);
			member.setDCT_UT(usrType);
			if (PhoneLocation.veriyMobile(contPhone)) {
				member.setMOBILE1(contPhone);
			} else {
//				member.setTEL1(contPhone);
				
				//如果有6个0的话，说明是分机，保存到000000
				if(contPhone.indexOf(Constants.YSX_MOBILE_FENJI)>0){
					member.setTEL2(contPhone);
				}else{
					member.setTEL1(contPhone);
				}
				
			}
			// 根据电话号码去判断
			String location = super.getLocationCode(contPhone);
			member.setPLACE(location);
			member.setCOMPANYNAME(companyName);
			member.setBUSILINES(busiLines);
			member.setBUSIADDRESS(busiAddress);
			member.setTRANSPORTINFO(transportInfo);
			AccountLog accountLog = new AccountLog();
			// 获取IP地址

			accountLog.setDCT_ACCAT(DC.ACCAT_CS);
			accountLog.setDCT_ACCA(DC.ACCA_LOGIN);
			accountLog.setDCT_APP(DC.APP_REMOTE);
			accountLog.setALIPADDR((String) map.get(Command.P_IPADDR));
			Long id=null;
			try {
				id = yslMemberService.createMember(credential, member);
			} catch (AccountException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MemberException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			contact.setUSRID(id);

		} else {
			
			
			try{
				//如果没有审核过的可修改资料
			YslMember temp = yslMemberService.getYslMemberById(usrid);
//			if(!DC.UA_PS.equals(temp.getDCT_UA())){
				YslMember tempMember = new YslMember();
				tempMember.setUSRID(temp.getUSRID());
				tempMember.setREALNAME(realName);
				tempMember.setCOMPANYNAME(companyName);
				tempMember.setBUSIADDRESS(busiAddress);
				tempMember.setBUSILINES(busiLines);
				tempMember.setTRANSPORTINFO(transportInfo);
				tempMember.setDCT_UT(usrType);
				yslMemberService.update(tempMember);
				//如果用户更新联系人的姓名，帮助完善名称这一行，给与1个积分
				if(StringUtils.hasText(realName)){
					pointRecordService.addPoint(credential,temp.getUSRID(),Constants.POINT_TYPE1,Constants.YSX_ADD_POINT1);
				}
//			}
			}catch(Throwable e){
				e.printStackTrace();
			}
			
			
			
			contact.setUSRID(usrid);
			// 如果用户存在，还要判断是否是自己的联系人
			if (yslContactService.hasContact(credential, Long.valueOf(usrid),
					credential.getUSRID())) {
				res.setMsg("已经是您的联系人！");
				res.setSucc(0);
				return res;
			}
			
		}

		contact.setCONTPHONE(contPhone);
		contact.setCONTREMARK(comment);
		contact.setDELETED(Constants.FALSE);

		// 添加联系人，
		Long id = yslContactService.create(credential, contact);

		Map mapPara = new HashMap();
		mapPara.put("userIDOfContact", contact.getUSRID());

		res.setResult(mapPara);

		return res;
	}

	public RemoteResponse addContactByID(Credential credential,
			Map<String, Object> map) {

		JSONObject jsonObject = JSONObject.fromObject(map.get("MemberInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(
				jsonObject, Map.class);

		RemoteResponse res = new RemoteResponse();
		// 需要加为联系人的
		Integer id = (Integer) userMap.get("UserIDOfContact");
		String remark = (String) userMap.get("Comment");
		
		if(StringUtils.hasText(remark)&&remark.length()>=200){
			res.setSucc(0);
			res.setMsg("备注不能超过200！");
			return res;
		}
		
		
		// 联系人真实姓名
			
				String usrType = (String) userMap.get("UserType");
				// 备注
				String comment = (String) userMap.get("Comment");
				
			
				
				String companyName = (String) userMap.get("CompanyName");
				String realName = (String) userMap.get("RealName");
				String busiAddress = (String) userMap.get("BusiAddress");
				
				String busiLines = (String) userMap.get("BusiLines");
				
				String transportInfo = (String) userMap.get("TransportInfo");

		Long contact_id = null;

		// 传过来的是会员ID
		if (credential.getUSRID().equals( Long.valueOf(id))) {
			res.setSucc(0);
			res.setMsg("不能添加自己为联系人！");
			return res;
		}
		
		YslMember bo = yslMemberService.getYslMemberById(Long.valueOf(id));
		try{
			//如果没有审核过的可修改资料
//		if(!DC.UA_PS.equals(bo.getDCT_UA())){
			YslMember tempMember = new YslMember();
			tempMember.setUSRID(bo.getUSRID());
			tempMember.setREALNAME(realName);
			tempMember.setCOMPANYNAME(companyName);
			tempMember.setBUSIADDRESS(busiAddress);
			tempMember.setBUSILINES(busiLines);
			tempMember.setTRANSPORTINFO(transportInfo);
			tempMember.setDCT_UT(usrType);
			yslMemberService.update(tempMember);
			if(StringUtils.hasText(realName)){
				pointRecordService.addPoint(credential,bo.getUSRID(),Constants.POINT_TYPE1,Constants.YSX_ADD_POINT1);
			}
//		}
		}catch(Throwable e){
			e.printStackTrace();
		}
		

		if (yslContactService.hasContact(credential, Long.valueOf(id),
				credential.getUSRID())) {
			res.setMsg("已经是您的联系人！");
			res.setSucc(0);
			return res;
		}

	
		

		if (bo == null) {
			res.setSucc(0);
			res.setMsg("该用户不存在！");
			return res;
		}

		YslContact contact = new YslContact();
		contact.setUSRID(Long.valueOf(id));
		String mobile = bo.getMOBILE1();
		String tel = bo.getTEL1();
		if (StringUtils.hasText(tel)) {
			contact.setCONTPHONE(tel);
		} else {
			contact.setCONTPHONE(mobile);
		}
		contact.setCONTREMARK(remark);

		contact_id = yslContactService.create(credential, contact);

		Map mapPara = new HashMap();
		mapPara.put("ContactID", contact_id);

		res.setResult(mapPara);

		return res;
	}

	// 根据id查询
	public RemoteResponse getContactInfoById(Credential credential,
			Map<String, Object> map) {

		JSONObject jsonObject = JSONObject.fromObject(map.get("ContactInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(
				jsonObject, Map.class);

		RemoteResponse res = new RemoteResponse();

		// 需要进行操作的联系人的ID
		Integer contactID = (Integer) userMap.get("UserIDOfContact");

		// 执行此操作的当前会员的ID
		// String usrid = (String) map.get("UserID");

		if (!yslContactService.hasContact(credential, Long.valueOf(contactID),
				credential.getUSRID())) {
			res.setMsg("此联系人不存在！");
			res.setSucc(0);
			return res;
		}


	    YslMemberBO  memberBO = yslMemberService.getYslMemberBO(Long.valueOf(contactID));

		getUserInfo(credential, res, memberBO);

		return res;

	}

	public RemoteResponse getContactInfoByPhoneNumber(Credential credential,
			Map<String, Object> map) {

		JSONObject jsonObject = JSONObject.fromObject(map.get("ContactInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(
				jsonObject, Map.class);

		RemoteResponse res = new RemoteResponse();

		// 电话号码
		String phoneNumber = (String) userMap.get("PhoneNumber");

		// boolean isMobile = PhoneLocation.veriyMobile(phoneNumber);

		// 手机号码带0的话，需要去掉
		if (PhoneLocation.veriyMobile(phoneNumber)) {
			phoneNumber = PhoneLocation.formatMobile(phoneNumber);
		}
		Long id=yslMemberService.getUsridByPhone(phoneNumber);
		
		if (id == null) {
			res.setMsg("您要查找的联系人不存在！");
			res.setSucc(0);
			return res;

		}

		
	    YslMemberBO  memberBO = yslMemberService.getYslMemberBO(id);
	    
	    getUserInfo(credential, res, memberBO);

		return res;
	}
	
	
	
	//android打电话使用该接口
	public RemoteResponse callPhoneInfoByPhoneNumber(Credential credential,
			Map<String, Object> map) {

//		JSONObject jsonObject = JSONObject.fromObject(map.get("ContactInfo"));
//		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(
//				jsonObject, Map.class);

		RemoteResponse res = new RemoteResponse();

		// 电话号码
		String phoneNumber = (String) map.get("PhoneNumber");
		log.warn("call phone is:"+phoneNumber+"and the usr is +"+credential.getUSRNAME());
		if(!StringUtils.hasText(phoneNumber)){
			//手机客服端要求返回为空，那就为空吧
			ContactParameter parameter = new ContactParameter();
			Map mapContact = new HashMap();
			mapContact.put("ContactInfo", parameter);
			res.setResult(mapContact);			
			res.setSucc(1);
			res.setMsg("手机号码不能为空！");
			return res;
		}
		if(phoneNumber.length()<=7){
			ContactParameter parameter = new ContactParameter();
			Map mapContact = new HashMap();
			//手机客服端要求返回为空，那就为空吧
			mapContact.put("ContactInfo", parameter);
			res.setResult(mapContact);
			res.setSucc(1);
			res.setMsg("短号，本地号码不支持！");
			return res;
		}

		// boolean isMobile = PhoneLocation.veriyMobile(phoneNumber);

		// 手机号码带0的话，需要去掉
		if (PhoneLocation.veriyMobile(phoneNumber)) {
			phoneNumber = PhoneLocation.formatMobile(phoneNumber);
		}
		Long id=yslMemberService.getUsridByPhone(phoneNumber);
		
		//该号码不存在，需要添加到数据库
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
		
		Account account = accountService.query(credential, id);
		if (account.getUSRID().equals(credential.getUSRID())) {
			res.setSucc(0);
			res.setMsg("不能查询自己");
			return res;
		}
		
		YslCall  call =new YslCall();
		call.setCALLIO(Constants.FALSE);
		call.setCALLNUM(phoneNumber);
		call.setUSRID(account.getUSRID());
		call.setTYPE("0");
		call.setCREATE_BY(credential.getUSRID());
		
		
		
		Long callid = yslCallService.create(credential, call);

		//

		//
		// // 要进行积分，运商联指数增加操作，规则是活跃度满10,指数增加1 个
//		try {
////			yslAccountService.updateActiveDeg(credential, account == null ? null : account.getUSRID());
//			pointRecordService.addPoint(credential,account.getUSRID(),Constants.POINT_TYPE2,"打电话送积分");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
		
	    YslMemberBO  memberBO = yslMemberService.getYslMemberBO(id);
	    
	    getUserInfo(credential, res, memberBO);

		return res;
	}

	
	

	private void getUserInfo(Credential credential, RemoteResponse res,
			YslMemberBO memberBO) {
		ContactParameter parameter = new ContactParameter();

		Long usrid = null;

		String idcard = null;
		// 场站ID
		Long stid = null;

		String booth = null;



		
			parameter.setUserID(memberBO.getUSRID());
			parameter.setRealName(memberBO.getREALNAME());
			parameter.setMobilePhoneNumber(CommonUtil.getMobiles(memberBO));
			parameter.setPhoneNumber(CommonUtil.getTels(memberBO));
			
			//司机，货主等
			parameter.setUserType(memberBO.getDCT_UT());

			String dct_UCERT = memberBO.getDCT_UA();
			if (DC.UCERT_PS.equals(dct_UCERT)) {
				parameter.setInfoConfirmed(Constants.TRUE);
				parameter.setIsCert(Constants.TRUE);
			} else {
				parameter.setInfoConfirmed(Constants.FALSE);
				parameter.setIsCert(Constants.FALSE);
			}

			// 取户籍所在地
			if (StringUtils.hasText(memberBO.getIDNO())) {
				
				parameter.setUserHouseHold(IdcardLocation.getLocation(memberBO.getIDNO().substring(0, 6)));
			
			}

			parameter.setBusiAddress(memberBO.getBUSIADDRESS());
			parameter.setCompanyName(memberBO.getCOMPANYNAME());
			parameter.setBusiLines(memberBO.getBUSILINES());
			parameter.setTransportInfo(memberBO.getTRANSPORTINFO());

			SimpleDateFormat dateformat1 = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			parameter.setRegisterTime(dateformat1.format(memberBO
					.getCREATE_TIME()));

			String remark = memberBO.getREMARK();

			// 获取联系人信息
			YslContact tact = yslContactService.getContactByUserID(credential,
					memberBO.getUSRID(), credential.getUSRID());
			if (tact != null) {
				parameter.setComment(tact.getCONTREMARK());
			}

			// 取服务					
			String serviceCode =getServiceOrderCode(credential,memberBO.getUSRID());
			if (serviceCode != null) {
				parameter.setService(serviceCode);				

			}	

			parameter.getTrustInfo().setTrustPromptInfo(getRemark(credential,remark, serviceCode==null ? false:true));

			usrid = memberBO.getUSRID();
		
			String place =memberBO.getPLACE();
			if (!StringUtils.hasText(place)) {
				// 如果不存在，则优先取固话所在地注意点，tel1,mobile1是主号码，一定存在一个
				String phone = memberBO.getTEL1();
				if (!StringUtils.hasText(phone)) {
					phone=memberBO.getMOBILE1();
				}
				place = super.getPalced(credential, memberBO.getUSRID(),
						phone);
				parameter.setPlace(place);
				
			 
			}
			if (StringUtils.hasText(place)) {
			  String city=	getCityService().translate(place, true);
			   if(city!=null){
				   parameter.setPlaceName(city);
			   }
			}
			idcard = memberBO.getIDNO();
			
			

			// 当地信用排名
			if (StringUtils.hasText(place)) {	
				parameter.getTrustInfo().setLocalTrustLevel(getYslRegionRank(credential, place, usrid, memberBO.getDCT_UT()));
			}
			//

	

		parameter.setPlace(place);
		// 被投诉次数
	

		parameter.getTrustInfo().setComplaintNumber(memberBO.getBTSCOUNT());

		parameter.getTrustInfo().setComplaintOtherPeopleNumber(memberBO.getTSCOUNT());

		// 活跃度(积分)只有会员才展示+运商联指数,
		if (DC.UC_MBR.equals(memberBO.getDCT_UC())) {
		  parameter.getTrustInfo()
				.setActivityNumber(memberBO.getYSLACTIVEDEG());
		}
		
		parameter.getTrustInfo().setCredit(memberBO.getYSLACCOUNT());
		 parameter.setCertificationID(memberBO.getIDCERTIFICATIONID());

		Map mapContact = new HashMap();
		mapContact.put("ContactInfo", parameter);
		res.setResult(mapContact);
	}

	// 修改联系人备注

	public RemoteResponse modifyContactComment(Credential credential,
			Map<String, Object> map) {

		JSONObject jsonObject = JSONObject.fromObject(map.get("ContactInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(
				jsonObject, Map.class);

		RemoteResponse res = new RemoteResponse();
	
		YslContact  contact = new YslContact();

		Integer contactID = (Integer) userMap.get("UserIDOfContact");

		String comment = (String) userMap.get("Comment");

		Integer usrid = (Integer) map.get("UserID");

		YslContact con = yslContactService.getContactByUserID(credential, Long.valueOf(contactID), credential.getUSRID());
		
		
		
		if (con == null) {
			res.setSucc(0);
			res.setMsg("该用户不是你的联系人！");
			return res;
		}

		yslContactService.updateComment(credential, con.getCONTID(), comment);

		return res;

	}

	// 获取当前用户的联系人
	public RemoteResponse getContact(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		// Contact contact = new Contact();

		Integer usrid = (Integer) map.get("UserID");

		ContactCriteria contactCriteria = new ContactCriteria();
		// contactCriteria.setUSRID(credential.getUSRID());
		contactCriteria.setCREATE_BY(credential.getUSRID());
		contactCriteria.setPagesize(0);

		PaginatedList<YslMemberBO> plist = yslMemberService.listYSLContactBO(
				credential, contactCriteria);
		if (null != plist && plist.getResult() != null) {
			List<YslMemberBO> list = plist.getResult();
			List<ContactListParameter> ps = new ArrayList<ContactListParameter>(
					list.size());
			for (int i = 0; i < list.size(); i++) {
				ContactListParameter ter = new ContactListParameter();
				ter.setUserID(list.get(i).getUSRID());
				ter.setRealName(list.get(i).getREALNAME());
				//如果是会员取的是会员的类型，如果不是则放登录的大类型
//				if(!DC.UC_MBR.equals(list.get(i).getDCT_UC())){
//					ter.setUserType(list.get(i).getDCT_UC());
//				}else{
					ter.setUserType(list.get(i).getDCT_UT());
//				}
					
				ter.setTrustcreditNumber(list.get(i).getYSLACCOUNT());
				ter.setMobilePhoneNumber(CommonUtil.getMobiles(list.get(i)));
				ter.setPhoneNumber(CommonUtil.getTels(list.get(i)));
				ter.setPlace(list.get(i).getPLACE());
				if (DC.UCERT_PS.equals(list.get(i).getDCT_UA())) {
					ter.setIsCert(1);
				} else {
					ter.setIsCert(0);
				}

				// if (list.get(i).getBDDATA() != null) {
				// ter.setIdCardPhoto(CLSBase64.encode(list.get(i).getBDDATA()));
				// }
				ps.add(ter);
			}
			Map newMap = new HashMap();
			newMap.put("contactInfo", ps);
			res.setResult(newMap);

		} else {
			res.setMsg("您没有联系人");
			res.setSucc(0);
		}

		return res;

	}

	// 删除联系人
	public RemoteResponse removeContact(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();

		JSONObject jsonObject = JSONObject.fromObject(map.get("ContactInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(
				jsonObject, Map.class);
		System.out.println(userMap);
		Integer usr_id = (Integer) map.get("UserID");

		JSONArray jsonArray = JSONArray.fromObject(userMap
				.get("UserIDOfContact"));
		Object obj[] = jsonArray.toArray();

		long[] ids = new long[obj.length];
		for (int i = 0; i < obj.length; i++) {
			ids[i] = (Integer) obj[i];
		}
		yslContactService.delete(credential, ids);
		return res;

	}

	// 纠错 MISSTS_ADDR 来电人地址有误
	// MISSTS_NAME 来电人姓名有误
	// MISSTS_UC 来电人地址有误
	// PROSTS_AD 0 不处理
	// PROSTS_PS 0 已处理
	// PROSTS_WT 0 待处理
	public RemoteResponse reportContactInfoError(Credential credential,
			Map<String, Object> map) {

		// String usrid = (String) map.get("UserID");

		JSONObject jsonObject = JSONObject.fromObject(map.get("ContactInfo"));
		Map<String, Object> userMap = (Map<String, Object>) JSONObject.toBean(
				jsonObject, Map.class);

		Integer contactID = (Integer) userMap.get("UserIDOfContact");

		// 根据联系人ID，得到被纠错人的用户ID
		// Contact tact = contactService.query(credential,
		// Long.valueOf(contactID));

		// Contact tact = contactService.getContactByUserID(credential,
		// Long.valueOf(contactID), credential.getUSRID());
		RemoteResponse res = new RemoteResponse();
		// if (tact == null) {
		// res.setSucc(0);
		// res.setMsg("请先加入联系人！");
		// return res;
		// }
	
		String remark = (String) userMap.get("CorrectionComments");
		String phone = (String) userMap.get("phone");
		JSONArray jsonArray = JSONArray.fromObject(userMap.get("ErrorType"));

		Object obj[] = jsonArray.toArray();

	   //提交人的信息
	    YslMemberBO yslmemberBO = yslMemberService.getYslMemberBO(credential.getUSRID());
	  
	    //被纠错人的信息
	    YslMemberBO misMemberBO = null;
	    if(contactID!=null){
	    	 misMemberBO = yslMemberService.getYslMemberBO(Long.valueOf(contactID));
	    }else{
	    	misMemberBO = yslMemberService.getYslMemberBO(yslMemberService.getUsridByPhone(phone));
	    }
	    
		
		
		if (yslmemberBO == null) {
			res.setSucc(0);
			res.setMsg("该用户不存在！");
			return res;
		}

		MisUser[] misUser = new MisUser[obj.length];
		for (int i = 0; i < obj.length; i++) {
			misUser[i] = new MisUser();
			if(!(obj[i] instanceof JSONObject)){
				String mis = (String) obj[i];
				if ("name".equals(mis)) {
					misUser[i].setDCT_MISSTS(DC.MISSTS_NAME);
				} else if ("address".equals(mis)) {
					misUser[i].setDCT_MISSTS(DC.MISSTS_ADDR);
				} else if ("membertype".equals(mis)||"MISSTS_UC".equals(mis)) {
					misUser[i].setDCT_MISSTS(DC.MISSTS_UC);
	
				} else {
					misUser[i].setDCT_MISSTS(DC.MISSTS_OTHER);
				}
		 }

			misUser[i].setDCT_PROSTS(DC.PROSTS_WT);
			misUser[i].setUSRID(Long.valueOf(yslmemberBO.getUSRID()));
			
			if(yslmemberBO!=null){
				misUser[i].setREQMOBILE(yslmemberBO.getMOBILE1());
				misUser[i].setREQTEL(yslmemberBO.getTEL1());
				misUser[i].setERQNAME(yslmemberBO.getREALNAME());
			}

			if(misMemberBO!=null){
				misUser[i].setMISNAME(misMemberBO.getREALNAME());
				misUser[i].setMOBILE1(misMemberBO.getMOBILE1());
				misUser[i].setTEL1(misMemberBO.getTEL1());
			}
			
			misUser[i].setREMARK(remark);

		}
		misUserService.create(credential, misUser);
		return res;

	}

	public RemoteResponse execute(Credential credential,
			Map<String, Object> args) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 编辑个人备注信息,主要是私人备注信息,掐的信息在这里不允许进行修改
	 * 
	 * @param args
	 * @return {@link RemoteResponse}
	 */
	public RemoteResponse edit(Map<String, Object> args) {

		return null;
	}

	/**
	 * 查询联系人的信息
	 * 
	 * @param args
	 * @return {@link RemoteResponse}
	 */
	public RemoteResponse list(Map<String, Object> args) {

		return null;
	}

}
