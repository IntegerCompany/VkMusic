package com.company.integer.vkmusic.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.company.integer.vkmusic.R;
import com.company.integer.vkmusic.pojo.StylePOJO;

public class SettingsListAdapter extends BaseAdapter{



StylePOJO[] stylePOJOs;

    @Override
    public int getCount() {
        return stylePOJOs.length;
    }

    @Override
    public Object getItem(int position) {
        return stylePOJOs[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.available_room_item, parent,false);
        }

//
        //

        return view;
    }
}
