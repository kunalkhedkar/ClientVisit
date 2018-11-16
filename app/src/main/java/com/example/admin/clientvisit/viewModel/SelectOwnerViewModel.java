package com.example.admin.clientvisit.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.admin.clientvisit.database.AppDatabase;
import com.example.admin.clientvisit.database.BusinessEntity;
import com.example.admin.clientvisit.database.OwnerEntity;
import com.example.admin.clientvisit.model.SelectOwnerData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectOwnerViewModel extends AndroidViewModel {

    private MutableLiveData<List<SelectOwnerData>> selectOwnerMutableLiveData;
    private List<SelectOwnerData> SELECT_OWNER_DATA_LIST = new ArrayList<>();
    private AppDatabase appDatabase;

    public SelectOwnerViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getInstance(application);
        selectOwnerMutableLiveData = new MutableLiveData<>();
        new getOwnerBusinessdataTask().execute();
    }

    public LiveData<List<SelectOwnerData>> getOwnerListWithBusiness() {

        if (selectOwnerMutableLiveData == null)
            new getOwnerBusinessdataTask().execute();

        return selectOwnerMutableLiveData;
    }

    public void addToOwner(OwnerEntity ownerEntity) {
        new AddToOwnerTask(appDatabase,ownerEntity).execute();
        new getOwnerBusinessdataTask().execute();     // refreshList
    }

    class AddToOwnerTask extends AsyncTask<Void, Void, Void> {
        AppDatabase appDatabase;
        OwnerEntity ownerEntity;

         AddToOwnerTask(AppDatabase appDatabase, OwnerEntity ownerEntity) {
            this.appDatabase = appDatabase;
            this.ownerEntity = ownerEntity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            long id = appDatabase.ownerDao().insertOwnerData(ownerEntity);
            Log.d("TAG", "doInBackground: owner added " + id);
            return null;
        }
    }

    class getOwnerBusinessdataTask extends AsyncTask<Void, Void, Void> {

        List<OwnerEntity> ownerEntities;

        @Override
        protected Void doInBackground(Void... voids) {
            SELECT_OWNER_DATA_LIST.clear();
            ownerEntities = appDatabase.ownerDao().getAllOwnerData();


            for (int i = 0; i < ownerEntities.size(); i++) {
                String businessNames = "";
                List<BusinessEntity> businessEntities = appDatabase.businessOwnerDao().getBusinessDatabyOwnerId(ownerEntities.get(i).getOid());
                if (businessEntities.size() != 0) {
                    businessNames = getBusinessNameStringFromBusinessList(businessEntities);
                }

                SELECT_OWNER_DATA_LIST.add(new SelectOwnerData(ownerEntities.get(i).getOid(), ownerEntities.get(i).getOwnerName(), businessNames, false));

            }
            Collections.reverse(SELECT_OWNER_DATA_LIST);
            selectOwnerMutableLiveData.postValue(SELECT_OWNER_DATA_LIST);
            return null;
        }
    }

    private String getBusinessNameStringFromBusinessList(List<BusinessEntity> businessEntities) {
        String businessNames = "";

        for (int i = 0; i < businessEntities.size(); i++) {
            if (i == 0)
                businessNames = businessEntities.get(i).getBusinessName();
            else
                businessNames = businessNames + ", " + businessEntities.get(i).getBusinessName();
        }

        return businessNames;

    }

    public void markStatusTrue(List<OwnerEntity> ownerEntities) {

        for (int i = 0; i < ownerEntities.size(); i++) {
            for (int j = 0; j < SELECT_OWNER_DATA_LIST.size(); j++) {
                if (ownerEntities.get(i).getOid() == SELECT_OWNER_DATA_LIST.get(j).getOid()) {
                    SELECT_OWNER_DATA_LIST.get(j).setStatus(true);
                }
            }
        }

        selectOwnerMutableLiveData.setValue(SELECT_OWNER_DATA_LIST);
    }

//    public List<SelectOwnerData> markStatusTrue1(List<OwnerEntity> ownerEntities , List<SelectOwnerData> selectOwnerDataChanged){
//
//        for (int i = 0; i < ownerEntities.size(); i++) {
//            for (int j = 0; j < selectOwnerDataChanged.size(); j++) {
//                if(ownerEntities.get(i).getOid()==selectOwnerDataChanged.get(j).getOid()){
//                    selectOwnerDataChanged.get(j).setStatus(true);
//                }
//            }
//        }
//
//        return selectOwnerDataChanged;
//    }


//    class GetOwnerListWithBusinessTask extends AsyncTask<Void, Void, Void> {
//
//        List<SelectOwnerData> SELECT_OWNER_DATA_LIST;
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            // contains duplicate owner
//            SELECT_OWNER_DATA_LIST = appDatabase.businessOwnerDao().getAllOwnerAndBusiness();
//            List<SelectOwnerData> SELECT_OWNER_DATA_LISTDistinct = new ArrayList<>();
//
//            for (int i = 0; i < SELECT_OWNER_DATA_LIST.size(); i++) {
//                String businessNameString = SELECT_OWNER_DATA_LIST.get(i).getBusinessName();
//                for (int j = i + 1; j < SELECT_OWNER_DATA_LIST.size(); j++) {
//                    if (SELECT_OWNER_DATA_LIST.get(i).getOid() == SELECT_OWNER_DATA_LIST.get(i).getOid())
//                        businessNameString = businessNameString + ", " + SELECT_OWNER_DATA_LIST.get(i).getOid();
//
//                }
//                SELECT_OWNER_DATA_LISTDistinct.add(new SelectOwnerData(SELECT_OWNER_DATA_LIST.get(i).getOid(), SELECT_OWNER_DATA_LIST.get(i).getBid(), SELECT_OWNER_DATA_LIST.get(i).getOwnerName(), SELECT_OWNER_DATA_LIST.get(i).getBusinessName()));
//            }
//            selectOwnerMutableLiveData.postValue(SELECT_OWNER_DATA_LISTDistinct);
//            Log.d("TAG", "doInBackground: " + SELECT_OWNER_DATA_LISTDistinct);
//            return null;
//        }
//    }

}
