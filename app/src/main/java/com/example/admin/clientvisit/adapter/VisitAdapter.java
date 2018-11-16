package com.example.admin.clientvisit.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.clientvisit.R;
import com.example.admin.clientvisit.database.FeedbackEntity;
import com.example.admin.clientvisit.util.Z;

import java.util.List;

public class VisitAdapter extends RecyclerView.Adapter<VisitAdapter.MyViewHolder> {


    private List<FeedbackEntity> list;

    public VisitAdapter(List<FeedbackEntity> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public VisitAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_single_visit_item_view, parent, false);

        return new VisitAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitAdapter.MyViewHolder holder, int position) {

        holder.date_tv.setText(list.get(position).getDate());
        holder.time_tv.setText(list.get(position).getTime());
        holder.reason_of_visit_tv.setText(list.get(position).getReasonBehindVisit());

        int feedLocationDistance = list.get(position).getFeedlocation();
        
        if (feedLocationDistance > 0 && feedLocationDistance <= Z.DISTANCCE_TO_COMPARE) {
            holder.isOnLocation.setImageResource(R.drawable.location_on_blue);
        } else {
            holder.isOnLocation.setImageResource(R.drawable.location_off_blue);
        }

        Log.d("TAG", "onBindViewHolder: onLocation " + feedLocationDistance);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date_tv, time_tv, reason_of_visit_tv;
        ImageView isOnLocation;

        MyViewHolder(View itemView) {
            super(itemView);

            date_tv = itemView.findViewById(R.id.date_tv);
            time_tv = itemView.findViewById(R.id.time_tv);
            reason_of_visit_tv = itemView.findViewById(R.id.reason_of_visit_tv);
            isOnLocation = itemView.findViewById(R.id.isOnLocation);


        }
    }

    public void addItems(List<FeedbackEntity> feedbackEntities) {
        this.list = feedbackEntities;
        notifyDataSetChanged();
    }
}
