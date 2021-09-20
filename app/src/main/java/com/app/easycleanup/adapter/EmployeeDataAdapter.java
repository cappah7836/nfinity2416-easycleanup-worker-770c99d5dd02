package com.app.easycleanup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.easycleanup.R;
import com.app.easycleanup.interfaces.OnItemClick;
import com.app.easycleanup.models.EmployeeListResponse;

import java.util.List;

public class EmployeeDataAdapter extends RecyclerView.Adapter<EmployeeDataAdapter.MyViewHolder> {

    private Context mContext;
    private List<EmployeeListResponse.Employee> mDataset;
    OnItemClick onItemClick;

    public EmployeeDataAdapter(Context mContext, List<EmployeeListResponse.Employee> mDataset, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.mDataset = mDataset;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public EmployeeDataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        v = inflater.inflate(R.layout.item_employee_list, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeDataAdapter.MyViewHolder holder, final int i) {

        holder.tvName.setText(mDataset.get(i).getFirstname()+" "+mDataset.get(i).getLastname());
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onClick(i);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        private TextView tvName;
        RelativeLayout llMain;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            llMain= itemView.findViewById(R.id.llMain);
        }
    }


}
