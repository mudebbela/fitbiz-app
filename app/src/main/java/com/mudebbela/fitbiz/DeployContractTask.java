package com.mudebbela.fitbiz;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.http.HttpService;

import java.net.URL;

public class DeployContractTask extends AsyncTask<RemoteCall<Health>, Integer, Long> {


    @Override
    protected Long doInBackground(RemoteCall<Health>... remoteCalls) {

        for(RemoteCall<Health> rc : remoteCalls){
            try {
                Admin web3Admin;
                web3Admin = Admin.build(new HttpService("http://104.248.126.56:8543/"));

                Log.d("TAG", "doInBackground: unlocking admin account ");
                web3Admin.personalUnlockAccount("0xf34974575c66ebfff6d5abcea2e2233ccfe20772", "password").send();
                Log.d("TAG", "doInBackground: Deploying contract");
                Health contract = rc.send();
                Log.d("TAG", "doInBackground: Done Deploying, new contract address: "+contract.getContractAddress());
//                TODO, send account number to firebase database to firebase

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference().child(FitbizConstants.CONTRACT_ADDRESS).setValue(contract.getContractAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
