package com.techsalt.tadmin.views.activity;

import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.techsalt.tadmin.R;
import com.techsalt.tadmin.adapter.GuardAttendanceAdapter;
import com.techsalt.tadmin.customviews.MyButton;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.helper.CheckNetworkConnection;
import com.techsalt.tadmin.helper.PrefData;
import com.techsalt.tadmin.helper.ProgressView;
import com.techsalt.tadmin.helper.Utils;
import com.techsalt.tadmin.webapi.ApiClient;
import com.techsalt.tadmin.webapi.ApiInterface;
import com.techsalt.tadmin.webapi.ApiResponse;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuardRouteWiseAttendActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_guard_attendance)
    RecyclerView recyclerGuardAttendance;
    @BindView(R.id.tv_date)
    MyTextview tvDate;
    @BindView(R.id.btn_guard_attendance)
    MyButton btnGuardAttendance;
    @BindView(R.id.root_guard_attend)
    LinearLayout rootGuardAttend;
    @BindView(R.id.tv_empty)
    MyTextview tvEmpty;
    @BindView(R.id.et_search_person)
    TextInputEditText etSearchPerson;
    @BindView(R.id.ti_search_person)
    TextInputLayout tiSearchPerson;

    GuardAttendanceAdapter mAdapter;
    List<ApiResponse.DataBean> guardAttendanceModelList = new ArrayList<>();

    List<ApiResponse.DataBean> searchedResult = new ArrayList<>();
    String searchedName="";

    ApiInterface apiInterface;
    ProgressView progressView;

    String guardRouteId = "", SelectedDate = "", formatSelectedDate = "";
    long timeStamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_route_wise_attend);
        ButterKnife.bind(this);

        initialize();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            guardRouteId = bundle.getString(GuardAttendanceActivity.routeId);
        }


        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerGuardAttendance.setLayoutManager(manager);
        mAdapter = new GuardAttendanceAdapter(this, guardAttendanceModelList);
        recyclerGuardAttendance.setAdapter(mAdapter);

        connectApiToGetAttendanceGuard();

        etSearchPerson.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchedName=s.toString().toLowerCase().trim();
                searchedResult.clear();

                for (int i=0;i<guardAttendanceModelList.size();i++){
                    if (guardAttendanceModelList.get(i).getName().toLowerCase().contains(searchedName)){
                        searchedResult.add(guardAttendanceModelList.get(i));
                    }
                }

                LinearLayoutManager manager = new LinearLayoutManager(GuardRouteWiseAttendActivity.this);
                recyclerGuardAttendance.setLayoutManager(manager);

                if (count==0){
                    mAdapter = new GuardAttendanceAdapter(GuardRouteWiseAttendActivity.this, guardAttendanceModelList);
                }else{
                    mAdapter = new GuardAttendanceAdapter(GuardRouteWiseAttendActivity.this, searchedResult);
                }
                recyclerGuardAttendance.setAdapter(mAdapter);

                tvEmpty.setVisibility(View.GONE);
                recyclerGuardAttendance.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void initialize() {
        setSupportActionBar(toolbar);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Guard Attendance");

        ivBack.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        btnGuardAttendance.setOnClickListener(this);

        apiInterface = ApiClient.getClient(GuardRouteWiseAttendActivity.this).create(ApiInterface.class);
        progressView = new ProgressView(GuardRouteWiseAttendActivity.this);

        timeStamp = Long.parseLong(Utils.currentTimeStamp());
        SelectedDate = Utils.selectedDateAndTimeFormat(timeStamp, tvDate);
        try {
            formatSelectedDate = Utils.formatDate(SelectedDate, "dd/MM/yyyy", "MM/dd/yyyy");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void connectApiToGetAttendanceGuard() {
        if (CheckNetworkConnection.isConnection1(GuardRouteWiseAttendActivity.this, true)) {
            progressView.showLoader();

            Call<ApiResponse> call = apiInterface.findAllGuardDetails(
                    PrefData.readStringPref(PrefData.PREF_Company_name),
                    formatSelectedDate,
                    guardRouteId
            );
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();
                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            guardAttendanceModelList.clear();
                            guardAttendanceModelList.addAll(response.body().getData());
                            tvEmpty.setVisibility(View.GONE);
                            recyclerGuardAttendance.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();
                            tiSearchPerson.setVisibility(View.VISIBLE);

                        } else {

                            guardAttendanceModelList.clear();
                            mAdapter.notifyDataSetChanged();
                            tvEmpty.setVisibility(View.VISIBLE);
                            recyclerGuardAttendance.setVisibility(View.GONE);
                            tvEmpty.setText(response.body().getMsg());
                            tiSearchPerson.setVisibility(View.GONE);

                            Utils.showSnackBar(rootGuardAttend, response.body().getMsg(), GuardRouteWiseAttendActivity.this);
                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(GuardRouteWiseAttendActivity.this, R.string.bad_request, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(GuardRouteWiseAttendActivity.this, R.string.network_busy, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(GuardRouteWiseAttendActivity.this, R.string.not_found, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(GuardRouteWiseAttendActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_date:
                Utils.showDatePicker(GuardRouteWiseAttendActivity.this, tvDate);
                break;
            case R.id.btn_guard_attendance:
                try {

                    SelectedDate = tvDate.getText().toString();
                    formatSelectedDate = Utils.formatDate(SelectedDate, "dd/mm/yyyy", "mm/dd/yyyy");

                    connectApiToGetAttendanceGuard();

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                break;
        }
    }
}
