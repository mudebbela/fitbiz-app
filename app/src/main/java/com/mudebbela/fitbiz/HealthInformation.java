package com.mudebbela.fitbiz;

import java.util.Date;
import java.util.HashMap;

public class HealthInformation {
    String activity;
    Date startDate;
    Date endDate;
    HashMap<String, HashMap<String, String>> dataTypes;

    public HealthInformation() {
        dataTypes =  new HashMap<>();
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void addData(String field, HashMap<String, String> value){
        dataTypes.put(field, value);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
