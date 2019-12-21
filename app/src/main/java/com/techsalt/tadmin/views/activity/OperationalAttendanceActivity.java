package com.techsalt.tadmin.views.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.techsalt.tadmin.R;
import com.techsalt.tadmin.adapter.OperationalAttendanceAdapter;
import com.techsalt.tadmin.customviews.MyButton;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.customviews.customSpinner;
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

public class OperationalAttendanceActivity extends AppCompatActivity implements View.OnClickListener {

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
    @BindView(R.id.sp_designation)
    customSpinner spDesignation;
    @BindView(R.id.btn_operational_search)
    MyButton btnOperationalSearch;
    @BindView(R.id.recycler_operational_attend)
    RecyclerView recyclerOperationalAttend;
    @BindView(R.id.tv_date)
    MyTextview tvDate;
    @BindView(R.id.root_operational_attend)
    CoordinatorLayout rootOperationalAttend;
    @BindView(R.id.tv_empty)
    MyTextview tvEmpty;
    @BindView(R.id.et_search_person)
    TextInputEditText etSearchPerson;
    @BindView(R.id.ti_search_person)
    TextInputLayout tiSearchPerson;
    @BindView(R.id.btn_operational_absent)
    MyButton btnOperationalAbsent;

    String SelectedDate = "", formatSelectedDate = "", dateForApiFormat = "";
    long timeStamp;

    ApiInterface apiInterface;
    ProgressView progressView;

    OperationalAttendanceAdapter mAdapter;
    List<ApiResponse.DataBean> operationalAttendanceModels = new ArrayList<>();

    List<ApiResponse.DataBean> searchedResult = new ArrayList<>();
    String searchedName = "";

    List<ApiResponse.DataBean> allCategoryList = new ArrayList<>();
    String designationName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operational_attendance);
        ButterKnife.bind(this);

        initialize();

        getDataForOperationsCategory();

        spDesignation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PrefData.writeStringPref(PrefData.emp_designation, allCategoryList.get(position).getCategoryName());
                designationName = String.valueOf(allCategoryList.get(position).getCategoryName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (Build.VERSION.SDK_INT >=21){
            recyclerOperationalAttend.setNestedScrollingEnabled(false);
        }
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerOperationalAttend.setLayoutManager(manager);
        mAdapter = new OperationalAttendanceAdapter(this, operationalAttendanceModels);
        recyclerOperationalAttend.setAdapter(mAdapter);

        etSearchPerson.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchedName = s.toString().toLowerCase().trim();
                searchedResult.clear();

                for (int i = 0; i < operationalAttendanceModels.size(); i++) {
                    if (operationalAttendanceModels.get(i).getName().toLowerCase().contains(searchedName)) {
                        searchedResult.add(operationalAttendanceModels.get(i));
                    }
                }

                LinearLayoutManager manager = new LinearLayoutManager(OperationalAttendanceActivity.this);
                recyclerOperationalAttend.setLayoutManager(manager);

                if (count == 0) {
                    mAdapter = new OperationalAttendanceAdapter(OperationalAttendanceActivity.this, operationalAttendanceModels);
                } else {
                    mAdapter = new OperationalAttendanceAdapter(OperationalAttendanceActivity.this, searchedResult);
                }
                recyclerOperationalAttend.setAdapter(mAdapter);

                tvEmpty.setVisibility(View.GONE);
                recyclerOperationalAttend.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etSearchPerson.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etSearchPerson.getRight() - etSearchPerson.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        etSearchPerson.setText("");

                        return true;
                    }
                }
                return false;
            }
        });


    }

    private void initialize() {
        setSupportActionBar(toolbar);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Operational Attendance");

        recyclerOperationalAttend.setNestedScrollingEnabled(true);
        spDesignation.setPrompt(getString(R.string.select_category));

        ivBack.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        btnOperationalSearch.setOnClickListener(this);
        btnOperationalAbsent.setOnClickListener(this);

        apiInterface = ApiClient.getClient(OperationalAttendanceActivity.this).create(ApiInterface.class);
        progressView = new ProgressView(OperationalAttendanceActivity.this);

        timeStamp = Long.parseLong(Utils.currentTimeStamp());
        SelectedDate = Utils.selectedDateAndTimeFormat(timeStamp, tvDate);
        try {
            formatSelectedDate = Utils.formatDate(SelectedDate, "dd/MM/yyyy", "MM/dd/yyyy");
            dateForApiFormat = Utils.formatDate(SelectedDate, "dd/MM/yyyy", "dd-MM-yyyy");
            PrefData.writeStringPref(PrefData.date_searched, dateForApiFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void getDataForOperationsCategory() {

        if (CheckNetworkConnection.isConnection1(OperationalAttendanceActivity.this, true)) {

            progressView.showLoader();
            Call<ApiResponse> call = apiInterface.findAllTypeCategory(PrefData.readStringPref(PrefData.PREF_Company_name));

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();

                    try {
                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            allCategoryList.clear();
                            allCategoryList.addAll(response.body().getData());

                            spDesignation.setAdapter(new DesignationDetailsAdapter(OperationalAttendanceActivity.this, allCategoryList));


                        } else {
                            Utils.showSnackBar(rootOperationalAttend, response.body().getMsg(), OperationalAttendanceActivity.this);
                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(OperationalAttendanceActivity.this, "Bad Request!! Please retry.", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(OperationalAttendanceActivity.this, "Network Busy.", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(OperationalAttendanceActivity.this, "Resource Not Found.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OperationalAttendanceActivity.this, "Something went heywire!! please retry.", Toast.LENGTH_SHORT).show();
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
            case R.id.tv_date:
                Utils.showDatePicker(OperationalAttendanceActivity.this, tvDate);
                break;
            case R.id.btn_operational_search:
                try {

                    SelectedDate = tvDate.getText().toString();
                    formatSelectedDate = Utils.formatDate(SelectedDate, "dd/mm/yyyy", "mm/dd/yyyy");
                    dateForApiFormat = Utils.formatDate(SelectedDate, "dd/MM/yyyy", "dd-MM-yyyy");
                    PrefData.writeStringPref(PrefData.date_searched, dateForApiFormat);

                    if (designationName.equalsIgnoreCase("")) {
                        Toast.makeText(OperationalAttendanceActivity.this, R.string.please_select_designation, Toast.LENGTH_SHORT).show();
                    } else {
                        connectApiToGetAttendanceOperational();
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_operational_absent:

                try {

                    SelectedDate = tvDate.getText().toString();
                    formatSelectedDate = Utils.formatDate(SelectedDate, "dd/mm/yyyy", "mm/dd/yyyy");
                    dateForApiFormat = Utils.formatDate(SelectedDate, "dd/MM/yyyy", "dd-MM-yyyy");
                    PrefData.writeStringPref(PrefData.date_searched, dateForApiFormat);

                    if (designationName.equalsIgnoreCase("")) {
                        Toast.makeText(OperationalAttendanceActivity.this, R.string.please_select_designation, Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent=new Intent(OperationalAttendanceActivity.this,OperationalAbsentActivity.class);
                        intent.putExtra("date",formatSelectedDate);
                        intent.putExtra("designationName",designationName);
                        startActivity(intent);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    private void connectApiToGetAttendanceOperational() {
        if (CheckNetworkConnection.isConnection1(OperationalAttendanceActivity.this, true)) {
            progressView.showLoader();
            Call<ApiResponse> call = apiInterface.findAllOperationDetails(
                    PrefData.readStringPref(PrefData.PREF_Company_name),
                    formatSelectedDate,
                    designationName
            );
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();
                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            operationalAttendanceModels.clear();
                            operationalAttendanceModels.addAll(response.body().getData());
                            tvEmpty.setVisibility(View.GONE);
                            recyclerOperationalAttend.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();
                            tiSearchPerson.setVisibility(View.VISIBLE);

                        } else {

                            operationalAttendanceModels.clear();
                            mAdapter.notifyDataSetChanged();
                            tvEmpty.setVisibility(View.VISIBLE);
                            recyclerOperationalAttend.setVisibility(View.GONE);
                            tvEmpty.setText(response.body().getMsg());
                            tiSearchPerson.setVisibility(View.GONE);

                            Utils.showSnackBar(rootOperationalAttend, response.body().getMsg(), OperationalAttendanceActivity.this);
                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(OperationalAttendanceActivity.this, R.string.bad_request, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(OperationalAttendanceActivity.this, R.string.network_busy, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(OperationalAttendanceActivity.this, R.string.not_found, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OperationalAttendanceActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
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

    public class DesignationDetailsAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflter;
        List<ApiResponse.DataBean> employeeDetails;

        public DesignationDetailsAdapter(Context context, List<ApiResponse.DataBean> employeeDetails) {
            this.context = context;
            this.employeeDetails = employeeDetails;
            inflter = (LayoutInflater.from(context));
        }


        @Override
        public int getCount() {
            return employeeDetails.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflter.inflate(R.layout.spinner_layout, null);
            TextView names = convertView.findViewById(R.id.value);
            names.setText(employeeDetails.get(position).getCategoryName());

            return convertView;
        }
    }

}
