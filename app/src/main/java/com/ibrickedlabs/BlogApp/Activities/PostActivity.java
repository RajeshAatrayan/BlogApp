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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibrickedlabs.BlogApp.Model.Blog;
import com.ibrickedlabs.BlogApp.R;
import com.wang.avi.AVLoadingIndicatorView;


import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {
    private ImageView imageView;
    private EditText title;
    private EditText desc;
    private StorageReference mStorageReference;
    private TextView loadingView;
    private Button uploadButton;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressBar mProgressBar;
    private Uri ImageUri;
    private FirebaseUser mUser;
    public static final int GALLERY_ID = 123;
    private AVLoadingIndicatorView mAvLoadingIndicatorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mProgressBar = new ProgressBar(this);
        mAvLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);
        loadingView = (TextView) findViewById(R.id.loading_text_view);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        imageView = (ImageView) findViewById(R.id.uploadImageView);
        title = (EditText) findViewById(R.id.uploadText);
        desc = (EditText) findViewById(R.id.uploadDescription);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_ID);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_ID && resultCode == RESULT_OK) {
            ImageUri = data.getData();
            imageView.setImageURI(ImageUri);
        }
    }

    private void startPosting() {
        invisibleState();
        final String titleValue = title.getText().toString();
        final String descValue = desc.getText().toString();
        if (!TextUtils.isEmpty(titleValue) && !TextUtils.isEmpty(descValue) && ImageUri != null) {
            //So we are ready to upload the feed to db
            //child("Blog_images") it will just create a folder and then ImageUri.getLastPathSegment() will create the picture name in there
            final StorageReference filePath = mStorageReference.child("Blog_images").child(ImageUri.getLastPathSegment());

            //here we are acyually pushing the image onto the name we created earlier
            //Dragged from udacity chat app since getDownloadUrl not really working

            filePath.putFile(ImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        //Above taken from udacity repo since geDownloadurl is depricated
                        //From here our regular code starts
                        Uri downloadUri = task.getResult();
                        //Push creates a new object with unique id every time we push
                        DatabaseReference newPost = databaseReference.push();
//                        Map<String, String> dataToSave = new HashMap<>();
//                        dataToSave.put("title", titleValue);
//                        dataToSave.put("desc", descValue);
//                        dataToSave.put("image", String.valueOf(downloadUri));
//                        dataToSave.put("timeStamp", String.valueOf(System.currentTimeMillis()));
//                        dataToSave.put("userid", mUser.getUid());
                        Blog blog=new Blog(titleValue,descValue,String.valueOf(downloadUri),String.valueOf(System.currentTimeMillis()),mUser.getUid());
                        newPost.setValue(blog);
                        Toast.makeText(PostActivity.this, "Feed Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(PostActivity.this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } else {
            visibleState();

        }
    }

    private void invisibleState() {
        mAvLoadingIndicatorView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        desc.setVisibility(View.GONE);
        uploadButton.setVisibility(View.GONE);
    }

    private void visibleState() {
        mAvLoadingIndicatorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        desc.setVisibility(View.VISIBLE);

        uploadButton.setVisibility(View.VISIBLE);
    }


}
