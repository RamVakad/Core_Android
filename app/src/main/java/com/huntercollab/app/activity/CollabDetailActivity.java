package com.huntercollab.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.huntercollab.app.fragment.CollabDetailFragment;
import com.example.socialmediaapp.R;
import com.huntercollab.app.network.loopjtasks.CollabModel;

/**
 * An activity representing a single Collab detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link CollabListActivity}.
 */
public class CollabDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collab_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //

        if (savedInstanceState == null) {

            Intent intent = getIntent();

            //@author: Hugh Leow & Edwin Quintuna
            //@brief:
            //Gets parcel that was passed from CollabListActivity.java
            //@pre condition: No collaboration data
            //@post condition: Collaboration data in object 'collab'
            CollabModel collab = intent.getParcelableExtra("collab");

            //@author: Hugh Leow & Edwin Quintuna
            //@brief:
            //Puts values from the parcel into a bundle for the fragment transaction
            //@pre condition: Collaboration data isolated in variables
            //@post condition: Collaboration data inside a new bundle to create fragment
            Bundle arguments = new Bundle();
            arguments.putString("description", collab.getDescription());
            arguments.putString("title", collab.getTitle());
            arguments.putString("owner", collab.getOwner());
            arguments.putString("location", collab.getLocation());
            arguments.putInt("size", collab.getSize());
            arguments.putLong("date", collab.getDate());
            arguments.putLong("duration", collab.getDuration());
            arguments.putStringArrayList("skills", collab.getSkills());
            arguments.putStringArrayList("classes", collab.getClasses());
            arguments.putStringArrayList("members", collab.getMembers());
            arguments.putString("collabId", collab.getCollabId());

            //@author: Hugh Leow & Edwin Quintuna
            //@brief:
            //Create fragment and add it to the activity using a fragment transaction
            //See: CollabDetailFragment.java
            //@pre condition: No fragment to display the collaboration
            //@post condition: Fragment created to display collaboration data
            CollabDetailFragment fragment = new CollabDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.collab_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, CollabListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
