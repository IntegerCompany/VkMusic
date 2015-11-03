package com.company.integer.vkmusic.supportclasses;

import android.content.Context;
import android.content.SharedPreferences;

import com.company.integer.vkmusic.pojo.StylePOJO;
import com.company.integer.vkmusic.pojo.UserPOJO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AppState {

    public static final int TRACKS_PER_LOADING = 10;
    public static final String FOLDER = "/VkMusic/";


    private static UserPOJO loggedUser;
    private static Context context;
    private static SharedPreferences sharedPreferences;
    private static Gson gson;
    private static int tab;
    private static ArrayList<Integer> ids = new ArrayList<>();
    private static ArrayList<String> searchHistory = new ArrayList<>();
    public static int adclick;

    public static void setupAppState(Context ctx) {
        context = ctx;
        sharedPreferences = context.getSharedPreferences("VkMusicData", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static UserPOJO getLoggedUser() {
        return gson.fromJson(sharedPreferences.getString("loggedUser", ""), UserPOJO.class);
    }

    public static void setLoggedUser(UserPOJO loggedUser) {
        AppState.loggedUser = loggedUser;
        sharedPreferences.edit().putString("loggedUser", gson.toJson(loggedUser)).apply();
    }

    public static int getTab() {
        return tab;
    }

    public static void setTab(int tab) {
        AppState.tab = tab;
    }

    public static void setTheme(int themeID,
                                StylePOJO stylePOJO) {
        sharedPreferences.edit().putInt("appTheme", themeID).apply();
        sharedPreferences.edit().putInt("primaryColor", stylePOJO.getColorPrimaryID()).apply();
        sharedPreferences.edit().putInt("primaryColorDark", stylePOJO.getColorPrimaryDarkID()).apply();
        sharedPreferences.edit().putInt("accentColor", stylePOJO.getColorAccentID()).apply();
        sharedPreferences.edit().putInt("tabIndicatorColor", stylePOJO.getTabDividerColorID()).apply();
        sharedPreferences.edit().putInt("albumImage", stylePOJO.getImageDrawableID()).apply();
    }

    public static int getTheme() {
        return sharedPreferences.getInt("appTheme", 0);
    }

    public static StylePOJO getColors() {
        StylePOJO stylePOJO = new StylePOJO();
        stylePOJO.setColorAccentID(sharedPreferences.getInt("accentColor", 0));
        stylePOJO.setColorPrimaryID(sharedPreferences.getInt("primaryColor", 0));
        stylePOJO.setColorPrimaryDarkID(sharedPreferences.getInt("primaryColorDark", 0));
        stylePOJO.setImageDrawableID(sharedPreferences.getInt("albumImage", 0));
        stylePOJO.setTabDividerColorID(sharedPreferences.getInt("tabIndicatorColor", 0));
        return stylePOJO;
    }

    public static void saveTrackId(int id){
        ids.add(id);
        sharedPreferences.edit().putString("ids", gson.toJson(ids)).apply();
    }

    public static ArrayList<Integer> getSavedIds(){
        Type type = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        ArrayList ids = gson.fromJson(sharedPreferences.getString("ids", ""), type);
        if (ids == null) ids = new ArrayList<>();

        return ids;
    }

    public static ArrayList<String> getSearchHistory(){
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        searchHistory = gson.fromJson(sharedPreferences.getString("searchHistory", ""), type);
        if (searchHistory == null) searchHistory = new ArrayList<>();
        return searchHistory;
    }

    public static void clearSearchHistory(){
        searchHistory.clear();
        sharedPreferences.edit().putString("searchHistory", "").apply();
    }

    public static void addToSearchHistory(String query){
        if (searchHistory.contains(query)){
            searchHistory.remove(query);
        }
        searchHistory.add(0, query);

        sharedPreferences.edit().putString("searchHistory", gson.toJson(searchHistory)).apply();
    }
}
