package com.topjet.fmp.yls.command.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.topjet.fmp.aa.Credential;
import com.topjet.fmp.base.domain.PaginatedList;
import com.topjet.fmp.user.domain.YslGift;
import com.topjet.fmp.user.domain.YslGiftCriteria;
import com.topjet.fmp.user.service.YslGiftService;
import com.topjet.fmp.util.Constants;
import com.topjet.fmp.yls.RemoteResponse;
import com.topjet.fmp.yls.command.BaseCommand;


@Controller("gift_command")
public class GiftCommand extends BaseCommand {
	
	@Autowired
	private YslGiftService  yslGiftService;
	
	public RemoteResponse listGift(Credential credential, Map<String, Object> map) {
		Map tempmap = new HashMap();
		RemoteResponse res = new RemoteResponse();
		YslGiftCriteria   cri = new YslGiftCriteria();
		cri.setCurrentDate(getCommonService().getSystemTime());
		cri.setPGISVALID(Constants.TRUE);
		PaginatedList<YslGift> pages=yslGiftService.page(credential, cri);
		if(pages!=null&&pages.getResult()!=null){
			List<YslGift> gitfs =pages.getResult();
			tempmap.put("gift", gitfs);
		}else{
			tempmap.put("gift", "");
		}

		res.setResult(tempmap);
		return res;

	}
	
	
	//∂“ªª¿Ò∆∑
	public RemoteResponse changeGift(Credential credential, Map<String, Object> map) {

		RemoteResponse res = new RemoteResponse();

		Map tempmap = new HashMap();

		tempmap.put("goodsSource", null);
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
	
	

}
