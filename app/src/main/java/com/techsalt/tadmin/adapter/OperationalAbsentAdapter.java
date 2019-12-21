package com.techsalt.tadmin.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techsalt.tadmin.R;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.webapi.ApiResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OperationalAbsentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Context context;
    List<ApiResponse.DataBean> absentList = new ArrayList<>();


    public OperationalAbsentAdapter(Context context, List<ApiResponse.DataBean> absentList) {
        this.context = context;
        this.absentList = absentList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_absent, viewGroup, false);


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        final MyViewHolder viewHolder1 = (MyViewHolder) viewHolder;

        viewHolder1.tvSerialNumber.setText("# " + String.valueOf(i + 1));
        viewHolder1.tvName.setText(absentList.get(i).getName());
        viewHolder1.tvEmpCode.setText(absentList.get(i).getEmpcode());
        viewHolder1.tvMobile.setText(absentList.get(i).getMobile());
        viewHolder1.tvMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String mobileNo = viewHolder1.tvMobile.getText().toString();
                String number=viewHolder1.tvMobile.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+number));
                context.startActivity(callIntent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return absentList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_serial_number)
        MyTextview tvSerialNumber;
        @BindView(R.id.tv_name)
        MyTextview tvName;
        @BindView(R.id.tv_emp_code)
        MyTextview tvEmpCode;
        @BindView(R.id.tv_mobile)
        MyTextview tvMobile;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
