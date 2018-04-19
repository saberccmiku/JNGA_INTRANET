package com.yskj.jnga.network.xml;

import java.io.File;

public class Document extends File
{
	private static final long serialVersionUID = 5416346761091489740L;

	public Document(String path)
	{
		super(path);
	}
	
	public String getSuffix()
	{
		if(getPath().lastIndexOf('.')!=-1)
		{
			return getPath().substring(getPath().lastIndexOf('.')+1);
		}
		else
		{
			return null;
		}
	}
	
	public String getFileType()
	{
		return "JEPG";
		//return "";
	}
}
