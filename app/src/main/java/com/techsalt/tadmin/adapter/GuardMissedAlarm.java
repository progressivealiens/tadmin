package com.techsalt.tadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techsalt.tadmin.R;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.webapi.ApiResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuardMissedAlarm extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<ApiResponse.DataBean.MissedAlarmBean> missedAlarmBeans = new ArrayList<>();
    Context context;

    public GuardMissedAlarm(List<ApiResponse.DataBean.MissedAlarmBean> missedAlarmBeans, Context context) {
        this.missedAlarmBeans = missedAlarmBeans;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_missed_alarms, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder holder1 = (MyViewHolder) holder;
        holder1.tvSerialNo.setText("# "+(position+1));
        holder1.tvTimeMissedAlarm.setText("Time :- "+missedAlarmBeans.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return missedAlarmBeans.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_serial_no)
        MyTextview tvSerialNo;
        @BindView(R.id.tv_time_missed_alarm)
        MyTextview tvTimeMissedAlarm;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
