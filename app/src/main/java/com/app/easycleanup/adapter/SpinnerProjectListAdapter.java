package com.app.easycleanup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.app.easycleanup.R;
import com.app.easycleanup.models.SupervisorProjectResponse;
import com.app.easycleanup.utils.ApplicationUtils;

import java.util.ArrayList;

public class SpinnerProjectListAdapter extends BaseAdapter {



        ArrayList<SupervisorProjectResponse.Project> list = new ArrayList<>();
        private LayoutInflater layoutInflater = null;
        private Context context;

        public SpinnerProjectListAdapter(Context context, ArrayList<SupervisorProjectResponse.Project> list) {

            this.list = list;
            this.context = context;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

   

    public class ViewHolder {
            TextView tvName;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = new ViewHolder();
            View view = convertView;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.item_spinner, null);
                viewHolder.tvName = (TextView) view.findViewById(R.id.tvItem);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            SupervisorProjectResponse.Project data = list.get(position);
            if (ApplicationUtils.isSet(data.getName())) {
                viewHolder.tvName.setText(data.getName());
            }
            return view;
        }
    }
