package com.techsalt.tadmin.views.activity;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

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

    ApiInterface apiInterface;
    ProgressView progressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initialize();

    }

    private void initialize() {
        apiInterface = ApiClient.getClient(LoginActivity.this).create(ApiInterface.class);
        progressView = new ProgressView(LoginActivity.this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_login:

                if (Validation.nullValidator(etLoginEmail.getText().toString())){
                    Utils.showSnackBar(rootLogin,"Email Id can't be blank",etLoginEmail,LoginActivity.this);
                }else if (!Validation.emailValidator(etLoginEmail.getText().toString())){
                    Utils.showSnackBar(rootLogin,"Please fill the Email Id in proper format",etLoginEmail,LoginActivity.this);
                }else if(Validation.nullValidator(etLoginPassword.getText().toString())){
                    Utils.showSnackBar(rootLogin,"Password can't be blank",etLoginPassword,LoginActivity.this);
                }else if (!Validation.passValidator(etLoginPassword.getText().toString())){
                    Utils.showSnackBar(rootLogin,"Password must be 6 characters long",etLoginPassword,LoginActivity.this);
                }else{
                    connectApiToLogin(etLoginEmail.getText().toString(),etLoginPassword.getText().toString());
                }

                break;
        }


    }

    private void connectApiToLogin(String email,String password) {
        if (CheckNetworkConnection.isConnection1(LoginActivity.this, true)) {
            progressView.showLoader();
            Call<ApiResponse> call = apiInterface.Login(email,password);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();
                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            PrefData.writeBooleanPref(PrefData.PREF_LOGINSTATUS, true);


                            /*String companyName=;
                            String firstLetterOfCompanyName=companyName.substring(0,1).toUpperCase();
                            companyName=companyName.substring(1);

                            String finalCompanyName=firstLetterOfCompanyName+companyName;*/
                            PrefData.writeStringPref(PrefData.PREF_Company_name,response.body().getCompanyName());
                            PrefData.writeStringPref(PrefData.company_logo,response.body().getCompanyLogo());

                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();
                        } else {
                            Utils.showSnackBar(rootLogin, response.body().getMsg(), LoginActivity.this);
                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(LoginActivity.this, R.string.bad_request, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(LoginActivity.this, R.string.network_busy, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(LoginActivity.this, R.string.not_found, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
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

}
