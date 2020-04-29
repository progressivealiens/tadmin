package com.techsalt.tadmin.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.techsalt.tadmin.helper.CheckNetworkConnection;
import com.techsalt.tadmin.helper.PrefData;
import com.techsalt.tadmin.helper.ProgressView;
import com.techsalt.tadmin.helper.Utils;
import com.techsalt.tadmin.interfaces.NotifyAdapterBtnClickedOperations;
import com.techsalt.tadmin.webapi.ApiClient;
import com.techsalt.tadmin.webapi.ApiInterface;
import com.techsalt.tadmin.webapi.ApiResponse;
import com.techsalt.tadmin.webapi.SiteVisitRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OperationalQrPatrolHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements NotifyAdapterBtnClickedOperations {

    RecyclerView qrCodeScanRecycler;

    Context context;
    List<ApiResponse.DataBean.QrCodeScanVisitListBean> qrCodeScanVisitListBeans = new ArrayList<>();
    List<ApiResponse.DataBean.QrCodeScanVisitListBean> qrCodeScanVisitToSendMail = new ArrayList<>();
    boolean isFieldEditable;

    ProgressView progressView;
    ApiInterface apiInterface;
    View view;

    public OperationalQrPatrolHistoryAdapter(RecyclerView qrCodeScanRecycler, Context context, List<ApiResponse.DataBean.QrCodeScanVisitListBean> qrCodeScanVisitListBeans, boolean isFieldEditable) {
        this.qrCodeScanRecycler=qrCodeScanRecycler;
        this.context = context;
        this.qrCodeScanVisitListBeans = qrCodeScanVisitListBeans;
        this.isFieldEditable=isFieldEditable;
        progressView=new ProgressView(context);
        apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_qr_petrol, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder holder1 = (MyViewHolder) holder;

        holder1.tvSerialNumber.setText((position + 1)+". ");
        holder1.tvSiteName.setText(qrCodeScanVisitListBeans.get(position).getSiteName());
        holder1.tvQrName.setText(qrCodeScanVisitListBeans.get(position).getQrName());
        holder1.tvQrScanTimestamp.setText(qrCodeScanVisitListBeans.get(position).getTimeStamp());
        Picasso.get().load(Utils.BASE_IMAGE_QR_PETROL_HISTORY + qrCodeScanVisitListBeans.get(position).getScanSelfie()).resize(300, 300).placeholder(R.drawable.progress_animation).into(holder1.ivPostImage);

        holder1.ivPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogToShowImage(Utils.BASE_IMAGE_QR_PETROL_HISTORY + qrCodeScanVisitListBeans.get(position).getScanSelfie());
            }
        });

        if (isFieldEditable){
            holder1.tvSiteName.setFocusable(true);
            holder1.tvQrName.setFocusable(true);
            holder1.tvQrScanTimestamp.setFocusable(true);
        }else{
            holder1.tvSiteName.setFocusable(false);
            holder1.tvQrName.setFocusable(false);
            holder1.tvQrScanTimestamp.setFocusable(false);
        }
    }

    @Override
    public int getItemCount() {
        return qrCodeScanVisitListBeans.size();
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
    public void onButtonClicked(Dialog dialog,String empId,String siteId,String clientId) {

        qrCodeScanVisitToSendMail=new ArrayList<>();

        for (int i=0; i<qrCodeScanVisitListBeans.size();i++){
            RecyclerView.ViewHolder viewHolder=qrCodeScanRecycler.findViewHolderForAdapterPosition(i);
            View views= null;
            if (viewHolder != null) {
                views = viewHolder.itemView;
                TextInputEditText qrType=views.findViewById(R.id.et_qr_type);
                TextInputEditText qrName=views.findViewById(R.id.et_qr_name);
                TextInputEditText qrScanTimestamp=views.findViewById(R.id.et_qr_scan_timestamp);

                qrCodeScanVisitToSendMail.add(new ApiResponse.DataBean.QrCodeScanVisitListBean(qrCodeScanVisitListBeans.get(i).getSiteName(),qrCodeScanVisitListBeans.get(i).getVpmuid(),qrType.getText().toString(),qrName.getText().toString(),qrCodeScanVisitListBeans.get(i).getScanSelfie(),qrScanTimestamp.getText().toString()));
            }

        }

        JSONArray qrScanArray = new JSONArray();
        try{
            for (int i=0; i<qrCodeScanVisitToSendMail.size();i++){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("siteName", ""+qrCodeScanVisitToSendMail.get(i).getSiteName());
                jsonObject.put("vpmuid", ""+qrCodeScanVisitToSendMail.get(i).getVpmuid());
                jsonObject.put("qrType", qrCodeScanVisitToSendMail.get(i).getQrType());
                jsonObject.put("qrName", qrCodeScanVisitToSendMail.get(i).getQrName());
                jsonObject.put("scanSelfie", Utils.BASE_IMAGE_QR_PETROL_HISTORY +qrCodeScanVisitToSendMail.get(i).getScanSelfie());
                jsonObject.put("timeStamp", qrCodeScanVisitToSendMail.get(i).getTimeStamp());
                qrScanArray.put(jsonObject);
            }

            Log.e("surveyArray",qrScanArray.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }

        connectApiToSendEmailToClient(context,dialog,empId,qrScanArray.toString(),siteId,clientId);
    }

    private void connectApiToSendEmailToClient(Context context, Dialog dialog, String empId, String qrPetrolScanData,String siteId,String clientId) {
        if (CheckNetworkConnection.isConnection1(context, true)) {
            progressView.showLoader();

            Call<SiteVisitRes> call = apiInterface.sendPatrolEmailReportToClient(
                    PrefData.readStringPref(PrefData.admin_id),
                    empId,
                    siteId,
                    clientId,
                    qrPetrolScanData
            );

            call.enqueue(new Callback<SiteVisitRes>() {
                @Override
                public void onResponse(Call<SiteVisitRes> call, Response<SiteVisitRes> response) {
                    progressView.hideLoader();

                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        dialog.dismiss();
                        Utils.showToast(context,response.body().getMsg(), Toast.LENGTH_SHORT,context.getResources().getColor(R.color.colorGreen),context.getResources().getColor(R.color.colorWhite));

                    } else {
                        Utils.showToast(context,response.body().getMsg(), Toast.LENGTH_SHORT,context.getResources().getColor(R.color.colorPink),context.getResources().getColor(R.color.colorWhite));
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.et_qr_type)
        TextInputEditText tvSiteName;
        @BindView(R.id.et_qr_name)
        TextInputEditText tvQrName;
        @BindView(R.id.et_qr_scan_timestamp)
        TextInputEditText tvQrScanTimestamp;
        @BindView(R.id.iv_post_image)
        ImageView ivPostImage;
        @BindView(R.id.tv_serial_number)
        MyTextview tvSerialNumber;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
