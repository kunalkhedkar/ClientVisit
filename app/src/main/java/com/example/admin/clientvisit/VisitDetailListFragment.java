package com.example.admin.clientvisit;


import android.app.Application;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.admin.clientvisit.adapter.RecyclerTouchListener;
import com.example.admin.clientvisit.adapter.VisitAdapter;
import com.example.admin.clientvisit.database.FeedbackEntity;
import com.example.admin.clientvisit.viewModel.FeedbackViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.example.admin.clientvisit.ClientFragment.ADD_FEEDBACK_ACTIVITY_REQUEST_CODE;


public class VisitDetailListFragment extends Fragment {


    public static final String FEEDBACK_KEY = "feedback_key";
    View view;
    RecyclerView visitRecyclerView;
    int businessId;
    List<FeedbackEntity> feedbackList;
    FloatingActionButton fab_add_feedback;
    FeedbackViewModel viewModel;

    RelativeLayout emptyListMessage;

    public VisitDetailListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_visit_detail_list, container, false);
        visitRecyclerView = view.findViewById(R.id.visitRecyclerView);
        fab_add_feedback = view.findViewById(R.id.fab_add_feedback);
        emptyListMessage = view.findViewById(R.id.emptyListMessage);

        Bundle bundle = getArguments();
        String id = "",name="";
        if (bundle != null) {
            id = bundle.getString("id");
            name = bundle.getString("name");
            Log.d("TAG", "onCreateView: id " + id);
            ((NavigationActivity) getActivity()).getSupportActionBar().setTitle(name);
            businessId = Integer.parseInt(id);
        }


        feedbackList = new ArrayList<>();
        final VisitAdapter visitAdapter = new VisitAdapter(feedbackList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        visitRecyclerView.setLayoutManager(mLayoutManager);
        visitRecyclerView.setAdapter(visitAdapter);

        fab_add_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), AddFeedback.class).putExtra("id", String.valueOf(businessId)), ADD_FEEDBACK_ACTIVITY_REQUEST_CODE);
            }
        });


        //livedata
        viewModel = ViewModelProviders.of(this, new MyViewModelFactory(getActivity().getApplication(), businessId)).get(FeedbackViewModel.class);
        viewModel.getFeedbackList().observe(getActivity(), new Observer<List<FeedbackEntity>>() {
            @Override
            public void onChanged(@Nullable List<FeedbackEntity> feedbackEntities) {
                visitAdapter.addItems(feedbackEntities);
                feedbackList = feedbackEntities;
                if(feedbackEntities.size()==0)
                    emptyListMessage.setVisibility(View.VISIBLE);
                else
                    emptyListMessage.setVisibility(View.GONE);
            }
        });


        visitRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), visitRecyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                showVisitDetailFragment(feedbackList.get(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }


    private void showVisitDetailFragment(FeedbackEntity feedbackEntity) {
        try {
            VisitItemDetailFragment visitItemDetailFragment = new VisitItemDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(FEEDBACK_KEY, feedbackEntity);
            visitItemDetailFragment.setArguments(bundle);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, visitItemDetailFragment, "visitItemDetailFragment");
            fragmentTransaction.addToBackStack("visitItemDetailFragment");
            fragmentTransaction.commit();
        } catch (Exception e) {
            Log.d("TAG", "showVisitDetailFragment : exp " + e.getMessage());
        }
    }


    // for factory Obj parameter  | modelView constructor
    public static class MyViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        private Application mApplication;
        private int id;

        public MyViewModelFactory(Application application, int id) {
            mApplication = application;
            this.id = id;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new FeedbackViewModel(mApplication, id);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_FEEDBACK_ACTIVITY_REQUEST_CODE) {
            viewModel.getFeedbackList();
        }
    }
}

