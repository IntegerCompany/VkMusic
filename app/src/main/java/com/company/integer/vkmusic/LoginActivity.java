package com.company.integer.vkmusic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViewsById();
        setListeners();
        showLoading();
        VKSdk.wakeUpSession(this, loginStateCallback);
        if (!VKSdk.isLoggedIn()) {
            VKSdk.login(this, VKScope.AUDIO);
        }
        launchingIntent = getIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                startMainActivity();
                AppState.setLoggedUser(new UserPOJO(res.userId));
            }

            @Override
            public void onError(VKError error) {
                tvSigningIn.setText(error.errorMessage);
                showErrorScreen();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
                if (loginState == VKSdk.LoginState.LoggedIn) {
                    startMainActivity();
                }
            }

            @Override
            public void onError(VKError vkError) {
                tvSigningIn.setText(vkError.errorMessage);
                showErrorScreen();
            }
        };

        btnTrySignInAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKSdk.login(LoginActivity.this, VKScope.AUDIO);
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
        AppState.setTab(launchingIntent.getIntExtra("tab", 1));
        startActivity(intent);
        finish();
    }


}
