package com.yskj.jnga.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class HistoryRecord implements Serializable
{
	public OneKeySearchRecord oneKeySearchRecord = new OneKeySearchRecord();
	public PeopleSearchRecord peopleSearchRecord = new PeopleSearchRecord();
	public VehicleSearchRecord vehicleSearchRecord = new VehicleSearchRecord();
	public GoodsSearchRecord goodsSearchRecord = new GoodsSearchRecord();
	public CaseSearchRecord caseSearchRecord = new CaseSearchRecord();
	
	public class OneKeySearchRecord implements Serializable
	{
		public ArrayList<String> keyWord = new ArrayList<String>();
	}
	
	public class PeopleSearchRecord implements Serializable
	{
		public ArrayList<String> pplName = new ArrayList<String>();
		public ArrayList<String> pplId = new ArrayList<String>();
		public ArrayList<String> pplSex = new ArrayList<String>();
		public ArrayList<String> pplBirt = new ArrayList<String>();
		public ArrayList<String> pplAdd = new ArrayList<String>();
	}
	
	public class VehicleSearchRecord implements Serializable
	{
		public ArrayList<String> vehLpn = new ArrayList<String>();
		public ArrayList<String> vehEn = new ArrayList<String>();
		public ArrayList<String> vehVin = new ArrayList<String>();
		public ArrayList<String> vehColor = new ArrayList<String>();
		
		public ArrayList<String> pplName = new ArrayList<String>();
		public ArrayList<String> pplSex = new ArrayList<String>();
		public ArrayList<String> pplBirt = new ArrayList<String>();
		public ArrayList<String> pplId = new ArrayList<String>();
		public ArrayList<String> pplAdd = new ArrayList<String>();
	}
	
	public class GoodsSearchRecord implements Serializable
	{
		public ArrayList<String> ioiName = new ArrayList<String>();
		public ArrayList<String> ioiType = new ArrayList<String>();
		
		public ArrayList<String> pplName = new ArrayList<String>();
		public ArrayList<String> pplSex = new ArrayList<String>();
		public ArrayList<String> pplBirt = new ArrayList<String>();
		public ArrayList<String> pplId = new ArrayList<String>();
		public ArrayList<String> pplAdd = new ArrayList<String>();
	}
	
	public class CaseSearchRecord implements Serializable
	{
		public ArrayList<String> caseNumber = new ArrayList<String>();
		public ArrayList<String> caseName = new ArrayList<String>();
		public ArrayList<String> caseType = new ArrayList<String>();
		public ArrayList<String> caseUnit = new ArrayList<String>();
		public ArrayList<String> pplName = new ArrayList<String>();
		public ArrayList<String> policeNumber = new ArrayList<String>();
		
		public ArrayList<String> suspectPplName = new ArrayList<String>();
		public ArrayList<String> suspectPplSex = new ArrayList<String>();
		public ArrayList<String> suspectPplBirt = new ArrayList<String>();
		public ArrayList<String> suspectPplId = new ArrayList<String>();
		public ArrayList<String> suspectPplAdd = new ArrayList<String>();
		
		public ArrayList<String> victimPplName = new ArrayList<String>();
		public ArrayList<String> victimPplSex = new ArrayList<String>();
		public ArrayList<String> victimPplBirt = new ArrayList<String>();
		public ArrayList<String> victimPplId = new ArrayList<String>();
		public ArrayList<String> victimPplAdd = new ArrayList<String>();
	}
}
