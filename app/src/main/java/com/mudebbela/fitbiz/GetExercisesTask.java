package com.mudebbela.fitbiz;

import android.os.AsyncTask;
import android.util.Log;

import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.tuples.generated.Tuple4;

import java.math.BigInteger;
import java.util.List;

public class GetExercisesTask extends AsyncTask<RemoteFunctionCall<Tuple4<List<BigInteger>, List<BigInteger>, List<String>, List<String>>>, Integer, Long>  {

    @Override
    protected Long doInBackground(RemoteFunctionCall<Tuple4<List<BigInteger>, List<BigInteger>, List<String>, List<String>>>... remoteFunctionCalls) {
        for (RemoteCall<Tuple4<List<BigInteger>, List<BigInteger>, List<String>, List<String>>> rc: remoteFunctionCalls) {
            try {
                Tuple4<List<BigInteger>, List<BigInteger>, List<String>, List<String>> resp = rc.send();

                Log.d("GetExcersisesTask", "doInBackground: "+ resp.component1().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}
