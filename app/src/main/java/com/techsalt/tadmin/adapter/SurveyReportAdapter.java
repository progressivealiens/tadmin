package com.techsalt.tadmin.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.techsalt.tadmin.R;
import com.techsalt.tadmin.helper.CheckNetworkConnection;
import com.techsalt.tadmin.helper.PrefData;
import com.techsalt.tadmin.helper.ProgressView;
import com.techsalt.tadmin.helper.Utils;
import com.techsalt.tadmin.interfaces.ToNotifyAdapterButtonClicked;
import com.techsalt.tadmin.model.SurveyResponseBean;
import com.techsalt.tadmin.views.activity.SiteVisitHistory;
import com.techsalt.tadmin.webapi.ApiClient;
import com.techsalt.tadmin.webapi.ApiInterface;
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

public class SurveyReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ToNotifyAdapterButtonClicked {

    RecyclerView surveyRecycler;
    Context context;
    List<SurveyResponseBean> surveyResponseBeans;
    List<SurveyResponseBean> surveyResponseBeansTOSendMail;

    boolean isFieldEditable;

    ProgressView progressView;
    ApiInterface apiInterface;
    View view;

    public SurveyReportAdapter(RecyclerView surveyRecycler,Context context, List<SurveyResponseBean> surveyResponseBeans,boolean isFieldEditable) {
        this.surveyRecycler=surveyRecycler;
        this.context = context;
        this.surveyResponseBeans = surveyResponseBeans;
        this.isFieldEditable=isFieldEditable;
        progressView=new ProgressView(context);
        apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_survey_report_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder holder1 = (MyViewHolder) holder;

        holder1.tvSerialNumber.setText("# "+(position+1));
        holder1.tvSurveyQuestion.setText(surveyResponseBeans.get(position).getQuestion());
        holder1.tvSurveyAnswer.setText(surveyResponseBeans.get(position).getResponse());

        if (isFieldEditable){
            holder1.tvSurveyQuestion.setFocusable(true);
            holder1.tvSurveyAnswer.setFocusable(true);
        }else{
            holder1.tvSurveyQuestion.setFocusable(false);
            holder1.tvSurveyAnswer.setFocusable(false);
        }
    }




    @Override
    public int getItemCount() {
        return surveyResponseBeans.size();
    }

    @Override
    public void onButtonClicked(Dialog dialog, String suid, String empId) {
        surveyResponseBeansTOSendMail=new ArrayList<>();

        for (int i=0; i<surveyResponseBeans.size();i++){
            if (i == surveyResponseBeans.size()-1){
                MyViewHolder myViewHolder = new MyViewHolder(view);
                TextInputEditText tvSurveyQue=myViewHolder.itemView.findViewById(R.id.et_survey_question);
                TextInputEditText tvSurveyAns=myViewHolder.itemView.findViewById(R.id.et_survey_answer);
                surveyResponseBeansTOSendMail.add(new SurveyResponseBean(tvSurveyQue.getText().toString(),tvSurveyAns.getText().toString()));
            }else{
                RecyclerView.ViewHolder viewHolder=surveyRecycler.findViewHolderForAdapterPosition(i);
                View views= null;
                if (viewHolder != null) {
                    views = viewHolder.itemView;
                    TextInputEditText tvSurveyQuestion=views.findViewById(R.id.et_survey_question);
                    TextInputEditText tvSurveyAnswer=views.findViewById(R.id.et_survey_answer);
                    surveyResponseBeansTOSendMail.add(new SurveyResponseBean(tvSurveyQuestion.getText().toString(),tvSurveyAnswer.getText().toString()));
                }
            }
        }

        JSONArray QuestionAnswer = new JSONArray();
        try{
            for (int i=0; i<surveyResponseBeansTOSendMail.size();i++){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("question", surveyResponseBeansTOSendMail.get(i).getQuestion());
                jsonObject.put("answer", surveyResponseBeansTOSendMail.get(i).getResponse());
                QuestionAnswer.put(jsonObject);
            }
            connectApiToSendEmailToClient(context,dialog,suid,empId,QuestionAnswer.toString());

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void connectApiToSendEmailToClient(Context context, Dialog surveyDialog,String suid,String empid,String survey) {
        if (CheckNetworkConnection.isConnection1(context, true)) {
            progressView.showLoader();

            Call<SiteVisitRes> call = apiInterface.sendDarEmailReportToClient(
                    PrefData.readStringPref(PrefData.admin_id),
                    suid,
                    empid,
                    survey
            );

            call.enqueue(new Callback<SiteVisitRes>() {
                @Override
                public void onResponse(Call<SiteVisitRes> call, Response<SiteVisitRes> response) {
                    progressView.hideLoader();

                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        surveyDialog.dismiss();
                        Utils.showToast(context,response.body().getMsg(),Toast.LENGTH_SHORT,context.getResources().getColor(R.color.colorGreen),context.getResources().getColor(R.color.colorWhite));

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
        @BindView(R.id.et_survey_question)
        TextInputEditText tvSurveyQuestion;
        @BindView(R.id.et_survey_answer)
        TextInputEditText tvSurveyAnswer;
        @BindView(R.id.tv_serial_number)
        TextView tvSerialNumber;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
