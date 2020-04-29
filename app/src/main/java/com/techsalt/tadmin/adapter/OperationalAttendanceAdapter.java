package com.techsalt.tadmin.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.techsalt.tadmin.R;
import com.techsalt.tadmin.customviews.MyButton;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.customviews.customSpinner;
import com.techsalt.tadmin.helper.CheckNetworkConnection;
import com.techsalt.tadmin.helper.PrefData;
import com.techsalt.tadmin.helper.ProgressView;
import com.techsalt.tadmin.helper.Utils;
import com.techsalt.tadmin.interfaces.NotifyAdapterBtnClickedOperations;
import com.techsalt.tadmin.model.PostDataModel;
import com.techsalt.tadmin.views.activity.LiveTrackingActivity;
import com.techsalt.tadmin.views.activity.TrackingHistoryOperationsActivity;
import com.techsalt.tadmin.webapi.AllSiteListResponse;
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

public class OperationalAttendanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    ApiInterface apiInterface;
    ProgressView progressView;

    List<AllSiteListResponse.SiteListBean> allSiteList = new ArrayList<>();
    List<ApiResponse.DataBean> operationalAttendanceModels;
    List<ApiResponse.DataBean.CommunicationsListDataBean> feedbackList = new ArrayList<>();
    List<ApiResponse.DataBean.QrCodeScanVisitListBean> qrCodeScanVisitListBeans = new ArrayList<>();
    public static List<PostDataModel> postDataModels = new ArrayList<>();


    String checkInTime = "", checkOutTime = "", checkinDate = "", checkoutDate = "", dutyHours;
    String siteId = "", clientId = "";
    int pos = 0;


    public OperationalAttendanceAdapter(Context context, List<ApiResponse.DataBean> operationalAttendanceModels) {
        this.context = context;
        this.operationalAttendanceModels = operationalAttendanceModels;
        apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
        progressView = new ProgressView(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_operational_attend_layout, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final ViewHolder holder = (ViewHolder) viewHolder;

        holder.tvSerialNumber.setText("# " + String.valueOf(i + 1));
        holder.tvName.setText(operationalAttendanceModels.get(i).getName());
        holder.tvEmpCode.setText(operationalAttendanceModels.get(i).getEmpcode());
        holder.tvLoginVia.setText("Self Mobile");
        holder.ratingBar.setRating(Float.valueOf(operationalAttendanceModels.get(i).getLevel()));
        holder.tvRating.setText("Score : "+operationalAttendanceModels.get(i).getLevel());

        dutyHours = operationalAttendanceModels.get(i).getDutyHours();
        String[] seperatedDuty = dutyHours.split(":");
        holder.tvDutyHours.setText(seperatedDuty[0] + "h : " + seperatedDuty[1] + "m : " + seperatedDuty[2] + "s");
        holder.tvCheckinBattery.setText(operationalAttendanceModels.get(i).getCheckInBatteryLevel() + " %");

        if (operationalAttendanceModels.get(i).getCheckOutBatteryLevel().equalsIgnoreCase("")) {
            holder.tvCheckoutBattery.setText("On Work");
        } else {
            holder.tvCheckoutBattery.setText(operationalAttendanceModels.get(i).getCheckOutBatteryLevel() + " %");
        }

        holder.tvCheckinTime.setText(operationalAttendanceModels.get(i).getCheckInTime());
        holder.tvCheckinAddress.setText(operationalAttendanceModels.get(i).getCheckInAddress());
        holder.tvCheckoutTime.setText(operationalAttendanceModels.get(i).getCheckOutTime());
        holder.tvCheckoutAddress.setText(operationalAttendanceModels.get(i).getCheckOutAddress());
        holder.tvEmpMobile.setText(operationalAttendanceModels.get(i).getMobile());

        Picasso.get().load(Utils.BASE_IMAGE + operationalAttendanceModels.get(i).getStartImageName()).placeholder(R.drawable.progress_animation).resize(300, 300).into(holder.ivOpeartional);

        if (operationalAttendanceModels.get(i).isIsLiveTracking()) {
            holder.btnViewTracking.setBackground(context.getResources().getDrawable(R.drawable.selector_button));
            holder.btnViewTracking.setText("CURRENT LOCATION");
        } else {
            holder.btnViewTracking.setBackground(context.getResources().getDrawable(R.drawable.selector_button_red));
            holder.btnViewTracking.setText("User Checked Out");
        }

        if (operationalAttendanceModels.get(i).isShowHistory()) {
            holder.btnTrackingHistory.setVisibility(View.VISIBLE);
        } else {
            holder.btnTrackingHistory.setVisibility(View.GONE);
        }

        holder.ivOpeartional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogToViewImage(operationalAttendanceModels.get(i).getStartImageName());
            }
        });

        holder.btnQrPetrolHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (operationalAttendanceModels.get(i).getQrCodeScanVisitList().size() > 0) {

                    qrCodeScanVisitListBeans.clear();
                    qrCodeScanVisitListBeans.addAll(operationalAttendanceModels.get(i).getQrCodeScanVisitList());
                    openDialogToViewQrPetrolHistory(context, qrCodeScanVisitListBeans, String.valueOf(operationalAttendanceModels.get(i).getEmployeeId()));

                } else {
                    Utils.showToast(context, context.getResources().getString(R.string.no_qr_patrol_histroty_found), Toast.LENGTH_SHORT, context.getResources().getColor(R.color.colorPink), context.getResources().getColor(R.color.colorWhite));
                }
            }
        });

        holder.btnViewTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.btnViewTracking.getText().toString().equalsIgnoreCase("CURRENT LOCATION")) {
                    PrefData.writeStringPref(PrefData.emp_Id, String.valueOf(operationalAttendanceModels.get(i).getEmployeeId()));

                    Intent intent = new Intent(context, LiveTrackingActivity.class);
                    intent.putExtra(LiveTrackingActivity.callingActivity, "operational");
                    context.startActivity(intent);
                } else {
                    Utils.showToast(context, context.getResources().getString(R.string.user_is_checkedout), Toast.LENGTH_SHORT, context.getResources().getColor(R.color.colorLightGreen), context.getResources().getColor(R.color.colorWhite));
                }
            }
        });

        holder.btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (operationalAttendanceModels.get(i).getCommunicationsListData().size() > 0) {

                    feedbackList.clear();
                    feedbackList.addAll(operationalAttendanceModels.get(i).getCommunicationsListData());
                    openDialogToViewFeedback();

                } else {
                    Utils.showToast(context, context.getResources().getString(R.string.no_feedback_posted), Toast.LENGTH_SHORT, context.getResources().getColor(R.color.colorLightGreen), context.getResources().getColor(R.color.colorWhite));
                }
            }
        });


        holder.btnTrackingHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefData.writeStringPref(PrefData.emp_Id, String.valueOf(operationalAttendanceModels.get(i).getEmployeeId()));
                checkInTime = (operationalAttendanceModels.get(i).getCheckInTime()).substring(10, 19).trim();
                checkinDate = (operationalAttendanceModels.get(i).getCheckInTime()).substring(0, 10).trim();

                postDataModels.clear();
                feedbackList.clear();
                feedbackList.addAll(operationalAttendanceModels.get(i).getCommunicationsListData());

                for (int i = 0; i < feedbackList.size(); i++) {
                    postDataModels.add(new PostDataModel(feedbackList.get(i).getText(), feedbackList.get(i).getImage(), feedbackList.get(i).getPostAddress(), feedbackList.get(i).getDate(), feedbackList.get(i).getPostLat(), feedbackList.get(i).getPostLong()));
                }

                if (holder.tvCheckoutBattery.getText().toString().equalsIgnoreCase("On Work")) {
                    PrefData.writeStringPref(PrefData.checkout_time, "On Work");

                    String currentDate = Utils.selectedDate(Long.valueOf(Utils.currentTimeStamp()));
                    PrefData.writeStringPref(PrefData.checkout_date, currentDate);
                } else {
                    checkOutTime = operationalAttendanceModels.get(i).getCheckOutTime().substring(10, 19).trim();
                    checkoutDate = operationalAttendanceModels.get(i).getCheckOutTime().substring(0, 10).trim();
                    PrefData.writeStringPref(PrefData.checkout_time, checkOutTime);
                    PrefData.writeStringPref(PrefData.checkout_date, checkoutDate);
                }

                PrefData.writeStringPref(PrefData.checkin_time, checkInTime);
                PrefData.writeStringPref(PrefData.checkin_date, checkinDate);
                PrefData.writeStringPref(PrefData.emp_name, operationalAttendanceModels.get(i).getName());
                PrefData.writeStringPref(PrefData.checkin_image, operationalAttendanceModels.get(i).getStartImageName());

                Intent intent = new Intent(context, TrackingHistoryOperationsActivity.class);
                context.startActivity(intent);
            }
        });
    }

    private void openDialogToViewImage(String image) {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_image_layout);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels);
        int height = (int) (displaymetrics.heightPixels);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button btnDone = dialog.findViewById(R.id.btn_done);
        ImageView selfieImage = dialog.findViewById(R.id.iv_selfie);
        Picasso.get().load(Utils.BASE_IMAGE + image).placeholder(R.drawable.progress_animation).into(selfieImage);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openDialogToViewFeedback() {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_feedback_layout);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * 0.9);
        int height = (int) (displaymetrics.heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        MyButton btnDone = dialog.findViewById(R.id.btn_done);
        MyTextview emptyView = dialog.findViewById(R.id.tv_empty);
        RecyclerView feedbackRecycler = dialog.findViewById(R.id.recycler_feedback);
        OperationsFeedbackAdapter mAdapter;

        if (feedbackList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("No Feedbacks to show");
            feedbackRecycler.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            LinearLayoutManager manager = new LinearLayoutManager(context);
            feedbackRecycler.setLayoutManager(manager);
            mAdapter = new OperationsFeedbackAdapter(context, feedbackList);
            feedbackRecycler.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void openDialogToViewQrPetrolHistory(Context con, List<ApiResponse.DataBean.QrCodeScanVisitListBean> qrCodeScanVisitListBeans, String empId) {
        final Dialog dialog = new Dialog(con);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_qr_petrol_history);

        WindowManager wm = (WindowManager) con.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * 0.9);
        int height = (int) (displaymetrics.heightPixels * 0.9);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        MyButton btnDone = dialog.findViewById(R.id.btn_done);
        MyButton btnEditSendMail = dialog.findViewById(R.id.btn_edit_send_mail);
        RecyclerView qrPetrolHistoryRecycler = dialog.findViewById(R.id.recycler_qr_petrol_history);
        OperationalQrPatrolHistoryAdapter mAdapter;

        LinearLayoutManager manager = new LinearLayoutManager(context);
        qrPetrolHistoryRecycler.setLayoutManager(manager);
        mAdapter = new OperationalQrPatrolHistoryAdapter(qrPetrolHistoryRecycler, context, qrCodeScanVisitListBeans, false);
        qrPetrolHistoryRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnEditSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openDialogToViewQrPetrolHistoryEditable(context, qrCodeScanVisitListBeans, empId);
            }
        });

        dialog.show();
    }

    public void openDialogToViewQrPetrolHistoryEditable(Context context, List<ApiResponse.DataBean.QrCodeScanVisitListBean> qrCodeScanVisitListBeans, String empId) {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_qr_petrol_history_editable);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * 0.9);
        int height = (int) (displaymetrics.heightPixels * 0.9);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        MyButton sendMail = dialog.findViewById(R.id.btn_send_mail);
        MyButton btnDone = dialog.findViewById(R.id.btn_done);
        RecyclerView qrPetrolHistoryRecycler = dialog.findViewById(R.id.recycler_qr_petrol_history);
        customSpinner spSiteDetails, spClientDetails;
        spSiteDetails = dialog.findViewById(R.id.sp_site_name);
        spClientDetails = dialog.findViewById(R.id.sp_client_name);
        spSiteDetails.setPrompt(context.getString(R.string.select_site));
        spClientDetails.setPrompt(context.getString(R.string.select_client));
        OperationalQrPatrolHistoryAdapter mAdapter;
        NotifyAdapterBtnClickedOperations buttonListener;

        LinearLayoutManager manager = new LinearLayoutManager(context);
        qrPetrolHistoryRecycler.setLayoutManager(manager);
        mAdapter = new OperationalQrPatrolHistoryAdapter(qrPetrolHistoryRecycler, context, qrCodeScanVisitListBeans, true);
        qrPetrolHistoryRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        buttonListener = mAdapter;

        connectApiToFetchSiteAndClientDetailsSpinner(spSiteDetails);

        spSiteDetails.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                siteId = String.valueOf(allSiteList.get(position).getSuid());
                pos = position;

                if (allSiteList.get(position).getClient() != null && !allSiteList.get(position).getClient().isEmpty()) {
                    spClientDetails.setAdapter(new ClientDetailAdapter(context, allSiteList.get(position).getClient()));
                } else {
                    spClientDetails.setAdapter(new ClientDetailAdapter(context, allSiteList.get(position).getClient()));
                    Utils.showToast(context, "Can't send Mail.No Client is associated with this site", Toast.LENGTH_LONG, context.getResources().getColor(R.color.colorPink), context.getResources().getColor(R.color.colorWhite));
                    clientId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spClientDetails.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clientId = String.valueOf(allSiteList.get(pos).getClient().get(position).getClientId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (siteId.equalsIgnoreCase("")) {
                    Utils.showToast(context, context.getResources().getString(R.string.pls_select_site), Toast.LENGTH_SHORT, context.getResources().getColor(R.color.colorPink), context.getResources().getColor(R.color.colorWhite));
                } else if (clientId.equalsIgnoreCase("")) {
                    Utils.showToast(context, context.getResources().getString(R.string.pls_select_client), Toast.LENGTH_SHORT, context.getResources().getColor(R.color.colorPink), context.getResources().getColor(R.color.colorWhite));
                } else {
                    buttonListener.onButtonClicked(dialog, empId, siteId, clientId);
                    siteId = "";
                    clientId = "";
                }
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void connectApiToFetchSiteAndClientDetailsSpinner(customSpinner spSite) {
        if (CheckNetworkConnection.isConnection1(context, true)) {

            progressView.showLoader();
            Call<AllSiteListResponse> call = apiInterface.getSiteListOfCompany(PrefData.readStringPref(PrefData.PREF_Company_name));

            call.enqueue(new Callback<AllSiteListResponse>() {
                @Override
                public void onResponse(Call<AllSiteListResponse> call, Response<AllSiteListResponse> response) {
                    progressView.hideLoader();

                    try {
                        if (response.body().getStatus().equalsIgnoreCase(context.getString(R.string.success))) {

                            allSiteList.clear();
                            allSiteList.addAll(response.body().getSiteList());

                            spSite.setAdapter(new SiteDetailAdapter(context, allSiteList));
                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Utils.showToast(context, context.getResources().getString(R.string.bad_request), Toast.LENGTH_SHORT, context.getResources().getColor(R.color.colorPink), context.getResources().getColor(R.color.colorWhite));
                        } else if (response.code() == 500) {
                            Utils.showToast(context, context.getResources().getString(R.string.network_busy), Toast.LENGTH_SHORT, context.getResources().getColor(R.color.colorPink), context.getResources().getColor(R.color.colorWhite));
                        } else if (response.code() == 404) {
                            Utils.showToast(context, context.getResources().getString(R.string.not_found), Toast.LENGTH_SHORT, context.getResources().getColor(R.color.colorPink), context.getResources().getColor(R.color.colorWhite));
                        } else {
                            Utils.showToast(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT, context.getResources().getColor(R.color.colorPink), context.getResources().getColor(R.color.colorWhite));
                        }
                        e.printStackTrace();

                    }

                }

                @Override
                public void onFailure(Call<AllSiteListResponse> call, Throwable t) {
                    progressView.hideLoader();
                    t.printStackTrace();
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return operationalAttendanceModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_serial_number)
        MyTextview tvSerialNumber;
        @BindView(R.id.tv_name)
        MyTextview tvName;
        @BindView(R.id.iv_opeartional)
        ImageView ivOpeartional;
        @BindView(R.id.tv_emp_code)
        MyTextview tvEmpCode;
        @BindView(R.id.tv_login_via)
        MyTextview tvLoginVia;
        @BindView(R.id.tv_duty_hours)
        MyTextview tvDutyHours;
        @BindView(R.id.tv_checkin_battery)
        MyTextview tvCheckinBattery;
        @BindView(R.id.tv_checkout_battery)
        MyTextview tvCheckoutBattery;
        @BindView(R.id.tv_checkin_time)
        MyTextview tvCheckinTime;
        @BindView(R.id.tv_checkin_address)
        MyTextview tvCheckinAddress;
        @BindView(R.id.tv_checkout_time)
        MyTextview tvCheckoutTime;
        @BindView(R.id.tv_checkout_address)
        MyTextview tvCheckoutAddress;
        @BindView(R.id.btn_qr_petrol_history)
        MyButton btnQrPetrolHistory;
        @BindView(R.id.btn_view_tracking)
        MyButton btnViewTracking;
        @BindView(R.id.btn_feedback)
        MyButton btnFeedback;
        @BindView(R.id.btn_tracking_history)
        MyButton btnTrackingHistory;
        @BindView(R.id.tv_emp_mobile)
        MyTextview tvEmpMobile;
        @BindView(R.id.rating_bar)
        AppCompatRatingBar ratingBar;
        @BindView(R.id.tv_rating)
        MyTextview tvRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class SiteDetailAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflter;
        List<AllSiteListResponse.SiteListBean> siteListBeans;

        public SiteDetailAdapter(Context context, List<AllSiteListResponse.SiteListBean> allSiteList) {
            this.context = context;
            this.siteListBeans = allSiteList;
            inflter = (LayoutInflater.from(context));
        }


        @Override
        public int getCount() {
            return siteListBeans.size();
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
            convertView = inflter.inflate(R.layout.spinner_layout_small, null);
            TextView names = convertView.findViewById(R.id.value);
            names.setText(siteListBeans.get(position).getSiteName());

            return convertView;
        }
    }

    public class ClientDetailAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflter;
        List<AllSiteListResponse.SiteListBean.ClientBean> clientListBeans;

        public ClientDetailAdapter(Context context, List<AllSiteListResponse.SiteListBean.ClientBean> clientList) {
            this.context = context;
            this.clientListBeans = clientList;
            inflter = (LayoutInflater.from(context));
        }


        @Override
        public int getCount() {
            return clientListBeans.size();
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
            convertView = inflter.inflate(R.layout.spinner_layout_small, null);
            TextView names = convertView.findViewById(R.id.value);
            names.setText(clientListBeans.get(position).getClientName() + " / " + clientListBeans.get(position).getClientEmail());

            return convertView;
        }
    }

}
