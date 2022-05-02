package com.mudebbela.fitbiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateEmployeeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_employee);

        Button btnCreateEmployee =  findViewById(R.id.buttonCreateUser);
        btnCreateEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name =  ((EditText) findViewById(R.id.editTextTextPersonName)).getText().toString();
                String age =  ((EditText) findViewById(R.id.editTextAge)).getText().toString();
                String sex =    ((EditText) findViewById(R.id.editTextSex)).getText().toString();
                String password = ((EditText) findViewById(R.id.editTextTextPassword2)).getText().toString();

                if(FitbizUtils.allFilled(name, age, sex, password)){
                    Intent returnData =  new Intent();
                    returnData.putExtra(FitbizConstants.NAME, name);
                    returnData.putExtra(FitbizConstants.AGE, age);
                    returnData.putExtra(FitbizConstants.SEX, sex);
                    returnData.putExtra(FitbizConstants.PASSWORD, password);
                    setResult(Activity.RESULT_OK, returnData);
                    finish();

                }

            }
        });
    }
}