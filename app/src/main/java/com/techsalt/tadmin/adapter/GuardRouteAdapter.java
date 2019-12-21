package com.techsalt.tadmin.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techsalt.tadmin.R;
import com.techsalt.tadmin.customviews.MyButton;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.helper.PrefData;
import com.techsalt.tadmin.views.activity.GuardAttendanceActivity;
import com.techsalt.tadmin.views.activity.GuardRouteWiseAttendActivity;
import com.techsalt.tadmin.webapi.ApiResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuardRouteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<ApiResponse.DataBean> guardRouteModels = new ArrayList<>();

    public GuardRouteAdapter(Context context, List<ApiResponse.DataBean> guardRouteModels) {
        this.context = context;
        this.guardRouteModels = guardRouteModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_guard_route_layout, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ViewHolder holder = (ViewHolder) viewHolder;

        holder.tvSiteName.setText("Site Name :- " + guardRouteModels.get(i).getSiteName());
        holder.tvRouteName.setText("Route Name :- " + guardRouteModels.get(i).getRouteName());

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefData.writeStringPref(PrefData.route_Id, String.valueOf(guardRouteModels.get(i).getRouteId()));
                Intent intent = new Intent(context, GuardRouteWiseAttendActivity.class);
                intent.putExtra(GuardAttendanceActivity.routeId, String.valueOf(guardRouteModels.get(i).getRouteId()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return guardRouteModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_site_name)
        MyTextview tvSiteName;
        @BindView(R.id.tv_route_name)
        MyTextview tvRouteName;
        @BindView(R.id.btn_view)
        MyButton btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
