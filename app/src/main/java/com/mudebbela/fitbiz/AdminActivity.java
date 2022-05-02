package com.mudebbela.fitbiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminActivity extends AppCompatActivity {
    private Button btnDeployContract;
    private ECKeyPair keyPair;
    private final String TAG = "AdminActivity";
    private Credentials credentials;
    private OkHttpClient client;
    private File keyFile;
    private Admin web3Admin;

//this will be the admin acivity
//    the admin should be able to deploy the smart contract
//    Once deployed the admin should update the address of the deployed contract in firebase
//    admin should be able to generate Exercise reports of all the users
//    Link on how to connect to not localhost using rpc
//    https://github.com/web3j/web3j/issues/382
//    104.248.126.56
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setupBouncyCastle();

        client  =  new OkHttpClient();

        Web3j web3 = Web3j.build(new HttpService("http://104.248.126.56:8543/"));
        web3Admin = Admin.build(new HttpService("http://104.248.126.56:8543/"));

        
//        Deploy contract button
        btnDeployContract = findViewById(R.id.buttonDeployContract);
        btnDeployContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TODO deploy smart contract
                        ClientTransactionManager ctm = new ClientTransactionManager(web3,"0xf34974575c66ebfff6d5abcea2e2233ccfe20772");
                        deployContract(ctm, web3);
            }
        });


        Button btnGenerateKeys = findViewById(R.id.buttonSetKeys);
        btnGenerateKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(credentials == null){
                    Log.d(TAG, "credentials: getting credentials");
//                    TODO do in the backend as its a bit intensive
                    credentials = getCredentials();

                    credentials.getEcKeyPair().toString();
                    Log.d(TAG, "credentials: DONE "+credentials.getAddress());

                }
            }
        });

//        Setup send keys button

        Button btn =  findViewById(R.id.buttonSendKeys);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sendKeysUrl = "http://104.248.126.56:8000/keys";
                String keyData = "";
                StringBuilder sb =  new StringBuilder();
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(keyFile.toString()));
                    String line;
                    while((line = reader.readLine()) != null){

                        sb.append(line);
                    }
                    reader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Runnable runner = () -> {
                    String json = sb.toString();
                    Log.d(TAG, "onClick: JSON "+ json);
                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
                    post(sendKeysUrl, body);
                };

                Thread t1 =  new Thread(runner);

                t1.start();

                Log.d(TAG, "onClick: Keys sent");
            }
        });

        Button btnSignOutAdmin =  findViewById(R.id.buttonSignOutAdmin);
        btnSignOutAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });







    }
//https://stackoverflow.com/questions/32244851/androidjava-lang-outofmemoryerror-failed-to-allocate-a-23970828-byte-allocatio
//    https://stackoverflow.com/questions/55123228/java-security-nosuchalgorithmexception-no-such-algorithm-ecdsa-for-provider-bc
    private Credentials getCredentials() {
        String password="";
        keyFile = null;
        try {
            password = "password";

            File keysDir = new File(getFilesDir(), FitbizConstants.WALLET_FILE_DIRECTORY);

            recreateDir(keysDir);
            String filename = WalletUtils.generateNewWalletFile(password,keysDir);


            Log.d(TAG, "getCredentials: wallet filename: "+ filename);

            keyFile = new File(keysDir, filename);
//            todo, send keys to server and import them using geth
//            https://geth.ethereum.org/docs/interface/managing-your-accounts#:~:text=Creating%20an%20account%20by%20importing%20a%20private%20key
            return WalletUtils.loadCredentials(password, keyFile.getPath());


        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void recreateDir(File dir) {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }
        dir.delete();
        dir.mkdirs();
    }

    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            // Web3j will set up the provider lazily when it's first used.
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            // BC with same package name, shouldn't happen in real life.
            return;
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    private void deployContract(ClientTransactionManager ctm, Web3j web3){
        Log.d(TAG, "deployContract: Deploying");
        try {
            RemoteCall<Health> rc = Health.deploy(web3, ctm, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
             new DeployContractTask().execute(rc);


        } catch (Exception e) {
             e.printStackTrace();
        }
        Log.d(TAG, "deployContract: Done");

    }

    private String post(String url, RequestBody body){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.d(TAG, "post: "+response);
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    };

}