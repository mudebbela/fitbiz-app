package com.mudebbela.fitbiz;

public class FitbizUtils {

    public static boolean allFilled( String ... d){

        for (String s: d)if(s.trim().isEmpty()) return false;
        return true;
    }
}
