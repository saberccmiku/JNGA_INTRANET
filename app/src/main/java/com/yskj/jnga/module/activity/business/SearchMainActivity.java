package com.yskj.jnga.module.activity.business;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonRecyclerAdapter;
import com.yskj.jnga.adapter.CommonRecyclerViewHolder;
import com.yskj.jnga.entity.MenusEntity;
import com.yskj.jnga.module.base.RxBaseActivity;
import com.yskj.jnga.network.ApiConstants;
import com.yskj.jnga.utils.SpUtil;
import com.yskj.jnga.widget.search.SearchAdapter;
import com.yskj.jnga.widget.search.SharedPreference;
import com.yskj.jnga.widget.search.Utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 业务查询主界面，呈现查询类别
 *
 * @author perng
 */
public class SearchMainActivity extends RxBaseActivity implements OnItemClickListener {
    private SpUtil mSpUtil;
    private ArrayList<MenusEntity> mSearchMainList;
    private CommonRecyclerAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        mSpUtil = App.getInstance().getSpUtil();
        mSearchMainList = new ArrayList<>();
        RecyclerView rv_menu = findViewById(R.id.rv_menu);
        rv_menu.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new CommonRecyclerAdapter<MenusEntity>(SearchMainActivity.this, R.layout.item_menu, mSearchMainList) {
            @Override
            public void convertView(CommonRecyclerViewHolder holder, MenusEntity menuEntity) {
                holder.setBackgroundResource(R.id.iv, menuEntity.getDrawableId());
                holder.setText(R.id.tv, menuEntity.getMenuTitle());
            }

        };
        rv_menu.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mSearchMainList.get(position).getToIntent() != null) {
                    // 设置物品查询功能暂时不能使用
                    if (mSearchMainList.get(position).getMenuTitle().equals("物品查询")) {
                        return;
                    }

                    startActivityForResult(mSearchMainList.get(position).getToIntent(), 0);
                } else {
                    if (mSearchMainList.get(position).getMenuTitle().equals("警力查询")) {
                        loadToolBarSearch(SearchMainActivity.this, view);
                    }
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        initMenuDate();

        ImageButton ib_back = findViewById(R.id.ib_baack);
        ib_back.setOnClickListener(v -> SearchMainActivity.this.finish());
    }

    @Override
    public void initToolBar() {

    }

    private void initMenuDate() {

        // 一键查询OneKey
        MenusEntity okEnity;
        okEnity = new MenusEntity();
        okEnity.setMenuTitle("一键查询");
        okEnity.setDrawableId(R.drawable.menu_news_search);
        okEnity.setToIntent(new Intent(this, SearchInfoActivity.class).putExtra("Tag", ApiConstants.ONE_KEY_RESEARCH));
        mSearchMainList.add(okEnity);

        // 人员查询Person
        MenusEntity pEnity;
        pEnity = new MenusEntity();
        pEnity.setMenuTitle("人员查询");
        pEnity.setDrawableId(R.drawable.menu_people_search);
        pEnity.setToIntent(new Intent(this, SearchInfoActivity.class).putExtra("Tag", ApiConstants.PERSON_RESEARCH));
        mSearchMainList.add(pEnity);

        // 车辆查询Vehicle
        MenusEntity vEnity;
        vEnity = new MenusEntity();
        vEnity.setMenuTitle("车辆查询");
        vEnity.setDrawableId(R.drawable.menu_car_id);
        vEnity.setToIntent(new Intent(this, SearchInfoActivity.class).putExtra("Tag", ApiConstants.VEHICLE_RESEARCH));
        mSearchMainList.add(vEnity);

        // 物品查询Goods
        MenusEntity gEnity;
        gEnity = new MenusEntity();
        gEnity.setMenuTitle("物品查询");
        gEnity.setDrawableId(R.drawable.menu_goods_search);
        gEnity.setToIntent(new Intent(this, SearchInfoActivity.class).putExtra("Tag", ApiConstants.GOODS_RESEARCH));
        mSearchMainList.add(gEnity);

        // 案件查询Case
        MenusEntity cEnity;
        cEnity = new MenusEntity();
        cEnity.setMenuTitle("案件查询");
        cEnity.setDrawableId(R.drawable.menu_record);
        cEnity.setToIntent(new Intent(this, SearchInfoActivity.class).putExtra("Tag", ApiConstants.CASE_RESEARCH));
        mSearchMainList.add(cEnity);

//        // 地图查询Map
//        MenusEntity mapEnity;
//        mapEnity = new MenusEntity();
//        mapEnity.setMenuTitle("地图查询");
//        mapEnity.setDrawableId(R.drawable.menu_map_search);
//        mapEnity.setToIntent(new Intent(this, MapActivity.class).putExtra("Tag", ApiConstants.MAP_RESEARCH));
//        // 若用户名为"xxx"则开放地图查询功能
//        // if (mSpUtil.getAccount().equals("103932") ||
//        // mSpUtil.getAccount().equals("NX")) {
//        mSearchMainList.add(mapEnity);
//        // }

        // 警情查询(领导拥有权限)
        MenusEntity alarmEnity;
        alarmEnity = new MenusEntity();
        alarmEnity.setMenuTitle("警情查询");
        alarmEnity.setDrawableId(R.drawable.menu_jqcx);
        alarmEnity
                .setToIntent(new Intent(this, SearchAlarmActivity.class).putExtra("Tag", ApiConstants.ALARM_RESEARCH));
        mSearchMainList.add(alarmEnity);

        // 警员去向查询(领导拥有权限，关联请假表)
        MenusEntity policeStatusEnity;
        policeStatusEnity = new MenusEntity();
        policeStatusEnity.setMenuTitle("警力查询");
        policeStatusEnity.setDrawableId(R.drawable.ico_police_status);
        mSearchMainList.add(policeStatusEnity);

        mAdapter.notifyDataSetChanged();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mSearchMainList.get(position).getToIntent() != null) {
            // 协警是否在值班范围

            startActivityForResult(mSearchMainList.get(position).getToIntent(), 0);

            // 设置物品查询功能暂时不能使用
            if (mSearchMainList.get(position).getMenuTitle().equals("物品查询")) {
                return;
            }

            startActivityForResult(mSearchMainList.get(position).getToIntent(), 0);
        } else {
            if (mSearchMainList.get(position).getMenuTitle().equals("警力查询")) {
                loadToolBarSearch(SearchMainActivity.this, view);
            }
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    /**
     * 加载搜索栏目
     *
     * @param activity
     */
    public void loadToolBarSearch(final Activity activity, final View positionView) {

        ArrayList<String> countryStored = SharedPreference.loadList(activity, Utils.PREFS_NAME, Utils.KEY_PEOPLE);
        View view = activity.getLayoutInflater().inflate(R.layout.view_toolbar_search, null);
        LinearLayout parentToolbarSearch = view.findViewById(R.id.parent_toolbar_search);
        ImageView imgToolBack = view.findViewById(R.id.img_tool_back);
        final EditText edtToolSearch = view.findViewById(R.id.edt_tool_search);
        ImageView img_tool_alarm = view.findViewById(R.id.img_tool_alarm);
        img_tool_alarm.setVisibility(View.GONE);
        ImageView imgToolMic = view.findViewById(R.id.img_tool_mic);
        final ListView listSearch = view.findViewById(R.id.list_search);
        final TextView txtEmpty = view.findViewById(R.id.txt_empty);

        Utils.setListViewHeightBasedOnChildren(listSearch);

        edtToolSearch.setHint("输入警号查警员询最近信息");

        final Dialog toolbarSearchDialog = new Dialog(activity, R.style.MaterialSearch);
        toolbarSearchDialog.setContentView(view);
        //toolbarSearchDialog.setCancelable(false);
        toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
        toolbarSearchDialog.show();

        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        countryStored = (countryStored != null && countryStored.size() > 0) ? countryStored : new ArrayList<>();
        final SearchAdapter searchAdapter = new SearchAdapter(activity, countryStored, false);

        listSearch.setVisibility(View.VISIBLE);
        listSearch.setAdapter(searchAdapter);

        listSearch.setOnItemClickListener((adapterView, view1, position, l) -> {

            String country = String.valueOf(adapterView.getItemAtPosition(position));
            SharedPreference.addList(activity, Utils.PREFS_NAME, Utils.KEY_PEOPLE, country);
            edtToolSearch.setText(country);
            listSearch.setVisibility(View.GONE);

        });
        edtToolSearch.setOnEditorActionListener((v, actionId, event) -> {

            if (!TextUtils.isEmpty(edtToolSearch.getText().toString().trim())) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) edtToolSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(positionView.getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                    // enter AlarmSearchActivity information

                    if (com.yskj.jnga.utils.Utils.isNetworkAvailable(activity)) {

                        activity.startActivity(new Intent(activity, PoliceStatusActivity.class)
                                .putExtra("PoliceStatusActivity", v.getText().toString()));
                        return true;
                    }
                }
            } else {
                Toast.makeText(activity, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                return false;
            }

            return false;
        });
        edtToolSearch.addTextChangedListener(new TextWatcher() {

            private ArrayList<String> mCountries;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                String[] country = activity.getResources().getStringArray(R.array.people_array);
                mCountries = new ArrayList<>(Arrays.asList(country));
                listSearch.setVisibility(View.VISIBLE);
                searchAdapter.updateList(mCountries, true);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> filterList = new ArrayList<>();
                boolean isNoData = false;
                if (s.length() > 0) {
                    for (int i = 0; i < mCountries.size(); i++) {
                        if (mCountries.get(i).toLowerCase().startsWith(s.toString().trim().toLowerCase())) {
                            filterList.add(mCountries.get(i));
                            listSearch.setVisibility(View.VISIBLE);
                            searchAdapter.updateList(filterList, true);
                            isNoData = true;
                        }
                    }
                    if (!isNoData) {
                        listSearch.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.VISIBLE);
                        txtEmpty.setText("No data found");
                    }
                } else {
                    listSearch.setVisibility(View.GONE);
                    txtEmpty.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imgToolBack.setOnClickListener(view12 -> toolbarSearchDialog.dismiss());

        imgToolMic.setOnClickListener(view13 -> edtToolSearch.setText(""));

    }


}
