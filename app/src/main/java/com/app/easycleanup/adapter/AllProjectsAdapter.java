package com.app.easycleanup.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.easycleanup.R;
import com.app.easycleanup.interfaces.OnOpenJobDetails;
import com.app.easycleanup.models.AllProjectListResponse;


import java.util.ArrayList;
import java.util.List;

public class AllProjectsAdapter extends RecyclerView.Adapter<AllProjectsAdapter.MyViewHolder> {
    private ArrayList<AllProjectListResponse> allProjectListResponses=new ArrayList<>();
    private Context context;
    OnOpenJobDetails onOpenJobDetails;

    public AllProjectsAdapter(Context context, ArrayList<AllProjectListResponse> allProjectListResponses,OnOpenJobDetails onOpenJobDetails) {
        this.allProjectListResponses = allProjectListResponses;
        this.context=context;
        this.onOpenJobDetails = onOpenJobDetails;
    }

    @NonNull
    @Override
    public AllProjectsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.item_weekly, viewGroup, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AllProjectsAdapter.MyViewHolder holder, final int i) {
        holder.tvProject.setText(allProjectListResponses.get(i).getProject_name());
        holder.tvTask.setText(allProjectListResponses.get(i).getWeekcardId());
        holder.btDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpenJobDetails.onOpen(i);
            }
        });


    }

    public void filterProjectList(List<AllProjectListResponse> sl) {
        allProjectListResponses = (ArrayList<AllProjectListResponse>) sl;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return allProjectListResponses.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        private TextView tvProject, tvTask;
        private Button btDetails;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProject = itemView.findViewById(R.id.tvProject);
            tvTask = itemView.findViewById(R.id.tvTask);
            btDetails = itemView.findViewById(R.id.btDetails);

        }
    }


}
