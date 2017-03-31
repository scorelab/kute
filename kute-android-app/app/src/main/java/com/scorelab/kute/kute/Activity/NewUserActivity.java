package com.scorelab.kute.kute.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.scorelab.kute.kute.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//This class is to
public class NewUserActivity extends AppCompatActivity {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public ProgressDialog mDialog;

    EditText email;
    EditText password;
    Button register;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Processing...");
        mDialog.setCancelable(false);

        email = (EditText) findViewById(R.id.reg_email);
        password = (EditText) findViewById(R.id.reg_password);
        register = (Button) findViewById(R.id.button_register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show();
                registerNew();
            }
        });
    }

    private void registerNew(){
        String s_email = email.getText().toString().trim();
        String s_password = password.getText().toString();

        if ((s_email.trim().length() != 0) && (s_password.length() >= 6)) {

            if(validateEmail(s_email)){
                mAuth.createUserWithEmailAndPassword(s_email, s_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User Added", Toast.LENGTH_SHORT).show();
                            email.setText("");
                            password.setText("");
                        } else {
                            Log.d("REG ERROR",task.getResult().toString());
                            Toast.makeText(getApplicationContext(), "Error in Teacher Registration", Toast.LENGTH_SHORT).show();
                        }
                        mDialog.hide();
                    }
                });
            }else {
                Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
                mDialog.hide();
            }

        }else {
            Toast.makeText(getApplicationContext(), "Incorrect Email / Password", Toast.LENGTH_SHORT).show();
            mDialog.hide();
        }
    }

    //To validate email address
    public boolean validateEmail(String passedEmail){
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(passedEmail);
        return matcher.find();
    }
}
