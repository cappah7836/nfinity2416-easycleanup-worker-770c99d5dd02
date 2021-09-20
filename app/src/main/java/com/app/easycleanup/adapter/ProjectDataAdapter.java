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
import com.app.easycleanup.models.ProjectListResponse;

import java.util.List;

public class ProjectDataAdapter extends RecyclerView.Adapter<ProjectDataAdapter.MyViewHolder> {

    private Context mContext;
    private List<ProjectListResponse.Record> mDataset;
    OnItemClick onItemClick;

    public ProjectDataAdapter(Context mContext, List<ProjectListResponse.Record> mDataset, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.mDataset = mDataset;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ProjectDataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        v = inflater.inflate(R.layout.item_projects, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectDataAdapter.MyViewHolder holder, final int i) {

        holder.tvBody.setText(mDataset.get(i).getNotes());
        holder.tvProject.setText(mDataset.get(i).getProjectName());
        holder.tvDate.setText(mDataset.get(i).getPlanDate());
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
            tvProject= itemView.findViewById(R.id.tvProject);
            tvBody= itemView.findViewById(R.id.tvBody);
            llMain= itemView.findViewById(R.id.llMain);
        }
    }


}
