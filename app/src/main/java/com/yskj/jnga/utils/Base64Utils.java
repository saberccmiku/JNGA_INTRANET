package com.yskj.jnga.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Base64工具类；</br>
 * 
 * @author lmc
 */
public class Base64Utils
{
	private Base64Utils()
	{
	}

	/** base64分段编码时使用的缓冲区大小 */
	public static final int BUFFER_SIZE_ENCODE = 3 * IOUtils.BUFFER_SIZE_DEFAULT;

	/** base64分段解码时使用的缓冲区大小 */
	public static final int BUFFER_SIZE_DECODE = 4 * IOUtils.BUFFER_SIZE_DEFAULT;

	/**
	 * 将输入流转换为经base64编码转换后的输入流
	 * 
	 * @param is
	 * @return
	 */
	public static InputStream streamToBase64Stream(InputStream is)
	{
		if (is != null)
		{
			InputStream sum = null;
			try
			{
				byte[] byteBuf = new byte[BUFFER_SIZE_ENCODE];
				byte[] base64ByteBlock;
				int count;
				while ((count = is.read(byteBuf)) != -1)
				{
					if (count != byteBuf.length)
					{
						byte[] copy = Arrays.copyOf(byteBuf, count);
						base64ByteBlock = byteToBase64Byte(copy);
					}
					else
					{
						base64ByteBlock = byteToBase64Byte(byteBuf);
					}
					sum = IOUtils.mergeStream(sum, IOUtils.byteToStream(base64ByteBlock));
				}
				is.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			return sum;
		}

		return null;
	}

	/**
	 * 将输入流转换为经Base64编码转换的字节数组
	 * 
	 * @param is
	 * @return
	 */
	public static byte[] streamToBase64Byte(InputStream is)
	{
		return byteToBase64Byte(IOUtils.streamToByte(is)); // 将输入流转换为base64编码字节数组
	}

	/**
	 * 将输入流转换为经Base64编码转换的字符串
	 * 
	 * @param is
	 * @return
	 */
	public static String streamToBase64String(InputStream is)
	{
		return new String(streamToBase64Byte(is));
	}

	/**
	 * 将字节数组转换为经Base64编码转换的字节数组
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] byteToBase64Byte(byte[] bytes)
	{
		return Base64.encodeBase64(bytes);
	}

	/**
	 * 将文件中的数据转换为经Base64编码转换的输入流
	 * 
	 * @param file
	 * @return
	 */
	public static InputStream fileToBase64Stream(File file)
	{
		return streamToBase64Stream(IOUtils.fileToStream(file));
	}

	/**
	 * 将文件中的数据转换为经Base64编码转换的输入流
	 * 
	 * @param path
	 * @return
	 */
	public static InputStream fileToBase64Stream(String path)
	{
		return fileToBase64Stream(new File(path));
	}

	/**
	 * 将文件中的数据转换为经Base64编码转换的字节数组
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] fileToBase64Byte(File file)
	{
		return streamToBase64Byte(IOUtils.fileToStream(file));
	}

	/**
	 * 将文件中的数据转换为经Base64编码转换的字节数组
	 * 
	 * @param path
	 * @return
	 */
	public static byte[] fileToBase64Byte(String path)
	{
		return fileToBase64Byte(new File(path));
	}

	/**
	 * 将文件中的数据转换为经Base64编码转换的字符串
	 * 
	 * @param file
	 * @return
	 */
	public static String fileToBase64String(File file)
	{
		return streamToBase64String(IOUtils.fileToStream(file));
	}

	/**
	 * 将文件中的数据转换为经Base64编码转换的字符串
	 * 
	 * @param path
	 * @return
	 */
	public static String fileToBase64String(String path)
	{
		return fileToBase64String(new File(path));
	}

	/**
	 * 将经Base64编码转换的字节数组转换为解码后的字节数组
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] base64ByteToByte(byte[] bytes)
	{
		return Base64.decodeBase64(bytes);
	}

	/**
	 * 将经Base64编码转换的字符串进行解码后转换为解码后的字节数组
	 * 
	 * @param base64String
	 * @return
	 */
	public static byte[] base64StringToByte(String base64String)
	{
		return Base64.decodeBase64(base64String); // 对base64编码字符串进行解码
	}

	/**
	 * 将经Base64编码转换的字符串转换为解码后的字符串
	 * 
	 * @param base64String
	 * @return
	 */
	public static String base64StringToString(String base64String)
	{
		return IOUtils.byteToString(base64StringToByte(base64String));
	}

	/**
	 * 将经Base64编码转换的字符串转换为文件
	 * 
	 * @param base64String
	 * @param file
	 *            转换结果的存放目标文件
	 * @return 转换结果的存放目标文件
	 */
	public static File base64StringToFile(String base64String, File file)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(base64StringToByte(base64String));
			fos.flush();
			fos.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return file;
	}

	/**
	 * 将经Base64编码转换的字符串转换为文件
	 * 
	 * @param base64String
	 * @param path
	 *            转换结果的存放目标文件路径
	 * @return 转换结果的存放目标文件
	 */
	public static File base64StringToFile(String base64String, String path)
	{
		return base64StringToFile(base64String, new File(path));
	}

	/**
	 * 对流进行[分块编码]处理；</br>
	 * 
	 * @param is
	 *            要进行分块编码的流
	 * @param blockEncodeListener
	 *            分块编码监听器，用以设置分块编码处理方法
	 */
	public static void blockEncode(InputStream is, BlockEncodeListener blockEncodeListener)
	{
		try
		{
			byte[] byteBuf = new byte[Base64Utils.BUFFER_SIZE_ENCODE];
			byte[] base64ByteBlock;
			int count;
			while ((count = is.read(byteBuf)) != -1)
			{
				if (count != byteBuf.length)
				{
					byte[] copy = Arrays.copyOf(byteBuf, count);
					base64ByteBlock = Base64Utils.byteToBase64Byte(copy);
				}
				else
				{
					base64ByteBlock = Base64Utils.byteToBase64Byte(byteBuf);
				}

				blockEncodeListener.blockEncoding(base64ByteBlock);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 对base64编码流进行[分块解码]处理；</br>
	 * 
	 * @param is
	 *            要进行base64解码的base64编码流
	 * @param blockDecodeListener
	 *            分块解码监听器，用以设置分块解码处理方法
	 */
	public static void blockDecode(InputStream is, BlockDecodeListener blockDecodeListener)
	{
		try
		{
			byte[] byteBuf = new byte[Base64Utils.BUFFER_SIZE_DECODE];
			byte[] byteBlock;
			int count;
			while ((count = is.read(byteBuf)) != -1)
			{
				if (count != byteBuf.length)
				{
					byte[] copy = Arrays.copyOf(byteBuf, count);
					byteBlock = Base64Utils.base64ByteToByte(copy);
				}
				else
				{
					byteBlock = Base64Utils.base64ByteToByte(byteBuf);
				}

				blockDecodeListener.blockDecoding(byteBlock);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 分块编码监听器
	 */
	public interface BlockEncodeListener
	{
		/**
		 * 分块编码处理方法；</br>
		 * 分段编码过程中，获取到编码完成的块后，进行处理的方法；</br>
		 * 每编码完成一块，该方法会被回调一次，直至目标全部分块编码完成；
		 * 
		 * @param base64ByteBlock
		 *            已进行base64编码的块
		 * @throws IOException
		 */
		public void blockEncoding(byte[] base64ByteBlock) throws IOException;
	}

	/**
	 * 分块解码监听器
	 */
	public interface BlockDecodeListener
	{
		/**
		 * 分块解码处理方法；</br>
		 * 分块解码过程中，获取到解码完成的块后，进行处理的方法；</br>
		 * 每解码完成一块，该方法会被回调一次，直至目标全部分块解码完成；
		 * 
		 * @param byteBlock
		 *            已进行base64解码的块
		 * @throws IOException
		 */
		public void blockDecoding(byte[] byteBlock) throws IOException;
	}
}
