package com.techsalt.tadmin.views.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.techsalt.tadmin.R;
import com.techsalt.tadmin.adapter.GuardRouteAdapter;
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

public class GuardAttendanceActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_guard_route)
    RecyclerView recyclerGuardRoute;
    @BindView(R.id.root_route)
    LinearLayout rootRoute;

    GuardRouteAdapter mAdapter;
    List<ApiResponse.DataBean> guardRouteModels = new ArrayList<>();

    ApiInterface apiInterface;
    ProgressView progressView;

    public static String routeId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_attendance);
        ButterKnife.bind(this);

        initialize();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerGuardRoute.setLayoutManager(manager);
        mAdapter = new GuardRouteAdapter(this, guardRouteModels);
        recyclerGuardRoute.setAdapter(mAdapter);

        connectApiToGetRoutesForGuard();
    }

    private void initialize() {
        setSupportActionBar(toolbar);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Select Site & Route");

        ivBack.setOnClickListener(this);

        apiInterface = ApiClient.getClient(GuardAttendanceActivity.this).create(ApiInterface.class);
        progressView = new ProgressView(GuardAttendanceActivity.this);

    }

    private void connectApiToGetRoutesForGuard() {
        if (CheckNetworkConnection.isConnection1(GuardAttendanceActivity.this, true)) {

            progressView.showLoader();
            Call<ApiResponse> call = apiInterface.findAllRoute(PrefData.readStringPref(PrefData.PREF_Company_name));

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();

                    try {
                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            guardRouteModels.clear();
                            guardRouteModels.addAll(response.body().getData());

                            mAdapter.notifyDataSetChanged();


                        } else {
                            Utils.showSnackBar(rootRoute, response.body().getMsg(), GuardAttendanceActivity.this);
                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Utils.showToast(GuardAttendanceActivity.this,getResources().getString(R.string.bad_request),Toast.LENGTH_SHORT,getResources().getColor(R.color.colorPink),getResources().getColor(R.color.colorWhite));
                        } else if (response.code() == 500) {
                            Utils.showToast(GuardAttendanceActivity.this,getResources().getString(R.string.network_busy),Toast.LENGTH_SHORT,getResources().getColor(R.color.colorPink),getResources().getColor(R.color.colorWhite));
                        } else if (response.code() == 404) {
                            Utils.showToast(GuardAttendanceActivity.this,getResources().getString(R.string.not_found),Toast.LENGTH_SHORT,getResources().getColor(R.color.colorPink),getResources().getColor(R.color.colorWhite));
                        } else {
                            Utils.showToast(GuardAttendanceActivity.this,getResources().getString(R.string.something_went_wrong),Toast.LENGTH_SHORT,getResources().getColor(R.color.colorPink),getResources().getColor(R.color.colorWhite));
                        }
                        e.printStackTrace();

                    }

                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    progressView.hideLoader();
                    t.printStackTrace();
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
        }
    }
}
