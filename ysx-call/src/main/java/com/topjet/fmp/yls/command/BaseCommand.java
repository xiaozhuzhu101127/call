package com.topjet.fmp.yls.command;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.topjet.fmp.aa.Credential;
import com.topjet.fmp.aa.CredentialFactory;
import com.topjet.fmp.aa.domain.AccountLog;
import com.topjet.fmp.app.domain.CargoOnline;
import com.topjet.fmp.app.service.CargoOnlineService;
import com.topjet.fmp.base.DC;
import com.topjet.fmp.base.domain.City;
import com.topjet.fmp.base.domain.Dict;
import com.topjet.fmp.base.domain.PhonePlace;
import com.topjet.fmp.base.service.CommonService;
import com.topjet.fmp.base.service.PhonePlaceService;
import com.topjet.fmp.biz.tk.domain.TkOnlineTemp;
import com.topjet.fmp.exception.AccountException;
import com.topjet.fmp.exception.MemberException;
import com.topjet.fmp.svc.domain.SVCCriteria;
import com.topjet.fmp.svc.domain.ServiceOrder;
import com.topjet.fmp.svc.service.SVCOrderService;
import com.topjet.fmp.sys.service.CityService;
import com.topjet.fmp.sys.service.DictService;
import com.topjet.fmp.user.domain.Agent;
import com.topjet.fmp.user.domain.MemberGpsCurrent;
import com.topjet.fmp.user.domain.YslAccountCriteria;
import com.topjet.fmp.user.domain.YslMember;
import com.topjet.fmp.user.domain.YslMemberBO;
import com.topjet.fmp.user.service.AgentService;
import com.topjet.fmp.user.service.MemberGpsCurrentService;
import com.topjet.fmp.user.service.YslAccountService;
import com.topjet.fmp.user.service.YslMemberService;
import com.topjet.fmp.util.CommonUtils;
import com.topjet.fmp.util.Constants;
import com.topjet.fmp.yls.RemoteResponse;
import com.topjet.fmp.yls.util.IpUtil;
import com.topjet.fmp.yls.util.PhoneLocation;

public class BaseCommand implements Command {

	@Autowired
	private PhonePlaceService phonePlaceService;

	@Autowired
	private CargoOnlineService cargoOnlineService;

	@Autowired
	private CityService cityService;

	@Autowired
	private AgentService agentService;

	@Autowired
	private DictService dictService;

	@Autowired
	private YslMemberService yslMemberService;

	@Autowired
	private YslAccountService yslAccountService;

	@Autowired
	private SVCOrderService svcOrderService;

	@Autowired
	private MemberGpsCurrentService memberGpsCurrentService;

	@Autowired
	private CommonService commonService;

	public RemoteResponse execute(Credential credential,
			Map<String, Object> args) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPalced(Credential credential, Long userid, String phone) {

		if (phone == null) {
			return null;
		}

		String place = getLocationCode(phone);

		if (StringUtils.hasText(place)) {
			YslMember member = new YslMember();
			member.setUSRID(userid);
			member.setPLACE(place);
			try {
				// ���Ը��²��ɹ�
				yslMemberService.update(member);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return place;

	}

	public String getLocationCode(String tel) {

		// t_phone_place
		if (PhoneLocation.veriyMobile(tel)) {
			tel = PhoneLocation.formatMobile(tel);
			PhonePlace phonePlace = phonePlaceService.selectByPrimaryKey(tel
					.substring(0, 7));
			if (phonePlace != null) {
				return phonePlace.getCODE();
			}
		}

		// �˽ӿڻ�����Ҫ�ģ������ȴ����ݿ���ң����ݿ������ˣ��Ͳ����ӿ��ˣ�û�вŵ�
		// http://apis.juhe.cn/mobile/get?phone=13429667914&key=cf7381ed5aca866a4e75e2528cb58c70
		String area = PhoneLocation.getLocaleByTel(tel);
		if (area != null) {
			area = area.trim();
		}
		if ("�Ϻ�".equals(area) || "����".equals(area) || "���".equals(area)
				|| "����".equals(area)) {

			area = area + " " + area;

		}
		if (PhoneLocation.UNKNOW.equals(area)) {
			return null;
		}
		try {
			City city = cityService.selectCodeByCity(area, " ");
			if (city == null) {
				return null;
			}

			return city.getCTCODE();
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}

	}

	public String getRemark(Credential tail, String remark, boolean isService) {

		if (StringUtils.hasText(remark)) {
			return remark;
		}

		Dict city = dictService.transDictCode(DC.YSL_REMARK);
		// �����˷����
		if (isService) {
			return city.getDCTVALUE1();
		} else {

			// �����û�ж�������Ļ�Ա����Ҫ�õ���¼�˵ĳ�վ
			YslMember temp = yslMemberService.getYslMemberById(tail.getUSRID());

			Long stid = temp.getTSID();
			if (stid != null && stid != 0) {
				List<Agent> sts = agentService.selectBoothByStid(stid);
				if (sts == null || sts.size() <= 0 || !isBooth(sts)) {
					return city.getDCTVALUE3();
				} else {
					remark = city.getDCTVALUE2();
					// ȡ�иó�վ�����д�����̯λ
					StringBuilder sbr = new StringBuilder();
					for (int i = 0; i < sts.size(); i++) {
						sbr.append(sts.get(i).getBOOTH()).append(",");
					}

					Object[] obj = new Object[] { sbr.toString().substring(0,
							sbr.length() - 1) };
					remark = MessageFormat.format(remark, obj);
					return remark;

				}

			} else {
				return city.getDCTVALUE3();
			}
		}

	}

	// �������Ƿ���̯λ
	public boolean isBooth(List<Agent> list) {
		for (Agent agent : list) {
			if (StringUtils.hasText(agent.getBOOTH()))
				return true;
		}
		return false;

	}

	public String getSmsMessage(String svccode) {

		if ("YSXTP".equals(svccode)) {
			return Constants.YSL_TP_SMS;
		}
		if ("YSXZS".equals(svccode)) {
			return Constants.YSL_ZS_SMS;
		}
		if ("YSXTRIAL".equals(svccode)) {
			return Constants.YSL_SY_SMS;

		}
		return null;
	}

	// ȡ���������code,����ʯ����ͭ�ƣ��� ����
	public String getServiceOrderCode(Credential tail, Long USRID) {

		SVCCriteria svcCriteria = new SVCCriteria();
		svcCriteria.setUSRID(USRID);
		svcCriteria.setSVCCODE(Constants.YSX);

		List<ServiceOrder> list = svcOrderService.selectAllValidServiceByCode(
				tail, svcCriteria);
		if (list == null || list.size() <= 0) {
			return null;
		}

		// ����ʯ
		for (int i = 0; i < list.size(); i++) {
			if (Constants.YSX_ZS.equals(list.get(i).getSVCCODE())) {
				return Constants.YSX_ZS;
			}
		}

		// ����ʯ
		for (int i = 0; i < list.size(); i++) {
			if (Constants.YSX_YP.equals(list.get(i).getSVCCODE())) {
				return Constants.YSX_YP;
			}
		}

		// ��ͭ��
		for (int i = 0; i < list.size(); i++) {
			if (Constants.YSX_TP.equals(list.get(i).getSVCCODE())) {
				return Constants.YSX_TP;
			}
		}

		// ������
		for (int i = 0; i < list.size(); i++) {
			if (Constants.YSX_SY.equals(list.get(i).getSVCCODE())) {
				return Constants.YSX_SY;
			}
		}
		return null;
	}

	public Integer getYslRegionRank(Credential tail, String place, Long usrid,
			String DCT_UT) {
		YslAccountCriteria criter = new YslAccountCriteria();
		criter.setCT_HOMETOWN(CommonUtils.stripBX(place));
		criter.setUSRID(usrid);
		criter.setDCT_UT(DCT_UT);

		return yslAccountService.getYslRegionRank(tail, criter);

	}

	protected Long addCallMember(Credential credential,
			Map<String, Object> map, String phoneNumber, YslMemberBO yslMemberBO)
			throws MemberException, AccountException {

		if (PhoneLocation.veriyMobile(phoneNumber)) {
			// ������ֻ��Ժ���ȥ0����

			yslMemberBO.setMOBILE1(PhoneLocation.formatMobile(phoneNumber));

		} else {
			// �����6��0�Ļ���˵���Ƿֻ������浽000000
			if (phoneNumber.indexOf(Constants.YSX_MOBILE_FENJI) > 0) {
				yslMemberBO.setTEL2(phoneNumber);
			} else {
				yslMemberBO.setTEL1(phoneNumber);
			}

		}

		// ���ݵ绰����ȥ�ж�
		// System.out.println("�������" + bindPhone);
		String location = getLocationCode(phoneNumber);

		// ��פ��

		yslMemberBO.setPLACE(location);
		// yslMemberBO.setDCT_UT(DC.UT_TW);

		HttpServletRequest request = (HttpServletRequest) map
				.get(Command.P_HTTPREQUEST);

		AccountLog accountLog = new AccountLog();
		// ��ȡIP��ַ

		accountLog.setDCT_ACCAT(DC.ACCAT_CS);
		accountLog.setDCT_ACCA(DC.ACCA_LOGIN);
		accountLog.setDCT_APP(DC.APP_REMOTE);
		accountLog.setALIPADDR(IpUtil.getRemoteIP(request));

		return yslMemberService.createMember(credential, yslMemberBO);

	}

	protected Long addCallMember(Credential credential,
			Map<String, Object> map, String phoneNumber)
			throws MemberException, AccountException {
		YslMemberBO yslMemberBO = new YslMemberBO();
		return addCallMember(credential, map, phoneNumber, yslMemberBO);

	}

	protected Long addCallMember(Credential credential, TkOnlineTemp temp)
			throws MemberException, AccountException {
		YslMemberBO yslMemberBO = new YslMemberBO();

		return null;
	}

	// û�е�¼��Ҫ�������ݣ�����һ��Credential
	protected Credential getEmptyCredential() {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("USRID", 1L);
		values.put("DCT_UC", "");
		values.put("SESSIONID", "");
		values.put("USRNAME", "");
		values.put("IPADDR", "");

		return CredentialFactory.getInstance().create(values);
	}

	// û�е�¼��Ҫ�������ݣ�����һ��Credential
	protected Credential getNB2DCredential() {
		Agent agent = agentService.queryByLoginName("NB2D");
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("USRID", agent.getUSRID());
		values.put("DCT_UC", agent.getDCT_UC());
		values.put("SESSIONID", "");
		values.put("USRNAME", "");
		values.put("IPADDR", "");

		return CredentialFactory.getInstance().create(values);
	}

	public CityService getCityService() {
		return cityService;
	}

	public YslMemberService getYslMemberService() {
		return yslMemberService;
	}

	public AgentService getAgentService() {
		return agentService;
	}

	public void setAgentService(AgentService agentService) {
		this.agentService = agentService;
	}

	public void setCityService(CityService cityService) {
		this.cityService = cityService;
	}

	public DictService getDictService() {
		return dictService;
	}

	public void setDictService(DictService dictService) {
		this.dictService = dictService;
	}

	public SVCOrderService getSvcOrderService() {
		return svcOrderService;
	}

	public String getPalcedDCTCodeByBaiDu(Credential credential, String place) {

		if (StringUtils.hasText(place)) {
			// �����ж��Ƿ�������
			Pattern pattern = Pattern.compile("^[0-9]+(.[0-9]*)?$");
			Matcher match = pattern.matcher(place);
			if (!match.matches()) {
				place = place.replaceAll("_BX", "");
				City city = cityService.transCityCode(place);
				if (city != null) {
					place = city.getCTVALUE2();
				}

			}
			
		}

		return place;

	}

	public String getPalcedDCTCode(Credential credential, String place) {

		if (StringUtils.hasText(place)) {
			// �����ж��Ƿ�������
			Pattern pattern = Pattern.compile("^[0-9]+(.[0-9]*)?$");
			Matcher match = pattern.matcher(place);
			if (match.matches()) {
				City city = cityService.queryCityByValue2(place);
				if (city != null) {
					place = city.getCTCODE();
				}

			}
			place = place.replaceAll("_BX", "");
			if (place.toString().indexOf("SH_SH")>=0 || place.toString().indexOf("BJ_BJ")>=0 
					|| place.toString().indexOf("TJ_TJ")>=0  || place.toString().indexOf("ZQ_ZQ")>=0 ) {
				place=place.substring(0,2);
			}
			
		}

		return place;

	}

	public String getCityCodeByBaiduCode(Credential tail, String baiduCode) {

		// ����������ǿյģ���ô�ȴӰٶȵ�ǰλ�ñ���ȡ��code,baiduCode=code
		if (!StringUtils.hasText(baiduCode)) {
			MemberGpsCurrent current = memberGpsCurrentService.select(tail
					.getUSRNAME());
			baiduCode = current == null ? null : current.getCITYCODE();
		}

		if (StringUtils.hasText(baiduCode)) {
			City city = cityService.queryCityByValue2(baiduCode);
			if (city != null) {
				return city.getCTCODE();
			}
		}

		YslMember yslmember = yslMemberService
				.getYslMemberById(tail.getUSRID());
		if (StringUtils.hasText(yslmember.getPLACE())) {
			return yslmember.getPLACE();
		}
		return null;
	}

	protected Integer getPage(Integer offset, int pagesize) {

		if (offset == null || offset == 0) {
			return 1;
		}

		return offset / pagesize + 1;

	}

	public MemberGpsCurrentService getMemberGpsCurrentService() {
		return memberGpsCurrentService;
	}

	protected void addCargoOnline(Credential credential, String CT_REGOIN,
			String msg, String place) {
		CargoOnline online = new CargoOnline();
		online.setCT_REGOIN(CT_REGOIN);
		online.setMSG(msg);
		online.setPLACE(place);
		online.setMOBILE(credential.getUSRNAME());
		YslMemberBO memberBO = yslMemberService.getYslMemberBO(credential
				.getUSRID());
		// �ֻ�д��һ��Ϊ4��ʾ��Ա������Ϊ��̨������
		online.setDCT_UC("4");
		online.setREALNAME(memberBO.getREALNAME());
		online.setYSLACCOUNT(memberBO.getYSLACCOUNT());
		if (memberBO.getPHOTOID() != null) {
			online.setPICTURE_ID(memberBO.getPHOTOID());
		}
		cargoOnlineService.add(credential, online);
	}

	// timeUnit���ӵ�ʱ�䵥λ
	// cd.add(Calendar.DATE, n);//����һ��
	// cd.add(Calendar.MONTH, n);//����һ����
	protected static String addDay(int timeUnit, int n) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			Calendar cd = Calendar.getInstance();
			cd.setTime(new Date());
			cd.add(timeUnit, n);// ����һ��
			return sdf.format(cd.getTime());

		} catch (Exception e) {
			return null;
		}

	}

	public CargoOnlineService getCargoOnlineService() {
		return cargoOnlineService;
	}

	public CommonService getCommonService() {
		return commonService;
	}

	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}

}