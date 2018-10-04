package com.ibrickedlabs.BlogApp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibrickedlabs.BlogApp.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wang.avi.AVLoadingIndicatorView;

public class CreateUser extends AppCompatActivity {

    private static final int PROFILE_CODE = 112;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private Button createButton;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private ImageView profileImage;
    Uri resultUri;
    private AVLoadingIndicatorView mAvLoadingIndicatorView;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.lname);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        createButton = (Button) findViewById(R.id.createAccount);
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference().child("BlogUsers");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAvLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.aviuser);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PROFILE_CODE);

            }
        });


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPBar();
                createAccountWithDetails();
            }
        });
    }

    private void setPBar() {
        mAvLoadingIndicatorView.setVisibility(View.VISIBLE);
        firstName.setVisibility(View.GONE);
        lastName.setVisibility(View.GONE);
        email.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        createButton.setVisibility(View.GONE);
        profileImage.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROFILE_CODE && resultCode == RESULT_OK) {
            Uri profileURL = data.getData();
            CropImage.activity(profileURL).setAspectRatio(1, 1).setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

        }
        //for image cropping
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                profileImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void createAccountWithDetails() {
        final String fName = firstName.getText().toString().trim();
        final String lName = lastName.getText().toString().trim();
        String emailString = email.getText().toString().trim();
        String passwordString = password.getText().toString().trim();
        if (!TextUtils.isEmpty(fName) && !TextUtils.isEmpty(lName) && !TextUtils.isEmpty(emailString) && !TextUtils.isEmpty(passwordString) && resultUri!=null) {
            mFirebaseAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if (authResult != null) {
                        final StorageReference filePath = mStorageReference.child("UserProfilePictures").child(resultUri.getLastPathSegment());
                        filePath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri uploadedUri = task.getResult();
                                    String userId = mFirebaseAuth.getCurrentUser().getUid();
                                    DatabaseReference currentDB = database.getReference().child(userId);
                                    currentDB.child("firstName").setValue(fName);
                                    currentDB.child("lastName").setValue(lName);
                                    currentDB.child("image").setValue(String.valueOf(uploadedUri));
                                    Intent intent = new Intent(CreateUser.this, PostFeedActivity.class);
                                    Toast.makeText(CreateUser.this, "Account Created", Toast.LENGTH_SHORT).show();
                                    intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);

                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(CreateUser.this, "Account Creation Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                }
            });
        }
        else {
            //If the details go wrong
            debolishPBAR();
            Toast.makeText(CreateUser.this,"Please fill all the details",Toast.LENGTH_SHORT).show();
        }

    }

    private void debolishPBAR() {
        mAvLoadingIndicatorView.setVisibility(View.GONE);
        firstName.setVisibility(View.VISIBLE);
        lastName.setVisibility(View.VISIBLE);
        email.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        createButton.setVisibility(View.VISIBLE);
        profileImage.setVisibility(View.VISIBLE);
    }
}
