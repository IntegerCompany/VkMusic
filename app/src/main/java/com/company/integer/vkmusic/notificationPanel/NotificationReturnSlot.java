package com.company.integer.vkmusic.notificationPanel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.company.integer.vkmusic.services.MusicPlayerService;

/**
 * Created by maxvitruk on 25.09.15.
 */
public class NotificationReturnSlot extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("NotificationReturnSlot", "onCreate");
        super.onCreate(savedInstanceState);

        String action = (String) getIntent().getExtras().get("DO");
        assert action != null;
        if (action.equals("reboot")) {
            Log.i("NotificationReturnSlot", "reboot");
            Intent serviceIntent = new Intent(NotificationReturnSlot.this, MusicPlayerService.class);
            startService(serviceIntent);
            //Your code
        } else if (action.equals("stopNotification")) {
            //Your code
            Log.i("NotificationReturnSlot", "stopNotification");
        }
    }
}
