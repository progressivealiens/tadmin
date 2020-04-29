package com.techsalt.tadmin.views.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.techsalt.tadmin.R;
import com.techsalt.tadmin.adapter.SiteVisitAdapter;
import com.techsalt.tadmin.customviews.MyButton;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.helper.CheckNetworkConnection;
import com.techsalt.tadmin.helper.PrefData;
import com.techsalt.tadmin.helper.ProgressView;
import com.techsalt.tadmin.helper.Utils;
import com.techsalt.tadmin.model.SiteVisitChildren;
import com.techsalt.tadmin.model.SiteVisitParent;
import com.techsalt.tadmin.model.SubChildrenCommunication;
import com.techsalt.tadmin.model.SurveyResponseBean;
import com.techsalt.tadmin.webapi.ApiClient;
import com.techsalt.tadmin.webapi.ApiInterface;
import com.techsalt.tadmin.webapi.SiteVisitRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SiteVisitHistory extends AppCompatActivity implements View.OnClickListener {

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
    @BindView(R.id.tv_site_visit_date)
    MyTextview tvSiteVisitDate;
    @BindView(R.id.btn_site_visit_search)
    MyButton btnSiteVisitSearch;
    @BindView(R.id.et_search_person)
    TextInputEditText etSearchPerson;
    @BindView(R.id.ti_search_person)
    TextInputLayout tiSearchPerson;
    @BindView(R.id.recycler_site_visit)
    RecyclerView recyclerSiteVisit;

    String SelectedDate = "", formatSelectedDate = "";
    long timeStamp;

    SiteVisitAdapter mAdapter;
    ApiInterface apiInterface;
    ProgressView progressView;

    List<SiteVisitParent> siteVisitParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_visit_history);
        ButterKnife.bind(this);

        initialization();
    }

    private void initialization() {
        setSupportActionBar(toolbar);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        linToolbar.setVisibility(View.VISIBLE);
        tvTitle.setText("Site Visit Report");

        ivBack.setOnClickListener(this);
        tvSiteVisitDate.setOnClickListener(this);
        btnSiteVisitSearch.setOnClickListener(this);

        timeStamp = Long.parseLong(Utils.currentTimeStamp());
        SelectedDate = Utils.selectedDateAndTimeFormat(timeStamp, tvSiteVisitDate);
        try {
            formatSelectedDate = Utils.formatDate(SelectedDate, "dd/MM/yyyy", "yyyy/MM/dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        apiInterface = ApiClient.getClient(SiteVisitHistory.this).create(ApiInterface.class);
        progressView = new ProgressView(SiteVisitHistory.this);
        siteVisitParent = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_site_visit_date:

                Utils.showDatePicker(SiteVisitHistory.this, tvSiteVisitDate);

                break;
            case R.id.btn_site_visit_search:

                try {

                    SelectedDate = tvSiteVisitDate.getText().toString();
                    formatSelectedDate = Utils.formatDate(SelectedDate, "dd/MM/yyyy", "yyyy/MM/dd");

                    connectApiToGetSiteVisitDetails();

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    private void connectApiToGetSiteVisitDetails() {

        if (CheckNetworkConnection.isConnection1(SiteVisitHistory.this, true)) {
            progressView.showLoader();

            Call<SiteVisitRes> call = apiInterface.siteVisitHistory(
                    PrefData.readStringPref(PrefData.PREF_Company_name),
                    formatSelectedDate);

            call.enqueue(new Callback<SiteVisitRes>() {
                @Override
                public void onResponse(Call<SiteVisitRes> call, Response<SiteVisitRes> response) {
                    progressView.hideLoader();

                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        siteVisitParent.clear();

                        try {
                            JSONObject json = new JSONObject(String.valueOf(new Gson().toJson(response.body())));
                            JSONArray array = json.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                String euid = jsonObject.optString("euid");
                                String fieldOfficerName = jsonObject.optString("fieldOfficerName");
                                String mobile = jsonObject.optString("mobile");
                                String empcode = jsonObject.optString("empcode");
                                JSONArray newArray = jsonObject.getJSONArray("siteVisitAttendance");
                                List<SiteVisitChildren> siteVisitChildren = new ArrayList<>();
                                siteVisitChildren.clear();
                                for (int j = 0; j < newArray.length(); j++) {
                                    JSONObject siteVisitAttendanceJson = newArray.getJSONObject(j) ;
                                    String siteName = "", msg = "", suid = "", visitStartTime = "", visitEndTime = "", visitStartAddress = "", visitEndAddress = "",selfie="";
                                    boolean isVisitFound = false, isVisitCompleted = false,isSurveyFound=false;

                                    siteName = siteVisitAttendanceJson.optString("siteName");
                                    msg = siteVisitAttendanceJson.optString("msg");
                                    suid = siteVisitAttendanceJson.optString("suid");
                                    visitStartTime = siteVisitAttendanceJson.optString("visitStartTime");
                                    visitEndTime = siteVisitAttendanceJson.optString("visitEndTime");
                                    visitStartAddress = siteVisitAttendanceJson.optString("visitStartAddress");
                                    visitEndAddress = siteVisitAttendanceJson.optString("visitEndAddress");
                                    selfie = siteVisitAttendanceJson.optString("selfie");
                                    isVisitFound = siteVisitAttendanceJson.optBoolean("isVisitFound");
                                    isVisitCompleted = siteVisitAttendanceJson.optBoolean("isVisitCompleted");
                                    isSurveyFound = siteVisitAttendanceJson.optBoolean("isSurveyFound");

                                    List<SubChildrenCommunication> subChildrenCommunication = new ArrayList<>();
                                    subChildrenCommunication.clear();
                                    List<SurveyResponseBean> surveyResponseBeans=new ArrayList<>();
                                    surveyResponseBeans.clear();

                                    if (siteVisitAttendanceJson.has("visitCommunication")) {
                                        JSONArray communicationArray = siteVisitAttendanceJson.getJSONArray("visitCommunication");
                                        for (int k = 0; k < communicationArray.length(); k++) {
                                            JSONObject communicationJson = communicationArray.optJSONObject(k);
                                            boolean isCommunicationFound = communicationJson.optBoolean("isCommunicationFound");
                                            String type = communicationJson.optString("type");
                                            String text = communicationJson.optString("text");
                                            String image = communicationJson.optString("image");
                                            Log.e("Communicationtype",type);
                                            Log.e("Communicationtext",text);
                                            Log.e("Communicationimage",image);
                                            subChildrenCommunication.add(new SubChildrenCommunication(isCommunicationFound, type, text, image));
                                        }
                                    }
                                    if (siteVisitAttendanceJson.has("survey")) {
                                        JSONArray communicationArray = siteVisitAttendanceJson.getJSONArray("survey");
                                        for (int k = 0; k < communicationArray.length(); k++) {
                                            JSONObject communicationJson = communicationArray.optJSONObject(k);
                                            String type = communicationJson.optString("question");
                                            String text = communicationJson.optString("response");
                                            Log.e("surveyQuestion",type);
                                            Log.e("surveyAnswer",text);
                                            surveyResponseBeans.add(new SurveyResponseBean(type, text));
                                        }
                                    }
                                    siteVisitChildren.add(new SiteVisitChildren(siteName, msg, suid, visitStartTime, visitEndTime, visitStartAddress, visitEndAddress, selfie,isVisitFound, isVisitCompleted, isSurveyFound, subChildrenCommunication,surveyResponseBeans));
                                }
                                siteVisitParent.add(new SiteVisitParent("" + i, siteVisitChildren, euid, fieldOfficerName, mobile, empcode));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //tiSearchPerson.setVisibility(View.VISIBLE);
                        recyclerSiteVisit.setVisibility(View.VISIBLE);

                        mAdapter = new SiteVisitAdapter(siteVisitParent, SiteVisitHistory.this);
                        recyclerSiteVisit.setLayoutManager(new LinearLayoutManager(SiteVisitHistory.this));
                        recyclerSiteVisit.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();

                    } else {
                        Utils.showToast(SiteVisitHistory.this,response.body().getMsg(),Toast.LENGTH_SHORT,getResources().getColor(R.color.colorPink),getResources().getColor(R.color.colorWhite));
                    }
                }

                @Override
                public void onFailure(Call<SiteVisitRes> call, Throwable t) {
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
}
