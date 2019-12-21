package com.techsalt.tadmin.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.techsalt.tadmin.R;
import com.techsalt.tadmin.customviews.MyButton;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.helper.Utils;
import com.techsalt.tadmin.model.SiteVisitChildren;
import com.techsalt.tadmin.model.SiteVisitParent;
import com.techsalt.tadmin.model.SubChildrenCommunication;
import com.techsalt.tadmin.model.SurveyResponseBean;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition.GROUP;

public class SiteVisitAdapter extends ExpandableRecyclerViewAdapter<SiteVisitAdapter.ParentViewHolder, SiteVisitAdapter.ChildrenViewHolder> {

    Context context;

    public SiteVisitAdapter(List<? extends ExpandableGroup> groups, Context context) {
        super(groups);
        this.context = context;
    }

    @Override
    public ParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_site_visit, parent, false);

        return new ParentViewHolder(view);
    }

    @Override
    public ChildrenViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content_site_visit, parent, false);

        return new ChildrenViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(ChildrenViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {

        ChildrenViewHolder holder1 = holder;

        SiteVisitChildren child = ((SiteVisitParent) group).getItems().get(childIndex);

        if (((SiteVisitParent) group).getItems().get(childIndex).isVisitFound()) {

            holder1.tvSiteName.setVisibility(View.VISIBLE);
            holder1.tvSiteVisitStatus.setVisibility(View.VISIBLE);
            holder1.tvVisitStartTime.setVisibility(View.VISIBLE);
            holder1.tvVisitEndTime.setVisibility(View.VISIBLE);
            holder1.ivSiteStartSelfie.setVisibility(View.VISIBLE);
            holder1.tvVisitStartAddress.setVisibility(View.VISIBLE);
            holder1.tvVisitEndAddress.setVisibility(View.VISIBLE);
            holder1.viewEndTime.setVisibility(View.VISIBLE);
            holder1.viewVisitEndAddress.setVisibility(View.VISIBLE);
            holder1.viewStartTime.setVisibility(View.VISIBLE);

            holder1.tvSiteName.setText(context.getResources().getString(R.string.site_name) + "\n" + child.getSiteName());
            holder1.tvSiteVisitStatus.setText(context.getResources().getString(R.string.status) + "\n" + child.getMsg());
            holder1.tvVisitStartTime.setText(context.getResources().getString(R.string.visit_start_time) + "\n" + child.getVisitStartTime());
            holder1.tvVisitEndTime.setText(context.getResources().getString(R.string.visit_end_time) + "\n" + child.getVisitEndTime());
            Picasso.get().load(Utils.BASE_IMAGE_SITE_START_SELFIE + child.getSelfie()).placeholder(context.getResources().getDrawable(R.drawable.progress_image)).resize(300, 300).into(holder1.ivSiteStartSelfie);
            holder1.tvVisitStartAddress.setText(context.getResources().getString(R.string.visit_strat_address) + child.getVisitStartAddress());
            holder1.tvVisitEndAddress.setText(context.getResources().getString(R.string.visit_end_address) + child.getVisitEndAddress());
            holder1.linRoot.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));

            holder1.ivSiteStartSelfie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialogToShowImage(Utils.BASE_IMAGE_SITE_START_SELFIE + child.getSelfie());
                }
            });

        } else {

            holder1.tvSiteName.setVisibility(View.VISIBLE);
            holder1.tvSiteVisitStatus.setVisibility(View.VISIBLE);
            holder1.ivSiteStartSelfie.setVisibility(View.VISIBLE);
            holder1.tvVisitStartTime.setVisibility(View.GONE);
            holder1.tvVisitEndTime.setVisibility(View.GONE);
            holder1.tvVisitStartAddress.setVisibility(View.GONE);
            holder1.tvVisitEndAddress.setVisibility(View.GONE);
            holder1.viewEndTime.setVisibility(View.GONE);
            holder1.viewVisitEndAddress.setVisibility(View.GONE);
            holder1.viewStartTime.setVisibility(View.GONE);

            holder1.tvSiteName.setText(context.getResources().getString(R.string.site_name) + child.getSiteName());
            holder1.tvSiteVisitStatus.setText(context.getResources().getString(R.string.status) + child.getMsg());
            holder1.linRoot.setBackgroundColor(context.getResources().getColor(R.color.colorMediumRed));
            Picasso.get().load(R.drawable.no_image_available).into(holder1.ivSiteStartSelfie);
        }

        if (((SiteVisitParent) group).getItems().get(childIndex).getSubChildrenCommunications().size() > 0) {

            holder1.btnViewCommunication.setVisibility(View.VISIBLE);
            holder1.btnNoSiteActivity.setVisibility(View.GONE);

            holder1.btnViewCommunication.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialogToShowCommunicationData(context, ((SiteVisitParent) group).getItems().get(childIndex).getSubChildrenCommunications());
                }
            });

        } else {
            holder1.btnViewCommunication.setVisibility(View.GONE);
            holder1.btnNoSiteActivity.setVisibility(View.VISIBLE);
        }

        if (((SiteVisitParent) group).getItems().get(childIndex).getSurveyResponse().size() > 0) {

            holder1.btnViewSurveyReport.setVisibility(View.VISIBLE);
            holder1.btnViewSurveyReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialogToShowSurveyData(context, ((SiteVisitParent) group).getItems().get(childIndex).getSurveyResponse());
                }
            });

        } else {
            holder1.btnViewSurveyReport.setVisibility(View.GONE);
        }
    }

    private void openDialogToShowCommunicationData(Context context, List<SubChildrenCommunication> subChildrenCommunications) {

        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_communication);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * 0.9);
        int height = (int) (displaymetrics.heightPixels * 0.9);
        dialog.getWindow().setLayout(width, height);

        RecyclerView recyclerSiteCommunicationDetails;
        SiteCommunicationAdapter mAdapter;
        MyButton btnDone;

        recyclerSiteCommunicationDetails = dialog.findViewById(R.id.recycler_site_communication_details);
        btnDone = dialog.findViewById(R.id.btn_done);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerSiteCommunicationDetails.setLayoutManager(manager);
        mAdapter = new SiteCommunicationAdapter(context, subChildrenCommunications);
        recyclerSiteCommunicationDetails.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();

    }

    private void openDialogToShowSurveyData(Context context, List<SurveyResponseBean> surveyResponse) {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_survey);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * 0.9);
        int height = (int) (displaymetrics.heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);

        RecyclerView recyclerSiteSurveyReport;
        SurveyReportAdapter mAdapter;
        MyButton btnDone;

        recyclerSiteSurveyReport = dialog.findViewById(R.id.recycler_site_survey_report);
        btnDone = dialog.findViewById(R.id.btn_done);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerSiteSurveyReport.setLayoutManager(manager);
        mAdapter = new SurveyReportAdapter(context, surveyResponse);
        recyclerSiteSurveyReport.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
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
        Picasso.get().load(image).placeholder(R.drawable.progress_image).placeholder(R.drawable.progress_animation).into(zoomedImage);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    @Override
    public void onBindGroupViewHolder(ParentViewHolder holder, int flatPosition, ExpandableGroup group) {

        ParentViewHolder holder1 = holder;

        holder1.tvFoName.setText("FO Name :- " + ((SiteVisitParent) group).getFieldOfficerName());
        holder1.tvMobileNo.setText("Mobile No :- " + ((SiteVisitParent) group).getMobile());
        holder1.tvEmpCode.setText("Emplyee Code :- " + ((SiteVisitParent) group).getEmpcode());

    }

    public class ParentViewHolder extends GroupViewHolder {
        @BindView(R.id.tv_fo_name)
        MyTextview tvFoName;
        @BindView(R.id.tv_mobile_no)
        MyTextview tvMobileNo;
        @BindView(R.id.tv_emp_code)
        MyTextview tvEmpCode;

        public ParentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onGroupExpanded(int positionStart, int itemCount) {
        super.onGroupExpanded(positionStart, itemCount);
        for (int i = getItemCount() - 1; i >= 0; i--) {
            if (i <= getItemCount()) {
                if (i != positionStart - 1) {
                    if (getItemViewType(i) == GROUP && isGroupExpanded(i)) {
                        toggleGroup(i);
                    }
                }
            }
        }
    }

    public class ChildrenViewHolder extends ChildViewHolder {
        @BindView(R.id.tv_site_name)
        MyTextview tvSiteName;
        @BindView(R.id.tv_site_visit_status)
        MyTextview tvSiteVisitStatus;
        @BindView(R.id.tv_visit_start_time)
        MyTextview tvVisitStartTime;
        @BindView(R.id.tv_visit_end_time)
        MyTextview tvVisitEndTime;
        @BindView(R.id.iv_site_start_selfie)
        CircleImageView ivSiteStartSelfie;
        @BindView(R.id.tv_visit_start_address)
        MyTextview tvVisitStartAddress;
        @BindView(R.id.tv_visit_end_address)
        MyTextview tvVisitEndAddress;
        @BindView(R.id.btn_view_communication)
        MyButton btnViewCommunication;
        @BindView(R.id.btn_no_site_activity)
        MyButton btnNoSiteActivity;
        @BindView(R.id.btn_view_survey_report)
        MyButton btnViewSurveyReport;
        @BindView(R.id.lin_root)
        LinearLayout linRoot;
        @BindView(R.id.view_end_time)
        View viewEndTime;
        @BindView(R.id.view_visit_end_address)
        View viewVisitEndAddress;
        @BindView(R.id.view_start_time)
        View viewStartTime;

        public ChildrenViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
