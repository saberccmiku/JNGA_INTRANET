package com.yskj.jnga.network.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.yskj.jnga.network.ApiConstants;

public final class DataOperation {
	private DataOperation() {
	}

	/**
	 * 根据表名，获取所有表记录；用于无需查询条件的查询
	 *
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<BaseTable> queryTable(String tableName) {
		return queryTable(tableName, -1, -1);
	}

	public static ArrayList<BaseTable> queryTable(String tableName, int currentPage, int pageSize) {
		return queryTable(tableName, currentPage, pageSize, (HashMap<String, String>) null);
	}

	/**
	 * 根据表名，获取符合条件的表记录；用于以单个字段为查询条件的简单查询
	 *
	 * @param tableName
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<?> queryTable(String tableName, String fieldName, String fieldValue) {
		return queryTable(tableName, fieldName, -1, -1, fieldValue);
	}

	public static ArrayList<?> queryTable(String tableName, String fieldName, String fieldValue, String order) {
		return queryTable(tableName, fieldName, -1, -1, fieldValue, order);
	}

	public static ArrayList<?> queryTable(String tableName, String fieldName, int currentPage, int pageSize,
			String fieldValue) {
		HashMap<String, String> fieldList = new HashMap<>();
		fieldList.put(fieldName, fieldValue);
		return queryTable(tableName, currentPage, pageSize, fieldList);
	}

	public static ArrayList<?> queryTable(String tableName, String fieldName, int currentPage, int pageSize,
			String fieldValue, String order) {
		HashMap<String, String> fieldList = new HashMap<>();
		fieldList.put(fieldName, fieldValue);
		return queryTable(tableName, currentPage, pageSize, fieldList, order);
	}

	/**
	 * 根据表名，获取符合条件的表记录；用于以多个字段为查询条件的简单查询
	 *
	 * @param tableName
	 * @param fieldList
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<?> queryTable(String tableName, Map<String, String> fieldList) {
		return queryTable(tableName, -1, -1, fieldList);
	}

	public static ArrayList<?> queryTable(String tableName, Map<String, String> fieldList, String orderStr) {
		return queryTable(tableName, -1, -1, fieldList, orderStr);
	}

	// new
	public static ArrayList<BaseTable> queryTable(String tableName, int currentPage, int pageSize,
			Map<String, String> fieldList) {
		StringBuilder sqlStr = new StringBuilder();

		sqlStr.append("from (select * from " + tableName);
		if (fieldList != null && fieldList.keySet() != null) {
			Iterator<String> iterator = fieldList.keySet().iterator();
			List<String> keyList = new ArrayList<>();
			while (iterator.hasNext()) {
				keyList.add(iterator.next());
			}
			for (int i = 0; i < keyList.size(); i++) {
				if (i == 0)
					sqlStr.append(" where ");
				String key = keyList.get(i);
				String value = fieldList.get(key);
				sqlStr.append(key + "='" + value + "'");
				if (keyList.size() >= 2 && i != keyList.size() - 1)
					sqlStr.append(" AND ");
			}
		}
		sqlStr.append(" ) " + tableName);
		return queryTable(tableName, sqlStr.toString(), currentPage, pageSize);
	}

	//升序排序
	public static ArrayList<?> queryTable(String tableName, int currentPage, int pageSize,
			Map<String, String> fieldList, String orderStr) {
		StringBuilder sqlStr = new StringBuilder();

		sqlStr.append("from (select * from " + tableName);
		if (fieldList != null && fieldList.keySet() != null) {
			Iterator<String> iterator = fieldList.keySet().iterator();
			List<String> keyList = new ArrayList<>();
			while (iterator.hasNext()) {
				keyList.add(iterator.next());
			}
			for (int i = 0; i < keyList.size(); i++) {
				if (i == 0)
					sqlStr.append(" where ");
				String key = keyList.get(i);
				String value = fieldList.get(key);
				sqlStr.append(key + "='" + value + "'");
				if (keyList.size() >= 2 && i != keyList.size() - 1)
					sqlStr.append(" AND ");
			}
		}
		if (orderStr != null) {
			sqlStr.append("order by " + orderStr + " " + "DESC");
		}
		sqlStr.append(" ) " + tableName);

		return queryTable(tableName, sqlStr.toString(), currentPage, pageSize);
	}

	/**
	 * 根据表名，执行sql语句查询表记录；
	 *
	 * @param tableName
	 * @param sqlStr
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<BaseTable> queryTable(String tableName, String sqlStr) {
		return queryTable(tableName, sqlStr, -1, -1);
	}

	public static ArrayList<BaseTable> queryTable(String tableName, String sqlStr, int currentPage, int pageSize) {
		return queryTable(sqlStr, pageSize == -1 ? "" : String.valueOf(pageSize), "", "", "SEARCHYOUNGCONTENT",
				new DocInfor("", tableName), true, false,
				currentPage * pageSize == -1 ? "" : String.valueOf(((currentPage - 1) * pageSize)));
	}

	/**
	 * 查询指定的表，取offset(默认从第1个开始取)到num(默认取结果总数)之间的结果
	 *
	 * @param sqlStr
	 *           sql语句
	 * @param num
	 *            取结果数量
	 * @param orderBy
	 *            排序
	 * @param fieldNameList
	 *            字段名列表
	 * @param command
	 *            指令
	 * @param docInfor
	 *            表名和表记录contentId
	 * @param isSimpleSearch
	 * @param noTincludedocInfo
	 * @param offset
	 *            取结果起始位置
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<BaseTable> queryTable(String sqlStr, String num, String orderBy, String fieldNameList,
			String command, DocInfor docInfor, boolean isSimpleSearch, boolean noTincludedocInfo, String offset) {
		DataSetList resultData = null;
		ArrayList<BaseTable> tableDataList = new ArrayList<BaseTable>();

		String xmlStr = XmlPackage.packageSelect(sqlStr, num, orderBy, fieldNameList, command, docInfor, isSimpleSearch,
				noTincludedocInfo, offset);
		try {
			resultData = PostHttp.PostXML(xmlStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		tableDataList = DataParser.getTable(resultData, docInfor.getContentName());
		return tableDataList;
	}

	/**
	 * 向服务器端 插入一条表记录/更新现有的一条记录
	 *
	 * @param tableData
	 *            tableData 要 插入/更新 的表记录
	 * @return 插入成功的表记录的contentId
	 * @throws Exception
	 */

	public static boolean insertOrUpdateTable(BaseTable tableData) {
		boolean result = false;

		String xmlStr = "";

		xmlStr = XmlPackage.packageForSaveOrUpdate((HashMap<?, ?>) tableData.getFieldList(),
				new DocInfor(tableData.getContentId(), tableData.getTableName()), // 褰撹琛ㄨ褰曠殑contentId瀵瑰簲鏁版嵁搴撲腑鐨勪竴鏉″凡鏈夌殑琛ㄨ褰曟椂锛屾洿鏂拌鏉¤褰曪紱涓嶅搴斿凡鏈夎褰曟椂锛屾坊鍔犳柊璁板綍
				false);

		DataSetList resultData = null;
		try {
			resultData = PostHttp.PostXML(xmlStr);

		} catch (Exception e) {
			e.printStackTrace();

		}
		if (ApiConstants.REQUEST_RESULT_SUCCESS.equals(resultData.SUCCESS)) {
			if (resultData.CONTENTID != null && resultData.CONTENTID.size() != 0) {
				tableData.setContentId(resultData.CONTENTID.get(0));
			}
			result = true;
		} else {
			Log.i(tableData.getTableName(), resultData.ERROR);
		}

		return result;
	}

	/**
	 * 向服务器端 插入一条表记录/更新现有的一条记录
	 * 
	 * @param tableData
	 *            要插入/更新 的表记录
	 * @param file
	 *            附件
	 * @return 插入成功的表记录的contentId
	 * @throws Exception
	 */
	public static boolean insertOrUpdateTable(BaseTable tableData, Document file) {
		return insertOrUpdateTable(tableData, new Document[] { file });
	}

	public static boolean insertOrUpdateTable(BaseTable tableData, Document[] file) {
		boolean result = false;

		String xmlStr = "";

		String[] filePath = new String[file.length];
		String[] fileType = new String[file.length];
		for (int i = 0; i < file.length; i++) {
			filePath[i] = file[i] == null ? "" : file[i].getPath();
			fileType[i] = file[i] == null ? "" : file[i].getFileType();
		}

		xmlStr = XmlPackage.packageForInsertFileData((HashMap<?, ?>) tableData.getFieldList(),
				new DocInfor(tableData.getContentId(), tableData.getTableName()), true, filePath, fileType);
		DataSetList resultData = null;
		try {
			resultData = PostHttp.PostXML(xmlStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (resultData != null) {
			if (ApiConstants.REQUEST_RESULT_SUCCESS.equals(resultData.SUCCESS)) {
				if (resultData.CONTENTID != null && resultData.CONTENTID.size() != 0) {
					tableData.setContentId(resultData.CONTENTID.get(0));
				}
				result = true;
			}
		}

		return result;
	}

	/**
	 * 删除服务器上的一条表记录
	 *
	 * @param tableData
	 * @return 删除成功或失败
	 * @throws Exception
	 */
	public static boolean deleteTable(BaseTable tableData) {
		boolean result = false;

		String xmlStr = XmlPackage.packageDelete(tableData.getContentId());

		DataSetList resultData = null;
		try {
			resultData = PostHttp.PostXML(xmlStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ApiConstants.REQUEST_RESULT_SUCCESS.equals(resultData.SUCCESS))
			result = true;

		return result;
	}
}
