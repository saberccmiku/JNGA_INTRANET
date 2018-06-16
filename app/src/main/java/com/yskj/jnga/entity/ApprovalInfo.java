package com.yskj.jnga.entity;

/**
 * 审批信息
 * @author saber
 *
 */

public class ApprovalInfo {
	
	private int id ;
	private String level;
	private String handlerName;
	private String opinion;
	private String result;
	private String approvalTime;
	

	public ApprovalInfo(){}
	

	public ApprovalInfo(int id, String level, String handlerName, String opinion, String result, String approvalTime) {
		this.id = id;
		this.level = level;
		this.handlerName = handlerName;
		this.opinion = opinion;
		this.result = result;
		this.approvalTime = approvalTime;
	}




	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getLevel() {
		return level;
	}


	public void setLevel(String level) {
		this.level = level;
	}


	public String getHandlerName() {
		return handlerName;
	}


	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}


	public String getOpinion() {
		return opinion;
	}


	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}


	public String getResult() {
		return result;
	}


	public void setResult(String result) {
		this.result = result;
	}


	public String getApprovalTime() {
		return approvalTime;
	}


	public void setApprovalTime(String approvalTime) {
		this.approvalTime = approvalTime;
	}


	@Override
	public String toString() {
		return "ApprovalInfo [id=" + id + ", level=" + level + ", handlerName=" + handlerName + ", opinion=" + opinion
				+ ", result=" + result + ", approvalTime=" + approvalTime + "]";
	}
	
	
}
