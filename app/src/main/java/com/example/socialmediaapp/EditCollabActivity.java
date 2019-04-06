package com.example.socialmediaapp;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class EditCollabActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_collab);

        // grab key from previous activity
        Bundle x = getIntent().getExtras();
        int value = 0;
        if (x != null)
            value = x.getInt("key");

        if (value == 1) {
            FragmentTransaction editTitle = getSupportFragmentManager().beginTransaction();
            editTitle.replace(R.id.fragmentContainer, new EditCollabTitleFragment());
            editTitle.commit();
        } else if (value == 2) {
            FragmentTransaction editDescription = getSupportFragmentManager().beginTransaction();
            editDescription.replace(R.id.fragmentContainer, new EditCollabDescripFragment());
            editDescription.commit();
        } else if (value == 3) {
            FragmentTransaction editLocation = getSupportFragmentManager().beginTransaction();
            editLocation.replace(R.id.fragmentContainer, new EditCollabLocationFragment());
            editLocation.commit();
        } else if (value == 4) {
            FragmentTransaction editStartDate = getSupportFragmentManager().beginTransaction();
            editStartDate.replace(R.id.fragmentContainer, new EditCollabStartFragment());
            editStartDate.commit();
        } else if (value == 5) {
            FragmentTransaction editEndDate = getSupportFragmentManager().beginTransaction();
            editEndDate.replace(R.id.fragmentContainer, new EditCollabEndFragment());
            editEndDate.commit();
        }

    }

}