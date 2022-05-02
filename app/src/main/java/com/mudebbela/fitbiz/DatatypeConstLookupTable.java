package com.mudebbela.fitbiz;

import android.util.Log;

import java.util.HashMap;

public class DatatypeConstLookupTable {
    private static final HashMap<String, String> namesMap =  new HashMap<>();



    public void init(){
        if(namesMap.containsKey("init")) return;
        namesMap.put("com.google.step_count.delta", "TYPE_STEP_COUNT_DELTA");
        namesMap.put("com.google.step_count.cumulative", "TYPE_STEP_COUNT_CUMULATIVE");
        namesMap.put("com.google.step_count.cadence", "TYPE_STEP_COUNT_CADENCE");
        namesMap.put("com.google.internal.goal", "zzmd");
        namesMap.put("com.google.activity.segment", "TYPE_ACTIVITY_SEGMENT");
        namesMap.put("com.google.sleep.segment", "TYPE_SLEEP_SEGMENT");
        namesMap.put("com.google.calories.expended", "TYPE_CALORIES_EXPENDED");
        namesMap.put("com.google.calories.bmr", "TYPE_BASAL_METABOLIC_RATE");
        namesMap.put("com.google.power.sample", "TYPE_POWER_SAMPLE");
        namesMap.put("com.google.sensor.events", "zzme");
        namesMap.put("com.google.heart_rate.bpm", "TYPE_HEART_RATE_BPM");
        namesMap.put("com.google.respiratory_rate", "zzmf");
        namesMap.put("com.google.location.sample", "TYPE_LOCATION_SAMPLE");
        namesMap.put("com.google.location.track", "TYPE_LOCATION_TRACK");
        namesMap.put("com.google.distance.delta", "TYPE_DISTANCE_DELTA");
        namesMap.put("com.google.distance.cumulative", "zzmg");
        namesMap.put("com.google.speed", "TYPE_SPEED");
        namesMap.put("com.google.cycling.wheel_revolution.cumulative", "TYPE_CYCLING_WHEEL_REVOLUTION");
        namesMap.put("com.google.cycling.wheel_revolution.rpm", "TYPE_CYCLING_WHEEL_RPM");
        namesMap.put("com.google.cycling.pedaling.cumulative", "TYPE_CYCLING_PEDALING_CUMULATIVE");
        namesMap.put("com.google.cycling.pedaling.cadence", "TYPE_CYCLING_PEDALING_CADENCE");
        namesMap.put("com.google.height", "TYPE_HEIGHT");
        namesMap.put("com.google.weight", "TYPE_WEIGHT");
        namesMap.put("com.google.body.fat.percentage", "TYPE_BODY_FAT_PERCENTAGE");
        namesMap.put("com.google.nutrition", "TYPE_NUTRITION");
        namesMap.put("com.google.hydration", "TYPE_HYDRATION");
        namesMap.put("com.google.activity.exercise", "TYPE_WORKOUT_EXERCISE");
        namesMap.put("com.google.active_minutes", "TYPE_MOVE_MINUTES");
        namesMap.put("com.google.device_on_body", "zzmh");
        namesMap.put("com.google.internal.primary_device", "zzmi");
        namesMap.put("com.google.activity.summary", "AGGREGATE_ACTIVITY_SUMMARY");
        namesMap.put("com.google.calories.bmr.summary", "AGGREGATE_BASAL_METABOLIC_RATE_SUMMARY");
        namesMap.put("com.google.heart_minutes", "TYPE_HEART_POINTS");
        namesMap.put("com.google.heart_minutes.summary", "AGGREGATE_HEART_POINTS");
        namesMap.put("com.google.heart_rate.summary", "AGGREGATE_HEART_RATE_SUMMARY");
        namesMap.put("com.google.location.bounding_box", "AGGREGATE_LOCATION_BOUNDING_BOX");
        namesMap.put("com.google.power.summary", "AGGREGATE_POWER_SUMMARY");
        namesMap.put("com.google.speed.summary", "AGGREGATE_SPEED_SUMMARY");
        namesMap.put("com.google.body.fat.percentage.summary", "AGGREGATE_BODY_FAT_PERCENTAGE_SUMMARY");
        namesMap.put("com.google.weight.summary", "AGGREGATE_WEIGHT_SUMMARY");
        namesMap.put("com.google.height.summary", "AGGREGATE_HEIGHT_SUMMARY");
        namesMap.put("com.google.nutrition.summary", "AGGREGATE_NUTRITION_SUMMARY");
        namesMap.put("init", "true");
    }

    public String get(String name){
        if(namesMap.containsKey(name)) return namesMap.get(name);
        Log.d("TAG", "get: names map does not contain"  +name);
        return name;
    }
}
