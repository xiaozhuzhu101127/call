package com.topjet.fmp.yls.command.user;

import com.topjet.fmp.sys.domain.NoticeBO;

public class NoticeParameter {

	public NoticeParameter(NoticeBO noticeBO) {
		this.URL = "NULL";
		this.content = noticeBO.getNTCCONTENT();
		this.ID = noticeBO.getNTCID();

	}

	private String content;

	private String URL;

	private Long ID;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public Long getID() {
		return ID;
	}

	public void setID(Long iD) {
		ID = iD;
	}

}
