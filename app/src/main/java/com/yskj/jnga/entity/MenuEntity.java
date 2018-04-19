package com.yskj.jnga.entity;

import android.content.Intent;

/**
 * Created by 63987 on 2018/4/18
 * 图标类
 */

public class MenuEntity {
    private String id;
    private String title;
    private int resId;
    private int mark;
    private Intent intent;


    public MenuEntity(String title, int resId, Intent intent) {
        this.title = title;
        this.resId = resId;
        this.intent = intent;
    }

    public MenuEntity(String id, String title, int resId, int mark) {
        this.id = id;
        this.title = title;
        this.resId = resId;
        this.mark = mark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    @Override
    public String toString() {
        return "MenuEntity{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", resId=" + resId +
                ", mark=" + mark +
                ", intent=" + intent +
                '}';
    }
}
