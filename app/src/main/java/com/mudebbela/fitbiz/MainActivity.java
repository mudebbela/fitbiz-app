package com.mudebbela.fitbiz;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.web3j.abi.datatypes.Int;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1001;

    // TODO: 3/28/22
    // google firebase authentication flow DONE
    // Integrate google
    // google fit integration

    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> signInLauncher;
    private FitnessOptions fitnessOptions;
    private GoogleSignInAccount account;
    private LinkedList<HealthInformation> healthInformationList;
    private HealthInformationAdapter hiAdapter;
    private FirebaseDatabase database;
    private Admin web3Admin;
    private FirebaseUser user;
    private DatatypeConstLookupTable lookupTable;
    private Intent signInIntent;
    private final String[] contractAddress = {""};
    private final String[] currentUserAddress = {""};
    private ActivityResultLauncher<Intent> createUserLauncher;
    private Health contract;
    private Web3j web3;
    private String adminAccount;
//    private Health cs;

    @Override
    protected void onStart() {
        super.onStart();
        createUserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
//                        TODO get Nam, age sex, password and create the user using it on return
                        Intent data = result.getData();

                        String name =  data.getStringExtra(FitbizConstants.NAME);
                        String age  =  data.getStringExtra(FitbizConstants.AGE);
                        String password =  data.getStringExtra(FitbizConstants.PASSWORD);
                        String sex  =   data.getStringExtra(FitbizConstants.SEX);




                        Runnable createAcc =  new Runnable() {
                            @Override
                            public void run() {
                                NewAccountIdentifier newAccount = null;

                                try {

                                    newAccount = web3Admin.personalNewAccount(password).send();
                                    String acc = newAccount.getAccountId();


                                    HashMap<String , String > info =  new HashMap<>();

                                    info.put(FitbizConstants.NAME, name);
                                    info.put(FitbizConstants.AGE, age);
                                    info.put(FitbizConstants.SEX, sex);
                                    Boolean has_address = false;
                                    Boolean in_contract = false;

                                    database.getReference(FitbizConstants.ADMIN_STRING)
                                            .child(FitbizConstants.ADDRESS)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String adminAcc = (String) snapshot.getValue();
                                                    Log.d("TAG", "createUser: new account: " +acc);
                                                    DatabaseReference userRef = database.getReference(FitbizConstants.EMPLOYEES_STRING).child(user.getUid());
                                                    userRef.child(FitbizConstants.ADDRESS).setValue(acc);
                                                    userRef.child(FitbizConstants.NAME).setValue(name);
                                                    userRef.child(FitbizConstants.AGE).setValue(age);
                                                    userRef.child(FitbizConstants.SEX).setValue(sex);
                                                    userRef.child(FitbizConstants.PASSWORD).setValue(password);
                                                    userRef.child(FitbizConstants.ADDRESS).setValue(acc);
                                                    userRef.child(FitbizConstants.IN_CONTRACT).setValue(false);
                                                    userRef.child(FitbizConstants.HAS_ADDRESS).setValue(true);

                                                    loadContractThenCreateUser(acc, info, password, adminAcc, true, false);



                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                        };

                        Thread t1 =  new Thread(createAcc);

                        t1.start();

                    }
                }
        );

    }

    private void loadContractThenCreateUser(String userAddress, HashMap<String, String> info, String password, String adminAcc, boolean has_address, boolean in_contract) {

        Log.d("TAG", "loadContractThenCreateUser: " +userAddress +"," +adminAcc);

        database.getReference()
                .child(FitbizConstants.CONTRACT_ADDRESS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("TAG", "onDataChange: "+snapshot.getValue());
                        if(snapshot.exists()){
                            String contractAddress = (String) snapshot.getValue();



                            Runnable r1 = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Log.d("Runnable r1", "run: Creating employee");
                                        web3Admin.personalUnlockAccount(userAddress, password).send();
                                        web3Admin.personalUnlockAccount(adminAcc, "password").send();

                                        Log.d("Runnable r1", "run: Employee Created");

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };

                            Runnable r2 =  new Runnable() {
                                @Override
                                public void run() {
                                    try {
//                                                        https://medium.datadriveninvestor.com/blockchain-using-java-part-iii-transaction-of-ethers-from-one-account-to-another-using-web3j-861c39e7a5e1
//                                                        https://ethereum.stackexchange.com/questions/31138/unable-to-retrieve-value-from-web3j-sendfunds-future

                                        String account = adminAcc;

                                        web3Admin.personalUnlockAccount(account, "password").send();
                                        ClientTransactionManager ctm =  new ClientTransactionManager(web3, account);


                                        Transfer tran = new Transfer(web3, ctm);

                                        tran.sendFunds(
                                                userAddress,
                                                BigDecimal.valueOf(1.0),
                                                Convert.Unit.ETHER
                                        ).send();


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            };

                            Runnable r3 =  new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("loadContractThenCreateUser", "onDataChange: Loading contract");

                                    ClientTransactionManager ctm = new ClientTransactionManager(web3, userAddress);
                                    contract = Health.load(contractAddress, web3, ctm, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

                                    Log.d("loadContractThenCreateUser", "onDataChange: Creating employee");
                                    try {
                                        web3Admin.personalUnlockAccount(userAddress, password).send();
                                        String safeInfo = new Gson().toJson(info)
                                                .replace("\"", "DoubleQuote")
                                                .replace("{", "\\LeftB")
                                                .replace("}", "\\RightB")
                                                .replace(":", "\\Colon")
                                                .replace(",", "\\comma");

                                        Log.d("r3", "run: GSON: " +info.get(FitbizConstants.NAME));
                                        TransactionReceipt result = contract.createEmployee(info.get(FitbizConstants.NAME)).send();
                                        Log.d("r3", "logs: "+ result.getLogs());
                                        database.getReference(FitbizConstants.EMPLOYEES_STRING).child(userAddress).child(FitbizConstants.IN_CONTRACT).setValue(true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            };

                            SerialExecutor sr =  new SerialExecutor();

                            sr.execute(r1);
                            sr.execute(r2);
                            sr.execute(r3);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void createUser() {
        Intent startCreateUser =  new Intent(this, CreateEmployeeActivity.class);
        createUserLauncher.launch(startCreateUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkUserCredentials();



        RecyclerView rvHealthInfo = findViewById(R.id.recyclerView);
        healthInformationList = new LinkedList<>();
        hiAdapter = new HealthInformationAdapter(healthInformationList, getApplicationContext());

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvHealthInfo.setHasFixedSize(true);
        rvHealthInfo.setAdapter(hiAdapter);
        rvHealthInfo.setLayoutManager(mLinearLayoutManager);



        web3Admin = Admin.build(new HttpService("http://104.248.126.56:8543/"));
        web3 = Web3j.build(new HttpService("http://104.248.126.56:8543/"));





        Button btnSignIn = findViewById(R.id.buttonSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    Toast.makeText(getApplicationContext(), "Already signed in", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getApplicationContext(), "Signing In", Toast.LENGTH_SHORT).show();
                signInLauncher.launch(signInIntent);

            }
        });

        Button btnSignOut = findViewById(R.id.buttonSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                if (mAuth.getCurrentUser() == null) {
                    Toast.makeText(getApplicationContext(), "Signed out, Bye", Toast.LENGTH_SHORT).show();
                    Log.d("btnSignOut", "onClick: user signed out successfully");
                    finish();
                }


            }
        });

        Button btnGetData =  findViewById(R.id.buttonGetData);
        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFitnessData();
            }
        });

        //Setup fitness information now

        fitnessOptions =  getFitnessOptions();

        account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
                    account,
                    fitnessOptions);
        }

        database.getReference()
                .child(FitbizConstants.CONTRACT_ADDRESS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("TAG", "onDataChange: "+snapshot.getValue());
                        if(snapshot.exists()){
                            String contractAddress = (String) snapshot.getValue();
                            if(mAuth.getUid() ==  null) return;

                            loadContract(contractAddress);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        Button btnGetBlockExercises =  findViewById(R.id.buttonGetBlockExcercises);
        btnGetBlockExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference()
                        .child(FitbizConstants.EMPLOYEES_STRING)
                        .child(user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                currentUserAddress[0] = (String) snapshot.child("address").getValue();

                                try {
//
//                                    ClientTransactionManager ctm = new ClientTransactionManager(web3, currentUserAddress[0]);
//                                    contract = Health.load(contractAddress[0], web3, ctm, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

                                    RemoteFunctionCall<Tuple4<List<BigInteger>, List<BigInteger>, List<String>, List<String>>> excercisesRc = contract.getUserExcersises();
                                    new GetExercisesTask().execute(excercisesRc);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

    }

    private void loadContract(String value) {

        database.getReference()
                .child(FitbizConstants.EMPLOYEES_STRING)
                .child(mAuth.getUid())
                .child(FitbizConstants.ADDRESS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()) return;

                        String userAddress = snapshot.getValue().toString();

                        ClientTransactionManager ctm = new ClientTransactionManager(web3, userAddress);
                        contract = Health.load(value, web3, ctm, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

                        Log.d("Load Contract", "onDataChange: Contract loaded "+ contract.getContractAddress());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void checkUserCredentials() {

//        INIT LOOKUP TABLE
        lookupTable =  new DatatypeConstLookupTable();
        lookupTable.init();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();


        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build());

        signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                    @Override
                    public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                        //TODO get into the application
                        Log.d("Firebase Auth", "onActivityResult: " + result.toString());
//                        Restart activity to get all the check done

                        Intent restartActivity = getIntent();
                        finish();
                        startActivity(restartActivity);


                    }
                }
        );

        // Create and launch sign-in intent
        signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
//                disable smart lock while im debugging
//                https://stackoverflow.com/questions/53072826/how-can-i-disable-smart-lock-for-passwords-auto-login-request-after-firebase-aut
                .setIsSmartLockEnabled(false, true)
                .build();


        if (user == null) {
            Log.d("Main Activity", "onStart: User not signed in");
            signInLauncher.launch(signInIntent);
        } else{
            Toast.makeText(getApplicationContext(), "Hi " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
//            TODO if Admin user, switch to Admin activity
            database.getReference(FitbizConstants.ADMIN_STRING).addListenerForSingleValueEvent(new ValueEventListener() {
                private String userAddress;

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("TAG", "onDataChange: "+ snapshot.getValue());;
                    if(user.getUid().equals(snapshot.child("id").getValue())){
                        Log.d("TAG", "onDataChange: User is admin, going to admin");
                        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    adminAccount = snapshot.child(FitbizConstants.ADDRESS).getValue().toString();

                            database.getReference(FitbizConstants.EMPLOYEES_STRING+"/"+user.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if(!snapshot.exists()){
                                        createUser();
                                    } else if((boolean)snapshot.child("is_admin").getValue()){
                                        return ;

                                    }
                                    else if(!(boolean)snapshot.child(FitbizConstants.HAS_ADDRESS).getValue() || !(boolean)snapshot.child(FitbizConstants.IN_CONTRACT).getValue()){
                                        HashMap<String, String> info = new HashMap<>() ;
                                        info.put(FitbizConstants.NAME, (String)snapshot.child(FitbizConstants.NAME).getValue());
                                        info.put(FitbizConstants.AGE, (String)snapshot.child(FitbizConstants.AGE).getValue());
                                        info.put(FitbizConstants.SEX, (String)snapshot.child(FitbizConstants.SEX).getValue());
                                        String password = snapshot.child(FitbizConstants.PASSWORD).toString();
                                        boolean has_address = (boolean)snapshot.child(FitbizConstants.HAS_ADDRESS).getValue();
                                        boolean in_contract = (boolean)snapshot.child(FitbizConstants.IN_CONTRACT).getValue();

                                        userAddress = snapshot.child(FitbizConstants.ADDRESS).getValue().toString();
                                        loadContractThenCreateUser(userAddress, info, password, adminAccount, has_address,  in_contract );

                                    }else
                                        userAddress = snapshot.child(FitbizConstants.ADDRESS).toString();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    private FitnessOptions getFitnessOptions() {
        return FitnessOptions.builder()
                .addDataType(DataType.TYPE_CALORIES_EXPENDED)
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_HEIGHT_SUMMARY, FitnessOptions.ACCESS_READ)
                .addDataType( DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .build();
    }


    private void getFitnessData() {
//        https://developers.google.com/fit/android/api-client-example
        // Read the data that's been collected throughout the past week.



        ZonedDateTime endTime = LocalDateTime.now().atZone(ZoneId.systemDefault());
        ZonedDateTime startTime = endTime.minusDays(1);


        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_DISTANCE_DELTA)
                .aggregate(DataType.TYPE_SPEED)
                .bucketByActivitySegment(5,TimeUnit.MINUTES)
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .build();


        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                .readData(readRequest)
                .addOnSuccessListener(response -> {
                    // The aggregate query puts datasets into buckets, so convert to a
                    // single list of datasets

                    for (Bucket bucket : response.getBuckets()) {
                        if(bucket.getActivity().equals(FitbizConstants.UNKNOWN)) continue; // skip any health data thats not recognized
                        if(bucket.getActivity().equals(FitbizConstants.IN_VEHICLE)) continue; // being in a vehicle is not exercise!


                        HealthInformation hi = new HealthInformation();
                        hi.setActivity(bucket.getActivity());
                        hi.setStartDate(new Date(bucket.getStartTime(TimeUnit.MILLISECONDS)));
                        hi.setEndDate(new Date(bucket.getEndTime(TimeUnit.MILLISECONDS)));

                        Log.d("TAG", "getFitnessData: activity: " +bucket.getActivity());
                        Log.d("getFitnessData", "\tStart: "+ new Date(bucket.getStartTime(TimeUnit.MILLISECONDS)));
                        Log.d("getFitnessData", "\tEnd: "+ new Date(bucket.getEndTime(TimeUnit.MILLISECONDS)));

                        bucket.getDataSets();
                        for (DataSet dataSet : bucket.getDataSets()) {
                            String dataType = lookupTable.get(dataSet.getDataType().getName());

                            Log.i("dumpDataSet", "Data returned for Data type: "+dataType);
                            for (DataPoint dp : dataSet.getDataPoints()) {
                                HashMap<String, String> fields = new HashMap();

                                Log.d("bucket", "Data point:");
                                Log.d("bucket", "\tType: "+ lookupTable.get(dp.getDataType().getName()));


                                for (Field field : dp.getDataType().getFields()) {
                                    fields.put(field.getName(), dp.getValue(field).toString());
                                    Log.d("dumpDataSet", "\tField: "+ field.getName() +" Value: "+ dp.getValue(field));

                                }

                                hi.addData(dataType, fields);
                            }
                        }
                        healthInformationList.add(hi);

                        Gson gson =  new Gson();

                        Log.d("TAG", "getFitnessData: GSON "+gson.toJson(healthInformationList));
                    }
                    Log.d("TAG", "getFitnessData: Data acquired");
                    hiAdapter.notifyDataSetChanged();

                    addDataToBlockchain(healthInformationList);

                })
                .addOnFailureListener(e ->
                        Log.w("getFitnessData", "There was an error reading data from Google Fit", e));

    }

    private void addDataToBlockchain(LinkedList<HealthInformation> healthInformationList) {
//        TODO send all the fitness data acquired to the Blockchain
//        TODO first u gotta create the employee
    }

    private void dumpDataSet(DataSet dataSet) {
        Log.i("dumpDataSet", "Data returned for Data type: "+lookupTable.get(dataSet.getDataType().getName()));
        for (DataPoint dp : dataSet.getDataPoints()) {

            Log.d("bucket", "Data point:");
            Log.d("bucket", "\tType: "+ lookupTable.get(dp.getDataType().getName()));


            for (Field field : dp.getDataType().getFields()) {
                Log.d("dumpDataSet", "\tField: "+ field.getName() +" Value: "+ dp.getValue(field));
//                healthInformationList.add(new HealthInformation(String.valueOf(field.getName()), dp.getValue(field).toString()));
            }
        }

        hiAdapter.notifyDataSetChanged();

    }

    private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;

        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }


}