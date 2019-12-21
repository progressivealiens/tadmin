package com.techsalt.tadmin.views.activity;

import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.techsalt.tadmin.R;
import com.techsalt.tadmin.adapter.OperationalAbsentAdapter;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.helper.CheckNetworkConnection;
import com.techsalt.tadmin.helper.PrefData;
import com.techsalt.tadmin.helper.ProgressView;
import com.techsalt.tadmin.helper.Utils;
import com.techsalt.tadmin.webapi.ApiClient;
import com.techsalt.tadmin.webapi.ApiInterface;
import com.techsalt.tadmin.webapi.ApiResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OperationalAbsentActivity extends AppCompatActivity {

    @BindView(R.id.root_absent)
    LinearLayout rootAbsent;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.tv_subtitle)
    MyTextview tvSubtitle;
    @BindView(R.id.lin_toolbar)
    LinearLayout linToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_emp_designation)
    TextInputEditText etEmpDesignation;
    @BindView(R.id.et_date)
    TextInputEditText etDate;
    @BindView(R.id.recycler_absent)
    RecyclerView recyclerAbsent;

    OperationalAbsentAdapter mAdapter;
    List<ApiResponse.DataBean> absentList = new ArrayList<>();


    String dateSearched = "", designation = "";
    ApiInterface apiInterface;
    ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operational_absent);
        ButterKnife.bind(this);

        initialize();


        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerAbsent.setLayoutManager(manager);
        mAdapter = new OperationalAbsentAdapter(this, absentList);
        recyclerAbsent.setAdapter(mAdapter);

        connectApiToGetAbsentList();

    }

    private void initialize() {
        if (getIntent() != null) {
            dateSearched = getIntent().getStringExtra("date");
            designation = getIntent().getStringExtra("designationName");
        }

        apiInterface = ApiClient.getClient(OperationalAbsentActivity.this).create(ApiInterface.class);
        progressView = new ProgressView(OperationalAbsentActivity.this);


        setSupportActionBar(toolbar);
        tvTitle.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setText("ABSENT LIST");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        etEmpDesignation.setText(designation);
        etDate.setText(dateSearched);

    }


    private void connectApiToGetAbsentList() {

        if (CheckNetworkConnection.isConnection1(OperationalAbsentActivity.this, true)) {

            progressView.showLoader();

            Call<ApiResponse> call = apiInterface.findAllAbsentOperationDetails(
                    PrefData.readStringPref(PrefData.PREF_Company_name),
                    dateSearched,
                    designation
            );
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();
                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            absentList.clear();
                            absentList.addAll(response.body().getData());
                            //tvEmpty.setVisibility(View.GONE);
                            recyclerAbsent.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();

                        } else {

                            Utils.showSnackBar(rootAbsent, response.body().getMsg(), OperationalAbsentActivity.this);
                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(OperationalAbsentActivity.this, R.string.bad_request, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(OperationalAbsentActivity.this, R.string.network_busy, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(OperationalAbsentActivity.this, R.string.not_found, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OperationalAbsentActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
