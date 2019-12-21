package com.techsalt.tadmin.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.techsalt.tadmin.R;
import com.techsalt.tadmin.customviews.MyButton;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.helper.PrefData;
import com.techsalt.tadmin.helper.Utils;
import com.techsalt.tadmin.model.PostDataModel;
import com.techsalt.tadmin.views.activity.LiveTrackingActivity;
import com.techsalt.tadmin.views.activity.TrackingHistoryActivity;
import com.techsalt.tadmin.webapi.ApiResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OperationalAttendanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<ApiResponse.DataBean> operationalAttendanceModels;
    List<ApiResponse.DataBean.CommunicationsListDataBean> feedbackList = new ArrayList<>();
    List<ApiResponse.DataBean.QrCodeScanVisitListBean> qrCodeScanVisitListBeans=new ArrayList<>();
    public static List<PostDataModel> postDataModels = new ArrayList<>();

    String checkInTime = "", checkOutTime = "", checkinDate = "", checkoutDate = "", dutyHours;

    public OperationalAttendanceAdapter(Context context, List<ApiResponse.DataBean> operationalAttendanceModels) {
        this.context = context;
        this.operationalAttendanceModels = operationalAttendanceModels;
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

                if (operationalAttendanceModels.get(i).getQrCodeScanVisitList().size()>0){

                    qrCodeScanVisitListBeans.clear();
                    qrCodeScanVisitListBeans.addAll(operationalAttendanceModels.get(i).getQrCodeScanVisitList());
                    openDialogToViewQrPetrolHistory();

                }else{
                    Toast.makeText(context, "No Qr Petrol History Found.", Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.btnViewTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.btnViewTracking.getText().toString().equalsIgnoreCase("VIEW TRACKING")){
                    PrefData.writeStringPref(PrefData.emp_Id, String.valueOf(operationalAttendanceModels.get(i).getEmployeeId()));

                    Intent intent = new Intent(context, LiveTrackingActivity.class);
                    intent.putExtra(LiveTrackingActivity.callingActivity, "operational");
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context, "User Is Checked Out", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(context, "No Feedback posted ", Toast.LENGTH_LONG).show();
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

                Intent intent = new Intent(context, TrackingHistoryActivity.class);
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

    private void openDialogToViewQrPetrolHistory() {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_qr_petrol_history);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * 0.9);
        int height = (int) (displaymetrics.heightPixels * 0.9);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        MyButton btnDone = dialog.findViewById(R.id.btn_done);
        MyTextview emptyView = dialog.findViewById(R.id.tv_empty);
        RecyclerView qrPetrolHistoryRecycler = dialog.findViewById(R.id.recycler_qr_petrol_history);
        OperationalQrPetrolHistoryAdapter mAdapter;

        if (qrCodeScanVisitListBeans.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("No Qr Patrol History To Show.");
            qrPetrolHistoryRecycler.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            LinearLayoutManager manager = new LinearLayoutManager(context);
            qrPetrolHistoryRecycler.setLayoutManager(manager);
            mAdapter = new OperationalQrPetrolHistoryAdapter(context, qrCodeScanVisitListBeans);
            qrPetrolHistoryRecycler.setAdapter(mAdapter);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
