package com.ibrickedlabs.BlogApp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibrickedlabs.BlogApp.Data.BlogRecyclerAdapter;
import com.ibrickedlabs.BlogApp.Model.Blog;
import com.ibrickedlabs.BlogApp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostFeedActivity extends AppCompatActivity {
    private FirebaseAuth mfFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FloatingActionButton fab;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private static final String TAG = "PostFeedActivity";
    //RecyclerView Content
    private RecyclerView recyclerView;
    private BlogRecyclerAdapter mBlogRecyclerAdapter;
    private List<Blog> blogList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_feed);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mfFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mfFirebaseAuth.getCurrentUser();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Blog");
        mDatabaseReference.keepSynced(true);//keeps the database synced

        //Instantiating the recycler view things

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        blogList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBlogRecyclerAdapter = new BlogRecyclerAdapter(PostFeedActivity.this, blogList);
        recyclerView.setAdapter(mBlogRecyclerAdapter);
        if (mFirebaseUser.getEmail().equals("rajesh.bammidy@gmail.com")) {
            fab.setVisibility(View.VISIBLE);

        }


    }

    protected void onResume() {
        super.onResume();
        blogList.clear();
    }

    @Override
    protected void onStart() {
        //onResume has the app live state before that we need grab the things up
        super.onStart();
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, dataSnapshot.toString());


                Blog blog = dataSnapshot.getValue(Blog.class);
                blogList.add(blog);
                Collections.reverse(blogList);
                mBlogRecyclerAdapter.notifyDataSetChanged();
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void newPost(View view) {
        startActivity(new Intent(PostFeedActivity.this, PostActivity.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signout:
                mfFirebaseAuth.signOut();
                startActivity(new Intent(PostFeedActivity.this, MainActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
