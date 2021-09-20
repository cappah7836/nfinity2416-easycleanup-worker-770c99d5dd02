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
import com.app.easycleanup.models.SupervisorProjectPlanResponse;

import java.util.List;

public class SupervisorProjectDataAdapter extends RecyclerView.Adapter<SupervisorProjectDataAdapter.MyViewHolder> {

    private Context mContext;
    private List<SupervisorProjectPlanResponse.Planning> mDataset;
    OnItemClick onItemClick;

    public SupervisorProjectDataAdapter(Context mContext, List<SupervisorProjectPlanResponse.Planning> mDataset, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.mDataset = mDataset;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public SupervisorProjectDataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        v = inflater.inflate(R.layout.item_supervisor_projects, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SupervisorProjectDataAdapter.MyViewHolder holder, final int i) {

        holder.tvDate.setText(mDataset.get(i).getDate());
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


        private TextView tvDate;
        private TextView tvBody;
        private TextView tvProject;
        RelativeLayout llMain;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tvDate);
     ;
            llMain= itemView.findViewById(R.id.llMain);
        }
    }


}
