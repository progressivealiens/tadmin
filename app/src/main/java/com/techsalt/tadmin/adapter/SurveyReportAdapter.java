package com.techsalt.tadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techsalt.tadmin.R;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.model.SurveyResponseBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SurveyReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<SurveyResponseBean> surveyResponseBeans;

    public SurveyReportAdapter(Context context, List<SurveyResponseBean> surveyResponseBeans) {
        this.context = context;
        this.surveyResponseBeans = surveyResponseBeans;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_survey_report_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder holder1 = (MyViewHolder) holder;

        holder1.tvSurveyQuestion.setText("Question :- "+surveyResponseBeans.get(position).getQuestion());
        holder1.tvSurveyAnswer.setText("Answer :- "+surveyResponseBeans.get(position).getResponse());

    }

    @Override
    public int getItemCount() {
        return surveyResponseBeans.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_survey_question)
        MyTextview tvSurveyQuestion;
        @BindView(R.id.tv_survey_answer)
        MyTextview tvSurveyAnswer;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
