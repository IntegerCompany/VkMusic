package com.company.integer.vkmusic.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.company.integer.vkmusic.R;
import com.company.integer.vkmusic.pojo.StylePOJO;

public class SettingsListAdapter extends BaseAdapter{
    Context ctx;
    StylePOJO[] stylePOJOs;
    LayoutInflater lInflater;

    public SettingsListAdapter(Context ctx, StylePOJO[] stylePOJOs) {
        this.ctx = ctx;
        this.stylePOJOs = stylePOJOs;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

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
            view = lInflater.inflate(R.layout.settings_list_item, parent,false);
        }

        ImageView ivColorAccent = (ImageView) view.findViewById(R.id.iv_color_accent);
        ImageView ivColorPrimary = (ImageView) view.findViewById(R.id.iv_color_primary);

        ivColorAccent.setImageDrawable(new ColorDrawable(stylePOJOs[position].getColorAccentID()));
        ivColorPrimary.setImageDrawable(new ColorDrawable(stylePOJOs[position].getColorPrimaryID()));

        return view;
    }
}
