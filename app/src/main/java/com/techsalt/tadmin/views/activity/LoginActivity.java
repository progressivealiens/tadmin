package com.techsalt.tadmin.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.techsalt.tadmin.R;
import com.techsalt.tadmin.customviews.MyButton;
import com.techsalt.tadmin.helper.CheckNetworkConnection;
import com.techsalt.tadmin.helper.PrefData;
import com.techsalt.tadmin.helper.ProgressView;
import com.techsalt.tadmin.helper.Utils;
import com.techsalt.tadmin.helper.Validation;
import com.techsalt.tadmin.webapi.ApiClient;
import com.techsalt.tadmin.webapi.ApiInterface;
import com.techsalt.tadmin.webapi.ApiResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.et_login_email)
    TextInputEditText etLoginEmail;
    @BindView(R.id.et_login_password)
    TextInputEditText etLoginPassword;
    @BindView(R.id.tiPassword)
    TextInputLayout tiPassword;
    @BindView(R.id.btn_login)
    MyButton btnLogin;
    @BindView(R.id.root_login)
    ScrollView rootLogin;
    @BindView(R.id.iv_trackkers)
    ImageView ivTrackkers;
    @BindView(R.id.et_login_admin_email)
    TextInputEditText etLoginAdminEmail;
    @BindView(R.id.ti_login_admin_email)
    TextInputLayout tiLoginAdminEmail;

    ApiInterface apiInterface;
    ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initialize();

        getFirebaseToken();

        Log.e("FireBase Token : ", PrefData.readStringPref(PrefData.firebase_token));
    }

    private void initialize() {
        apiInterface = ApiClient.getClient(LoginActivity.this).create(ApiInterface.class);
        progressView = new ProgressView(LoginActivity.this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_login:

                if (Validation.nullValidator(etLoginEmail.getText().toString())) {
                    Utils.showSnackBar(rootLogin, "Email Id can't be blank", etLoginEmail, LoginActivity.this);
                } else if (!Validation.emailValidator(etLoginEmail.getText().toString())) {
                    Utils.showSnackBar(rootLogin, "Please fill the Email Id in proper format", etLoginEmail, LoginActivity.this);
                } else if (Validation.nullValidator(etLoginAdminEmail.getText().toString())) {
                    Utils.showSnackBar(rootLogin, "Email Id can't be blank", etLoginAdminEmail, LoginActivity.this);
                } else if (!Validation.emailValidator(etLoginAdminEmail.getText().toString())) {
                    Utils.showSnackBar(rootLogin, "Please fill the Email Id in proper format", etLoginAdminEmail, LoginActivity.this);
                } else if (Validation.nullValidator(etLoginPassword.getText().toString())) {
                    Utils.showSnackBar(rootLogin, "Password can't be blank", etLoginPassword, LoginActivity.this);
                } /*else if (!Validation.passValidator(etLoginPassword.getText().toString())) {
                    Utils.showSnackBar(rootLogin, "Password must be 6 characters long", etLoginPassword, LoginActivity.this);
                } */else {
                    connectApiToLogin(etLoginEmail.getText().toString(),etLoginAdminEmail.getText().toString(), etLoginPassword.getText().toString());
                }

                break;
        }
    }

    private void connectApiToLogin(String companyEmail,String adminEmail, String password) {
        if (CheckNetworkConnection.isConnection1(LoginActivity.this, true)) {
            progressView.showLoader();
            Call<ApiResponse> call = apiInterface.Login(
                    PrefData.readStringPref(PrefData.firebase_token),
                    companyEmail,
                    adminEmail,
                    password
            );
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();
                    try {
                        if (response.body()!=null && response.body().getStatus()!=null){
                            if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                                PrefData.writeBooleanPref(PrefData.PREF_LOGINSTATUS, true);
                                PrefData.writeStringPref(PrefData.PREF_Company_name, response.body().getCompanyName());
                                PrefData.writeStringPref(PrefData.company_logo, response.body().getCompanyLogo());
                                PrefData.writeStringPref(PrefData.PREF_Company_email, companyEmail);
                                PrefData.writeStringPref(PrefData.PREF_admin_email, adminEmail);
                                PrefData.writeStringPref(PrefData.admin_id,String.valueOf(response.body().getAdminId()));
                                Utils.showToast(LoginActivity.this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorLightGreen), getResources().getColor(R.color.colorWhite));

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Utils.showSnackBar(rootLogin, response.body().getMsg(), LoginActivity.this);
                            }
                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Utils.showToast(LoginActivity.this, getResources().getString(R.string.bad_request), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                        } else if (response.code() == 500) {
                            Utils.showToast(LoginActivity.this, getResources().getString(R.string.network_busy), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                        } else if (response.code() == 404) {
                            Utils.showToast(LoginActivity.this, getResources().getString(R.string.not_found), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                        } else {
                            Utils.showToast(LoginActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                        }
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    progressView.hideLoader();
                }
            });
        }
    }

    public void getFirebaseToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult().getToken();
                            PrefData.writeStringPref(PrefData.firebase_token, token);
                            Log.e("FireBase Token : ", token);
                        } else {
                            Log.e("FireBase TokenFail", "getInstanceId failed", task.getException());
                            return;
                        }
                    }
                });
    }


}
