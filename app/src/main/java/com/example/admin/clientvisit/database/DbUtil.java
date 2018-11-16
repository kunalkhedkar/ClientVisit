package com.example.admin.clientvisit.database;

import com.example.admin.clientvisit.model.ClientData;

import java.util.List;

public class DbUtil {

    public static String buildOwnerNameStringFromList(ClientData clientData) {
        String owners = "";
        if (clientData != null) {
            for (int i = 0; i < clientData.getOwnerList().size(); i++) {
                if (i == 0)
                    owners = clientData.getOwnerList().get(i).getOwnerName();
                else
                    owners = owners + ", " + clientData.getOwnerList().get(i).getOwnerName();
            }
        }
        return owners;
    }

    public static String buildOwnerNameStringFromList(List<OwnerEntity> ownerEntities) {
        String owners = "";
        if (ownerEntities != null) {
            for (int i = 0; i < ownerEntities.size(); i++) {
                if (i == 0)
                    owners = ownerEntities.get(i).getOwnerName();
                else
                    owners = owners + ", " + ownerEntities.get(i).getOwnerName();
            }
        }
        return owners;
    }
}