package com.example.admin.clientvisit;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.admin.clientvisit.adapter.RecyclerTouchListener;
import com.example.admin.clientvisit.adapter.SelectOwnerAdapter;
import com.example.admin.clientvisit.database.OwnerEntity;
import com.example.admin.clientvisit.model.SelectOwnerData;
import com.example.admin.clientvisit.viewModel.SelectOwnerViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectOwnerDialog extends AppCompatActivity {

    RecyclerView ownerlist;
    List<SelectOwnerData> selectOwnerData;
    SelectOwnerAdapter SelectOwnerAdapter;
    Button add_button, done_button;
    String ownerToAdd;
    EditText owner_name;
    SelectOwnerViewModel viewModel;
    boolean newOwnerAddedFlag = false, firstTime = true;

    List<OwnerEntity> checkedOwnerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_owner_dialog);

        // init viewModel
        viewModel = ViewModelProviders.of(this).get(SelectOwnerViewModel.class);

        final List<OwnerEntity> savedOwnerEntities = (List<OwnerEntity>) getIntent().getSerializableExtra("owners");
        checkedOwnerData = new ArrayList<>();

        ownerlist = findViewById(R.id.owner_recycleview);
        owner_name = findViewById(R.id.owner_name);
        add_button = findViewById(R.id.add_button);
        done_button = findViewById(R.id.done_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ownerToAdd = owner_name.getText().toString();

                if (ownerToAdd != null && !ownerToAdd.equals("")) {
                    viewModel.addToOwner(new OwnerEntity(0, ownerToAdd));
//                    SelectOwnerAdapter.notifyDataSetChanged();
                    newOwnerAddedFlag = true;
                    owner_name.setText("");
                }

            }
        });

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<OwnerEntity> ownerEntities = new ArrayList<>();
                for (int i = 0; i < selectOwnerData.size(); i++) {
                    if (selectOwnerData.get(i).getStatus() == true) {
                        ownerEntities.add(new OwnerEntity(selectOwnerData.get(i).getOid(), selectOwnerData.get(i).getOwnerName()));
                    }

                }
                Intent intent = new Intent();
                intent.putExtra("owners", (Serializable) ownerEntities);
                setResult(666, intent);
                finish();
            }
        });


        selectOwnerData = new ArrayList<>();

        SelectOwnerAdapter = new SelectOwnerAdapter(selectOwnerData);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        ownerlist.setLayoutManager(mLayoutManager);

        ownerlist.setAdapter(SelectOwnerAdapter);


        ownerlist.addOnItemTouchListener(new RecyclerTouchListener(this, ownerlist, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {


                AppCompatCheckBox checkBox = view.findViewById(R.id.checkbox);
                Log.d("TAG", "onClick:  addOnItemTouchListener called " + checkBox.isChecked());
                if (checkBox.isChecked()) {

                    checkBox.setChecked(false);
                    selectOwnerData.get(position).setStatus(false);
                    SelectOwnerAdapter.notifyDataSetChanged();
                    try {
                        checkedOwnerData.remove(getIndexBySelectOwnerData(selectOwnerData.get(position)));
                    }catch (Exception e){
                        Log.d("TAG", "onClick: fail to get index");
                    }

                } else {

                    checkedOwnerData.add(new OwnerEntity(selectOwnerData.get(position).getOid(), selectOwnerData.get(position).getOwnerName()));
                    checkBox.setChecked(true);
                    selectOwnerData.get(position).setStatus(true);
                    SelectOwnerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        //liveData
        viewModel.getOwnerListWithBusiness().observe(this, new Observer<List<SelectOwnerData>>() {
            @Override
            public void onChanged(@Nullable List<SelectOwnerData> selectOwnerDataChanged) {


                if (newOwnerAddedFlag == true) {
                    selectOwnerDataChanged.get(0).setStatus(true);      // newly added item
                    checkedOwnerData.add(new OwnerEntity(selectOwnerDataChanged.get(0).getOid(),selectOwnerDataChanged.get(0).getOwnerName()));
                    newOwnerAddedFlag = false;
                    selectOwnerDataChanged = markStatusTrue(checkedOwnerData, selectOwnerDataChanged);  // past check item
                }

                if (firstTime && savedOwnerEntities != null) {  // already set owner have mark true
                    selectOwnerDataChanged = markStatusTrue(savedOwnerEntities, selectOwnerDataChanged);
                    checkedOwnerData.addAll(savedOwnerEntities);
                    firstTime = false;
                }

                selectOwnerData = selectOwnerDataChanged;
                SelectOwnerAdapter.addItems(selectOwnerDataChanged);

            }
        });


    }

    private int getIndexBySelectOwnerData(SelectOwnerData selectOwnerData) {
        int i;
        for (i = 0; i < checkedOwnerData.size(); i++) {
            if (checkedOwnerData.get(i).getOid() == selectOwnerData.getOid() && checkedOwnerData.get(i).getOwnerName().equals(selectOwnerData.getOwnerName()))
                break;
        }
        return i;
    }

    public List<SelectOwnerData> markStatusTrue(List<OwnerEntity> input, List<SelectOwnerData> output) {

        for (int i = 0; i < input.size(); i++) {
            for (int j = 0; j < output.size(); j++) {
                if (input.get(i).getOid() == output.get(j).getOid()) {
                    output.get(j).setStatus(true);
                }
            }
        }
        return output;
    }


}
