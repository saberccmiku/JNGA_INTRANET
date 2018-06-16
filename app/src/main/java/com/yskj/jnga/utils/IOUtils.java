package com.yskj.jnga.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.SequenceInputStream;

/**
 * IO操作工具类；</br>
 * 
 * @author lmc
 */
public class IOUtils
{
	private IOUtils()
	{
	}

	/** 缓冲区大小 */
	public static final int BUFFER_SIZE_DEFAULT = 1024;

	/** 编码字符集：UTF-8 */
	public static final String ENCODING_CHARSET_UTF_8 = "UTF-8";

	/** 编码字符集：UTF-16 */
	public static final String ENCODING_CHARSET_UTF_16 = "UTF-16";

	/** 编码字符集：UTF-16BE */
	public static final String ENCODING_CHARSET_UTF_16BE = "UTF-16BE";

	/** 编码字符集：UTF-16LE */
	public static final String ENCODING_CHARSET_UTF_16LE = "UTF-16LE";

	/** 编码字符集：ISO-8859-1 */
	public static final String ENCODING_CHARSET_ISO_8859_1 = "ISO-8859-1";

	/**
	 * 将输入流转换为字节数组
	 * 
	 * @param is
	 * @return
	 */
	public static byte[] streamToByte(InputStream is)
	{
		if (is != null)
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte data[] = new byte[BUFFER_SIZE_DEFAULT];
			int count;
			try
			{
				while (((count = is.read(data)) != -1))
				{
					baos.write(data, 0, count);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return baos.toByteArray();
		}

		return null;
	}

	/**
	 * 将输入流转换为字符串；使用字节流进行读取
	 * 
	 * @param is
	 * @return
	 */
	public static String streamToString(InputStream is)
	{
		if (is != null)
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte data[] = new byte[BUFFER_SIZE_DEFAULT];
			int count;
			try
			{
				while (((count = is.read(data)) != -1))
				{
					baos.write(data, 0, count);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			return baos.toString();
		}

		return null;
	}

	/**
	 * 将对象转换为字节数组
	 * 
	 * @param obj
	 * @return
	 */
	public static byte[] objectToByte(Object obj)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		try
		{
			return baos.toByteArray();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将字节数组转换为输入流
	 * 
	 * @param bytes
	 * @return
	 */
	public static InputStream byteToStream(byte[] bytes)
	{
		return new ByteArrayInputStream(bytes);
	}

	public static Object byteToObject(byte[] bytes)
	{
		Object obj = null;

		try
		{
			ObjectInputStream ois = new ObjectInputStream(byteToStream(bytes));
			obj = ois.readObject();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		return obj;
	}

	/**
	 * 将字节数组转换为字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String byteToString(byte[] bytes)
	{
		return new String(bytes);
	}

	/**
	 * 将对象转换为字符串
	 * 
	 * @param obj
	 * @return
	 */
	/* public static String objectToString(Object obj)
	 * {
	 * return byteToString(objectToByte(obj));
	 * } */

	/**
	 * 将字符串转换为字节输入流
	 * 
	 * @param str
	 * @return
	 */
	public static InputStream stringToStream(String str)
	{
		return byteToStream(stringToByte(str));
	}

	/**
	 * 将字符串转换为字节数组
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] stringToByte(String str)
	{
		return str.getBytes();
	}

	public static Object stringToObject(String str)
	{
		return byteToObject(stringToByte(str));
	}

	/**
	 * 将文件中的数据转换为输入流
	 * 
	 * @param file
	 * @return
	 */
	public static InputStream fileToStream(File file)
	{
		InputStream is = null;

		try
		{
			is = new FileInputStream(file);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		return is;
	}

	/**
	 * 将文件中的数据转换为输入流
	 * 
	 * @param path
	 * @return
	 */
	public static InputStream fileToStream(String path)
	{
		return fileToStream(new File(path));
	}

	/**
	 * 将文件中的数据转换为字节数组
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] fileToByte(File file)
	{
		return streamToByte(fileToStream(file));
	}

	/**
	 * 将文件中的数据转换为字节数组
	 * 
	 * @param path
	 * @return
	 */
	public static byte[] fileToByte(String path)
	{
		return fileToByte(new File(path));
	}

	/**
	 * 将文件中的数据转换为字符串
	 * 
	 * @param file
	 * @return
	 */
	public static String fileToString(File file)
	{
		return byteToString(fileToByte(file));
	}

	/**
	 * 将文件中的数据转换为字符串
	 * 
	 * @param path
	 * @return
	 */
	public static String fileToString(String path)
	{
		return fileToString(new File(path));
	}

	/**
	 * 将多个字节输入流合并为一个字节输入流
	 * 
	 * @param streams
	 * @return
	 */
	public static InputStream mergeStream(InputStream... streams)
	{
		InputStream sum = null;

		for (int i = 0; i < streams.length; i++)
		{
			if (sum == null && streams[i] != null)
			{
				sum = streams[i];
			}
			else if (sum != null && streams[i] != null)
			{
				sum = new SequenceInputStream(sum, streams[i]);
			}
		}

		return sum;
	}

	public static File writeStreamToFile(InputStream is, File file)
	{
		return writeByteToFile(streamToByte(is), file);
	}

	public static File writeStreamToFile(InputStream is, String path)
	{
		return writeStreamToFile(is, new File(path));
	}

	public static File writeByteToFile(byte[] bytes, File file)
	{
		try
		{
			FileOutputStream fis = new FileOutputStream(file);
			fis.write(bytes);
			fis.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return file;
	}

	public static File writeByteToFile(byte[] bytes, String path)
	{
		return writeByteToFile(bytes, new File(path));
	}

	public static File writeStringToFile(String str, File file)
	{
		return writeByteToFile(stringToByte(str), file);
	}

	public static File writeStringToFile(String str, String path)
	{
		return writeStringToFile(str, new File(path));
	}

	public static File writeObjectToFile(Object obj, File file)
	{
		return writeByteToFile(objectToByte(obj), file);
	}

	public static File writeObjectToFile(Object obj, String path)
	{
		return writeObjectToFile(obj, new File(path));
	}

	public static Object readObjectFromFile(File file)
	{
		Object obj = null;

		try
		{
			InputStream is = fileToStream(file);
			if (is != null)
			{
				ObjectInputStream ois = new ObjectInputStream(is);
				obj = ois.readObject();
				ois.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		return obj;
	}

	public static Object readObjectFromFile(String path)
	{
		return readObjectFromFile(new File(path));
	}

	/**
	 * 创建一个File，当path参数中包含文件目录时若文件目录不存在会自动创建
	 * 
	 * @param path
	 * @return
	 */
	public static File mkFileWithDirs(String path)
	{
		File file = new File(path);
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) parent.mkdirs();
		return file;
	}
}
