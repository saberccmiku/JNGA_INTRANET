package com.yskj.jnga.network.xml;

import com.yskj.jnga.network.ApiConstants;

import java.util.ArrayList;
import java.util.List;

public final class DataParser {
    private DataParser() {

    }

    public static ArrayList<BaseTable> getTable(DataSetList dataList, String tableName) {
        ArrayList<BaseTable> tableDataList = new ArrayList<>();
        if (dataList != null) {
            String type = dataList.type; //表名
            ArrayList<String> contentIdList = (ArrayList<String>) dataList.CONTENTID; //contentId
            List<List<String>> documentIdList =  dataList.DOCUMENTIDLIST; //附件目录
            ArrayList<String> nameList = (ArrayList<String>) dataList.nameList; //字段名
            ArrayList<String> valueList = (ArrayList<String>) dataList.valueList; //字段值

            if (contentIdList.size() == 0) return tableDataList; //若相匹配的结果数为0，则直接返回

            //将数据库表记录数据从DataSetList中解析出来
            BaseTable tableData = null;
            int recordNum = nameList.size() / contentIdList.size(); //记录的总数
            int recordIndex = 0; //当前解析到第几条记录
            for (int i = 0; i < nameList.size(); i++) //循环解析所有的记录
            {
                if (i % recordNum == 0) {
                    tableData = newTableInstance(tableName);
                    tableData.setTableName(type);
                    tableData.setContentId(contentIdList.get(recordIndex));

                    for (String documentId : documentIdList.get(recordIndex)) {
                        String fileUrl = "http://" + ApiConstants.CONNIP + ApiConstants.FILE_PATH + documentId + "/" + "file";
                        tableData.getAccessaryFileUrlList().add(fileUrl);
                    }
                } else {
                    tableData.putField(nameList.get(i), valueList.get(i));

                    if (i % recordNum == recordNum - 1) {
                        tableDataList.add(tableData);
                        recordIndex++;
                    }
                }
            }
        }

        return tableDataList;
    }

    /**
     * 根据给定的表类型返回对应的表实例，实例以BaseTable类型保存
     *
     * @param tableName
     * @return
     */
    public static BaseTable newTableInstance(String tableName) {
        return new BaseTable();
    }
}
