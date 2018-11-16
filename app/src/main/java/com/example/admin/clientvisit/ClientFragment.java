package com.example.admin.clientvisit;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.admin.clientvisit.database.AppDatabase;
import com.example.admin.clientvisit.database.BusinessEntity;
import com.example.admin.clientvisit.database.BusinessOwnerEntity;
import com.example.admin.clientvisit.database.ContactEntity;
import com.example.admin.clientvisit.database.DbUtil;
import com.example.admin.clientvisit.database.OwnerEntity;
import com.example.admin.clientvisit.model.ClientData;
import com.example.admin.clientvisit.util.Z;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.admin.clientvisit.util.AppCompatActivityWithPermission.MY_CAMERA_PERMISSITION_REQUEST;
import static com.example.admin.clientvisit.util.Z.isNotNullNotBlank;

public class ClientFragment extends Fragment {

    boolean isAllowedEdit = false;

    boolean UPDATE_MODE = false;
    boolean clientNameEditFlag = false, businessNameEditFlag = false;
    boolean contactDataEditFlag = false;
    boolean imageEdit = false;
    String oldClientName = "";

    static FragmentManager fragmentManager;


    View view;
    FloatingActionButton fab;
    ClientData clientData;
    ImageView addImage, image, editModeImage,getDirectionButton;
    Bitmap bitmap;
    String latitude, longitude;

    AppDatabase db;
    List<OwnerEntity> selectedOwnerList;
    List<OwnerEntity> TempOwnerListSelected;

    public static final int ADD_FEEDBACK_ACTIVITY_REQUEST_CODE = 111;
    public static final int PLACE_PICKER_REQUEST_CODE = 222;
    public static final int OPEN_CAMERA_INTENT_REQUEST_CODE = 333;
    public static final int SELECT_OWNER_ACTIVITY_CODE = 666;

    Drawable editTextDefaultDrawable;

    TextInputEditText client_name, business_name, website_edit, mobile_edit, phone_edit, email_edit, choose_location, address_area, address_brief, address_pincode;

    public ClientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_add_client, container, false);
        setHasOptionsMenu(true);

        TempOwnerListSelected = new ArrayList<>();

        fab = view.findViewById(R.id.fab);
        client_name = view.findViewById(R.id.client_name);
        business_name = view.findViewById(R.id.business_name);
        website_edit = view.findViewById(R.id.website_edit);
        mobile_edit = view.findViewById(R.id.mobile_edit);
        phone_edit = view.findViewById(R.id.phone_edit);
        email_edit = view.findViewById(R.id.email_edit);
        choose_location = view.findViewById(R.id.choose_location);
        address_area = view.findViewById(R.id.address_area);
        address_brief = view.findViewById(R.id.address_brief);
        address_pincode = view.findViewById(R.id.address_pincode);


        if (editTextDefaultDrawable == null)
            editTextDefaultDrawable = address_pincode.getBackground();

        getDirectionButton = view.findViewById(R.id.getDirectionButton);

        addImage = view.findViewById(R.id.addImage);
        image = view.findViewById(R.id.image);
        editModeImage = view.findViewById(R.id.editModeImage);


        fragmentManager = getActivity().getSupportFragmentManager();


        db = AppDatabase.getInstance(getActivity());
        selectedOwnerList = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            clientData = (ClientData) bundle.getSerializable(ClientListFragment.CLIENT_DATA_KEY);
            if (clientData != null) {

                populateData(clientData);
                if (clientData.getBusinessName() != null)
                    ((NavigationActivity) getActivity()).getSupportActionBar().setTitle(clientData.getBusinessName());

                UPDATE_MODE = true;
                selectedOwnerList.clear();
                selectedOwnerList.addAll(clientData.getOwnerList());

                isAllowedEdit = false;

//                disableEdit();      calling inside populate
            } else {
                fab.setVisibility(View.GONE);
                ((NavigationActivity) getActivity()).getSupportActionBar().setTitle("Add Client");

                UPDATE_MODE = false;

                isAllowedEdit = true;
                enableEdit();
                editModeImage.setVisibility(View.GONE);
                getDirectionButton.setVisibility(View.GONE);

            }
        } else {
            ((NavigationActivity) getActivity()).getSupportActionBar().setTitle("Add Client");
            fab.setVisibility(View.GONE);


            UPDATE_MODE = false;
            isAllowedEdit = true;
            enableEdit();
            editModeImage.setVisibility(View.GONE);
            getDirectionButton.setVisibility(View.GONE);
        }


        editModeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAllowedEdit) {      // on edit
                    editModeImage.setImageResource(R.drawable.edit_icon_blue);
                    isAllowedEdit = true;
                    enableEdit();
                } else {                   // off edit
                    disableEdit();
                    isAllowedEdit = false;
                    editModeImage.setImageResource(R.drawable.edit_icon_grey);
                }
            }
        });

        getDirectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                        Uri.parse("http://maps.google.com/maps?saddr=18.506559,73.8271981&daddr=18.473822,73.82210909999999"));

//                startActivity(intent);


//                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", sourceLatitude, sourceLongitude, "Home Sweet Home", destinationLatitude, destinationLongitude, "Where the party is at");
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                intent.setPackage("com.google.android.apps.maps");
//                startActivity(intent);

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + clientData.getLatitude() + "," + clientData.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);


            }
        });


        //textChange Listeners
        client_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clientNameEditFlag = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        business_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                businessNameEditFlag = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //common client data watcher
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactDataEditFlag = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        website_edit.addTextChangedListener(textWatcher);
        mobile_edit.addTextChangedListener(textWatcher);
        phone_edit.addTextChangedListener(textWatcher);
        email_edit.addTextChangedListener(textWatcher);
        address_area.addTextChangedListener(textWatcher);
        choose_location.addTextChangedListener(textWatcher);
        address_brief.addTextChangedListener(textWatcher);
        address_pincode.addTextChangedListener(textWatcher);

        choose_location.setLongClickable(false);   // disable paste
        client_name.setLongClickable(false);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showVisitListFragment();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageEdit = true;
                showDialogImage();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageEdit = true;
                showDialogImage();
            }
        });


        choose_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isAllowedEdit) {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                    try {
                        startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        client_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isAllowedEdit) {
                    Intent selectOwnerIntent = new Intent(getActivity(), SelectOwnerDialog.class);
                    if (UPDATE_MODE) {
                        selectOwnerIntent.putExtra("owners", (Serializable) clientData.getOwnerList());

                    } else {          // while adding new entry before save

                        if (TempOwnerListSelected.size() > 0) {
                            selectOwnerIntent.putExtra("owners", (Serializable) TempOwnerListSelected);
                        }
                    }
                    startActivityForResult(selectOwnerIntent, SELECT_OWNER_ACTIVITY_CODE);
                }
            }
        });

        return view;

    }


    private void enableEdit() {

        business_name.setFocusableInTouchMode(true);
        website_edit.setFocusableInTouchMode(true);
        mobile_edit.setFocusableInTouchMode(true);
        phone_edit.setFocusableInTouchMode(true);
        email_edit.setFocusableInTouchMode(true);
        address_area.setFocusableInTouchMode(true);
        address_brief.setFocusableInTouchMode(true);
        address_pincode.setFocusableInTouchMode(true);

        client_name.setClickable(true);
        choose_location.setClickable(true);
        business_name.setClickable(true);
        website_edit.setClickable(true);
        mobile_edit.setClickable(true);
        phone_edit.setClickable(true);
        email_edit.setClickable(true);
        address_area.setClickable(true);
        address_brief.setClickable(true);
        address_pincode.setClickable(true);

        business_name.setLongClickable(true);
        website_edit.setLongClickable(true);
        mobile_edit.setLongClickable(true);
        phone_edit.setLongClickable(true);
        email_edit.setLongClickable(true);
        address_area.setLongClickable(true);
        address_brief.setLongClickable(true);
        address_pincode.setLongClickable(true);

        business_name.setCursorVisible(true);
        website_edit.setCursorVisible(true);
        mobile_edit.setCursorVisible(true);
        phone_edit.setCursorVisible(true);
        email_edit.setCursorVisible(true);
        address_area.setCursorVisible(true);
        address_brief.setCursorVisible(true);
        address_pincode.setCursorVisible(true);

        client_name.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        choose_location.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        business_name.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        website_edit.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        mobile_edit.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        phone_edit.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        email_edit.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        address_area.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        address_brief.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        address_pincode.setTextColor(getResources().getColor(R.color.colorPrimaryDark));


//        client_name.getBackground().clearColorFilter();
//        choose_location.getBackground().clearColorFilter();
//        website_edit.getBackground().clearColorFilter();
//        business_name.getBackground().clearColorFilter();
//        mobile_edit.getBackground().clearColorFilter();
//        phone_edit.getBackground().clearColorFilter();
//        email_edit.getBackground().clearColorFilter();
//        address_area.getBackground().clearColorFilter();
//        address_brief.getBackground().clearColorFilter();
//        address_pincode.getBackground().clearColorFilter();

//        client_name.getBackground().setColorFilter(getResources().getColor(R.color.black),
//                PorterDuff.Mode.SRC_ATOP);
//        choose_location.getBackground().setColorFilter(getResources().getColor(R.color.black),
//                PorterDuff.Mode.SRC_ATOP);
//        business_name.getBackground().setColorFilter(getResources().getColor(R.color.black),
//                PorterDuff.Mode.SRC_ATOP);
//        website_edit.getBackground().setColorFilter(getResources().getColor(R.color.black),
//                PorterDuff.Mode.SRC_ATOP);
//        business_name.getBackground().setColorFilter(getResources().getColor(R.color.black),
//                PorterDuff.Mode.SRC_ATOP);
//        mobile_edit.getBackground().setColorFilter(getResources().getColor(R.color.black),
//                PorterDuff.Mode.SRC_ATOP);
//        phone_edit.getBackground().setColorFilter(getResources().getColor(R.color.black),
//                PorterDuff.Mode.SRC_ATOP);
//        email_edit.getBackground().setColorFilter(getResources().getColor(R.color.black),
//                PorterDuff.Mode.SRC_ATOP);
//        address_area.getBackground().setColorFilter(getResources().getColor(R.color.black),
//                PorterDuff.Mode.SRC_ATOP);
//        address_brief.getBackground().setColorFilter(getResources().getColor(R.color.black),
//                PorterDuff.Mode.SRC_ATOP);
//        address_pincode.getBackground().setColorFilter(getResources().getColor(R.color.black),
//                PorterDuff.Mode.SRC_ATOP);


//        client_name.setBackgroundDrawable(editTextDefaultDrawable);
//        choose_location.setBackgroundDrawable(editTextDefaultDrawable);
//        business_name.setBackgroundDrawable(editTextDefaultDrawable);
//        website_edit.setBackgroundDrawable(editTextDefaultDrawable);
//        mobile_edit.setBackgroundDrawable(editTextDefaultDrawable);
//        phone_edit.setBackgroundDrawable(editTextDefaultDrawable);
//        email_edit.setBackgroundDrawable(editTextDefaultDrawable);
//        address_area.setBackgroundDrawable(editTextDefaultDrawable);
//        address_brief.setBackgroundDrawable(editTextDefaultDrawable);
//        address_pincode.setBackgroundDrawable(editTextDefaultDrawable);

//            client_name.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.editbox_background));
//        business_name.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.editbox_background_normal));
//        website_edit.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.edit_text));
//        mobile_edit.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame));

    }

    private void disableEdit() {

        business_name.setFocusable(false);
        website_edit.setFocusable(false);
        mobile_edit.setFocusable(false);
        phone_edit.setFocusable(false);
        email_edit.setFocusable(false);
        address_area.setFocusable(false);
        address_brief.setFocusable(false);
        address_pincode.setFocusable(false);

        business_name.setLongClickable(false);
        website_edit.setLongClickable(false);
        mobile_edit.setLongClickable(false);
        phone_edit.setLongClickable(false);
        email_edit.setLongClickable(false);
        address_area.setLongClickable(false);
        address_brief.setLongClickable(false);
        address_pincode.setLongClickable(false);

        client_name.setClickable(false);
        choose_location.setClickable(false);
        business_name.setClickable(false);
        website_edit.setClickable(false);
        mobile_edit.setClickable(false);
        phone_edit.setClickable(false);
        email_edit.setClickable(false);
        address_area.setClickable(false);
        address_brief.setClickable(false);
        address_pincode.setClickable(false);

        business_name.setCursorVisible(false);
        website_edit.setCursorVisible(false);
        mobile_edit.setCursorVisible(false);
        phone_edit.setCursorVisible(false);
        email_edit.setCursorVisible(false);
        address_area.setCursorVisible(false);
        address_brief.setCursorVisible(false);
        address_pincode.setCursorVisible(false);

        client_name.setTextColor(getResources().getColor(R.color.black));
        choose_location.setTextColor(getResources().getColor(R.color.black));
        business_name.setTextColor(getResources().getColor(R.color.black));
        website_edit.setTextColor(getResources().getColor(R.color.black));
        mobile_edit.setTextColor(getResources().getColor(R.color.black));
        phone_edit.setTextColor(getResources().getColor(R.color.black));
        email_edit.setTextColor(getResources().getColor(R.color.black));
        address_area.setTextColor(getResources().getColor(R.color.black));
        address_brief.setTextColor(getResources().getColor(R.color.black));
        address_pincode.setTextColor(getResources().getColor(R.color.black));


//        client_name.setBackgroundResource(android.R.color.transparent);
//        choose_location.setBackgroundResource(android.R.color.transparent);
//        business_name.setBackgroundResource(android.R.color.transparent);
//        website_edit.setBackgroundResource(android.R.color.transparent);
//        mobile_edit.setBackgroundResource(android.R.color.transparent);
//        phone_edit.setBackgroundResource(android.R.color.transparent);
//        email_edit.setBackgroundResource(android.R.color.transparent);
//        address_area.setBackgroundResource(android.R.color.transparent);
//        address_brief.setBackgroundResource(android.R.color.transparent);
//        address_pincode.setBackgroundResource(android.R.color.transparent);


//        client_name.getBackground().setColorFilter(getResources().getColor(R.color.white),
//                PorterDuff.Mode.SRC_ATOP);
//        choose_location.getBackground().setColorFilter(getResources().getColor(R.color.white),
//                PorterDuff.Mode.SRC_ATOP);
//        business_name.getBackground().setColorFilter(getResources().getColor(R.color.white),
//                PorterDuff.Mode.SRC_ATOP);
//        website_edit.getBackground().setColorFilter(getResources().getColor(R.color.white),
//                PorterDuff.Mode.SRC_ATOP);
//        business_name.getBackground().setColorFilter(getResources().getColor(R.color.white),
//                PorterDuff.Mode.SRC_ATOP);
//        mobile_edit.getBackground().setColorFilter(getResources().getColor(R.color.white),
//                PorterDuff.Mode.SRC_ATOP);
//        phone_edit.getBackground().setColorFilter(getResources().getColor(R.color.white),
//                PorterDuff.Mode.SRC_ATOP);
//        email_edit.getBackground().setColorFilter(getResources().getColor(R.color.white),
//                PorterDuff.Mode.SRC_ATOP);
//        address_area.getBackground().setColorFilter(getResources().getColor(R.color.white),
//                PorterDuff.Mode.SRC_ATOP);
//        address_brief.getBackground().setColorFilter(getResources().getColor(R.color.white),
//                PorterDuff.Mode.SRC_ATOP);
//        address_pincode.getBackground().setColorFilter(getResources().getColor(R.color.white),
//                PorterDuff.Mode.SRC_ATOP);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.save_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            onSaveClicked();
        }
        return super.onOptionsItemSelected(item);

    }

    private void onSaveClicked() {

        String clientName, businessName, website, mobile, phone, email, addressArea, location, addressBrief, addressPincode;

        clientName = client_name.getText().toString();
        businessName = business_name.getText().toString();
        website = website_edit.getText().toString();
        mobile = mobile_edit.getText().toString();
        phone = phone_edit.getText().toString();
        email = email_edit.getText().toString();
        addressArea = address_area.getText().toString();
        location = choose_location.getText().toString();
        addressBrief = address_brief.getText().toString();
        addressPincode = address_pincode.getText().toString();

        boolean errorFlag = false;
        if (!isNotNullNotBlank(clientName)) {
            errorFlag = true;
            Toast.makeText(getActivity(), "Kindly enter Owner name", Toast.LENGTH_SHORT).show();
        } else if (!isNotNullNotBlank(businessName)) {
            errorFlag = true;
            Toast.makeText(getActivity(), "Kindly enter valid Business name", Toast.LENGTH_SHORT).show();
        } else if (!isNotNullNotBlank(website) || !website.contains(".")) {
            errorFlag = true;
            Toast.makeText(getActivity(), "Kindly enter valid website url", Toast.LENGTH_SHORT).show();
        } else if (!isNotNullNotBlank(mobile) || mobile.length() != 10) {
            errorFlag = true;
            Toast.makeText(getActivity(), "Kindly enter valid mobile number", Toast.LENGTH_SHORT).show();
        } else if (!isNotNullNotBlank(email) || !Z.isEmailValid(email)) {
            errorFlag = true;
            Toast.makeText(getActivity(), "Kindly enter valid email address", Toast.LENGTH_SHORT).show();
        } else if (!isNotNullNotBlank(addressArea)) {
            errorFlag = true;
            Toast.makeText(getActivity(), "Kindly enter Address Area", Toast.LENGTH_SHORT).show();
        } else if (!isNotNullNotBlank(location)) {
            errorFlag = true;
            Toast.makeText(getActivity(), "Kindly select location", Toast.LENGTH_SHORT).show();
        } else if (!isNotNullNotBlank(addressBrief)) {
            errorFlag = true;
            Toast.makeText(getActivity(), "Kindly enter valid Address", Toast.LENGTH_SHORT).show();
        } else if (!isNotNullNotBlank(addressPincode)) {
            errorFlag = true;
            Toast.makeText(getActivity(), "Kindly enter valid Pincode", Toast.LENGTH_SHORT).show();
        } else if (bitmap == null) {
            errorFlag = true;
            Toast.makeText(getActivity(), "Kindly provide image", Toast.LENGTH_SHORT).show();
        } else if (selectedOwnerList.size() == 0 && UPDATE_MODE) {
            errorFlag = true;
            Toast.makeText(getActivity(), "Kindly enter Owner name", Toast.LENGTH_SHORT).show();
        }

        if (errorFlag == false) {

            if (UPDATE_MODE == false) {             // new record

                new SaveClientTask(
                        getActivity(),
                        db,
                        selectedOwnerList,
                        businessName,
                        mobile,
                        phone,
                        email,
                        addressArea,
                        latitude,
                        longitude,
                        addressBrief,
                        addressPincode,
                        website,
                        bitmap
                ).execute();

            } else {                      // update


                boolean ownerUpdate = false, businessUpdate = false, contactUpdate = false, imageUpdate = false;

                if (clientNameEditFlag && !client_name.getText().toString().equals(oldClientName)) {
                    ownerUpdate = true;
                    Log.d("TAG", "update : -------  update owner");
                }
                if (businessNameEditFlag && !clientData.getBusinessName().equals(business_name.getText().toString())) {
                    businessUpdate = true;
                    Log.d("TAG", "update : -------  update business table");
                }

                if (contactDataEditFlag) {
                    contactUpdate = true;
                    Log.d("TAG", "update : ------- update contact data");
                }
                if (imageEdit) {
                    imageUpdate = true;
                    Log.d("TAG", "update : ------- update image");
                }


                if (ownerUpdate || businessUpdate || contactUpdate || imageUpdate) {

                    new UpdateDataTask(
                            getActivity(),
                            db,
                            ownerUpdate, businessUpdate, contactUpdate, imageUpdate,
                            clientData.getBusinessId(), selectedOwnerList,
                            business_name.getText().toString(),
                            mobile, phone, email,
                            addressArea, latitude, longitude,
                            addressBrief, addressPincode, website,
                            bitmap
                    ).execute();
                } else {
                    Log.d("TAG", "update : Nothing to update");
                }

            }// update mode
        }
    }

    private void getImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
    }

    public void populateData(final ClientData clientData) {

        oldClientName = DbUtil.buildOwnerNameStringFromList(clientData);
        client_name.setText(oldClientName);
        business_name.setText(clientData.getBusinessName());
        website_edit.setText(clientData.getWebsite());
        mobile_edit.setText(clientData.getMobile());
        phone_edit.setText(clientData.getPhone());
        email_edit.setText(clientData.getEmail());
        address_area.setText(clientData.getAddressArea());
        latitude = clientData.getLatitude().substring(0, Math.min(clientData.getLatitude().length(), 8));
        longitude = clientData.getLongitude().substring(0, Math.min(clientData.getLongitude().length(), 8));
        choose_location.setText(latitude + ", " + longitude);
        address_brief.setText(clientData.getAddressBrief());
        address_pincode.setText(clientData.getAddressPincode());
//        image.setImageBitmap(clientData.getBitmap());
//

        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] imageByte = clientData.getImage();
                if (imageByte != null) {
                    bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (image != null) {
                                image.setImageBitmap(bitmap);
                                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }
                            if (getActivity() != null)
                                disableEdit();      // client_name amd loc disable clickable is not working so
                        }
                    });

                }
            }
        }).start();

        //reset edit flags because of populate
        clientNameEditFlag = false;
        businessNameEditFlag = false;
        contactDataEditFlag = false;
    }

    private void showVisitListFragment() {
        try {
            VisitDetailListFragment visitDetailListFragment = new VisitDetailListFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, visitDetailListFragment, "visitDetailListFragment");
            Bundle bundle = new Bundle();
            bundle.putString("id", String.valueOf(clientData.getBusinessId()));
            bundle.putString("name", clientData.getBusinessName());
            visitDetailListFragment.setArguments(bundle);
            fragmentTransaction.addToBackStack("visitDetailListFragment");
            fragmentTransaction.commit();
        } catch (Exception e) {
            Log.d("TAG", "showVisitListFragment: exp " + e.getMessage());
        }
    }

    private static void showClientListFragment() {
        try {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ClientListFragment clientListFragment = new ClientListFragment();
            fragmentTransaction.replace(R.id.container, clientListFragment, "clientListFragment");
            fragmentTransaction.addToBackStack("visitDetailListFragment");
            fragmentTransaction.commit();
        } catch (Exception e) {
            Log.d("TAG", "showVisitListFragment: exp " + e.getMessage());
        }
    }


    // Image

    void showDialogImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action").setItems(new String[]{"Take Photo", "Choose Photo"}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        openCameraIntent();
                    } else {
                        ((NavigationActivity) getActivity()).requestRunTimePermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSITION_REQUEST);
                    }


                } else if (which == 1) {
                    getImageFromGallery();
                }
            }
        });
        builder.show();
    }

    public void openCameraIntent() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, OPEN_CAMERA_INTENT_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                image.setImageBitmap(bitmap);
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageEdit = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (requestCode == PLACE_PICKER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                String toastMsg = String.format("Place: %s", place.getName());
                Log.d("TAG", "getLatLng: getLatLng " + place.getLatLng());
                Log.d("TAG", "onActivityResult: getAddress " + place.getAddress());
                Log.d("TAG", "onActivityResult: id " + place.getId());
                Log.d("TAG", "onActivityResult: getName " + place.getName());


                if (place != null) {
                    latitude = String.valueOf(place.getLatLng().latitude);
                    longitude = String.valueOf(place.getLatLng().longitude);

                    String lats = latitude.substring(0, Math.min(latitude.length(), 8));  // show 8 char lat/lng
                    String lngs = longitude.substring(0, Math.min(longitude.length(), 8));
                    choose_location.setText(lats + ", " + lngs);
                    address_brief.setText(String.valueOf(place.getAddress()));
                }


            }
        }

        if (requestCode == OPEN_CAMERA_INTENT_REQUEST_CODE) {
            if (data != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null)
                    image.setImageBitmap(bitmap);
            }
        }

        if (requestCode == SELECT_OWNER_ACTIVITY_CODE && resultCode == SELECT_OWNER_ACTIVITY_CODE) {

            selectedOwnerList = (List<OwnerEntity>) data.getSerializableExtra("owners");

            Log.d("TAG", "onActivityResult: " + selectedOwnerList);
            Log.d("TAG", "onActivityResult: " + selectedOwnerList.size());

            StringBuilder owners = new StringBuilder();
            for (int i = 0; i < selectedOwnerList.size(); i++) {
                if (i == 0)
                    owners.append(selectedOwnerList.get(i).getOwnerName());
                else
                    owners.append(", " + selectedOwnerList.get(i).getOwnerName());
            }

            client_name.setText(owners.toString());
            if (UPDATE_MODE) {
                clientData.setOwnerList(selectedOwnerList);
            } else
                TempOwnerListSelected = selectedOwnerList;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_PERMISSITION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCameraIntent();
        }
    }


    //              AsyncTasks


    static class DeleteBusinessLessOwner extends AsyncTask<Void, Void, Void> {

        AppDatabase db;

        DeleteBusinessLessOwner(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.ownerDao().deleteBusinessLessOwners();
            return null;
        }
    }

    static class SaveClientTask extends AsyncTask<Void, Void, Boolean> {

        Context context;
        AppDatabase db;

        List<OwnerEntity> selectedOwnerList;
        String businessName, mobile, phone, email, addressArea, latitude, longitude, addressBrief, addressPincode, website;
        Bitmap bitmap;

        public SaveClientTask(Context context, AppDatabase db, List<OwnerEntity> selectedOwnerList, String businessName, String mobile, String phone, String email, String addressArea, String latitude, String longitude, String addressBrief, String addressPincode, String website, Bitmap bitmap) {
            this.context = context;
            this.db = db;
            this.selectedOwnerList = selectedOwnerList;
            this.businessName = businessName;
            this.mobile = mobile;
            this.phone = phone;
            this.email = email;
            this.addressArea = addressArea;
            this.latitude = latitude;
            this.longitude = longitude;
            this.addressBrief = addressBrief;
            this.addressPincode = addressPincode;
            this.website = website;
            this.bitmap = bitmap;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                bitmap = getResizedBitmap(bitmap, 500);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

//                this bitmap obj is use to show image so not recycling
//                if (bitmap != null && !bitmap.isRecycled()) {
//                    bitmap.recycle();
//                    bitmap = null;
//                }

                db.beginTransaction();
                long bid = db.businessDao().insertBusinessData(new BusinessEntity(0, businessName));

//                long oid = db.ownerDao().insertOwnerData(new OwnerEntity(0, clientName));
                for (int i = 0; i < selectedOwnerList.size(); i++) {

                    db.businessOwnerDao().insertBusinessOwnerData(new BusinessOwnerEntity((int) bid, selectedOwnerList.get(i).getOid()));
                }

                //test rollback
                // throw exception to check roll back functionality
//                int op=8/0;

                db.contactDao().insertContactData(new ContactEntity(0, (int) bid, mobile, phone, email, addressArea, latitude, longitude, addressBrief, addressPincode, website, byteArray));
                db.setTransactionSuccessful();
                return true;

            } catch (Exception e) {
                Log.d("KUNAL EXC", "Exp inserting data :" + e.getMessage());



            } finally {
                db.endTransaction();


                //test rollback
//                List<BusinessEntity> allBusinessData = db.businessDao().getAllBusinessData();
//                for(BusinessEntity businessEntity: allBusinessData){
//                    Log.d("TAG", "doInBackground: BusinessEntity data "+businessEntity);
//                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                Toast.makeText(context, "successfully save!", Toast.LENGTH_SHORT).show();
                new DeleteBusinessLessOwner(db).execute();
                showClientListFragment();
            } else
                Toast.makeText(context, "Fail to save data!", Toast.LENGTH_SHORT).show();

        }
    }


    static class UpdateDataTask extends AsyncTask<Void, Void, Boolean> {
        Context context;
        AppDatabase db;
        boolean ownerUpdate, businessUpdate, contactUpdate, imageUpdate;

        int bid;
        List<OwnerEntity> ownerEntities;

        String NewBusinessName;


        String mobile, phone, email, addressArea, latitude, longitude, addressBrief, addressPincode, website;
        Bitmap bitmap;


        UpdateDataTask(Context context, AppDatabase db, boolean ownerUpdate, boolean businessUpdate, boolean contactUpdate, boolean imageUpdate, int bid, List<OwnerEntity> ownerEntities, String newBusinessName, String mobile, String phone, String email, String addressArea, String latitude, String longitude, String addressBrief, String addressPincode, String website, Bitmap bitmap) {
            this.context = context;
            this.db = db;
            this.ownerUpdate = ownerUpdate;
            this.businessUpdate = businessUpdate;
            this.contactUpdate = contactUpdate;
            this.imageUpdate = imageUpdate;
            this.bid = bid;
            this.ownerEntities = ownerEntities;
            this.NewBusinessName = newBusinessName;
            this.mobile = mobile;
            this.phone = phone;
            this.email = email;
            this.addressArea = addressArea;
            this.latitude = latitude;
            this.longitude = longitude;
            this.addressBrief = addressBrief;
            this.addressPincode = addressPincode;
            this.website = website;
            this.bitmap = bitmap;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                db.beginTransaction();


                if (ownerUpdate) {                                                   // Owner
                    db.businessOwnerDao().deleteAllByBusinessId(bid);

                    for (int i = 0; i < ownerEntities.size(); i++) {
                        db.businessOwnerDao().insertBusinessOwnerData(new BusinessOwnerEntity(bid, ownerEntities.get(i).getOid()));
                    }
                }
                if (businessUpdate) {                                                // Business
                    db.businessDao().updateBusinessName(NewBusinessName, bid);
                }
                if (imageUpdate) {                                                    // Image

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
//                    if (bitmap != null && !bitmap.isRecycled()) {
//                        bitmap.recycle();
//                        bitmap = null;
//                    }
                    db.contactDao().updateImage(bid, byteArray);
                }
                if (contactUpdate) {                                                  // contact

                    db.contactDao().updateContact(bid,
                            mobile, phone, email,
                            addressArea,
                            latitude, longitude,
                            addressBrief, addressPincode, website);

                }


                db.setTransactionSuccessful();
                return true;

            } catch (Exception e) {
                Log.d("TAG", "UPDATE EXCEPTION ----- : " + e.getMessage());
            } finally {
                db.endTransaction();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Toast.makeText(context, "Update data successfully", Toast.LENGTH_SHORT).show();
                new DeleteBusinessLessOwner(db).execute();
            } else
                Toast.makeText(context, "Fail to update Data", Toast.LENGTH_SHORT).show();
        }
    }


//    class UpdateClientDataTack extends AsyncTask<Void, Void, Void> {
//
//        int businessID;
//        String businessName, mobile, phone, email, addressArea, latitude, longitude, addressBrief, addressPincode, website;
//
//        public UpdateClientDataTack(int businessID, String businessName, String mobile, String phone, String email, String addressArea, String latitude, String longitude, String addressBrief, String addressPincode, String website) {
//            this.businessID = businessID;
//            this.businessName = businessName;
//            this.mobile = mobile;
//            this.phone = phone;
//            this.email = email;
//            this.addressArea = addressArea;
//            this.latitude = latitude;
//            this.longitude = longitude;
//            this.addressBrief = addressBrief;
//            this.addressPincode = addressPincode;
//            this.website = website;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            try {
//
//                db.contactDao().updateContact(businessID,
//                        mobile, phone, email,
//                        addressArea,
//                        latitude, longitude,
//                        addressBrief, addressPincode, website);
//
//                Log.d("TAG", "doInBackground: SuccessFully update contact data");
//            } catch (Exception e) {
//                Log.d("TAG", "doInBackground: Fail to update contact data");
//            }
//
//            return null;
//        }
//    }
//
//
//    class updateOwnersTask extends AsyncTask<Void, Void, Boolean> {
//        int bid;
//        List<OwnerEntity> ownerEntities;
//        boolean doneflag;
//
//        public updateOwnersTask(int bid, List<OwnerEntity> ownerEntities) {
//            this.bid = bid;
//            this.ownerEntities = ownerEntities;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//            try {
//
//                db.beginTransaction();
//                db.businessOwnerDao().deleteAllByBusinessId(bid);
//
//                for (int i = 0; i < ownerEntities.size(); i++) {
//                    db.businessOwnerDao().insertBusinessOwnerData(new BusinessOwnerEntity(bid, ownerEntities.get(i).getOid()));
//                }
//                db.setTransactionSuccessful();
//                doneflag = true;
//            } catch (Exception e) {
//                Log.d("TAG", "update owner EXP: " + e.getMessage());
//                doneflag = false;
//            } finally {
//                db.endTransaction();
//            }
//
//            return doneflag;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean aBoolean) {
//            if (aBoolean)
//                Toast.makeText(context, "Update Successfully", Toast.LENGTH_SHORT).show();
//            else
//                Toast.makeText(context, "Fail update data", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    class updateBusinessDataTask extends AsyncTask<Void, Void, Void> {
//
//        String NewBusinessName;
//        int bid;
//
//        public updateBusinessDataTask(String newBusinessName, int bid) {
//            NewBusinessName = newBusinessName;
//            this.bid = bid;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            try {
//                db.businessDao().updateBusinessName(NewBusinessName, bid);
//                Log.d("TAG", "update successfully business name ");
//            } catch (Exception e) {
//                Log.d("TAG", "Fail to update business name ");
//            }
//            return null;
//        }
//    }
//
//    class updateImageTask extends AsyncTask<Void, Void, Void> {
//
//        int bid;
//        Bitmap bitmap;
//
//        public updateImageTask(int bid, Bitmap bitmap) {
//            this.bid = bid;
//            this.bitmap = bitmap;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            try {
//
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();
//                bitmap.recycle();
//
//                db.contactDao().updateImage(bid, byteArray);
//                Log.d("TAG", "Image updated successfully");
//
//            } catch (Exception e) {
//                Log.d("TAG", "fail to update image");
//            }
//
//
//            return null;
//        }
//    }
//

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


}
