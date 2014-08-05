package com.topjet.fmp.yls.command;

import java.util.Map;

import com.topjet.fmp.aa.Credential;
import com.topjet.fmp.yls.RemoteResponse;

public interface Command {

	String P_IPADDR = "P_IPADDR";

	String P_HTTPREQUEST = "P_HTTPREQUEST";

	RemoteResponse execute(Credential credential, Map<String, Object> args);

}
