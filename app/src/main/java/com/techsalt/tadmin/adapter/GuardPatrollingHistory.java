package com.techsalt.tadmin.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.techsalt.tadmin.R;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.helper.Utils;
import com.techsalt.tadmin.webapi.ApiResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuardPatrollingHistory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<ApiResponse.DataBean.RoutePatrolBean> patrollingBeans = new ArrayList<>();

    public GuardPatrollingHistory(Context context, List<ApiResponse.DataBean.RoutePatrolBean> patrollingBeans) {
        this.context = context;
        this.patrollingBeans = patrollingBeans;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_guard_patrolling_history, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder holder1 = (MyViewHolder) holder;

        holder1.tvCheckpointName.setText("Checkpoint Name :- "+patrollingBeans.get(position).getCheckPointName());
        holder1.tvRoundNo.setText("Round No :- "+patrollingBeans.get(position).getRound()+"");
        holder1.tvBatteryStatus.setText("Battery Status :- "+patrollingBeans.get(position).getBatteryStatus());
        holder1.tvScannedAt.setText("Scanned At :- "+patrollingBeans.get(position).getCreated_at());

        Picasso.get().load(Utils.BASE_IMAGE_GUARD_PATROLLING_HISTORY + patrollingBeans.get(position).getGuardScanImage()).resize(300,300).placeholder(R.drawable.progress_animation).into(holder1.ivPostImage);

        holder1.ivPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogToShowImage(Utils.BASE_IMAGE_GUARD_PATROLLING_HISTORY + patrollingBeans.get(position).getGuardScanImage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return patrollingBeans.size();
    }

    private void openDialogToShowImage(String image) {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_image_layout);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels);
        int height = (int) (displaymetrics.heightPixels);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button btnDone;
        ImageView zoomedImage = dialog.findViewById(R.id.iv_selfie);
        btnDone = dialog.findViewById(R.id.btn_done);
        Picasso.get().load(image).placeholder(R.drawable.progress_animation).into(zoomedImage);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_checkpoint_name)
        MyTextview tvCheckpointName;
        @BindView(R.id.tv_round_no)
        MyTextview tvRoundNo;
        @BindView(R.id.tv_battery_status)
        MyTextview tvBatteryStatus;
        @BindView(R.id.tv_scanned_at)
        MyTextview tvScannedAt;
        @BindView(R.id.iv_post_image)
        ImageView ivPostImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
