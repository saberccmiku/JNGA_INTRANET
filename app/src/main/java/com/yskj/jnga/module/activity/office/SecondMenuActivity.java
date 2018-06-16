package com.yskj.jnga.module.activity.office;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonRecyclerAdapter;
import com.yskj.jnga.adapter.CommonRecyclerViewHolder;
import com.yskj.jnga.entity.MenusEntity;
import com.yskj.jnga.module.base.RxBaseActivity;
import com.yskj.jnga.utils.SpUtil;

public class SecondMenuActivity extends RxBaseActivity {

	public SpUtil mSpUtil;

	@Override
	public int getLayoutId() {
		return R.layout.activity_pinned_section_list;
	}

	@Override
	public void initViews(Bundle savedInstanceState) {

		ImageButton btn_back = findViewById(R.id.btn_back);
		btn_back.setOnClickListener(v->this.finish());

		mSpUtil = App.getInstance().getSpUtil();
		ArrayList<MenusEntity> list = new ArrayList<>();
		list.add(new MenusEntity("公务用车", R.drawable.gwyc,new Intent(this,OfficialVehiclesActivity.class)));
		list.add(new MenusEntity("请假申请", R.drawable.qjsq,new Intent(this,HolidaysActivity.class)));
		RecyclerView rv = findViewById(R.id.rv);
		rv.setLayoutManager(new GridLayoutManager(this,4));
		CommonRecyclerAdapter<MenusEntity> adapter = new CommonRecyclerAdapter<MenusEntity>(this,R.layout.list_section_item,list) {
			@Override
			public void convertView(CommonRecyclerViewHolder holder, MenusEntity entity) {

				holder.setBackgroundResource(R.id.iv,entity.getDrawableId());
				holder.setText(R.id.tv_item,entity.getMenuTitle());

			}
		};
		rv.setAdapter(adapter);
		adapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				startActivity(list.get(position).getToIntent());
			}

			@Override
			public void onItemLongClick(View view, int position) {

			}
		});

	}

	@Override
	public void initToolBar() {

	}


}