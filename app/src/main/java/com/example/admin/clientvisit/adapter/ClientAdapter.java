package com.example.admin.clientvisit.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.clientvisit.R;
import com.example.admin.clientvisit.model.ClientData;

import java.util.List;

import static com.example.admin.clientvisit.database.DbUtil.buildOwnerNameStringFromList;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.MyViewHolder> {

    private Activity activity;
    private List<ClientData> list;

    public ClientAdapter(List<ClientData> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_single_client_item_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder,  int position) {

        holder.business_name.setText(list.get(position).getBusinessName());
        holder.address_area.setText(list.get(position).getAddressArea() + " " + list.get(position).getAddressPincode());
        holder.client_name.setText(buildOwnerNameStringFromList(list.get(position)));

//      holder.image.setImageBitmap(list.get(position).getBitmap());

        final int pos=position;
        final Bitmap[] bitmap = new Bitmap[1];

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    byte[] imageByte = list.get(pos).getImage();
                    Log.d("TAG", "run: size " + imageByte.length / 1000);
                    if (imageByte != null) {
                        bitmap[0] = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.image.setImageBitmap(bitmap[0]);
                            }
                        });
                    }

                } catch (Exception e) {
                    Log.d("TAG", "onBindViewHolder: " + e.getMessage());
                }
            }
        }).start();


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItems(List<ClientData> clientData) {
        this.list = clientData;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView business_name, address_area, client_name;

         MyViewHolder(View itemView) {
            super(itemView);

            business_name = itemView.findViewById(R.id.business_name);
            address_area = itemView.findViewById(R.id.address_area);
            client_name = itemView.findViewById(R.id.client_name);
            image = itemView.findViewById(R.id.image);

        }
    }
}
