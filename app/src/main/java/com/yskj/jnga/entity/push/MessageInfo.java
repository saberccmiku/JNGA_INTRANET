package com.yskj.jnga.entity.push;

import java.util.List;

public class MessageInfo {

	private String RtnCode;
	private String ErrorMsg;
	private List<Message> result;

	public class Message {

		private String MS_CREATETIME;
		private String DETAILS;
		private String CONTENTID;

		public String getMS_CREATETIME() {
			return MS_CREATETIME;
		}

		public void setMS_CREATETIME(String mS_CREATETIME) {
			MS_CREATETIME = mS_CREATETIME;
		}

		public String getDETAILS() {
			return DETAILS;
		}

		public void setDETAILS(String dETAILS) {
			DETAILS = dETAILS;
		}

		public String getCONTENTID() {
			return CONTENTID;
		}

		public void setCONTENTID(String cONTENTID) {
			CONTENTID = cONTENTID;
		}

		@Override
		public String toString() {
			return "MessageInfo [MS_CREATETIME=" + MS_CREATETIME + ", DETAILS=" + DETAILS + ", CONTENTID=" + CONTENTID
					+ "]";
		}

	}

	public String getRtnCode() {
		return RtnCode;
	}

	public void setRtnCode(String rtnCode) {
		RtnCode = rtnCode;
	}

	public String getErrorMsg() {
		return ErrorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		ErrorMsg = errorMsg;
	}

	public List<Message> getResult() {
		return result;
	}

	public void setResult(List<Message> result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "MessageInfo [RtnCode=" + RtnCode + ", ErrorMsg=" + ErrorMsg + ", result=" + result + "]";
	}

}
