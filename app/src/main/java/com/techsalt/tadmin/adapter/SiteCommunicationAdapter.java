package com.techsalt.tadmin.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.techsalt.tadmin.R;
import com.techsalt.tadmin.customviews.MyButton;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.helper.Utils;
import com.techsalt.tadmin.model.SubChildrenCommunication;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SiteCommunicationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<SubChildrenCommunication> subChildrenCommunications;


    public SiteCommunicationAdapter(Context context, List<SubChildrenCommunication> subChildrenCommunications) {
        this.context = context;
        this.subChildrenCommunications = subChildrenCommunications;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_site_communication, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder holder1 = (MyViewHolder) holder;

        if (subChildrenCommunications.get(position).getText().equalsIgnoreCase("null")) {
            holder1.tvPostedText.setText("NO text posted");
        } else {
            holder1.tvPostedText.setText(subChildrenCommunications.get(position).getText());
        }

        if (subChildrenCommunications.get(position).getImage().equalsIgnoreCase("null")) {
            holder1.ivPostedSiteImage.setVisibility(View.GONE);
            holder1.tvPostedImage.setText("Posted Image :-" +"No Image Posted");
        } else {
            holder1.ivPostedSiteImage.setVisibility(View.VISIBLE);
            holder1.tvPostedImage.setText("Posted Image :-");
            Picasso.get().load(Utils.BASE_IMAGE_COMMUNICATION_FO + subChildrenCommunications.get(position).getImage()).resize(300,300).placeholder(R.drawable.progress_animation).into(holder1.ivPostedSiteImage);
        }


        holder1.ivPostedSiteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogToShowImage(subChildrenCommunications.get(position).getImage());
            }
        });


    }

    private void openDialogToShowImage(String image) {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_image_layout);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels );
        int height = (int) (displaymetrics.heightPixels );
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button btnDone;
        ImageView zoomedImage=dialog.findViewById(R.id.iv_selfie);
        btnDone = dialog.findViewById(R.id.btn_done);
        Picasso.get().load(Utils.BASE_IMAGE_COMMUNICATION_FO + image).placeholder(R.drawable.progress_animation).into(zoomedImage);

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
        return subChildrenCommunications.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_posted_text)
        MyTextview tvPostedText;
        @BindView(R.id.iv_posted_site_image)
        ImageView ivPostedSiteImage;
        @BindView(R.id.tv_posted_image)
        MyTextview tvPostedImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
