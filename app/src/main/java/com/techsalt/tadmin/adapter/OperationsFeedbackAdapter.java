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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;
import com.techsalt.tadmin.R;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.helper.Utils;
import com.techsalt.tadmin.webapi.ApiResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OperationsFeedbackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<ApiResponse.DataBean.CommunicationsListDataBean> feedbackList = new ArrayList<>();

    public OperationsFeedbackAdapter(Context context, List<ApiResponse.DataBean.CommunicationsListDataBean> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_feedback_layout, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder holder = (ViewHolder) viewHolder;

        if (feedbackList.get(i).getText().equalsIgnoreCase("")) {
            holder.tvPostText.setVisibility(View.GONE);
            Picasso.get().load(Utils.BASE_IMAGE_COMMUNICATION + feedbackList.get(i).getImage()).resize(300, 300).placeholder(R.drawable.progress_animation).into(holder.ivPostImage);
        } else if (feedbackList.get(i).getImage().equals("")) {
            holder.ivPostImage.setVisibility(View.VISIBLE);
            Picasso.get().load(R.drawable.no_image_posted).placeholder(R.drawable.progress_animation).into(holder.ivPostImage);
            holder.tvPostText.setText(feedbackList.get(i).getText());
        } else if (!feedbackList.get(i).getImage().equals("") && !feedbackList.get(i).getImage().equals("")) {
            holder.tvPostText.setText(feedbackList.get(i).getText());
            Picasso.get().load(Utils.BASE_IMAGE_COMMUNICATION + feedbackList.get(i).getImage()).resize(300, 300).placeholder(R.drawable.progress_animation).into(holder.ivPostImage);
        }

        holder.tvSerialNumber.setText("# "+(i+1));
        holder.tvPostedFrom.setText(feedbackList.get(i).getPostAddress());
        holder.tvPostedDate.setText(feedbackList.get(i).getDate());

        holder.ivPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedbackList.get(i).getImage().equals("")){
                    Utils.showToast(context,context.getResources().getString(R.string.no_image_posted),Toast.LENGTH_SHORT,context.getResources().getColor(R.color.colorLightGreen),context.getResources().getColor(R.color.colorWhite));
                }else{
                    openDialogToShowImage(Utils.BASE_IMAGE_COMMUNICATION + feedbackList.get(i).getImage());   
                }
            }
        });
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

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_serial_number)
        MyTextview tvSerialNumber;
        @BindView(R.id.et_post_text)
        TextInputEditText tvPostText;
        @BindView(R.id.iv_post_image)
        ImageView ivPostImage;
        @BindView(R.id.et_posted_from)
        TextInputEditText tvPostedFrom;
        @BindView(R.id.et_posted_date)
        TextInputEditText tvPostedDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
