package com.yskj.jnga.entity.push;

import com.yskj.jnga.network.xml.BaseTable;

import java.io.Serializable;

public class MessageReceive implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String FLAG_ID;
	private String MS_PoliceNum;
	private String MS_PoliceName;
	private String MS_PUnit;
	private String MS_CreateTime;
	private String MS_BiaoShi;
	private String MS_Content;
	private String MS_MType;
	private String MS_URL;
	private String MS_SLevel;
	private String MS_MID;
	private String MS_Source;
	private String MS_NType;
	private String Switch;

	public MessageReceive() {
	}

	public MessageReceive(BaseTable baseTable) {

		this.id = baseTable.getField("FLAG_ID");
		FLAG_ID = baseTable.getField("FLAG_ID");
		MS_PoliceNum = baseTable.getField("MS_PoliceNum");
		MS_PoliceName = baseTable.getField("MS_PoliceName");
		MS_PUnit = baseTable.getField("MS_PUnit");
		MS_CreateTime = baseTable.getField("MS_CreateTime");
		MS_BiaoShi = baseTable.getField("MS_BiaoShi");
		MS_Content = baseTable.getField("MS_Content");
		MS_MType = baseTable.getField("MS_MType");
		MS_URL = baseTable.getField("MS_URL");
		MS_SLevel = baseTable.getField("MS_SLevel");
		MS_MID = baseTable.getField("MS_MID");
		MS_Source = baseTable.getField("MS_Source");
		MS_NType = baseTable.getField("MS_NType");
		Switch = baseTable.getField("Switch");
	}

	public MessageReceive(MessageInfo.Message message) {

		MS_Content = message.getDETAILS();
		MS_CreateTime = message.getMS_CREATETIME();
		MS_MID = message.getCONTENTID();

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFLAG_ID() {
		return FLAG_ID;
	}

	public void setFLAG_ID(String fLAG_ID) {
		FLAG_ID = fLAG_ID;
	}

	public String getMS_PoliceNum() {
		return MS_PoliceNum;
	}

	public void setMS_PoliceNum(String mS_PoliceNum) {
		MS_PoliceNum = mS_PoliceNum;
	}

	public String getMS_PoliceName() {
		return MS_PoliceName;
	}

	public void setMS_PoliceName(String mS_PoliceName) {
		MS_PoliceName = mS_PoliceName;
	}

	public String getMS_PUnit() {
		return MS_PUnit;
	}

	public void setMS_PUnit(String mS_PUnit) {
		MS_PUnit = mS_PUnit;
	}

	public String getMS_CreateTime() {
		return MS_CreateTime;
	}

	public void setMS_CreateTime(String mS_CreateTime) {
		MS_CreateTime = mS_CreateTime;
	}

	public String getMS_BiaoShi() {
		return MS_BiaoShi;
	}

	public void setMS_BiaoShi(String mS_BiaoShi) {
		MS_BiaoShi = mS_BiaoShi;
	}

	public String getMS_Content() {
		return MS_Content;
	}

	public void setMS_Content(String mS_Content) {
		MS_Content = mS_Content;
	}

	public String getMS_MType() {
		return MS_MType;
	}

	public void setMS_MType(String mS_MType) {
		MS_MType = mS_MType;
	}

	public String getMS_URL() {
		return MS_URL;
	}

	public void setMS_URL(String mS_URL) {
		MS_URL = mS_URL;
	}

	public String getMS_SLevel() {
		return MS_SLevel;
	}

	public void setMS_SLevel(String mS_SLevel) {
		MS_SLevel = mS_SLevel;
	}

	public String getMS_MID() {
		return MS_MID;
	}

	public void setMS_MID(String mS_MID) {
		MS_MID = mS_MID;
	}

	public String getMS_Source() {
		return MS_Source;
	}

	public void setMS_Source(String mS_Source) {
		MS_Source = mS_Source;
	}

	public String getMS_NType() {
		return MS_NType;
	}

	public void setMS_NType(String mS_NType) {
		MS_NType = mS_NType;
	}

	public String getSwitch() {
		return Switch;
	}

	public void setSwitch(String switch1) {
		Switch = switch1;
	}

	@Override
	public String toString() {
		return "MessageReceive [id=" + id + ", FLAG_ID=" + FLAG_ID + ", MS_PoliceNum=" + MS_PoliceNum + ", MS_PoliceName="
				+ MS_PoliceName + ", MS_PUnit=" + MS_PUnit + ", MS_CreateTime=" + MS_CreateTime + ", MS_BiaoShi="
				+ MS_BiaoShi + ", MS_Content=" + MS_Content + ", MS_MType=" + MS_MType + ", MS_URL=" + MS_URL
				+ ", MS_SLevel=" + MS_SLevel + ", MS_MID=" + MS_MID + ", MS_Source=" + MS_Source + ", MS_NType="
				+ MS_NType + ", Switch=" + Switch + "]";
	}

}
