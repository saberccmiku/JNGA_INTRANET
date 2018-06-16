package com.yskj.jnga.task;

import java.util.ArrayList;
import java.util.Timer;

import com.yskj.jnga.App;
import com.yskj.jnga.network.xml.BaseTable;
import com.yskj.jnga.network.xml.DataOperation;
import com.yskj.jnga.utils.SpUtil;
import android.os.AsyncTask;
import android.util.Log;

public class QueryPoliceInfoTask {

	public QueryPoliceInfoTask() {
	}

	public void setPoliceInfo(String policeNum) {
		new MyTask().execute("XNGA_POLICE_REQ", policeNum);
	}

	public class MyTask extends AsyncTask<String, Integer, Integer> {
		private String sqlStr;
		private String policeNum;
		private String contentId;
		private Timer timer;
		private SpUtil mSpUtil = App.getInstance().getSpUtil();

		@Override
		protected Integer doInBackground(String... params) {
			switch (params[0]) {
			case "XNGA_POLICE_REQ":
				try {

					policeNum = params[1];
					BaseTable table = new BaseTable();
					table.setTableName("XNGA_POLICE_REQ");
					table.putField("RESULT_POLICE_NUM", policeNum);
					table.putField("Switch", "0");
					if (DataOperation.insertOrUpdateTable(table)) {
						contentId = table.getContentId();
						return 1;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return -1;
				}
				break;

			default:
				break;
			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer result) {

			switch (result) {
			case -1:

				break;
			case 1:

				sqlStr = "from (select * from XNGA_POLICE_RESULT WHERE FLAG_ID = '" + contentId
						+ "' ) XNGA_POLICE_RESULT";

				timer = new Timer();
				timer.schedule(new QueryDataTask(timer, "XNGA_POLICE_RESULT") {

					@Override
					public void doingTask() {
						try {
							BaseTable table = null;
							ArrayList<BaseTable> tables = DataOperation.queryTable("XNGA_POLICE_RESULT", sqlStr, 0, 1);
							if (tables != null && tables.size() != 0) {
								table = tables.get(0);
							}

							if (table != null) {
								mSpUtil.setPoliceInfo(table);
								timer.cancel();
								Log.v("XNGA_POLICE_RESULT", "POLICE已查到结果");
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}, 3 * 1000, 2 * 1000);

				break;

			default:
				break;
			}

		}

	}

}
