package com.yskj.jnga.entity.push;

import java.io.Serializable;
import java.util.List;

/**
 * 统计类
 *
 * @author saber
 *
 */
public class StatisticInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private String RtnCode;
	private String ErrorMsg;
	private List<BeanResult> result;

	public class BeanResult implements Serializable {

		private static final long serialVersionUID = 1L;
		private String ID;
		private String PID;
		private String NAME;
		private int COUNT;
		private int READ;
		private int UNREAD;

		public String getID() {
			return ID;
		}

		public void setID(String iD) {
			ID = iD;
		}

		public String getPID() {
			return PID;
		}

		public void setPID(String PID) {
			this.PID = PID;
		}

		public String getNAME() {
			return NAME;
		}

		public void setNAME(String nAME) {
			NAME = nAME;
		}



		public int getCOUNT() {
			return COUNT;
		}

		public void setCOUNT(int cOUNT) {
			COUNT = cOUNT;
		}

		public int getREAD() {
			return READ;
		}

		public void setREAD(int rEAD) {
			READ = rEAD;
		}

		public int getUNREAD() {
			return UNREAD;
		}

		public void setUNREAD(int uNREAD) {
			UNREAD = uNREAD;
		}


		@Override
		public String toString() {
			return "BeanResult{" +
					"ID='" + ID + '\'' +
					", PID='" + PID + '\'' +
					", NAME='" + NAME + '\'' +
					", COUNT=" + COUNT +
					", READ=" + READ +
					", UNREAD=" + UNREAD +
					'}';
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

	public List<BeanResult> getResult() {
		return result;
	}

	public void setResult(List<BeanResult> result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "StatisticInfo [RtnCode=" + RtnCode + ", ErrorMsg=" + ErrorMsg + ", result=" + result + "]";
	}

}
