package com.yskj.jnga.utils;

import com.yskj.jnga.network.xml.BaseTable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BeanRefUtil {

	/**
	 * 取Bean的属性和值对应关系的BaseTable
	 *
	 * @return BaseTable
	 */
	public static BaseTable getBaeTable(Object object) {

		Class<?> cls = object.getClass();
		BaseTable baseTable = new BaseTable();
		// 取出bean里的所有方法
		Method[] methods = cls.getDeclaredMethods();
		Field fields[] = object.getClass().getDeclaredFields();
		for (Field field : fields) {// 获取所有变量名称
			try {
				String fieldType = field.getType().getSimpleName();
				String fieldGetName = parGetName(field.getName());
				if (!checkGetMet(methods, fieldGetName)) {
					continue;
				}
				Method fieldGetMet = cls.getMethod(fieldGetName, new Class[] {});
				Object fieldVal = fieldGetMet.invoke(object, new Object[] {});
				String result = null;
				if ("Date".equals(fieldType)) {
					result = fmtDate((Date) fieldVal);
				} else {
					if (null != fieldVal) {
						result = String.valueOf(fieldVal);
					}
				}
				// String fieldKeyName = parKeyName(field.getName());
				if (result == null) {
					result = "";
				}
				baseTable.putField(field.getName(), result);
			} catch (Exception e) {
				continue;
			}

		}

		return baseTable;
	}

	/**
	 * 取Bean的属性和值对应关系的BaseTable 修改小写id为大写ID方便转成平台数据
	 *
	 * @return BaseTable
	 */
	public static BaseTable getDbBaeTable(Object object) {

		Class<?> cls = object.getClass();
		BaseTable baseTable = new BaseTable();
		// 取出bean里的所有方法
		Method[] methods = cls.getDeclaredMethods();
		Field fields[] = object.getClass().getDeclaredFields();
		for (Field field : fields) {// 获取所有变量名称
			try {
				String fieldType = field.getType().getSimpleName();
				String fieldGetName = parGetName(field.getName());
				if (!checkGetMet(methods, fieldGetName)) {
					continue;
				}
				Method fieldGetMet = cls.getMethod(fieldGetName, new Class[] {});
				Object fieldVal = fieldGetMet.invoke(object, new Object[] {});
				String result = null;
				if ("Date".equals(fieldType)) {
					result = fmtDate((Date) fieldVal);
				} else {
					if (null != fieldVal) {
						result = String.valueOf(fieldVal);
					}
				}
				// String fieldKeyName = parKeyName(field.getName());
				if (result == null) {
					result = "";
				}
				if (field.getName().equals("id")) {
					baseTable.putField(field.getName().toUpperCase(), result);
				} else {
					baseTable.putField(field.getName(), result);

				}

			} catch (Exception e) {
				continue;
			}

		}

		return baseTable;
	}

	/**
	 * set属性的值到Bean
	 */
	public static Object setFieldValue(Object object, BaseTable baseTable) {
		Class<?> cls = object.getClass();
		// 取出bean里的所有方法
		Method[] methods = cls.getDeclaredMethods();
		Field[] fields = cls.getDeclaredFields();

		for (Field field : fields) {
			try {
				String fieldSetName = parSetName(field.getName());
				if (!checkSetMet(methods, fieldSetName)) {
					continue;
				}
				Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());
				// String fieldKeyName = parKeyName(field.getName());
				String fieldKeyName = field.getName();
				String value = baseTable.getField(fieldKeyName);
				if (null != value && !"".equals(value)) {
					String fieldType = field.getType().getSimpleName();
					if ("String".equals(fieldType)) {
						fieldSetMet.invoke(object, value);
					} else if ("Date".equals(fieldType)) {
						Date temp = parseDate(value);
						fieldSetMet.invoke(object, temp);
					} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
						Integer intval = Integer.parseInt(value);
						fieldSetMet.invoke(object, intval);
					} else if ("Long".equalsIgnoreCase(fieldType)) {
						Long temp = Long.parseLong(value);
						fieldSetMet.invoke(object, temp);
					} else if ("Double".equalsIgnoreCase(fieldType)) {
						Double temp = Double.parseDouble(value);
						fieldSetMet.invoke(object, temp);
					} else if ("Boolean".equalsIgnoreCase(fieldType)) {
						Boolean temp = Boolean.parseBoolean(value);
						fieldSetMet.invoke(object, temp);
					} else {
						throw new Exception("not supper type " + fieldType);
					}
				}
			} catch (Exception e) {
				continue;
			}
		}

		return object;
	}

	/**
	 * set属性的值到Bean
	 */
	public static Object setDbValue(Object object, BaseTable baseTable) {
		Class<?> cls = object.getClass();
		// 取出bean里的所有方法
		Method[] methods = cls.getDeclaredMethods();
		Field[] fields = cls.getDeclaredFields();

		for (Field field : fields) {
			try {
				String fieldSetName = parSetName(field.getName());
				if (!checkSetMet(methods, fieldSetName)) {
					continue;
				}
				Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());
				// String fieldKeyName = parKeyName(field.getName());
				String fieldKeyName = field.getName();
				String value;
				if (fieldKeyName.equals("id")) {
					value = baseTable.getField(fieldKeyName.toUpperCase());
				} else {
					value = baseTable.getField(fieldKeyName);
				}

				if (null != value && !"".equals(value)) {
					String fieldType = field.getType().getSimpleName();
					if ("String".equals(fieldType)) {
						fieldSetMet.invoke(object, value);
					} else if ("Date".equals(fieldType)) {
						Date temp = parseDate(value);
						fieldSetMet.invoke(object, temp);
					} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
						Integer intval = Integer.parseInt(value);
						fieldSetMet.invoke(object, intval);
					} else if ("Long".equalsIgnoreCase(fieldType)) {
						Long temp = Long.parseLong(value);
						fieldSetMet.invoke(object, temp);
					} else if ("Double".equalsIgnoreCase(fieldType)) {
						Double temp = Double.parseDouble(value);
						fieldSetMet.invoke(object, temp);
					} else if ("Boolean".equalsIgnoreCase(fieldType)) {
						Boolean temp = Boolean.parseBoolean(value);
						fieldSetMet.invoke(object, temp);
					} else {
						throw new Exception("not supper type " + fieldType);
					}
				}
			} catch (Exception e) {
				continue;
			}
		}

		return object;
	}

	/**
	 * 判断是否存在某属性的 set方法
	 * 
	 * @param methods
	 * @param fieldSetMet
	 * @return boolean
	 */
	public static boolean checkSetMet(Method[] methods, String fieldSetMet) {
		for (Method met : methods) {
			if (fieldSetMet.equals(met.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否存在某属性的 get方法
	 * 
	 * @param methods
	 * @param fieldGetMet
	 * @return boolean
	 */
	public static boolean checkGetMet(Method[] methods, String fieldGetMet) {
		for (Method met : methods) {
			if (fieldGetMet.equals(met.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 拼接某属性的 get方法
	 * 
	 * @param fieldName
	 * @return String
	 */
	public static String parGetName(String fieldName) {
		if (null == fieldName || "".equals(fieldName)) {
			return null;
		}
		int startIndex = 0;
		if (fieldName.charAt(0) == '_')
			startIndex = 1;
		return "get" + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
				+ fieldName.substring(startIndex + 1);
	}

	/**
	 * 拼接在某属性的 set方法
	 * 
	 * @param fieldName
	 * @return String
	 */
	public static String parSetName(String fieldName) {
		if (null == fieldName || "".equals(fieldName)) {
			return null;
		}
		int startIndex = 0;
		if (fieldName.charAt(0) == '_')
			startIndex = 1;
		return "set" + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
				+ fieldName.substring(startIndex + 1);
	}

	/**
	 * 获取存储的键名称（调用parGetName）
	 * 
	 * @param fieldName
	 * @return 去掉开头的get
	 */
	public static String parKeyName(String fieldName) {
		String fieldGetName = parGetName(fieldName);
		if (fieldGetName != null && fieldGetName.trim() != "" && fieldGetName.length() > 3) {
			return fieldGetName.substring(3);
		}
		return fieldGetName;
	}

	/**
	 * 格式化string为Date
	 * 
	 * @param datestr
	 * @return date
	 */
	public static Date parseDate(String datestr) {
		if (null == datestr || "".equals(datestr)) {
			return null;
		}
		try {
			String fmtstr = null;
			if (datestr.indexOf(':') > 0) {
				fmtstr = "yyyy-MM-dd HH:mm:ss";
			} else {
				fmtstr = "yyyy-MM-dd";
			}
			SimpleDateFormat sdf = new SimpleDateFormat(fmtstr, Locale.UK);
			return sdf.parse(datestr);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 日期转化为String
	 * 
	 * @param date
	 * @return date string
	 */
	public static String fmtDate(Date date) {
		if (null == date) {
			return null;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
			return sdf.format(date);
		} catch (Exception e) {
			return null;
		}
	}

}
