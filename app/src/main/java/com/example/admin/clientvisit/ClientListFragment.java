package com.example.admin.clientvisit;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.clientvisit.adapter.ClientAdapter;
import com.example.admin.clientvisit.adapter.RecyclerTouchListener;
import com.example.admin.clientvisit.database.AppDatabase;
import com.example.admin.clientvisit.database.BusinessEntity;
import com.example.admin.clientvisit.database.ContactEntity;
import com.example.admin.clientvisit.database.DbUtil;
import com.example.admin.clientvisit.database.OwnerEntity;
import com.example.admin.clientvisit.model.ClientData;
import com.example.admin.clientvisit.viewModel.ClientViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.internal.view.SupportMenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW;
import static android.support.v4.internal.view.SupportMenuItem.SHOW_AS_ACTION_IF_ROOM;

public class ClientListFragment extends Fragment {

    public static final String CLIENT_DATA_KEY = "clientDataKey";

    RecyclerView clientRecyclerView;
    View view;
    Context context;

    private FragmentManager fragmentManager;
    List<ClientData> clientList;
    ClientAdapter clientAdapter;

    public ClientListFragment() {
        // Required empty public constructor
    }

    ArrayList<ClientData> OrignalFullClientlList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_client_list, container, false);
        clientRecyclerView = view.findViewById(R.id.clientRecyclerView);

        setHasOptionsMenu(true);
        fragmentManager = getActivity().getSupportFragmentManager();

        context = getActivity();
        OrignalFullClientlList = new ArrayList<>();

        ((NavigationActivity) getActivity()).getSupportActionBar().setTitle("Client List");

        FloatingActionButton fab = view.findViewById(R.id.fab_add_client);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddClientFragment();
                Toast.makeText(context, "test delete", Toast.LENGTH_SHORT).show();
//


            }
        });


        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                testDeleteCasCade();
                return false;
            }
        });

        clientList = new ArrayList<>();                  //test
//        OrignalFullClientlList.addAll(clientList);  // original list

        clientAdapter = new ClientAdapter(clientList,getActivity());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        clientRecyclerView.setLayoutManager(mLayoutManager);

        clientRecyclerView.setAdapter(clientAdapter);


        clientRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), clientRecyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {

                showClientFragment(clientList.get(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        //livedata
        ClientViewModel viewModel = ViewModelProviders.of(this).get(ClientViewModel.class);
        viewModel.buildClientdataList().observe(getActivity(), new Observer<List<ClientData>>() {
            @Override
            public void onChanged(@Nullable List<ClientData> clientData) {
                clientAdapter.addItems(clientData);
                Log.d("TAG", "onChanged: called");
                OrignalFullClientlList.clear();
                OrignalFullClientlList.addAll(clientData);
                clientList.clear();
                clientList.addAll(clientData);
            }
        });

        return view;
    }




    public void showMenuDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_custom_menu_dialog, null);
        dialogBuilder.setView(dialogView);

        final TextView view_client_details = (TextView) dialogView.findViewById(R.id.view_client_details);
        final TextView add_feedback = (TextView) dialogView.findViewById(R.id.add_feedback);
        final TextView view_visit_details = (TextView) dialogView.findViewById(R.id.view_visit_details);

//        dialogBuilder.setTitle("Custom dialog");
//        dialogBuilder.setMessage("Enter text below");
//        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                //do something with edt.getText().toString();
////                Toast.makeText(getContext(), ""+edt.getText().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                //pass
//            }
//        });

        view_client_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        add_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        view_visit_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void showClientFragment(ClientData clientData) {
        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CLIENT_DATA_KEY, clientData);
            ClientFragment clientFragment = new ClientFragment();
            clientFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, clientFragment, "clientListFragment");
            fragmentTransaction.addToBackStack("clientListFragment");
            fragmentTransaction.commit();
        } catch (Exception e) {
            Log.d("TAG", "showClientFragment: exp " + e.getMessage());
        }
    }

    private void showAddClientFragment() {
        try {
            ClientFragment clientFragment = new ClientFragment();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, clientFragment, "clientFragment");
            fragmentTransaction.addToBackStack("clientFragment");
            fragmentTransaction.commit();
        } catch (Exception e) {
            Log.d("TAG", "showAddClientFragment: exp " + e.getMessage());
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Context mContext = getActivity();
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(((NavigationActivity) mContext).getSupportActionBar().getThemedContext());
//        menu.findItem(R.id.action_search).setShowAsAction( MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
//        MenuItemCompat.setActionView(item, searchView);
        menu.findItem(R.id.action_search).setShowAsAction(SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | SHOW_AS_ACTION_IF_ROOM);
        menu.findItem(R.id.action_search).setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("TAG", "onQueryTextSubmit: " + query);
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("TAG", "onQueryTextSubmit newText : " + newText);
                filter(newText);
                return false;
            }
        });
//        searchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        }
//        );
    }


    public void filter(String searchText) {
        clientList.clear();
        if (searchText.isEmpty()) {
            clientList.addAll(OrignalFullClientlList);
        } else {
            searchText = searchText.toLowerCase();
            for (ClientData item : OrignalFullClientlList) {
                if (item.getBusinessName().toLowerCase().contains(searchText) ||
                        DbUtil.buildOwnerNameStringFromList(item).toLowerCase().contains(searchText) ||
                        item.getAddressArea().toLowerCase().contains(searchText) ||
                        item.getAddressPincode().toLowerCase().contains(searchText) ||
                        item.getMobile().toLowerCase().contains(searchText)) {
                    clientList.add(item);
                }
            }
        }
        clientAdapter.addItems(clientList);
    }





    private void testDeleteCasCade() {

        new Thread()
        {
            public void run() {

                Log.d("TAG", "run: inside thread");

                AppDatabase db = AppDatabase.getInstance(getActivity());

//                List<BusinessEntity> allBusinessData = db.businessDao().getAllBusinessData();
//
//                for(BusinessEntity businessEntity:allBusinessData){
//
//                    Log.d("TAG", "run: businessEntity "+businessEntity);
//                }
//
//                Log.d("TAG", "run: --------------------------------------- contactEntity ------------------------");
//                List<ContactEntity> allContactData = db.contactDao().getAllContactData();
//                for(ContactEntity contactEntity:allContactData){
//
//                    Log.d("TAG", "run: contactEntity "+contactEntity);
//                }
//
//
//                int count = db.businessDao().deleteBusinessById(new BusinessEntity(3, "Golden State empire"));
//                Log.d("TAG", "run: delete count "+count);
//
//
//                Log.d("TAG", "run: ------------------After DELETE --------------------- contactEntity ------------------------");
//                allContactData = db.contactDao().getAllContactData();
//                for(ContactEntity contactEntity:allContactData){
//
//                    Log.d("TAG", "run: contactEntity "+contactEntity);
//                }


//                Log.d("TAG", "run: ------------------After DELETE --------------------- contactEntity ------------------------");
//
//
//                List<OwnerEntity> allOwnerData = db.ownerDao().getAllOwnerData();
//                for(OwnerEntity ownerEntity:allOwnerData){
//
//                    Log.d("TAG", "run: contactEntity "+ownerEntity);
//                }




//                while (cursor.moveToNext()){
//
//
//                }



                List<OwnerEntity> allOwnerData = db.ownerDao().getAllOwnerData();
                for(OwnerEntity ownerEntity:allOwnerData){
                    Log.d("TAG", "run: contactEntity "+ownerEntity);
                    db.ownerDao().insertOwnerData(ownerEntity);
                }

                allOwnerData = db.ownerDao().getAllOwnerData();
                for(OwnerEntity ownerEntity:allOwnerData){
                    Log.i("TAG", "run: contactEntity "+ownerEntity);
                    db.ownerDao().insertOwnerData(ownerEntity);
                }


            }
        }.start();

    }

}
