package com.company.integer.vkmusic.supportclasses;

import com.company.integer.vkmusic.pojo.UserSO;

/**
 * Created by Andriy on 9/17/2015.
 */
public class AppState {

    private static UserSO loggedUser;

    public static UserSO getLoggedUser() {
        return loggedUser;
    }

    public static void setLoggedUser(UserSO loggedUser) {
        AppState.loggedUser = loggedUser;
    }
}
