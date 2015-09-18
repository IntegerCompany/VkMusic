package com.company.integer.vkmusic.supportclasses;

import android.content.Context;
import android.content.SharedPreferences;

import com.company.integer.vkmusic.pojo.UserPOJO;
import com.google.gson.Gson;

/**
 * Created by Andriy on 9/17/2015.
 */
public class AppState {

    private static UserPOJO loggedUser;
    private static Context context;
    private static SharedPreferences sharedPreferences;
    private static Gson gson;

    public static void setupAppState(Context ctx){
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
}
