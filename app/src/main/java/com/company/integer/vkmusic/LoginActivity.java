package com.company.integer.vkmusic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.company.integer.vkmusic.pojo.StylePOJO;
import com.company.integer.vkmusic.pojo.UserPOJO;
import com.company.integer.vkmusic.supportclasses.AppState;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

public class LoginActivity extends AppCompatActivity {

    ProgressBar pbSigningIn;
    LinearLayout signinErrorContainer;
    TextView tvSigningIn;
    Button btnTrySignInAgain;
    VKCallback<VKSdk.LoginState> loginStateCallback;
    Intent launchingIntent;

//    private VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
//        @Override
//        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
//            if (newToken == null) {
//                startMainActivity();
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Testing", getPackageName());
        super.onCreate(savedInstanceState);
        AppState.setupAppState(this);
        setDefaultStyle();
        setContentView(R.layout.activity_login);
        initViewsById();
        setListeners();
        showLoading();
        VKSdk.wakeUpSession(this, loginStateCallback);

        //vkAccessTokenTracker.startTracking();

//        if (!VKSdk.isLoggedIn()) {
//
//        }
        launchingIntent = getIntent();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Testing", "Activity onResult1");
            VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
                @Override
                public void onResult(VKAccessToken res) {
                    Log.d("Testing", "Activity VK onResult");
                    startMainActivity();
                    AppState.setLoggedUser(new UserPOJO(res.userId));
                    res.save();


                }

                @Override
                public void onError(VKError error) {
                    Log.d("Testing", "Activity VK onError");
                    if (error.errorMessage == null) {
                        tvSigningIn.setText(R.string.check_internet);
                    } else {
                        tvSigningIn.setText(error.errorMessage);
                    }
                    showErrorScreen();
                }
            });

    }

    private void initViewsById() {
        pbSigningIn = (ProgressBar) findViewById(R.id.pb_signing_in);
        signinErrorContainer = (LinearLayout) findViewById(R.id.signin_error_container);
        tvSigningIn = (TextView) findViewById(R.id.tv_signin_error);
        btnTrySignInAgain = (Button) findViewById(R.id.btn_try_signin_again);
    }

    private void setListeners() {
        loginStateCallback = new VKCallback<VKSdk.LoginState>() {
            @Override
            public void onResult(VKSdk.LoginState loginState) {

                if (loginState == VKSdk.LoginState.Pending) return;
                if (loginState == VKSdk.LoginState.LoggedIn && AppState.getLoggedUser() != null) {
                    startMainActivity();
                }else{

                    Log.d("Testing", "trying to login");
                    VKSdk.logout();
                    VKSdk.login(LoginActivity.this, VKScope.AUDIO);
                    tvSigningIn.setText(R.string.check_internet);

                }
            }

            @Override
            public void onError(VKError vkError) {
                if(vkError.errorMessage.equals("")){
                    tvSigningIn.setText(R.string.check_internet);
                }else {
                    tvSigningIn.setText(vkError.errorMessage);
                }
                showErrorScreen();
            }
        };

        btnTrySignInAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKSdk.wakeUpSession(LoginActivity.this, loginStateCallback);
                showLoading();
            }
        });
    }

    private void showErrorScreen() {
        pbSigningIn.setVisibility(View.GONE);
        signinErrorContainer.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        pbSigningIn.setVisibility(View.VISIBLE);
        signinErrorContainer.setVisibility(View.GONE);
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        AppState.setTab(launchingIntent.getIntExtra("tab", 0));
        startActivity(intent);
        finish();
    }

    private void setDefaultStyle(){
        if(AppState.getTheme()==0){
            StylePOJO stylePOJO = new StylePOJO();
            stylePOJO.setColorAccentID(ContextCompat.getColor(this,R.color.accentColor));
            stylePOJO.setColorPrimaryID(ContextCompat.getColor(this, R.color.primaryColor));
            stylePOJO.setColorPrimaryDarkID(ContextCompat.getColor(this, R.color.primaryColorDark));
            stylePOJO.setTabDividerColorID(ContextCompat.getColor(this, R.color.primaryColorDark));
            stylePOJO.setImageDrawableID(R.drawable.ic_guitar);
            AppState.setTheme(R.style.AppTheme,stylePOJO);
        }
    }


}
