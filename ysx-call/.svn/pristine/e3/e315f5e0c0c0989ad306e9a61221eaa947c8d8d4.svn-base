package com.topjet.fmp.yls.command.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ibm.icu.text.Transliterator.Position;
import com.topjet.fmp.aa.Credential;
import com.topjet.fmp.base.TypeToDC;
import com.topjet.fmp.biz.tk.domain.TkOnlineBO;
import com.topjet.fmp.biz.tk.domain.TkOnlineCriteria;
import com.topjet.fmp.biz.tk.service.TkOnlineService;
import com.topjet.fmp.goods.domain.GSCriteria;
import com.topjet.fmp.goods.domain.GoodsSourceBO;
import com.topjet.fmp.goods.service.GoodsSourceService;
import com.topjet.fmp.util.DateUtil;
import com.topjet.fmp.yls.RemoteResponse;
import com.topjet.fmp.yls.command.BaseCommand;
import com.topjet.fmp.yls.command.parameter.GoodsInfoParameter;
import com.topjet.fmp.yls.command.parameter.GoodsListParameter;

@Component("truckSource_command")
public class TruckSourceCommand extends BaseCommand {

	@Autowired
	private TkOnlineService tkOnlineService;
	
	@Autowired
	private GoodsSourceService  goodsSourceService;

	// 查找周边空车
	public RemoteResponse getTrucksInfoList(Credential credential, Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();

		String MOBILE = (String) map.get("MOBILE");	
		String CITYCODE = (String) map.get("CITYCODE");
		CITYCODE=getPalcedDCTCodeByBaiDu(credential, CITYCODE);
		// 此处用的是偏移量，需要注意
		Integer offset = (Integer) map.get("offset");
		if(offset==null){
			offset=0;
		}
		TkOnlineCriteria criteria = new TkOnlineCriteria();
		criteria.setPagesize(20);
		criteria.setPage(getPage(offset, criteria.getPagesize()));
		criteria.setCitycode(CITYCODE);
		Map tempmap = new HashMap();
		//默认为有数据
	    boolean has=true;
		if (offset > 0 && offset < criteria.getPagesize()) {
			// 无数据了
			has=false;
		}		
		List<TkOnlineBO> bos = tkOnlineService.listNowTkByCityCode(criteria);
        if(bos==null||bos.size()<=0){
        	has=false;
        }
        if(has){
        	tempmap.put("trucksSourceInfo", bos);
        }else{
        	tempmap.put("trucksSourceInfo", new ArrayList<TkOnlineBO>());
        }
	
		res.setResult(tempmap);
		return res;

	}


	
	
	
	// 查找周边空车
		public RemoteResponse getTrucksList(Credential credential, Map<String, Object> map) {

			RemoteResponse res = new RemoteResponse();

			String MOBILE = (String) map.get("MOBILE");	
			String TKTARGET = (String) map.get("TKTARGET");
			String DCT_TT = (String) map.get("DCT_TT");
			String DCT_TKLEN = (String) map.get("DCT_TKLEN");
			String CITYCODE = (String) map.get("CITYCODE");
			//ios用的是小写的
			String citycode2 = (String) map.get("citycode");
			// 此处用的是偏移量，需要注意
			Integer offset = (Integer) map.get("offset");
			TkOnlineCriteria criteria = new TkOnlineCriteria();
			criteria.setPagesize(20);
			criteria.setPage(getPage(offset, criteria.getPagesize()));
			criteria.setDCT_TT(DCT_TT);
			criteria.setDCT_TKLEN(DCT_TKLEN);
			if(!StringUtils.hasText(CITYCODE)){
				CITYCODE=citycode2;
			}
			
			CITYCODE=getPalcedDCTCodeByBaiDu(credential, CITYCODE);
			criteria.setCitycode(CITYCODE);
			criteria.setTKTARGET(TKTARGET);
			Map tempmap = new HashMap();
			//默认为有数据
		    boolean has=true;
			if (offset > 0 && offset < criteria.getPagesize()) {
				// 无数据了
				has=false;
			}		
			List<TkOnlineBO> bos = tkOnlineService.listNowTk(criteria);
	        if(bos==null||bos.size()<=0){
	        	has=false;
	        }
	        if(has){
	        	tempmap.put("trucksSourceInfo", bos);
	        }else{
	        	tempmap.put("trucksSourceInfo", new ArrayList<TkOnlineBO>());
	        }
		
			res.setResult(tempmap);
			return res;

		}
		
		
		
		
		

	public RemoteResponse common(Credential credential, Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();
		// 货源拥有者的ID号
		Integer OWNERID = (Integer) map.get("OWNERID");
		Integer USRID = (Integer) map.get("USRID");
		String Depart = (String) map.get("Depart");

		Map tempmap = new HashMap();

		tempmap.put("goodsSource", null);
		res.setResult(tempmap);
		return res;

	}

}
