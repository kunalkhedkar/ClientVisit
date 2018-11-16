package com.example.admin.clientvisit;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.admin.clientvisit.database.AppDatabase;
import com.example.admin.clientvisit.database.BusinessEntity;
import com.example.admin.clientvisit.database.FeedbackEntity;
import com.example.admin.clientvisit.model.MyLocation;
import com.example.admin.clientvisit.util.AppCompatActivityWithPermission;
import com.example.admin.clientvisit.util.Z;
import com.example.admin.clientvisit.viewModel.FeedbackViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;

public class AddFeedback extends AppCompatActivityWithPermission {

    private static final String TAG = "TAG";
    private static final String TRY_AGAIN = "Try Again";

    String LATITUDE, LONGITUDE;


    TextInputEditText date_edit, time_edit, feedback_edit, location_edit, reason_of_visit, visited_person_name, visited_person_contact;
    Context context;
    ImageView addLocationBtn;
    AppDatabase db;
    AppDatabase db2;
    int businessId;

    String problemSolvedStatus = "";

    RadioGroup solved_status_radio;
    RadioButton radio_yes, radio_no;

    FeedbackViewModel viewModel;
    FusedLocationProviderClient mFusedLocationClient;
    ProgressBar progressBar;


    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feedback);
        context = AddFeedback.this;

        getSupportActionBar().setTitle("Add Feedback");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close_white);


        db = AppDatabase.getInstance(AddFeedback.this);
        db2=AppDatabase.getInstance2nd(AddFeedback.this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            final String bid = getIntent().getStringExtra("id");
            if (bid == null)
                Toast.makeText(this, "business id " + businessId, Toast.LENGTH_SHORT).show();
            else
                businessId = Integer.parseInt(bid);
        } catch (Exception e) {
            Toast.makeText(context, "Could not get business ID", Toast.LENGTH_SHORT).show();
        }

        viewModel = ViewModelProviders.of(this, new VisitDetailListFragment.MyViewModelFactory(getApplication(), businessId)).get(FeedbackViewModel.class);

        date_edit = findViewById(R.id.date_edit);
        time_edit = findViewById(R.id.time_edit);
        feedback_edit = findViewById(R.id.feedback);
        addLocationBtn = findViewById(R.id.addLocation);
        reason_of_visit = findViewById(R.id.reason_of_visit);
        visited_person_name = findViewById(R.id.visited_person_name);
        visited_person_contact = findViewById(R.id.visited_person_contact);

        solved_status_radio = findViewById(R.id.solved_status_radio);
        radio_yes = findViewById(R.id.radio_yes);
        radio_no = findViewById(R.id.radio_no);

        progressBar = findViewById(R.id.progressBar);

        location_edit = findViewById(R.id.location_edit);
        checkLocationPermissionCallLocation();
//        getLocation();


        final Calendar currentDateCalendar = Calendar.getInstance();
        setDate(currentDateCalendar);
        setTime(currentDateCalendar);

        //date picker listener
        final DatePickerDialog.OnDateSetListener DateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String str_task_date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                date_edit.setText(str_task_date);
            }
        };
        // Time picker listener
        final TimePickerDialog.OnTimeSetListener Timeistener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String am_pm;
                int hour = hourOfDay;
                Log.d("TAG", "setTime: " + hour);
                if (hourOfDay < 12) {
                    am_pm = "AM";
                } else {
                    am_pm = "PM";
                    hourOfDay = hourOfDay - 12;
                }
                if (hourOfDay == 0) {
                    hourOfDay = 12;
                }
                String str_task_time = hourOfDay + ":" + minute + " " + am_pm;
                time_edit.setText(str_task_time);

            }
        };


        date_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(context, DateListener, currentDateCalendar.get(Calendar.YEAR), currentDateCalendar.get(Calendar.MONTH), currentDateCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
//                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
//                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
        });

        time_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(context, Timeistener, currentDateCalendar.get(Calendar.HOUR_OF_DAY), currentDateCalendar.get(Calendar.MINUTE), false).show();
            }
        });


        solved_status_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    problemSolvedStatus = checkedRadioButton.getText().toString();
                }
            }
        });

        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                location_edit.setText("");
                progressBar.setVisibility(View.VISIBLE);
                addLocationBtn.setVisibility(View.GONE);
                checkLocationPermissionCallLocation();


                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        addLocationBtn.setVisibility(View.VISIBLE);
                    }

                }, 500);

                getLocation();
            }
        });

    }


    private void enableLocation() {
        googleApiClient = new GoogleApiClient.Builder(AddFeedback.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        googleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                        Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                    }
                }).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(AddFeedback.this, 191);

//                            finish();
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            onSaveClicked();
        }
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSaveClicked() {

        String date, time, feedback, reasonVisit, visitedPersonName, visitedPersonContat;

        date = date_edit.getText().toString();
        time = time_edit.getText().toString();
        feedback = feedback_edit.getText().toString();
        reasonVisit = reason_of_visit.getText().toString();
        visitedPersonName = visited_person_name.getText().toString();
        visitedPersonContat = visited_person_contact.getText().toString();

        boolean errorFlag = false;
        if (!Z.isNotNullNotBlank(date)) {
            Toast.makeText(AddFeedback.this, "Kindly select date", Toast.LENGTH_SHORT).show();
            errorFlag = true;
        } else if (!Z.isNotNullNotBlank(time)) {
            Toast.makeText(AddFeedback.this, "Kindly select time", Toast.LENGTH_SHORT).show();
            errorFlag = true;
        } else if (!Z.isNotNullNotBlank(problemSolvedStatus)) {
            Toast.makeText(AddFeedback.this, "Kindly select problem solved status", Toast.LENGTH_SHORT).show();
            errorFlag = true;
        } else if (!Z.isNotNullNotBlank(reasonVisit)) {
            Toast.makeText(AddFeedback.this, "Kindly enter reason behind visit", Toast.LENGTH_SHORT).show();
            errorFlag = true;
        } else if (!Z.isNotNullNotBlank(visitedPersonName)) {
            Toast.makeText(AddFeedback.this, "Kindly enter name of visited person", Toast.LENGTH_SHORT).show();
            errorFlag = true;
        } else if (!Z.isNotNullNotBlank(visitedPersonContat)) {
            Toast.makeText(AddFeedback.this, "Kindly enter contact no of visited person", Toast.LENGTH_SHORT).show();
            errorFlag = true;
        } else if (!Z.isNotNullNotBlank(feedback)) {
            Toast.makeText(AddFeedback.this, "Kindly enter feedback", Toast.LENGTH_SHORT).show();
            errorFlag = true;
        } else if (!validateLatLng(LATITUDE, LONGITUDE)) {
            Toast.makeText(AddFeedback.this, "Kindly provide location", Toast.LENGTH_SHORT).show();
            errorFlag = true;
        }

        if (errorFlag == false) {
            // save data

            boolean probSolved = false;
            if (problemSolvedStatus.equals("yes"))
                probSolved = true;

            new SaveFeedbackTask(businessId, probSolved, date, time, LATITUDE, LONGITUDE, reasonVisit, visitedPersonName, visitedPersonContat, feedback).execute();
        }
    }

    private void showVisitListFragment() {
        try {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            VisitDetailListFragment visitDetailListFragment = new VisitDetailListFragment();
            fragmentTransaction.replace(R.id.container, visitDetailListFragment, "visitDetailListFragment");
            Bundle bundle = new Bundle();
            bundle.putString("id", String.valueOf(businessId));
            visitDetailListFragment.setArguments(bundle);
            fragmentTransaction.addToBackStack("visitDetailListFragment");
            fragmentTransaction.commit();
        } catch (Exception e) {
            Log.d("TAG", "showVisitListFragment: exp " + e.getMessage());
        }
    }

    private boolean validateLatLng(String latitude, String longitude) {
        if (latitude != null && longitude != null) {
            if (latitude.equals("") || latitude.equals(TRY_AGAIN) || longitude.equals("") || longitude.equals(TRY_AGAIN))
                return false;
            else
                return true;

        } else
            return false;
    }

    private void setDate(Calendar cal) {
        String setDate = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
        date_edit.setText(setDate);
    }

    private void setTime(Calendar cal) {
        String am_pm;
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        Log.d("TAG", "setTime: " + hour);
        if (hour < 12) {
            am_pm = "AM";
        } else {
            am_pm = "PM";
            hour = hour - 12;
        }

        if (hour == 0) {
            hour = 12;
        }
        String str_time = hour + ":" + cal.get(Calendar.MINUTE) + " " + am_pm;
        time_edit.setText(str_time);
    }


    class SaveFeedbackTask extends AsyncTask<Void, Void, Boolean> {

        int businessId;
        boolean problemSolvedStatus;
        String date, time, latitude, longitude, reasonBehindVisit, nameOfVisitedPerson, contactOfVisitedPerson, feedback;

        SaveFeedbackTask(int businessId, boolean problemSolvedStatus, String date, String time, String latitude, String longitude, String reasonBehindVisit, String nameOfVisitedPerson, String contactOfVisitedPerson, String feedback) {
            this.businessId = businessId;
            this.problemSolvedStatus = problemSolvedStatus;
            this.date = date;
            this.time = time;
            this.latitude = latitude;
            this.longitude = longitude;
            this.reasonBehindVisit = reasonBehindVisit;
            this.nameOfVisitedPerson = nameOfVisitedPerson;
            this.contactOfVisitedPerson = contactOfVisitedPerson;
            this.feedback = feedback;
        }

        int distance=-1;

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {


                MyLocation businessLocation = db.contactDao().getBusinessLocation(businessId);
                if (businessLocation != null) {


                    try {
                        float result[] = new float[3];
                        Location.distanceBetween(Double.parseDouble(latitude), Double.parseDouble(longitude), Double.parseDouble(businessLocation.getLatitude()), Double.parseDouble(businessLocation.getLongitude()), result);
                        Log.d("TAG", "onClick: disstance betwwen " + result[0]);
                        distance = Math.round(result[0]);
                    } catch (Exception e) {
                        Log.d(TAG, "EXP parse & cal distance msg is : "+e.getMessage());
                    }

                } else {
                    Log.d("TAG", "Business Location not found ");
                }

                long insertedIndex = db.feedbackDao().insertFeedbackData(new FeedbackEntity(
                        0, businessId,distance, problemSolvedStatus, date, time, latitude, longitude,
                        reasonBehindVisit, nameOfVisitedPerson, contactOfVisitedPerson, feedback));

                //2nd db
                // 2nd db
//               long b= db2.businessDao().insertBusinessData(new BusinessEntity(0, "kunalTestBusinessName"));
//                long insertedIndex2 = db2.feedbackDao().insertFeedbackData(new FeedbackEntity(
//                        0, (int)b,distance, problemSolvedStatus, date, time, latitude, longitude,
//                        reasonBehindVisit, nameOfVisitedPerson, contactOfVisitedPerson, feedback));

                Log.d("database 2", "doInBackground: index1 "+insertedIndex);
//                Log.d("database 2", "doInBackground: index2 "+insertedIndex2);

                if (insertedIndex > 0) {
                    return true;
                }
            } catch (Exception e) {
                Log.d(TAG, "doInBackground: save Feedback EXP : " + e.getMessage());
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(context, "Save successfully!", Toast.LENGTH_SHORT).show();
                setResult(ClientFragment.ADD_FEEDBACK_ACTIVITY_REQUEST_CODE);
                finish();
            } else {
                Toast.makeText(context, "Fail to save data", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(context, "distance " + distance, Toast.LENGTH_SHORT).show();
        }
    }


    public void checkLocationPermissionCallLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            requestRunTimePermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_PERMISSITION_REQUEST);
        }
    }

    public void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {

                            LATITUDE = String.valueOf(location.getLatitude());
                            LONGITUDE = String.valueOf(location.getLongitude());
                            location_edit.setText(LATITUDE + ", " + LONGITUDE);

                        } else {
                            location_edit.setText(TRY_AGAIN);

                            if (isLocationEnabled(context)) {
                                Log.d(TAG, "displayLocation: LOCTION ON BUT please check your network connection");
                            } else {
                                Log.d(TAG, "displayLocation: LOCATION OFF");
                                enableLocation();
                            }

                        }
                    }
                });
    }


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_LOCATION_PERMISSITION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 191 && resultCode == RESULT_OK)
            getLocation();

    }
}
