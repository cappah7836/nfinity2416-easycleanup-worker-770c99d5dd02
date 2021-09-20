package com.app.easycleanup.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.easycleanup.R;
import com.app.easycleanup.models.SubmitedLeavesResponse;

import java.util.ArrayList;
import java.util.List;


public class LeavesDataAdapter extends RecyclerView.Adapter<LeavesDataAdapter.MyViewHolder> {

    private ArrayList<SubmitedLeavesResponse> submitedLeavesResponses=new ArrayList<>();
    private Context context;

    public LeavesDataAdapter(Context context, ArrayList<SubmitedLeavesResponse> submitedLeavesResponses) {
        this.submitedLeavesResponses = submitedLeavesResponses;
        this.context=context;
    }

    @NonNull
    @Override
    public LeavesDataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.item_leaves, viewGroup, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LeavesDataAdapter.MyViewHolder holder, final int i) {


        if (submitedLeavesResponses.get(i).getStatus().equals("0")) {

            holder.tvStatus.setText(context.getString(R.string.notapproved));
            holder.leavescard.setCardBackgroundColor(context.getResources().getColor(R.color.colorCoolGrey));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorEditStatus));
        } else if (submitedLeavesResponses.get(i).getStatus().equals("1")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorWhite));
            holder.tvStatus.setText(context.getString(R.string.approve));
            holder.leavescard.setCardBackgroundColor(context.getResources().getColor(R.color.green));
        } else {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.red));
            holder.tvStatus.setText(context.getString(R.string.decline));
            holder.leavescard.setCardBackgroundColor(context.getResources().getColor(R.color.red2));
        }
        if (submitedLeavesResponses.get(i).getLeaveType().equalsIgnoreCase("Sick Leave")) {
            holder.tvType.setText(R.string.sick_leave);
            holder.tvType.setTextColor(context.getResources().getColor(R.color.blue));

            holder.tvFromeDate.setVisibility(View.GONE);
            holder.tvToDate.setText(context.getString(R.string.date)+":" + submitedLeavesResponses.get(i).getStartDate());

        } else {
            holder.tvType.setText(R.string.holIDAY_LEAVE);
            holder.tvType.setTextColor(context.getResources().getColor(R.color.green));

            holder.tvFromeDate.setVisibility(View.VISIBLE);
            holder.tvToDate.setText(context.getString(R.string.from_date)+ " " + submitedLeavesResponses.get(i).getStartDate());


        }
       // holder.tvType.setText(mDataset.get(i).getApprovedBy().get(0).getComment());
        holder.tvFromeDate.setText(context.getString(R.string.to_date)+" "+ submitedLeavesResponses.get(i).getEndDate());
        holder.tvDetails.setText(submitedLeavesResponses.get(i).getDetails());


    }

    public void filterList(List<SubmitedLeavesResponse> sl) {
        submitedLeavesResponses = (ArrayList<SubmitedLeavesResponse>) sl;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return submitedLeavesResponses.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        private TextView tvStatus, tvDetails, tvType, tvFromeDate, tvToDate;
        private CardView leavescard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDetails = itemView.findViewById(R.id.tvType);
            tvType = itemView.findViewById(R.id.tvDetails);
            tvFromeDate = itemView.findViewById(R.id.tvFromeDate);
            tvToDate = itemView.findViewById(R.id.tvToDate);
            leavescard = itemView.findViewById(R.id.leavescard);
        }
    }


}
