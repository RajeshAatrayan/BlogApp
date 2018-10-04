package com.ibrickedlabs.BlogApp.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibrickedlabs.BlogApp.R;

public class MainActivity extends AppCompatActivity implements AuthInterFace {
    private EditText email;
    private EditText password;
    private Button submitButton;
    private Button createAccountButton;


    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        submitButton = (Button) findViewById(R.id.submit);
        createAccountButton = (Button) findViewById(R.id.createAccount);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,CreateUser.class);
                startActivity(intent);
                finish();
            }
        });
        database = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    //That is user is signed in
                    Toast.makeText(MainActivity.this, "Signed in!", Toast.LENGTH_SHORT).show();
                  Intent intent=  new Intent(MainActivity.this,PostFeedActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //user is signed out mode
                    Toast.makeText(MainActivity.this, "Signed out!", Toast.LENGTH_SHORT).show();

                }

            }
        };
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = email.getText().toString().trim();
                String pwd = password.getText().toString().trim();
                if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(pwd)) {
                    //So we have username and password with us
                    login(userName, pwd);
                } else {
                    //Enter correctly
                    Toast.makeText(MainActivity.this,"Enter the input",Toast.LENGTH_SHORT);
                }

            }
        });

    }

    private void login(String userName, String pwd) {

        //Login the user with credentails
        mFirebaseAuth.signInWithEmailAndPassword(userName, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(MainActivity.this, "Signed in Sucessfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,PostFeedActivity.class));
                    finish();
                } else {
                    Log.d(TAG, "createUserWithEmail:failure"+ task.getException());
                    Toast.makeText(MainActivity.this, "Sign in fails", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener!=null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.menu_overflow1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.signout:
                mFirebaseAuth.signOut();
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public FirebaseAuth signoutObject() {
        return mFirebaseAuth;
    }
}
