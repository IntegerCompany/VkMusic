package com.company.integer.vkmusic;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.company.integer.vkmusic.adapters.SettingsListAdapter;
import com.company.integer.vkmusic.pojo.StylePOJO;
import com.company.integer.vkmusic.supportclasses.AppState;

public class SettingsActivity extends AppCompatActivity {
    StylePOJO[] stylePOJOs;
    TypedArray themeArray;
    TypedArray albumPhotos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        int[] colorAccentArray = getResources().getIntArray(R.array.colorAccentArray);

        int[] colorPrimaryArray = getResources().getIntArray(R.array.colorPrimaryArray);

        int[] colorPrimaryDarkArray = getResources().getIntArray(R.array.colorPrimaryDarkArray);

        int[] colorTabIndicatorArray = getResources().getIntArray(R.array.colorTabIndicatorArray);

        themeArray = getResources().obtainTypedArray(R.array.themes);

        albumPhotos = getResources().obtainTypedArray(R.array.albumPhotos);


        stylePOJOs = new StylePOJO[colorAccentArray.length];
        for(int i = 0; i<colorAccentArray.length;i++){
            stylePOJOs[i] = new StylePOJO();
            stylePOJOs[i].setColorAccentID(colorAccentArray[i]);
            stylePOJOs[i].setColorPrimaryID(colorPrimaryArray[i]);
            stylePOJOs[i].setColorPrimaryDarkID(colorPrimaryDarkArray[i]);
            stylePOJOs[i].setTabDividerColorID(colorTabIndicatorArray[i]);
            stylePOJOs[i].setImageDrawableID(albumPhotos.getResourceId(i,0));
        }

        SettingsListAdapter adapter = new SettingsListAdapter(this,stylePOJOs);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppState.setTheme(themeArray.getResourceId(position,0),
                        stylePOJOs[position]);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        themeArray.recycle();
        albumPhotos.recycle();
        Intent in = new Intent(this,MainActivity.class);
        startActivity(in);
        finish();
    }
}
