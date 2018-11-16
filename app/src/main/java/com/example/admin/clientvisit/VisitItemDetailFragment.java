package com.example.admin.clientvisit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.clientvisit.database.FeedbackEntity;



public class VisitItemDetailFragment extends Fragment {

    View view;
    FeedbackEntity feedbackEntity;
    TextView date_tv, time_tv, problem_solved_status,name_visited_person,contact_visited_person,reason_of_visit,feedback_tv;



    public VisitItemDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_visit_item_detail, container, false);
        date_tv = view.findViewById(R.id.date_tv);
        time_tv = view.findViewById(R.id.time_tv);
        feedback_tv = view.findViewById(R.id.feedback_tv);

        problem_solved_status = view.findViewById(R.id.problem_sloved_status);
        name_visited_person = view.findViewById(R.id.name_visited_person);
        contact_visited_person = view.findViewById(R.id.contact_visited_person);
        reason_of_visit = view.findViewById(R.id.reason_of_visit);

        Bundle bundle = getArguments();
        if (bundle != null) {
            feedbackEntity = (FeedbackEntity) bundle.getSerializable(VisitDetailListFragment.FEEDBACK_KEY);
            if (feedbackEntity != null) {
                populateData(feedbackEntity);
            }
        }


        return view;
    }

    private void populateData(FeedbackEntity feedbackEntity) {
        date_tv.setText(feedbackEntity.getDate());
        time_tv.setText(feedbackEntity.getTime());
        if(feedbackEntity.isProblemSlovedStatus()) {
            problem_solved_status.setText("YES");
        }else{
            problem_solved_status.setText("No");
        }
        name_visited_person.setText(feedbackEntity.getNameOfVisitedPerson());
        contact_visited_person.setText(feedbackEntity.getContactOfVisitedPerson());
        reason_of_visit.setText(feedbackEntity.getReasonBehindVisit());
        feedback_tv.setText(feedbackEntity.getFeedback());
    }

}
