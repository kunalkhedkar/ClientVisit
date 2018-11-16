package com.example.admin.clientvisit.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.clientvisit.R;
import com.example.admin.clientvisit.model.SelectOwnerData;

import java.util.List;

public class SelectOwnerAdapter extends RecyclerView.Adapter<SelectOwnerAdapter.MyViewHolder> {

    private List<SelectOwnerData> list;

    public SelectOwnerAdapter(List<SelectOwnerData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public SelectOwnerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_single_owner_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectOwnerAdapter.MyViewHolder holder, int position) {

        Log.d("TAG", "onBindViewHolder: " + list.get(position));

        holder.ownerName.setText(list.get(position).getOwnerName());
        holder.businessNames.setText(list.get(position).getBusinessName());
        if (list.get(position).getStatus() == true) {
            holder.checkbox.setChecked(true);
        } else
            holder.checkbox.setChecked(false);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItems(List<SelectOwnerData> selectOwnerData) {
        list.clear();
        list.addAll(selectOwnerData);
        notifyDataSetChanged();
    }

     class MyViewHolder extends RecyclerView.ViewHolder {

         TextView ownerName, businessNames;
         AppCompatCheckBox checkbox;

        MyViewHolder(View itemView) {
            super(itemView);

            ownerName = itemView.findViewById(R.id.owner_name_tv);
            businessNames = itemView.findViewById(R.id.business_names_tv);
            checkbox = itemView.findViewById(R.id.checkbox);

        }
    }

}
