package com.topjet.fmp.yls.command.user;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.TimeLimitExceededException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import sun.util.logging.resources.logging;

import com.topjet.fmp.aa.Credential;
import com.topjet.fmp.base.DC;
import com.topjet.fmp.base.TypeToDC;
import com.topjet.fmp.base.domain.Dict;
import com.topjet.fmp.base.service.CommonService;
import com.topjet.fmp.biz.tk.domain.TkOnline;
import com.topjet.fmp.biz.tk.domain.TkOnlineCriteria;
import com.topjet.fmp.biz.tk.service.TkOnlineService;
import com.topjet.fmp.exception.FreightException;
import com.topjet.fmp.goods.domain.GSCriteria;
import com.topjet.fmp.goods.domain.GSCriteriaForPhone;
import com.topjet.fmp.goods.domain.GoodsAndPriceBO;
import com.topjet.fmp.goods.domain.GoodsSource;
import com.topjet.fmp.goods.domain.GoodsSourceBO;
import com.topjet.fmp.goods.domain.GoodsSourcePrice;
import com.topjet.fmp.goods.domain.GoodsSourcePriceBO;

import com.topjet.fmp.goods.service.CallPriceService;
import com.topjet.fmp.goods.service.GoodsSourceService;

import com.topjet.fmp.user.domain.MemberGpsCurrent;
import com.topjet.fmp.user.domain.MemberGpsCurrentCriteria;
import com.topjet.fmp.user.domain.YslMember;
import com.topjet.fmp.user.domain.YslMemberBO;
import com.topjet.fmp.user.service.MemberGpsCurrentService;
import com.topjet.fmp.user.service.YslMemberService;
import com.topjet.fmp.user.util.MobileConstants;
import com.topjet.fmp.util.CommonUtils;
import com.topjet.fmp.util.Constants;
import com.topjet.fmp.util.DateUtil;
import com.topjet.fmp.util.StringUtil;
import com.topjet.fmp.yls.RemoteResponse;
import com.topjet.fmp.yls.command.BaseCommand;
import com.topjet.fmp.yls.command.parameter.CurrentGPS;
import com.topjet.fmp.yls.command.parameter.GoodsAndPricePatameter;
import com.topjet.fmp.yls.command.parameter.GoodsInfoParameter;
import com.topjet.fmp.yls.command.parameter.GoodsListParameter;
import com.topjet.fmp.yls.command.parameter.GoodsSourceParameter;
import com.topjet.fmp.yls.command.parameter.GsStatInfo;
import com.topjet.fmp.yls.command.parameter.Price;
import com.topjet.fmp.yls.command.parameter.Shipper;
import com.topjet.fmp.yls.domain.LeftTime;

@Controller("goodsSource_command")
public class GoodsCommand extends BaseCommand {

	private Log log = LogFactory.getLog(UserCommand.class);

	@Autowired
	private TkOnlineService tkOnLineService;
	@Autowired
	private YslMemberService yslMemberService;

	@Autowired
	private GoodsSourceService goodService;

	@Autowired
	private CallPriceService callPrice;
	@Autowired
	private MemberGpsCurrentService memberGpsCurrentService;

	// 智能找货/手动找货：
	public RemoteResponse getGoodsInfoList(Credential credential,
			Map<String, Object> map) {
		RemoteResponse res = new RemoteResponse();

		YslMember member = getYslMemberService().getYslMemberById(
				credential.getUSRID());
		if (!DC.UCERT_PS.equals(member.getDCT_UA())) {
			res.setMsg("您的账号尚未认证，请完善并认证您的资料后，使用抢好货功能来获取第一手好货源。");
			res.setSucc(0);
			return res;
		}
		//
		// if(!DC.UT_TW.equals(member.getDCT_UT())){
		// res.setMsg("您不是司机，不能使用此功能，如信息有误，请联系客服！");
		// res.setSucc(0);
		// return res;
		// }
		//

		// if(!isTrade()){
		// res.setMsg("trade closed");
		// res.setSucc(0);
		// return res;
		// }

		String Depart = (String) map.get("Depart");
		String targets = (String) map.get("Target");
		String TKLen = (String) map.get("TKLen");
		String TKType = (String) map.get("TKType");
		String queryType = (String) map.get("queryType");
		// 此处用的是偏移量，需要注意
		Integer offset = (Integer) map.get("offset");
		// 定义智能抢货的值为0，手动找货的参数为1；
		// log.error("info--offset----------------------------"+offset);
		// 设置默认查询条件

		List<GoodsListParameter> prs = new ArrayList<GoodsListParameter>();
		GSCriteria phCriteria = new GSCriteria();
		// phCriteria.setSORT("CREATE_TIME");
		phCriteria.setIS_DEALED(Constants.FALSE); // 非成交记录
		phCriteria.setIS_VALIDATED(1); // 有效的货源
		phCriteria.setPagesize(20);
		phCriteria.setPage(getPage(offset, phCriteria.getPagesize()));
		Map tempmap = new HashMap();
		if (offset > 0 && offset < phCriteria.getPagesize()) {
			// 无数据了
			tempmap.put("goodsSourceInfo", prs);
			res.setResult(tempmap);
			return res;
		}

		if (!StringUtils.hasText(queryType)) {
			queryType = "0";
		}
		// 判断是只能找货(0)，还是非智能
		if ("0".equals(queryType)) {
			// 智能

			// TODO: XXX
			// 智能找货的时候，需要通过车牌,先从t_tk_online中获取到车长，车型，长跑方向，作为默认的查询条件,以方便查询出来的数据更加的精确
			TkOnline tkOnline = tkOnLineService.query(credential,
					member.getTKPLATENO());
			if (null != tkOnline) {
				phCriteria.setDCT_TKLEN(tkOnline.getDCT_TKLEN());
				phCriteria.setDCT_TT(tkOnline.getDCT_TT());
				List<String> CT_TARGETS = new ArrayList<String>();
				if (null != tkOnline.getTKTARGET1()) {
					CT_TARGETS.add(tkOnline.getTKTARGET1());
				}
				if (null != tkOnline.getTKTARGET2()) {
					CT_TARGETS.add(tkOnline.getTKTARGET2());
				}
				if (null != tkOnline.getTKTARGET3()) {
					CT_TARGETS.add(tkOnline.getTKTARGET3());
				}
				if (null != CT_TARGETS && CT_TARGETS.size() > 0) {
					phCriteria.setCT_TARGETS(CT_TARGETS);
				}
			}

		} else {
			// 手工找货
			if (null != targets && !"".equals(targets.trim())) {
				String[] target = targets.trim().split(",");
				List<String> tempTGT = new ArrayList<String>();
				if (null != targets) {
					for (String t : target) {
						if (StringUtils.hasText(t)) {
							tempTGT.add(getPalcedDCTCode(credential, t));
						}
					}
				}
				if (null != tempTGT && tempTGT.size() > 0) {
					phCriteria.setCT_TARGETS(tempTGT);
				}
			}
			phCriteria.setDCT_TKLEN(TKLen);
			phCriteria.setDCT_TT(TKType);

		}

		// if (phCriteria.getCT_TARGETS() == null) {
		// res.setMsg("目的地不能为空");
		// res.setSucc(0);
		// return res;
		// }
		phCriteria.setCT_DEPART(getPalcedDCTCode(credential, Depart));
		List<GoodsSourceBO> goods = goodService.listByCriteria(credential,
				phCriteria);
		// 如果是智能匹配没有找到的话，去掉车长，去掉车型查找
		if ((goods == null || goods.size() <= 0) && "0".equals(queryType)) {
			phCriteria.setDCT_TKLEN(null);
			phCriteria.setDCT_TT(null);
			phCriteria.setCT_TARGETS(null);
			goods = goodService.listByCriteria(credential, phCriteria);
		}

		if (goods != null && goods.size() > 0) {

			for (GoodsSourceBO g : goods) {
				GoodsListParameter pr = new GoodsListParameter();
				pr.setGSID(g.getGSID());

				pr.setCT_TARGET(g.getCT_TARGET());

				pr.setCT_DEPART(g.getCT_DEPART());

				pr.setGSSCALE(g.getGSSCALE() != null ? g.getGSSCALE()
						.toString() : null);
				pr.setGSTYPE(g.getDCT_GSTYPE());
				pr.setGSUNIT(g.getGSUNIT() != null ? g.getGSUNIT().toString()
						: null);
				pr.setTKLEN(g.getDCT_TKLEN());
				pr.setGSORDERNO(g.getGSORDERNO().toString());
				pr.setGSSTS(TypeToDC.getGSSTS(g.getIS_DEALED()));
				pr.setCOUNT(g.getOnCount());
				pr.setTKTYPE(g.getDCT_TT());
				pr.setGSDESC(g.getGSDESC());
				pr.setSHIPPOINT(Integer.valueOf(g.getSHIPPOINT()));

				pr.setCALLPRICE(g.getCALLPRICE() != null ? g.getCALLPRICE()
						+ "" : "");

				// pr.setSHIPPOINT(Integer.valueOf(g.getSHIPPOINT().toString()));
				prs.add(pr);
			}

		}
		tempmap.put("goodsSourceInfo", prs);
		res.setResult(tempmap);
		return res;

	}

	// 货源详情：
	public RemoteResponse getGoodsInfoDetail(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		Integer gsid = (Integer) map.get("GsId");
		//1不隐藏报价，直接显示，0的话，需要处理
		Integer ISGSOWNER = (Integer) map.get("ISGSOWNER");
		if(ISGSOWNER==null){
			ISGSOWNER=0;
		}
		if (gsid == null || 0 == gsid) {
			res.setMsg("非法操作，您提供的查货单号不存在！");
			res.setSucc(0);
			return res;
		}

		GoodsSourceBO g = goodService.query(credential, Long.valueOf(gsid),
				true);
		if (g == null) {
			res.setMsg("您查找的货源不存在！");
			res.setSucc(0);
			return res;
		}

//		if (g.getIS_VALIDATED() == 0) {
//			res.setMsg("该货源不支持疯抢!");
//			res.setSucc(0);
//			return res;
//
//		}
		YslMember bo = yslMemberService.getYslMemberById(credential.getUSRID());
		GoodsSourceParameter pr = new GoodsSourceParameter();
		pr.setGSID(g.getGSID());
		pr.setCT_TARGET(g.getCT_TARGET());
		pr.setCT_DEPART(g.getCT_DEPART());
		pr.setGSSCALE(g.getGSSCALE() != null ? g.getGSSCALE().toString() : null);
		pr.setGSTYPE(g.getDCT_GSTYPE());
		pr.setGSUNIT(g.getGSUNIT() != null ? g.getGSUNIT().toString() : null);
		pr.setTKLEN(g.getDCT_TKLEN());
		pr.setGSORDER(g.getGSORDERNO().toString());
		pr.setDCT_GSLOAD(g.getDCT_GSLOAD());
		pr.setDCT_PAYWAY(g.getDCT_PAYWAY());
		pr.setGSSTS(TypeToDC.getGSSTS(g.getIS_DEALED()));
		pr.setGSDESC(g.getGSDESC());
		pr.setGSADDR(g.getGSADDR());
		pr.setYSLACCOUNT(g.getYSLACCOUNT());
		pr.setSHIPPOINT(Integer.valueOf(g.getSHIPPOINT()));
		pr.setGSLOADADDR(g.getGSADDR());
		pr.setGSLOADDATE(g.getGSLOADDATE());
		pr.setTKTYPE(g.getDCT_TT());
		pr.setGSMOBILE(g.getGSMOBILE());
		pr.setINFOPRICE(g.getGS_INFO_PRICE()!=null?g.getGS_INFO_PRICE().toString():"");
		pr.setTRANSPORTPRICE(g.getTRANSPORT_PRICE()!=null?g.getTRANSPORT_PRICE().toString():"");
		pr.setGSISSUEDATE(null != g.getCREATE_TIME() ? DateUtil.formatDate(
				g.getCREATE_TIME(), DateUtil.shortSdf) : "");
		pr.setGSMSGPRICE(g.getGS_INFO_PRICE() != null ? g.getGS_INFO_PRICE()
				.toString() : "");
		pr.setGSINFOPRICE(pr.getGSMSGPRICE());

		// 货主信息
		Shipper per = new Shipper();
		per.setUSRID(bo.getUSRID());
		per.setSHIPPERMOBILE(g.getGSMOBILE());
		per.setSHIPPERTEL(g.getGSTEL1());
		per.setSHIPPERNAME(g.getUSRREALNAME());
		per.setSHIPPERCOMPANY(g.getGSORGNAME());
		per.setSHIPPERADDR(g.getGSADDR());
		// if (null != bo.getPHOTOID()) {
		// per.setPHOTOURL(MobileConstants.YSX_PHOTO
		// + bo.getPHOTOID());
		// }
		pr.setSHiPPER(per);

		// 该货源抢货列表
		if (null != g.getPrices() && g.getPrices().size() > 0) {
			for (GoodsSourcePriceBO p : g.getPrices()) {
				Price price = new Price();
				price.setCREATE_TIME(null != p.getUPDATE_TIME() ? DateUtil
						.formatDate(p.getUPDATE_TIME(), DateUtil.shortSdf) : "");
				price.setMOBILE(p.getMOBILE());
				if (!credential.getUSRID().equals(p.getUSRID())) {
					String str_price = p.getPRICE() + "";
					if(ISGSOWNER==1){
						price.setUSRREALNAME(p.getUSERNAME());
						price.setPRICE(p.getPRICE() + "");

					}else{
						if (str_price.length() > 1) {
							price.setPRICE(StringUtil.replace(str_price, 1,
									(str_price.length() - 1)));
						} else {
							price.setPRICE(p.getPRICE() + "***");
						}
						if (p.getUSERNAME() != null && p.getUSERNAME().length() > 1) {
							price.setUSRREALNAME(p.getUSERNAME().substring(0, 1)
									+ "师傅");
						}
					}
				} else {
					price.setUSRREALNAME(p.getUSERNAME());
					price.setPRICE(p.getPRICE() + "");

				}
				price.setUSRID(p.getUSRID());
				price.setSORT(p.getRANK() != null ? Long.valueOf(p.getRANK())
						: null);
				price.setPRICEID(p.getPRICEID());
				LeftTime time = getOverTime(p.getUPDATE_TIME());
				price.setLEFTTIME(time.getLEFTTIME());
				price.setLEFTTIME2(time.getLEFTTIME2());
				// price.setSORT(p.gets)
				pr.getPRICES().add(price);
			}

		}

		Map tempmap = new HashMap();
		tempmap.put("goodsDetail", pr);
		res.setResult(tempmap);
		return res;

	}

	//
	public RemoteResponse callPrice(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		Integer USRID = (Integer) map.get("USRID");
		Integer GsId = (Integer) map.get("GsId");
		Integer price = (Integer) map.get("PRICE");
		if (GsId == null || 0 == GsId) {
			res.setMsg("非法操作，您提供的报价单号不存在！");
			res.setSucc(0);
			return res;
		}
		if (price == null) {
			res.setMsg("价格不能不空！");
			res.setSucc(0);
			return res;
		}
		 int count = callPrice.countMySuccessCallPrice(credential,
		 credential.getUSRID());
//		int count = 0;

		if (count > 0) {
			res.setSucc(0);
			res.setMsg("对不起，你今日已有成功交易，无法再次报价。如有需要，可电话联系货主，线下交易。");
			return res;
		}
		// 货源与用户的状态（0[可抢]，1[已被抢]，2[已报价]，3[货主撤消],4[已取消报价]）
		GoodsSourceBO good = goodService.query(credential, GsId, true);
		// ISVALIDATED ---0表示有效，1表示无效
		int result = 0;
		if (1 == good.getIS_DEALED()) {
			res.setSucc(0);
			res.setMsg("对不起，该货源已经成交，不能进行报价");
			return res;
		} else if (0 == good.getIS_VALIDATED()) {
			res.setSucc(0);
			res.setMsg("对不起，该货源已经不支持疯抢");
			return res;
		} else if (null != good.getUSRSTATUS() && 4 == good.getUSRSTATUS()) {
			res.setSucc(0);
			res.setMsg("对不起，您已经不能对该货源进行报价");
			return res;
		} else {
			HashMap<String, Long> paras2 = new HashMap<String, Long>();
			paras2.put("ORDERNO", good.getGSORDERNO());
			paras2.put("USRID", credential.getUSRID());
			GoodsSourcePriceBO goodsBO = goodService
					.selectByOrderNOAndUSRID(paras2);
			if (goodsBO == null) {
				goodsBO = new GoodsSourcePriceBO();
				goodsBO.setPRICEID(0L);
				goodsBO.setCREATE_BY(credential.getUSRID());
				goodsBO.setCREATE_TIME(getCommonService().getSystemTime());
				goodsBO.setUPDATE_TIME(getCommonService().getSystemTime());
				goodsBO.setDELETED(Short.valueOf("0"));
				goodsBO.setGSORDERNO(good.getGSORDERNO());
				goodsBO.setISDEAL(Short.valueOf("0"));
				goodsBO.setISVALIDATED(Short.valueOf("1"));
				YslMember member = getYslMemberService().getYslMemberById(
						credential.getUSRID());
				goodsBO.setMOBILE(member.getMOBILE1() != null ? member
						.getMOBILE1() : member.getTEL1());
				goodsBO.setPRICE(BigDecimal.valueOf(Long.valueOf(price)));
				goodsBO.setUSERNAME(member.getREALNAME());
				goodsBO.setUSRID(credential.getUSRID());
				goodsBO.setVERSION(1);
				try {
					// @ TODO 4.报价
					result = callPrice.insertCallPrice(credential, goodsBO);
				} catch (FreightException e) {
					res.setSucc(0);
					res.setMsg(e.getMessage());
					return res;
				}
			} else

				goodsBO.setCREATE_BY(credential.getUSRID());
			goodsBO.setUPDATE_TIME(getCommonService().getSystemTime());
			goodsBO.setUPDATE_BY(credential.getUSRID());
			goodsBO.setDELETED(Short.valueOf("0"));
			goodsBO.setISDEAL(Short.valueOf("0"));
			goodsBO.setISVALIDATED(Short.valueOf("1"));
			goodsBO.setVERSION(goodsBO.getVERSION() + 1);
			goodsBO.setPRICE(BigDecimal.valueOf(Long.valueOf(price)));
			try {
				result = callPrice.updateCallPrice(credential, goodsBO);
			} catch (FreightException e) {
				res.setSucc(0);
				res.setMsg(e.getMessage());
				return res;
			}

			//
			int autoDealed = 0;
			if (null != good.getTRANSPORT_PRICE()) {
				if (price.doubleValue() < good.getTRANSPORT_PRICE()
						.doubleValue()) {
					try {
						autoDealed = goodService.updateStatus(credential,
								good.getGSID(), Short.valueOf("1"),
								goodsBO.getPRICEID(), goodsBO.getVERSION());
					} catch (NumberFormatException e) {
						// log.error("auto update deal for successed exceptin.",e);
					} catch (FreightException e) {
						// log.error("auto update deal for successed exceptin.",e);
					}
				}
			}
			// log.debug("call price result." + result);
			// 报价记录保持成功，并且非自动成交的，则设置报价有效时间
			if (1 == result && 1 != autoDealed) {

				res.setMsg("恭喜您，报价成功");
			} else if (1 == result && 1 == autoDealed) {

				res.setMsg("恭喜您，您的成功抢到好货一批，请刷新页面！");
			} else {

				res.setMsg("对不起，报价失败");
			}
			return res;

		}

	}

	// 已经报价
	public RemoteResponse callPriceHistory(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		GSCriteria gs = new GSCriteria();
		// gs.setIS_VALIDATED(1); // 有效的货源
		gs.setIS_NOWDAY(1);
		gs.setPRICEUSRID(credential.getUSRID());
		// gs.setIS_DEALED(Constants.FALSE);
		gs.setPagesize(100);
		List<GoodsAndPriceBO> list = goodService.selectpriceAndGoods(gs);
		List<GoodsAndPricePatameter> results = new ArrayList<GoodsAndPricePatameter>();
		if (list != null && list.size() > 0) {
			for (GoodsAndPriceBO bo : list) {
				GoodsAndPricePatameter gp = new GoodsAndPricePatameter();
				gp.setGSID(bo.getGSID());
				gp.setCT_DEPART(bo.getCT_DEPART());
				gp.setCT_TARGET(bo.getCT_TARGET());
				gp.setGSSCALE(bo.getGSSCALE() != null ? bo.getGSSCALE()
						.toString() : null);
				gp.setGSTYPE(bo.getDCT_GSTYPE());
				gp.setGSUNIT(bo.getGSUNIT() != null ? bo.getGSUNIT().toString()
						: null);
				gp.setTKLEN(bo.getDCT_TKLEN());
				gp.setYSLACCOUNT(bo.getYSLACCOUNT());
				gp.setSHIPPOINT(Integer.valueOf(bo.getSHIPPOINT() != null ? bo
						.getSHIPPOINT() : 0));
				if (bo.getIS_VALIDATED() == 0) {
					bo.setIS_DEALED((short) 5);
				}
				if (bo.getIS_VALIDATED() != null && bo.getIS_VALIDATED() == 0) {
					bo.setIS_DEALED((short) 4);
				}
				if (bo.getPRICE_ISVALIDATED() != null && bo.getIS_DEALED() != 1) {
					if (bo.getPRICE_ISVALIDATED() == 1) {
						bo.setIS_DEALED((short) 2);
					} else {
						bo.setIS_DEALED((short) 3);
					}
				}

				gp.setGSSTS(TypeToDC.getGSSTS(bo.getIS_DEALED()));

				// price.setCREATE_TIME(null !=
				// p.getUPDATE_TIME()?DateUtil.formatDate(p.getUPDATE_TIME(),
				// DateUtil.shortSdf):"");
				Dict dict = getDictService().query(DC.QHH_EXCHANGEHOUR);
				String outtime = "30";
				if (dict != null) {
					outtime = dict.getDCTVALUE3();
				}
				gp.getPRICE().setCREATE_TIME(
						null != bo.getPRICE_UPDATE_TIME() ? DateUtil
								.formatDate(bo.getPRICE_UPDATE_TIME(),
										DateUtil.shortSdf) : "");
				gp.getPRICE().setMOBILE(bo.getMOBILE());
				String str_price = bo.getPRICE() + "";
				gp.getPRICE().setPRICE(str_price);
				gp.getPRICE().setUSRID(bo.getPRICE_USRID());

				gp.getPRICE().setUSRREALNAME(bo.getUSERNAME());
				gp.getPRICE().setPRICE(bo.getPRICE() + "");
				gp.getPRICE().setPRICEID(bo.getPRICEID());
				LeftTime time = getOverTime(bo.getPRICE_UPDATE_TIME());
				gp.getPRICE().setLEFTTIME(time.getLEFTTIME());
				gp.getPRICE().setLEFTTIME2(time.getLEFTTIME2());
				gp.getPRICE().setSORT(bo.getPAIMING());
				// gp.getPRICE().setPRICEID(bo.getPRICEID());
				results.add(gp);

			}

		} else {
			list = new ArrayList<GoodsAndPriceBO>();
		}
		Map tempmap = new HashMap();

		tempmap.put("Detail", results);
		res.setResult(tempmap);
		return res;

	}

	public RemoteResponse canceCallPrice(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		Integer USRID = (Integer) map.get("USRID");
		String strs = (String) map.get("PRICEIDS");
		List<Long> list = new ArrayList<Long>();
		if (StringUtils.hasText(strs)) {
			for (String id : strs.split(",")) {
				list.add(Long.valueOf(id));
			}

		}
		callPrice.clearCallPrice(credential, list);

		return res;

	}

	public RemoteResponse getDealInfo(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		Integer USRID = (Integer) map.get("USRID");

		GSCriteria gs = new GSCriteria();
		gs.setIS_VALIDATED(1); // 有效的货源
		gs.setPRICEUSRID(credential.getUSRID());
		gs.setIS_DEALED(Constants.TRUE);
		gs.setPRICE_ISDEAL(Constants.TRUE);
		List<GoodsAndPriceBO> list = goodService.selectpriceAndGoods(gs);

		if (list == null || list.size() <= 0) {
			res.setSucc(0);
			return res;
		}

		GoodsAndPriceBO bo = list.get(0);

		GoodsAndPricePatameter gp = new GoodsAndPricePatameter();
		gp.setCT_DEPART(bo.getCT_DEPART());
		gp.setCT_TARGET(bo.getCT_TARGET());
		gp.setGSSCALE(bo.getGSSCALE() != null ? bo.getGSSCALE().toString()
				: null);
		gp.setGSTYPE(bo.getDCT_GSTYPE());
		gp.setGSUNIT(bo.getGSUNIT() != null ? bo.getGSUNIT().toString() : null);
		gp.setTKLEN(bo.getDCT_TKLEN());
		gp.setGSSTS(TypeToDC.getGSSTS(bo.getIS_DEALED()));
		gp.setGSID(bo.getGSID());
		Map tempmap = new HashMap();
		tempmap.put("dealInfo", gp);
		res.setResult(tempmap);
		return res;

	}

	public RemoteResponse getDealDetailInfo(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		Integer GSID = (Integer) map.get("GSID");
		GSCriteria gs = new GSCriteria();
		gs.setGSID(Long.valueOf(GSID));
		// gs.setIS_VALIDATED(1); // 有效的货源
		gs.setPRICEUSRID(credential.getUSRID());
		gs.setIS_DEALED(Constants.TRUE);
		gs.setPRICE_ISDEAL(Constants.TRUE);
		List<GoodsAndPriceBO> list = goodService.selectpriceAndGoods(gs);

		if (list == null || list.size() <= 0) {
			res.setSucc(0);
			return res;
		}
		GoodsAndPriceBO bo = list.get(0);
		YslMember member = yslMemberService.getYslMemberById(bo.getUSRID());
		GoodsAndPricePatameter gp = new GoodsAndPricePatameter();
		gp.setCT_DEPART(bo.getCT_DEPART());
		gp.setCT_TARGET(bo.getCT_TARGET());
		gp.setGSSCALE(bo.getGSSCALE() != null ? bo.getGSSCALE().toString()
				: null);
		gp.setGSTYPE(bo.getDCT_GSTYPE());
		gp.setGSUNIT(bo.getGSUNIT() != null ? bo.getGSUNIT().toString() : null);
		gp.setTKLEN(bo.getDCT_TKLEN());
		gp.setGSSTS(TypeToDC.getGSSTS(bo.getIS_DEALED()));
		gp.setGSID(bo.getGSID());
		gp.setGSORDERNO(bo.getGSORDERNO().toString());
		gp.setDCT_GSLOAD(bo.getDCT_GSLOAD());
		gp.setDCT_PAYWAY(bo.getDCT_PAYWAY());
		gp.setGSMSGPRICE(bo.getGS_INFO_PRICE() != null ? bo.getGS_INFO_PRICE()
				.toString() : "");
		gp.setSUBMITPRICE(bo.getPRICE() != null ? bo.getPRICE().toString() : "");
		gp.setYSLACCOUNT(bo.getYSLACCOUNT());
		gp.setGSLOADADDR(bo.getGSADDR());
		gp.setGSLOADDATE(bo.getGSLOADDATE());

		LeftTime time = getOverTime(bo.getPRICE_UPDATE_TIME());
		gp.setLEFTTIME(time.getLEFTTIME());
		gp.setLEFTTIME2(time.getLEFTTIME2());
		gp.setGSDESC(bo.getGSDESC());

		// 货主信息
		Shipper per = new Shipper();
		per.setUSRID(bo.getUSRID());
		per.setSHIPPERMOBILE(bo.getGSMOBILE());
		per.setSHIPPERTEL(bo.getGSTEL1());
		per.setSHIPPERNAME(bo.getUSRREALNAME());
		per.setSHIPPERCOMPANY(bo.getGSORGNAME());
		per.setSHIPPERADDR(bo.getGSADDR());
		if (null != member.getPHOTOID()) {
			per.setPHOTOURL(MobileConstants.YSX_PHOTO + member.getPHOTOID());
		}
		gp.setSHiPPER(per);
		Map tempmap = new HashMap();
		tempmap.put("dealDetailInfo", gp);
		res.setResult(tempmap);
		return res;

	}

	public RemoteResponse getExchangeHour(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();

		Map tempmap = new HashMap();

		Dict dict = getDictService().query(DC.QHH_EXCHANGEHOUR);

		if (dict != null) {
			tempmap.put("START_TIME", dict.getDCTVALUE1());
			tempmap.put("END_TIME", dict.getDCTVALUE2());
		} else {
			tempmap.put("START_TIME", "");
			tempmap.put("END_TIME", "");
		}

		res.setResult(tempmap);
		return res;

	}

	// 发布货源
	public RemoteResponse publish(Credential credential, Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		// 货源拥有者的ID号
		Integer OWNERID = (Integer) map.get("OWNERID");
		Integer USRID = (Integer) map.get("USRID");
		String Depart = (String) map.get("Depart");
		String Target = (String) map.get("Target");
		
		if (!StringUtils.hasText(Depart)) {
			res.setSucc(0);
			res.setMsg("目的地不能为空！");
			return res;
		}
		
		if (!StringUtils.hasText(Target)) {
			res.setSucc(0);
			res.setMsg("出发地不能为空！");
			return res;
		}
		
		String GSType = (String) map.get("GSType");
		String GSSCALE = (String) map.get("GSSCALE");
		String GSUNIT = (String) map.get("GSUNIT");
		String TKType = (String) map.get("TKType");
		String TKLen = (String) map.get("TKLen");
		// 支付方式
		String PayWay = (String) map.get("PayWay");
		// 装卸方式
		String GSLOAD = (String) map.get("GSLOAD");
		// 装货日期
		String GSLOADDATE = (String) map.get("GSLOADDATE");
		// 信息费；
		String GSINFOPRICE = (String) map.get("GSINFOPRICE");
		// 可接受运价
		String TRANSPORTPRICE = (String) map.get("TRANSPORTPRICE");
		// 用户发布的货源，有可能是属于转发的，也有可能是自发的
		String INFSRC = (String) map.get("INFSRC");
		String GSDESC = (String) map.get("GSDESC");

		GoodsSource gs = new GoodsSource();
		gs.setUSRID(credential.getUSRID());
		if (OWNERID != null) {
			gs.setOWNERID(Long.valueOf(OWNERID));
		}
		gs.setCT_DEPART(getPalcedDCTCode(credential, Depart));
		gs.setCT_TARGET(getPalcedDCTCode(credential, Target));
		// gs.setgs
		Dict dict = getDictService().query(DC.QHH_EXCHANGEHOUR);
		if (!isTrade(dict)) {
			try {
				String str = addDay(Calendar.DATE, 1);
				str = str + " " + dict.getDCTVALUE1();
				Calendar c = Calendar.getInstance();

				c.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(str));
				c.add(Calendar.MINUTE, 1);
				gs.setGSISSUEDATE(c.getTime());
				gs.setCREATE_TIME(c.getTime());
				gs.setUPDATE_TIME(c.getTime());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		gs.setDCT_GSTYPE(GSType);
		if (StringUtils.hasText(GSSCALE)) {
			gs.setGSSCALE(new BigDecimal(GSSCALE));
		}
		if (StringUtils.hasText(GSUNIT)) {
			gs.setGSUNIT(new BigDecimal(GSUNIT));
		}
		gs.setDCT_TKLEN(TKLen);
		gs.setDCT_TT(TKType);
		gs.setDCT_PAYWAY(PayWay);
		gs.setDCT_GSLOAD(GSLOAD);
		gs.setGSLOADDATE(GSLOADDATE);
		if(GSINFOPRICE!=null){
		  gs.setGS_INFO_PRICE(new BigDecimal(GSINFOPRICE));
		}
		if(StringUtils.hasText(TRANSPORTPRICE)){
			gs.setTRANSPORT_PRICE(new BigDecimal(TRANSPORTPRICE));
		}
		
		gs.setDCT_INFSRC(INFSRC);
		gs.setGSDESC(GSDESC);
		gs.setIS_VALIDATED(Constants.TRUE);
		goodService.add(credential, gs);

		return res;

	}

	private String cityName(String cityCODE) {
		String c = "";
		if (null != cityCODE) {
			c = getCityService().translate(cityCODE, true, 2);
			if (null != c) {
				String[] tc = c.split(" ");
				if (tc.length > 1) {
					c = tc[1];
				}
			} else {
				c = "";
			}

		}
		return c;
	}

	public RemoteResponse callPriceSubmit(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		GSCriteria gs = new GSCriteria();
		// gs.setIS_VALIDATED(1); // 有效的货源
		gs.setPRICEUSRID(credential.getUSRID());
		gs.setIS_DEALED(Constants.TRUE);
		gs.setPRICE_ISDEAL(Constants.TRUE);
		gs.setPagesize(100);
		List<GoodsAndPriceBO> list = goodService.selectpriceAndGoods(gs);
		List<GoodsAndPricePatameter> results = new ArrayList<GoodsAndPricePatameter>();
		if (list != null && list.size() > 0) {
			for (GoodsAndPriceBO bo : list) {
				GoodsAndPricePatameter gp = new GoodsAndPricePatameter();
				gp.setCT_DEPART(bo.getCT_DEPART());
				gp.setCT_TARGET(bo.getCT_TARGET());
				gp.setGSSCALE(bo.getGSSCALE() != null ? bo.getGSSCALE()
						.toString() : null);
				gp.setGSTYPE(bo.getDCT_GSTYPE());
				gp.setGSUNIT(bo.getGSUNIT() != null ? bo.getGSUNIT().toString()
						: null);
				gp.setTKLEN(bo.getDCT_TKLEN());
				gp.setTKTYPE(bo.getDCT_TT());
				gp.setGSSTS(TypeToDC.getGSSTS(bo.getIS_DEALED()));
				gp.setGSID(bo.getGSID());
				gp.setSHIPPOINT(Integer.valueOf(bo.getSHIPPOINT()));
				gp.setSHIPPERNAME(bo.getUSRREALNAME());
				gp.setGSORDERNO(bo.getGSORDERNO().toString());
				gp.setPRICEDATE(null != bo.getPRICE_UPDATE_TIME() ? DateUtil
						.formatDate(bo.getPRICE_UPDATE_TIME(),
								DateUtil.shortSdf) : "");

				// gp.getPRICE().setPRICEID(bo.getPRICEID());
				// 价格放到submitprice里边
				gp.setSUBMITPRICE(bo.getPRICE() + "");
				// gp.getPRICE().setPRICEID(bo.getPRICEID());
				results.add(gp);

			}

		} else {
			list = new ArrayList<GoodsAndPriceBO>();
		}
		Map tempmap = new HashMap();

		tempmap.put("Detail", results);
		res.setResult(tempmap);
		return res;

	}

	public RemoteResponse getStatInfo(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		Map tempmap = new HashMap();
		GSCriteria criteria = new GSCriteria();
		criteria.setIS_VALIDATED(1);

		String cityCode = (String) map.get("cityCode");
		// 今日发布的有效货源条数
		int countGs = goodService.countByCriteria(credential, criteria);

		// 今日自己发布的总货源条数；
		criteria.setUSRID(credential.getUSRID());
		int countMyGs = goodService.countByCriteria(credential, criteria);

		// 周边的车源信息
//		TkOnlineCriteria cri = new TkOnlineCriteria();
//		cri.setCitycode(cityCode);		
		MemberGpsCurrentCriteria cri = new MemberGpsCurrentCriteria();
//		cri.setCITY(cityCode);
		int aroundTs = memberGpsCurrentService.countByCurrentCriteria(cri);
 //       int aroundTs = tkOnLineService.countNowTk(cri);
		YslMemberBO bo = yslMemberService.getYslMemberBO(credential.getUSRID());
		GsStatInfo info = new GsStatInfo();
		info.setCountOwnerGs(countMyGs);
		info.setTotalGs(countGs-countMyGs);
		info.setAroundTs(aroundTs);
		info.setCompanyName(bo.getCOMPANYNAME());
		info.setPoint(bo.getYSLACTIVEDEG());
		info.setYslAccount(bo.getYSLACCOUNT());
		info.setShipPoint(bo.getSHIPPOINT());
		info.setShipCount(bo.getBSHIPCOUNT());
		if (bo.getPHOTOID() != null) {
			info
					.setPHOTOURL(
							MobileConstants.YSX_PHOTO
									+ bo.getPHOTOID());
		}
		
		tempmap.put("GsStatInfo", info);
		res.setResult(tempmap);
		return res;

	}

	// 货主查看司机位置
	public RemoteResponse getCurrentGPS(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		String mobile = (String) map.get("TKMOBILE");
		Integer gsid = (Integer) map.get("gsid");

		Map tempmap = new HashMap();
		MemberGpsCurrent current = memberGpsCurrentService
				.queryNowPlaceWithGoodsSource(mobile, Long.valueOf(gsid));
		CurrentGPS gps = new CurrentGPS();
		if (current != null) {
			
			gps.setLAT(current.getLAT());
			gps.setLNG(current.getLNG());
			gps.setMOBILE(current.getMOBILE());
			gps.setPOSITION(current.getPOSITION());
			gps.setPOSTIME(null != current.getUPDATE_TIME() ? DateUtil
					.formatDate(current.getUPDATE_TIME(), DateUtil.shortSdf)
					: "");
		
			
			

			tempmap.put("currentGPS", gps);
		} else {
			tempmap.put("currentGPS", new CurrentGPS());
		}

		Long id = yslMemberService.getUsridByPhone(mobile);
		YslMemberBO bo =yslMemberService.getYslMemberBO(id);
		gps.setRealName(bo.getREALNAME());
		gps.setYslAccount(bo.getYSLACCOUNT());
		gps.setCARRYPOINT(bo.getCARRYPOINT());
		gps.setCompanyName(bo.getCOMPANYNAME());
		gps.setBCARRYCOUNT(bo.getBCARRYCOUNT());
		gps.setHistoryCount(callPrice.countMyHistorySuccPriceRecord(id));
		gps.setMobile(mobile);
		if (bo.getPHOTOID() != null) {
			gps
					.setPHOTOURL(
							MobileConstants.YSX_PHOTO
									+ bo.getPHOTOID());
		}
		
		res.setResult(tempmap);
		return res;

	}

	// 货主主动触发成交接口
	public RemoteResponse confirmDeal(Credential credential,
			Map<String, Object> map) {

		Integer gsid = (Integer) map.get("GSID");
		
		Object PRICEID = (Object) map.get("PRICEID");
		RemoteResponse res = new RemoteResponse();
		try {
			goodService.confirmDeal(credential, Long.valueOf(gsid),
					Long.valueOf(PRICEID.toString()));
		} catch (FreightException e) {
			log.error(e.getMessage());
			res.setMsg(e.getMessage());
		    res.setSucc(0);
		}
		return res;

	}
	
	
	//已成交的货源详情
	public RemoteResponse getSubmitGoodsInfoDetail(Credential credential, Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		Integer gsid = (Integer) map.get("GSID");
		Map tempmap = new HashMap();
		
		GoodsSourceBO goods = goodService.getSubmitGoodsInfoDetail(Long.valueOf(gsid));
		if(goods==null){
			tempmap.put("goodsDetail", null);
			res.setResult(tempmap);
		}else{
		
			GoodsInfoWithTruckParameter  parameter = new GoodsInfoWithTruckParameter();
			parameter.setGSID(goods.getGSID());
			parameter.setUSRID(goods.getUSRID());
			parameter.setGSORDERNO(goods.getGSORDERNO().toString());
			parameter.setCT_DEPART(goods.getCT_DEPART());
			parameter.setCT_TARGET(goods.getCT_TARGET());
			parameter.setGSTYPE(goods.getDCT_GSTYPE());
			parameter.setGSUNIT(goods.getGSUNIT()!=null?goods.getGSUNIT().toString():"");
			parameter.setTKLEN(goods.getDCT_TKLEN());
			parameter.setINFSRC(goods.getDCT_INFSRC());
			parameter.setCOUNTCALLUSR(goods.getTSCOUNT());
			parameter.setSUBMITPRICE(goods.getCALLPRICE()!=null?goods.getCALLPRICE().toString():"");
			parameter.setCALLUSRREALNAME(goods.getCALLUSRREALNAME());
			parameter.setGSSTS(TypeToDC.getGSSTS(goods.getIS_DEALED()));
			parameter.setGSSCALE(goods.getGSSCALE()!=null?goods.getGSSCALE().toString():"");
			parameter.setCREATE_TIME(
					null != goods.getUPDATE_TIME() ? DateUtil
							.formatDate(goods.getUPDATE_TIME(),
									DateUtil.shortSdf) : "");
			parameter.setGSLOADDATE(goods.getGSLOADDATE());
			parameter.setINFSRC(goods.getDCT_INFSRC());
			parameter.setGSISSUEDATE(goods.getCREATE_TIME()!=null? DateUtil
							.formatDate(goods.getCREATE_TIME(),
									DateUtil.shortSdf) : "");
	
	        parameter.setDCT_GSLOAD(goods.getDCT_GSLOAD());
	        parameter.setDCT_PAYWAY(goods.getDCT_PAYWAY());
	        
	        YslMemberBO bo = yslMemberService.getYslMemberBO(goods.getCALLUSRID());
	        Carrier  carrier = new Carrier();
	        carrier.setCARRIERADDR(bo.getBUSIADDRESS());
	        carrier.setCARRIERCOMPANY(bo.getCOMPANYNAME());
	        carrier.setCARRIERMOBILE(bo.getMOBILE1());
	        carrier.setCARRIERTEL(bo.getTEL1());
	        carrier.setCARRYCOUNT(bo.getBCARRYCOUNT());
	        carrier.setCARRYPOINT(bo.getCARRYPOINT());
	        //抢货人 ，//历史成交的货源数
	        
	        if (bo.getPHOTOID() != null) {
	        	carrier
						.setPHOTOURL(
								MobileConstants.YSX_PHOTO
										+ bo.getPHOTOID());
			}
	        carrier.setUSRID(bo.getUSRID());
	        carrier.setHISTORYSUBMIT(callPrice.countMyHistorySuccPriceRecord(goods.getCALLUSRID()));
	        parameter.setCARRIER(carrier);
			tempmap.put("goodsDetail", parameter);
			res.setResult(tempmap);
		}
		return res;

	}

	
	//获取同行货源信息
	public RemoteResponse getPeerGoodsInfo(Credential credential, Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();

		Integer USRID = (Integer) map.get("USRID");	
		String CITYCODE = (String) map.get("CITYCODE");	
		CITYCODE=getPalcedDCTCode(credential, CITYCODE);
	
		// 此处用的是偏移量，需要注意
		Integer offset = (Integer) map.get("offset");
		GSCriteria criteria = new GSCriteria();
		criteria.setPagesize(20);
		criteria.setIS_VALIDATED(1);
		criteria.setNOTUSRID(credential.getUSRID());
		criteria.setPage(getPage(offset, criteria.getPagesize()));
		criteria.setCT_DEPART(CITYCODE);
//	criteria.setUSRID(credential.getUSRID());
		Map tempmap = new HashMap();
		//默认为有数据
	    boolean has=true;
		if (offset > 0 && offset < criteria.getPagesize()) {
			// 无数据了
			has=false;
		}		
		List<GoodsSourceBO> bos = goodService.getCurrentGoodsInfo(criteria);
        if(bos==null||bos.size()<=0){
        	has=false;
        }
        if(has){
        	List<GoodsInfoParameter> pts = new ArrayList<GoodsInfoParameter>();
        	for(GoodsSourceBO goods:bos){
        		GoodsInfoParameter  parameter = new GoodsInfoParameter();
        		parameter.setGSID(goods.getGSID());
        		parameter.setUSRID(goods.getUSRID());
        		parameter.setORDERNUM(goods.getGSORDERNO().toString());
        		parameter.setCONTACTTEL(goods.getGSMOBILE());
        		parameter.setGSORGNAME(goods.getCOMPANYNAME()!=null?goods.getCOMPANYNAME():goods.getUSRREALNAME());
        		parameter.setDepart(goods.getCT_DEPART());
        		parameter.setTarget(goods.getCT_TARGET());
        		parameter.setTKType(goods.getDCT_TT());
        		parameter.setGSSCALE(goods.getGSSCALE() != null ? goods.getGSSCALE()
						.toString() : null);
        		parameter.setGSType(goods.getDCT_GSTYPE());
        		parameter.setGSUNIT(goods.getGSUNIT()!=null?goods.getGSUNIT().toString():"");
        		parameter.setTKLen(goods.getDCT_TKLEN());
        		parameter.setINFOPRICE(goods.getGS_INFO_PRICE()!=null?goods.getGS_INFO_PRICE().toString():"");
        		parameter.setTRANSPORTPRICE(goods.getTRANSPORT_PRICE()!=null?goods.getTRANSPORT_PRICE().toString():"");
        		parameter.setINFSRC(goods.getDCT_INFSRC());
        		parameter.setCOUNTCALLPRICE(goods.getOnCount());
        		parameter.setCALLPRICE(goods.getCALLPRICE()!=null?goods.getCALLPRICE().toString():"");
        		parameter.setCALLUSERPHONENUMBER(goods.getCALLMOBILE());
        		parameter.setCALLUSRREALNAME(goods.getCALLUSRREALNAME());
        		parameter.setGSSTS(TypeToDC.getGSSTS(goods.getIS_DEALED()));
        		parameter.setCREATE_TIME(
						null != goods.getUPDATE_TIME() ? DateUtil
								.formatDate(goods.getUPDATE_TIME(),
										DateUtil.shortSdf) : "");
        		if(goods.getCALLUSRID()!=null){
         		   parameter.setHISTORYSUBMIT(callPrice.countMyHistorySuccPriceRecord(goods.getCALLUSRID()));
         		}
         		
        		
        		pts.add(parameter);
        	}
        	tempmap.put("goodsInfo", pts);
        }else{
        	tempmap.put("goodsInfo", new ArrayList<GoodsInfoParameter>());
        }
	
		res.setResult(tempmap);
		return res;

	}
	
	
	public RemoteResponse getCurrentGoodsInfo(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();

		Integer USRID = (Integer) map.get("USRID");	
	
		// 此处用的是偏移量，需要注意
		Integer offset = (Integer) map.get("offset");
		if(offset==null){
			offset=0;
		}
		GSCriteria criteria = new GSCriteria();
		criteria.setPagesize(20);
		criteria.setIS_VALIDATED(1);
		criteria.setPage(getPage(offset, criteria.getPagesize()));
		criteria.setUSRID(credential.getUSRID());
		Map tempmap = new HashMap();
		//默认为有数据
	    boolean has=true;
		if (offset > 0 && offset < criteria.getPagesize()) {
			// 无数据了
			has=false;
		}		
		List<GoodsSourceBO> bos = goodService.getCurrentGoodsInfo(criteria);
        if(bos==null||bos.size()<=0){
        	has=false;
        }
        if(has){
        	List<GoodsInfoParameter> pts = new ArrayList<GoodsInfoParameter>();
        	for(GoodsSourceBO goods:bos){
        		GoodsInfoParameter  parameter = new GoodsInfoParameter();
        		parameter.setGSID(goods.getGSID());
        		parameter.setUSRID(goods.getUSRID());
        		parameter.setORDERNUM(goods.getGSORDERNO().toString());
        		parameter.setDepart(goods.getCT_DEPART());
        		parameter.setTarget(goods.getCT_TARGET());
        		parameter.setGSType(goods.getDCT_GSTYPE());
        		parameter.setTKType(goods.getDCT_TT());
        		parameter.setGSSCALE(goods.getGSSCALE() != null ? goods.getGSSCALE()
						.toString() : null);
        		parameter.setGSUNIT(goods.getGSUNIT()!=null?goods.getGSUNIT().toString():"");
        		parameter.setTKLen(goods.getDCT_TKLEN());
        		parameter.setINFSRC(goods.getDCT_INFSRC());
        		parameter.setCOUNTCALLPRICE(goods.getOnCount());
        		parameter.setINFOPRICE(goods.getGS_INFO_PRICE()!=null?goods.getGS_INFO_PRICE().toString():"");
        		parameter.setTRANSPORTPRICE(goods.getTRANSPORT_PRICE()!=null?goods.getTRANSPORT_PRICE().toString():"");
        		parameter.setCALLPRICE(goods.getCALLPRICE()!=null?goods.getCALLPRICE().toString():"");
        		parameter.setCALLUSRREALNAME(goods.getCALLUSRREALNAME());
        		parameter.setCALLUSERPHONENUMBER(goods.getCALLMOBILE());
        		parameter.setGSSTS(TypeToDC.getGSSTS(goods.getIS_DEALED()));
        		parameter.setGSORGNAME(goods.getCOMPANYNAME()!=null?goods.getCOMPANYNAME():goods.getUSRREALNAME());
        		parameter.setCREATE_TIME(
						null != goods.getUPDATE_TIME() ? DateUtil
								.formatDate(goods.getUPDATE_TIME(),
										DateUtil.shortSdf) : "");
        		if(goods.getCALLUSRID()!=null){
         		   parameter.setHISTORYSUBMIT(callPrice.countMyHistorySuccPriceRecord(goods.getCALLUSRID()));
         		}
         		
        		
        		pts.add(parameter);
        	}
        	tempmap.put("goodsInfo", pts);
        }else{
        	tempmap.put("goodsInfo", new ArrayList<GoodsInfoParameter>());
        }
	
		res.setResult(tempmap);
		return res;

	}
	
	
	
	
   //撤销货源
	public RemoteResponse updateGoodsStatus(Credential credential, Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		String status = (String) map.get("Status");
		Integer GSID = (Integer) map.get("GSID");
		Map tempmap = new HashMap();
        
		goodService.updateGoodsStatus(Long.valueOf(GSID), Long.valueOf(status));
		return res;

	}
	
	//历史货源
	public RemoteResponse getHistoryGoodsInfo(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();

		Integer USRID = (Integer) map.get("USRID");	
		String GSSTS = (String) map.get("GSSTS");	
	
		// 此处用的是偏移量，需要注意
		Integer offset = (Integer) map.get("offset");
		GSCriteria criteria = new GSCriteria();
		//1表示已经交易，0 表示为成交货源
		if("1".equals(GSSTS)){
			criteria.setIS_DEALED(Constants.TRUE);
		}else{
			criteria.setIS_DEALED(Constants.FALSE);
		}
		criteria.setUSRID(credential.getUSRID());
		criteria.setPagesize(20);
		criteria.setPage(getPage(offset, criteria.getPagesize()));
		
		int count = goodService.getHistoryGoodsCount(criteria);
		Map tempmap = new HashMap();
		//默认为有数据
	    boolean has=true;
		if (offset > 0 && offset < criteria.getPagesize()) {
			// 无数据了
			has=false;
		}		
		List<GoodsSourceBO> bos = goodService.getHistoryGoodsInfo(criteria);
        if(bos==null||bos.size()<=0){
        	has=false;
        }
        if(has){
        	List<GoodsInfoParameter> pts = new ArrayList<GoodsInfoParameter>();
        	for(GoodsSourceBO goods:bos){
        		GoodsInfoParameter  parameter = new GoodsInfoParameter();
        		parameter.setGSID(goods.getGSID());
        		parameter.setUSRID(goods.getUSRID());
        		parameter.setORDERNUM(goods.getGSORDERNO().toString());
        		parameter.setDepart(goods.getCT_DEPART());
        		parameter.setTarget(goods.getCT_TARGET());
        		parameter.setGSType(goods.getDCT_GSTYPE());
        		parameter.setGSUNIT(goods.getGSUNIT()!=null?goods.getGSUNIT().toString():"");
        		parameter.setGSSCALE(goods.getGSSCALE() != null ? goods.getGSSCALE()
						.toString() : null);
        		parameter.setTKLen(goods.getDCT_TKLEN());
        		parameter.setINFSRC(goods.getDCT_INFSRC());
        		parameter.setCOUNTCALLPRICE(goods.getOnCount());
        		parameter.setCALLPRICE(goods.getCALLPRICE()!=null?goods.getCALLPRICE().toString():"");
        		parameter.setCALLUSRREALNAME(goods.getCALLUSRREALNAME());
        		parameter.setGSSTS(TypeToDC.getGSSTS(goods.getIS_DEALED()));
        		parameter.setCALLUSERPHONENUMBER(goods.getCALLMOBILE());
        		parameter.setINFOPRICE(goods.getGS_INFO_PRICE()!=null?goods.getGS_INFO_PRICE().toString():"");
        		parameter.setTRANSPORTPRICE(goods.getTRANSPORT_PRICE()!=null?goods.getTRANSPORT_PRICE().toString():"");
        		parameter.setCONTACTTEL(goods.getGSMOBILE());
        		parameter.setGSORGNAME(goods.getCOMPANYNAME()!=null?goods.getCOMPANYNAME():goods.getUSRREALNAME());
        		
//        		Date  date = goods.getUPDATE_TIME();
//        	    String dateStr=	DateUtil
//				.formatDate(goods.getUPDATE_TIME(),
//						DateUtil.longSdf);
//        		if(!dateStr.equals(DateUtil
//        				.formatDate(new Date(),
//        						DateUtil.longSdf))){
//        			parameter.set
//        			
//        		}
//        		
        		parameter.setCREATE_TIME(
						null != goods.getUPDATE_TIME() ? DateUtil
								.formatDate(goods.getUPDATE_TIME(),
										DateUtil.shortSdf) : "");
        		
        		if(goods.getCALLUSRID()!=null){
        		   parameter.setHISTORYSUBMIT(callPrice.countMyHistorySuccPriceRecord(goods.getCALLUSRID()));
        		}
        		
        		pts.add(parameter);
        	}
        	tempmap.put("goodsInfo", pts);
        	tempmap.put("TotalInfoCount",count );
        }else{
        	tempmap.put("goodsInfo", new ArrayList<GoodsInfoParameter>());
        }
	
		res.setResult(tempmap);
		return res;

	}
	
	
	
	
	
	//根据给定的条件，搜索货源
	public RemoteResponse searchGoodsSourceInfo(Credential credential,
			Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();

		Integer USRID = (Integer) map.get("USRID");	
		String DEPART = (String) map.get("DEPART");	
		String TARGET = (String) map.get("TARGET");	
		String GSType = (String) map.get("GSType");	
		String TKLen = (String) map.get("TKLen");	
	
		// 此处用的是偏移量，需要注意
		Integer offset = (Integer) map.get("offset");
		if(offset==null){
			offset=0;
		}
		GSCriteria criteria = new GSCriteria();
		criteria.setPagesize(20);
		criteria.setIS_VALIDATED(1);
		criteria.setCT_DEPART(DEPART);
		criteria.setCT_TARGET(TARGET);
		criteria.setDCT_GSTYPE(GSType);
		criteria.setDCT_TKLEN(TKLen);
		criteria.setPage(getPage(offset, criteria.getPagesize()));
//		criteria.setUSRID(credential.getUSRID());
		Map tempmap = new HashMap();
		//默认为有数据
	    boolean has=true;
		if (offset > 0 && offset < criteria.getPagesize()) {
			// 无数据了
			has=false;
		}		
		List<GoodsSourceBO> bos = goodService.getCurrentGoodsInfo(criteria);
        if(bos==null||bos.size()<=0){
        	has=false;
        }
        if(has){
        	List<GoodsInfoParameter> pts = new ArrayList<GoodsInfoParameter>();
        	for(GoodsSourceBO goods:bos){
        		GoodsInfoParameter  parameter = new GoodsInfoParameter();
        		parameter.setGSID(goods.getGSID());
        		parameter.setUSRID(goods.getUSRID());
        		parameter.setORDERNUM(goods.getGSORDERNO().toString());
        		parameter.setDepart(goods.getCT_DEPART());
        		parameter.setTarget(goods.getCT_TARGET());
        		parameter.setGSType(goods.getDCT_GSTYPE());
        		parameter.setTKType(goods.getDCT_TT());
        		parameter.setGSSCALE(goods.getGSSCALE() != null ? goods.getGSSCALE()
						.toString() : null);
        		parameter.setGSUNIT(goods.getGSUNIT()!=null?goods.getGSUNIT().toString():"");
        		parameter.setTKLen(goods.getDCT_TKLEN());
        		parameter.setINFSRC(goods.getDCT_INFSRC());
        		//报价人数
        		parameter.setCOUNTCALLPRICE(goods.getOnCount());
        		parameter.setCALLPRICE(goods.getCALLPRICE()!=null?goods.getCALLPRICE().toString():"");
        		parameter.setCALLUSRREALNAME(goods.getCALLUSRREALNAME());
        		parameter.setGSSTS(TypeToDC.getGSSTS(goods.getIS_DEALED()));
        		parameter.setCREATE_TIME(
						null != goods.getUPDATE_TIME() ? DateUtil
								.formatDate(goods.getUPDATE_TIME(),
										DateUtil.shortSdf) : "");
        		parameter.setGSORGNAME(goods.getCOMPANYNAME()!=null?goods.getCOMPANYNAME():goods.getUSRREALNAME());
        		
        		if(goods.getCALLUSRID()!=null){
         		   parameter.setHISTORYSUBMIT(callPrice.countMyHistorySuccPriceRecord(goods.getCALLUSRID()));
         		}
        		parameter.setCONTACTTEL(goods.getGSMOBILE());
         		
        		
        		pts.add(parameter);
        	}
        	tempmap.put("goodsInfo", pts);
        }else{
        	tempmap.put("goodsInfo", new ArrayList<GoodsInfoParameter>());
        }
	
		res.setResult(tempmap);
		return res;

	}
	

	

	public RemoteResponse common(Credential credential, Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();

		Map tempmap = new HashMap();

		tempmap.put("goodsSource", null);
		res.setResult(tempmap);
		return res;

	}

	public boolean isTrade(Dict dict) {

		if (dict == null) {
			dict = getDictService().query(DC.QHH_EXCHANGEHOUR);
		}
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		String cc = format.format(new Date());

		if (cc.compareTo(dict.getDCTVALUE1()) > 0
				&& cc.compareTo(dict.getDCTVALUE2()) < 0)
			return true;
		else
			return false;

	}

	private LeftTime getOverTime(Date create_time) {
		Dict dict = getDictService().query(DC.QHH_EXCHANGEHOUR);
		Long valiMins = Long.valueOf(dict.getDCTVALUE3());

		// 得到当前时间
		Date now = getCommonService().getSystemTime();
		// 得到报价时间
		Date pushTime = create_time;
		long m = now.getTime() - pushTime.getTime();
		return newFormat(m, valiMins);

	}

	private LeftTime format(long ms, Long valiMins) {// 将毫秒数换算成x天x时x分x秒x毫秒
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;

		long day = ms / dd;
		long hour = (ms - day * dd) / hh;
		long minute = (ms - day * dd - hour * hh) / mi;
		long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		second = second % 59;
		minute = minute % (valiMins - 1);
		LeftTime time = new LeftTime();
		time.setLEFTTIME((valiMins - 1 - minute) + "分" + (60 - second) + "秒");
		time.setLEFTTIME2((valiMins - 1 - minute) * 60 + (60 - second));
		return time;
	}

	private LeftTime newFormat(long ms, Long valiMins) {// 将毫秒数换算成x天x时x分x秒x毫秒

		long s = ms / 1000;
		long t = 30 * 60;
		s = s % t;
		// 求出分钟
		long x = 30 - s / 60 - 1;
		long x2 = 60 - s % 60;

		LeftTime time = new LeftTime();
		time.setLEFTTIME(x + "分" + x2 + "秒");
		time.setLEFTTIME2(x * 60 + x2);
		return time;
	}

}
