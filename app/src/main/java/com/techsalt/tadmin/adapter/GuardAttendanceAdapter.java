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
import com.techsalt.tadmin.views.activity.LiveTrackingActivity;
import com.techsalt.tadmin.views.activity.LoginActivity;
import com.techsalt.tadmin.views.activity.TrackingHistoryGuardActivity;
import com.techsalt.tadmin.webapi.ApiResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuardAttendanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<ApiResponse.DataBean> guardAttendanceModels = new ArrayList<>();
    List<ApiResponse.DataBean.RoutePatrolBean> patrollingBeans = new ArrayList<>();
    List<ApiResponse.DataBean.MissedAlarmBean> missedAlarmBeans = new ArrayList<>();

    String checkInTime = "", checkOutTime = "", checkinDate = "", checkoutDate = "", dutyHours;

    public GuardAttendanceAdapter(Context context, List<ApiResponse.DataBean> guardAttendanceModels) {
        this.context = context;
        this.guardAttendanceModels = guardAttendanceModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_guard_attend_layout, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ViewHolder holder = (ViewHolder) viewHolder;

        holder.tvSerialNumber.setText("# " + String.valueOf(i + 1));
        holder.tvName.setText(guardAttendanceModels.get(i).getName());
        holder.tvGuardCode.setText(guardAttendanceModels.get(i).getEmpcode());
        holder.tvLoginVia.setText(guardAttendanceModels.get(i).getLoginVia());
        holder.tvDutyHours.setText(guardAttendanceModels.get(i).getDutyHours());
        holder.tvCheckinBattery.setText(guardAttendanceModels.get(i).getCheckInBatteryLevel() + " %");
        if (guardAttendanceModels.get(i).getCheckOutBatteryLevel().equalsIgnoreCase("")) {
            holder.tvCheckoutBattery.setText("On Work");
        } else {
            holder.tvCheckoutBattery.setText(guardAttendanceModels.get(i).getCheckOutBatteryLevel() + " %");
        }

        holder.tvCheckinTime.setText(guardAttendanceModels.get(i).getCheckInTime());
        holder.tvCheckinAddress.setText(guardAttendanceModels.get(i).getCheckInAddress());
        holder.tvCheckoutTime.setText(guardAttendanceModels.get(i).getCheckOutTime());
        holder.tvCheckoutAddress.setText(guardAttendanceModels.get(i).getCheckOutAddress());

        Picasso.get().load(Utils.BASE_IMAGE_GUARD + guardAttendanceModels.get(i).getStartImageName()).resize(300, 300).placeholder(R.drawable.progress_animation).into(holder.ivGuards);

        if (guardAttendanceModels.get(i).getLoginVia().equalsIgnoreCase("E-Register")) {
            if (guardAttendanceModels.get(i).getCheckOutBatteryLevel().equalsIgnoreCase("")) {
                holder.btnViewTracking.setText("No Tracking Available");
            } else {
                holder.btnViewTracking.setText(guardAttendanceModels.get(i).getTrackingMessage());
            }
        } else {
            if (guardAttendanceModels.get(i).isIsLiveTracking()) {
                holder.btnViewTracking.setBackground(context.getResources().getDrawable(R.drawable.selector_button));
            } else {
                holder.btnViewTracking.setBackground(context.getResources().getDrawable(R.drawable.selector_button_red));
                holder.btnViewTracking.setText(guardAttendanceModels.get(i).getTrackingMessage());
            }
        }

        holder.ivGuards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogToViewImage(guardAttendanceModels.get(i).getStartImageName());
            }
        });

        holder.btnViewTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.btnViewTracking.getText().toString().equalsIgnoreCase("CURRENT LOCATION")) {
                    PrefData.writeStringPref(PrefData.emp_Id, String.valueOf(guardAttendanceModels.get(i).getEmployeeId()));

                    Intent intent = new Intent(context, LiveTrackingActivity.class);
                    intent.putExtra(LiveTrackingActivity.callingActivity, "guard");
                    context.startActivity(intent);
                } else {
                    Utils.showToast(context,context.getResources().getString(R.string.guard_checked_out),Toast.LENGTH_SHORT,context.getResources().getColor(R.color.colorPink),context.getResources().getColor(R.color.colorWhite));
                }
            }
        });

        /*holder.btnTrackingHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PrefData.writeStringPref(PrefData.emp_Id, String.valueOf(guardAttendanceModels.get(i).getEmployeeId()));
                checkInTime = (guardAttendanceModels.get(i).getCheckInTime()).substring(10, 19).trim();
                checkinDate = (guardAttendanceModels.get(i).getCheckInTime()).substring(0, 10).trim();

                if (holder.tvCheckoutBattery.getText().toString().equalsIgnoreCase("On Work")) {
                    PrefData.writeStringPref(PrefData.checkout_time, "On Work");

                    String currentDate = Utils.selectedDate(Long.valueOf(Utils.currentTimeStamp()));
                    PrefData.writeStringPref(PrefData.checkout_date, currentDate);
                } else {
                    checkOutTime = guardAttendanceModels.get(i).getCheckOutTime().substring(10, 19).trim();
                    checkoutDate = guardAttendanceModels.get(i).getCheckOutTime().substring(0, 10).trim();
                    PrefData.writeStringPref(PrefData.checkout_time, checkOutTime);
                    PrefData.writeStringPref(PrefData.checkout_date, checkoutDate);
                }

                PrefData.writeStringPref(PrefData.checkin_time, checkInTime);
                PrefData.writeStringPref(PrefData.checkin_date, checkinDate);
                PrefData.writeStringPref(PrefData.emp_name, guardAttendanceModels.get(i).getName());
                PrefData.writeStringPref(PrefData.checkin_image, guardAttendanceModels.get(i).getStartImageName());

                Intent intent = new Intent(context, TrackingHistoryGuardActivity.class);
                context.startActivity(intent);
            }
        });*/

        holder.btnPatrollingHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guardAttendanceModels.get(i).getRoutePatrol().size() > 0) {

                    patrollingBeans.clear();
                    patrollingBeans.addAll(guardAttendanceModels.get(i).getRoutePatrol());
                    openDialogToViewPatrollingHistory();

                } else {
                    Utils.showToast(context,context.getResources().getString(R.string.no_qr_patrol_histroty_found),Toast.LENGTH_SHORT,context.getResources().getColor(R.color.colorPink),context.getResources().getColor(R.color.colorWhite));
                }
            }
        });

        holder.btnMissedAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (guardAttendanceModels.get(i).getMissedAlarm().size()>0){
                    missedAlarmBeans.clear();
                    missedAlarmBeans.addAll(guardAttendanceModels.get(i).getMissedAlarm());

                    openDialogToViewMissedAlarm();

                }else{
                    Utils.showToast(context,context.getResources().getString(R.string.no_missed_alarms),Toast.LENGTH_SHORT,context.getResources().getColor(R.color.colorLightGreen),context.getResources().getColor(R.color.colorWhite));
                }

            }
        });

    }

    private void openDialogToViewPatrollingHistory() {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_guard_patrolling_history);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * 0.9);
        int height = (int) (displaymetrics.heightPixels * 0.9);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        MyButton btnDone = dialog.findViewById(R.id.btn_done);
        MyTextview emptyView = dialog.findViewById(R.id.tv_empty);
        RecyclerView guardPatrolHistoryRecycler = dialog.findViewById(R.id.recycler_patrolling_history);
        GuardPatrollingHistory mAdapter;

        if (patrollingBeans.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("No Patrolling History To Show.");
            guardPatrolHistoryRecycler.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            LinearLayoutManager manager = new LinearLayoutManager(context);
            guardPatrolHistoryRecycler.setLayoutManager(manager);
            mAdapter = new GuardPatrollingHistory(context, patrollingBeans);
            guardPatrolHistoryRecycler.setAdapter(mAdapter);
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


    private void openDialogToViewMissedAlarm() {

        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_guard_missed_alarm);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * 0.9);
        int height = (int) (displaymetrics.heightPixels * 0.9);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        MyButton btnDone = dialog.findViewById(R.id.btn_done);
        MyTextview emptyView = dialog.findViewById(R.id.tv_empty);
        RecyclerView guardMissedAlarmRecycler = dialog.findViewById(R.id.recycler_guard_missed_alarm);
        GuardMissedAlarm mAdapter;

        if (missedAlarmBeans.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("No Missed Alarms To Show.");
            guardMissedAlarmRecycler.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            LinearLayoutManager manager = new LinearLayoutManager(context);
            guardMissedAlarmRecycler.setLayoutManager(manager);
            mAdapter = new GuardMissedAlarm(missedAlarmBeans,context);
            guardMissedAlarmRecycler.setAdapter(mAdapter);
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


    private void openDialogToViewImage(String image) {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_image_layout);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * 0.9);
        int height = (int) (displaymetrics.heightPixels * 0.9);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button btnDone = dialog.findViewById(R.id.btn_done);
        ImageView selfieImage = dialog.findViewById(R.id.iv_selfie);
        Picasso.get().load(Utils.BASE_IMAGE_GUARD + image).placeholder(R.drawable.progress_animation).into(selfieImage);
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
        return guardAttendanceModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        MyTextview tvName;
        @BindView(R.id.iv_guards)
        ImageView ivGuards;
        @BindView(R.id.tv_serial_number)
        MyTextview tvSerialNumber;
        @BindView(R.id.tv_guard_code)
        MyTextview tvGuardCode;
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
        @BindView(R.id.btn_view_tracking)
        MyButton btnViewTracking;
        @BindView(R.id.btn_patrolling_history)
        MyButton btnPatrollingHistory;
        /*@BindView(R.id.btn_tracking_history)
        MyButton btnTrackingHistory;*/
        @BindView(R.id.btn_missed_alarm)
        MyButton btnMissedAlarm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
