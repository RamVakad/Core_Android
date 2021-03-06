package com.huntercollab.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcel;
import android.os.SystemClock;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huntercollab.app.activity.EditCollabClassesActivity;
import com.huntercollab.app.activity.EditCollabSkillsActivity;
import com.huntercollab.app.activity.MessagingActivity;
import com.example.socialmediaapp.R;
import com.huntercollab.app.activity.ViewMembersOfCollabActivity;
import com.huntercollab.app.activity.CollabDetailActivity;
import com.huntercollab.app.activity.CollabListActivity;
import com.huntercollab.app.activity.EditCollabActivity;
import com.huntercollab.app.network.loopjtasks.CollabModel;
import com.huntercollab.app.network.loopjtasks.GetUserData;
import com.huntercollab.app.network.loopjtasks.JoinDropCollab;
import com.huntercollab.app.utils.Interfaces;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * A fragment representing a single Collab detail screen.
 * This fragment is either contained in a {@link CollabListActivity}
 * in two-pane mode (on tablets) or a {@link CollabDetailActivity}
 * on handsets.
 */
public class CollabDetailFragment extends Fragment implements JoinDropCollab.JoinComplete, JoinDropCollab.LeaveComplete,
        JoinDropCollab.EditComplete, JoinDropCollab.DeleteComplete, Interfaces.DownloadComplete, Interfaces.DownloadProfleComplete,
        Interfaces.OwnerDownloadComplete {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */

    public static final String ARG_ITEM_ID = "item_id";

    private String currentUsername;
    private String currentCollabId;
    private CollabModel mItem;
    private CollabDetailFragment instance = null;
    private TextView collabOwner;
    private TextView collabStartDateTime;
    private TextView collabEndDateTime;
    private TextView collabLocation;
    private TextView collabSkills;
    private TextView collabClasses;
    private TextView collabMembers;

    private long mLastClickTime = 0;
    private Button editCollabTitle;
    private Button editCollabSize;
    private Button editCollabDescrip;
    private Button editCollabStart;
    private Button editCollabEnd;
    private Button editCollabLocation;
    private Button editCollabSkills;
    private Button editCollabClasses;
    private Button viewMembers;

    private Button joinCollab;
    private JoinDropCollab doJoinCollab;

    private Button leaveCollab;
    private JoinDropCollab doLeaveCollab;

    private Button deleteCollab;
    private JoinDropCollab doDeleteCollab;

    private FloatingActionButton fab;

    private GetUserData userDetails;
    private GetUserData memberDetails;
    private GetUserData ownerDetails;
    private ArrayList<String> membersArray;
    private ArrayList<String> skillsArray;
    private ArrayList<String> classesArray;
    private ArrayList<String> membersArrayForRecyclerView = new ArrayList<>();
    private ArrayList<String> membersArrayNicknames = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public CollabDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //mItem = CollabContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            mItem = new CollabModel(Parcel.obtain());

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(getArguments().getString("title"));
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.collab_detail, container, false);

        instance = this;

        //@author: Hugh Leow
        //@brief: Collab ID that is associated with this fragment and the information that will be displayed
        currentCollabId = getArguments().getString("collabId");

        //@author: Hugh Leow & Edwin Quintuna
        //@brief: API call to retrieve logged in user's information
        userDetails = new GetUserData(getContext(), instance, instance, instance);
        //@author: Hugh Leow
        //@brief: API call to retrieve information of members of the collaboration
        //Used to get each member's names for display rather than e-mails
        memberDetails = new GetUserData(getContext(), instance, instance, instance);
        //@author: Hugh Leow
        //@brief: API call to retrieve information of the owner of the collaboration
        //Used to get owner name for display rather than e-mail
        ownerDetails = new GetUserData(getContext(), instance, instance, instance);
        userDetails.getUserData();

        //@author: Hugh Leow
        //@brief: Sends user to MessagingActivity.java to chat with other members of the collaboration
        //@pre condition: User is viewing the collaboration details
        //@post condition: User is viewing the messages of a collaboration
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewChat = new Intent(getContext(), MessagingActivity.class);
                viewChat.putExtra("chatId", currentCollabId);
                viewChat.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(viewChat);
            }
        });

        // Show the content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.collab_detail)).setText(getArguments().getString("description"));
            collabLocation = (TextView) rootView.findViewById(R.id.collab_Location_Info);
            collabLocation.setText(getArguments().getString("location"));
            collabOwner = (TextView) rootView.findViewById(R.id.collab_Owner_Info);
            //collabOwner.setText(null);
            //collabOwner.setText(getArguments().getString("owner"));
            ownerDetails.getOwnerUserData(getArguments().getString("owner"));

            // populate skills
            collabSkills = (TextView) rootView.findViewById(R.id.collab_Skills_Request_Info);
            collabSkills.setText("");
            skillsArray = getArguments().getStringArrayList("skills");
            if (skillsArray != null){
                int len = skillsArray.size();
                for (int i = 0; i < len; i++){
                    collabSkills.append(skillsArray.get(i) + "\n");
                }
            }

            // populate classes
            collabClasses = (TextView) rootView.findViewById(R.id.collab_Classes_Request_Info);
            collabClasses.setText("");
            classesArray = getArguments().getStringArrayList("classes");
            if (classesArray != null){
                int len = classesArray.size();
                for (int i = 0; i < len; i++){
                    collabClasses.append(classesArray.get(i) + "\n");
                }
            }

            // populate members
            collabMembers = (TextView) rootView.findViewById(R.id.collab_Members_Info);
            collabMembers.setText("");
            membersArray = getArguments().getStringArrayList("members");
            if (membersArray != null){
                membersArrayNicknames.clear();
                int len = membersArray.size();
                for (int i = 0; i < len; i++){
                    memberDetails.getOtherUserData(membersArray.get(i));
                }
            }

            // populate start time
            collabStartDateTime = (TextView) rootView.findViewById(R.id.collab_StartDateTime_Info);
            long dateInMilli = getArguments().getLong("date");
            DateFormat convert = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
            //convert.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date result = new Date(dateInMilli);
            collabStartDateTime.setText(convert.format(result));

            // populate end time (duration)
            collabEndDateTime = (TextView) rootView.findViewById(R.id.collab_EndDateTime_Info);
            long endDateInMilli = getArguments().getLong("duration");
            DateFormat convert1 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
            //convert1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date result1 = new Date(endDateInMilli);
            collabEndDateTime.setText(convert1.format(result1));

            //@author: Hugh Leow
            //@brief: Sends user to ViewMembersOfCollabActivity.java so they can view the list of members as well as view their profiles from there
            //@pre condition: User is viewing the collaboration details
            //@post condition: User is viewing members of the collaboration
            viewMembers = (Button) rootView.findViewById(R.id.viewMembers_button);
            viewMembers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent viewMembersIntent = new Intent(getContext(), ViewMembersOfCollabActivity.class);
                    viewMembersIntent.putExtra("membersList", membersArrayForRecyclerView);
                    viewMembersIntent.putExtra("membersListNicknames", membersArrayNicknames);
                    viewMembersIntent.putExtra("currentUser", currentUsername);
                    viewMembersIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(viewMembersIntent);
                }
            } );

            //@author: Hugh Leow
            //@brief:
            //Attempts to join the current collaboration with an API call to the server
            //See: JoinDropCollab.java
            //@pre condition: User has not joined the collaboration
            //@post condition: Request is sent for user to join the collaboration
            joinCollab = (Button) rootView.findViewById(R.id.join_collab_button);
            joinCollab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        doJoinCollab = new JoinDropCollab(getContext(), instance, instance, instance, instance);
                        String collabId = getArguments().getString("collabId");
                        doJoinCollab.joinCollab(collabId);
                        joinCollab.setEnabled(false);
                }
            });

            //@author: Hugh Leow
            //@brief:
            //Attempts to leave the collaboration with an API call to the server
            //See: JoinDropCollab.java
            //If user is last members of collaboration, they will be prompted with a dialog box to confirm if they want to leave
            //@pre condition: User is part of the collaboration
            //@post condition: User is no longer part of the collaboration
            leaveCollab = (Button) rootView.findViewById(R.id.leave_collab_button);
            leaveCollab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // dialog box asking user to confirm leave/delete if they're the last member
                    if (membersArray.size() == 1){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Confirm");
                        builder.setMessage("You are the last member, this collaboration will be deleted, are you sure?");
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // delete the collab on yes click
                                doDeleteCollab = new JoinDropCollab(getContext(), instance, instance, instance, instance);
                                String collabId = getArguments().getString("collabId");
                                doDeleteCollab.deleteCollab(collabId);
                                Intent collabIntent = new Intent(getContext(), CollabListActivity.class);
                                collabIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(collabIntent);
                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing on no click
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        doLeaveCollab = new JoinDropCollab(getContext(), instance, instance, instance, instance);
                        String collabId = getArguments().getString("collabId");
                        doLeaveCollab.leaveCollab(collabId);
                        leaveCollab.setEnabled(false);
                    }
                }
            });

            //@author: Hugh Leow
            //@brief:
            //Sends user to appropriate fragments according to which edit button they clicked
            //See below
            //@pre condition: Owner is viewing their own collaboration
            //@post condition: Owner is editing a specific field of their collaboration
            editCollabTitle = (Button) rootView.findViewById(R.id.editCollabTitle_button);
            editCollabSize = (Button) rootView.findViewById(R.id.editCollabSize_button);
            editCollabDescrip = (Button) rootView.findViewById(R.id.editCollabDescription_button);
            editCollabLocation = (Button) rootView.findViewById(R.id.editCollabLocation_button);
            editCollabStart = (Button) rootView.findViewById(R.id.editCollabStartTime_button);
            editCollabEnd = (Button) rootView.findViewById(R.id.editCollabEndTime_button);
            editCollabSkills = (Button) rootView.findViewById(R.id.editCollabSkills_button);
            editCollabClasses = (Button) rootView.findViewById(R.id.editCollabClasses_button);

            // edit title button
            editCollabTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // prevents user from clicking button multiple times in less than 3 seconds
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 3000)
                        return;
                    mLastClickTime = SystemClock.elapsedRealtime();
                    sendUserToEditTitle();
                }
            });

            // edit size button
            editCollabSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // prevents user from clicking button multiple times in less than 3 seconds
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 3000)
                        return;
                    mLastClickTime = SystemClock.elapsedRealtime();
                    sendUserToEditSize();
                }
            });

            // edit description button
            editCollabDescrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // prevents user from clicking button multiple times in less than 3 seconds
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 3000)
                        return;
                    mLastClickTime = SystemClock.elapsedRealtime();
                    sendUserToEditDescription();
                }
            });

            // edit location button
            editCollabLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // prevents user from clicking button multiple times in less than 3 seconds
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 3000)
                        return;
                    mLastClickTime = SystemClock.elapsedRealtime();
                    sendUserToEditLocation();
                }
            });

            // edit start date button
            editCollabStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // prevents user from clicking button multiple times in less than 3 seconds
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 3000)
                        return;
                    mLastClickTime = SystemClock.elapsedRealtime();
                    sendUserToEditStartDate();
                }
            });

            // edit end date button
            editCollabEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // prevents user from clicking button multiple times in less than 3 seconds
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 3000)
                        return;
                    mLastClickTime = SystemClock.elapsedRealtime();
                    sendUserToEditEndDate();
                }
            });

            // edit skills button
            editCollabSkills.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // prevents user from clicking button multiple times in less than 3 seconds
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 3000)
                        return;
                    mLastClickTime = SystemClock.elapsedRealtime();
                    sendUserToEditSkills();
                }
            });

            // edit classes button
            editCollabClasses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // prevents user from clicking button multiple times in less than 3 seconds
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 3000)
                        return;
                    mLastClickTime = SystemClock.elapsedRealtime();
                    sendUserToEditClasses();
                }
            });

            //@author: Hugh Leow
            //@brief: Prompts user for confirmation, if they click yes, collaboration will be deleted
            //@pre condition: Collaboration is up and active
            //@post condition: Collaboration is removed from the database and from all user's 'My Collabs"
            deleteCollab = (Button) rootView.findViewById(R.id.delete_collab_button);
            deleteCollab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // dialog box asking user to confirm deletion
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // delete the collab on yes click
                            doDeleteCollab = new JoinDropCollab(getContext(), instance, instance, instance, instance);
                            String collabId = getArguments().getString("collabId");
                            doDeleteCollab.deleteCollab(collabId);
                            Intent collabIntent = new Intent(getContext(), CollabListActivity.class);
                            collabIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(collabIntent);
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing on no click
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

        }

        return rootView;
    }

    //@author: Hugh leow
    //@brief:
    // bundle + key to pass parameter to EditCollabActivity.java
    // send User to edit title fragment
    private void sendUserToEditTitle() {
        Intent editCollab = new Intent (getActivity(), EditCollabActivity.class);
        Bundle x = new Bundle();
        x.putInt("key",1);
        x.putString("collabId", currentCollabId);
        editCollab.putExtras(x);
        startActivityForResult(editCollab, 1);
        getActivity().finish();
    }

    //@author: Hugh leow
    //@brief:
    // send User to edit size fragment
    private void sendUserToEditSize() {
        Intent editCollab = new Intent (getActivity(), EditCollabActivity.class);
        Bundle x = new Bundle();
        x.putInt("key",9);
        x.putString("collabId", currentCollabId);
        x.putInt("numOfMembers", membersArray.size());
        editCollab.putExtras(x);
        startActivityForResult(editCollab, 1);
        getActivity().finish();
    }

    //@author: Hugh leow
    //@brief:
    // send User to edit description fragment
    private void sendUserToEditDescription() {
        Intent editCollab = new Intent (getActivity(), EditCollabActivity.class);
        Bundle x = new Bundle();
        x.putInt("key",2);
        x.putString("collabId", currentCollabId);
        editCollab.putExtras(x);
        startActivityForResult(editCollab, 1);
        getActivity().finish();
    }

    //@author: Hugh leow
    //@brief:
    // send User to edit location fragment
    private void sendUserToEditLocation() {
        Intent editCollab = new Intent (getActivity(), EditCollabActivity.class);
        Bundle x = new Bundle();
        x.putInt("key",3);
        x.putString("collabId", currentCollabId);
        editCollab.putExtras(x);
        startActivityForResult(editCollab, 1);
        getActivity().finish();
    }

    //@author: Hugh leow
    //@brief:
    // send User to edit end date fragment
    private void sendUserToEditStartDate() {
        Intent editCollab = new Intent (getActivity(), EditCollabActivity.class);
        Bundle x = new Bundle();
        x.putInt("key",4);
        x.putString("collabId", currentCollabId);
        editCollab.putExtras(x);
        startActivityForResult(editCollab, 1);
        getActivity().finish();
    }

    //@author: Hugh leow
    //@brief:
    // send User to edit end date fragment
    private void sendUserToEditEndDate() {
        Intent editCollab = new Intent (getActivity(), EditCollabActivity.class);
        Bundle x = new Bundle();
        x.putInt("key",5);
        x.putString("collabId", currentCollabId);
        editCollab.putExtras(x);
        startActivityForResult(editCollab, 1);
        getActivity().finish();
    }

    //@author: Hugh leow
    //@brief:
    // send User to edit skills activity
    private void sendUserToEditSkills() {
        Intent editCollab = new Intent (getActivity(), EditCollabSkillsActivity.class);
        Bundle x = new Bundle();
        x.putInt("key",6);
        x.putString("collabId", currentCollabId);
        x.putStringArrayList("skillsArray", skillsArray);
        editCollab.putExtras(x);
        startActivityForResult(editCollab, 1);
        getActivity().finish();
    }

    //@author: Hugh leow
    //@brief:
    // send User to edit classes activity
    private void sendUserToEditClasses() {
        Intent editCollab = new Intent (getActivity(), EditCollabClassesActivity.class);
        Bundle x = new Bundle();
        x.putInt("key",7);
        x.putString("collabId", currentCollabId);
        x.putStringArrayList("classesArray", classesArray);
        editCollab.putExtras(x);
        startActivityForResult(editCollab, 1);
        getActivity().finish();
    }

    //@author: Hugh Leow
    //@brief:
    //Interface function for ASYNC HTTP request from JoinDropCollab.java
    //If user successfully joins the collaboration, they are notified with a toast and are added to the list of members
    //Buttons change accordingly
    //Messaging will show up for the user
    //'Join' turns to 'Leave'
    //Etc
    //@params: [Boolean success] [String message]
    //@pre condition: User has not joined the collaboration
    //@post condition: User has joined collaboration if success = 'true', user is notified with a toast
    @Override
    public void joinComplete(Boolean success, String message) {
        if(success){
            Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            toast.show();

            joinCollab.setVisibility(INVISIBLE);
            leaveCollab.setVisibility(VISIBLE);
            fab.setVisibility(VISIBLE);
            joinCollab.setEnabled(true);

            // add member locally
            membersArray.add(userDetails.getUserName());
            membersArrayForRecyclerView.add(userDetails.getUserName());
            membersArrayNicknames.add(userDetails.getUserNickname());
            collabMembers.append(userDetails.getUserNickname() + "\n");
        } else {
            Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            toast.show();
            joinCollab.setEnabled(true);
        }

    }

    //@author: Hugh Leow
    //@brief:
    //Interface function for ASYNC HTTP request from JoinDropCollab.java
    //If user successfully leaves the collaboration, they are notified with a toast and are removed from the list of members
    //Buttons change accordingly
    //Messaging button will be removed
    //'Leave' turns to 'Join'
    //If they are owners, they are no longer allowed to edit any fields
    //Etc.
    //@params: [Boolean success]
    //@pre condition: User is part of the collaboration
    //@post condition: User has left the collaboration if success = 'true', user is notified with a toast
    @Override
    public void leaveComplete(Boolean success) {
        if(success){
            CharSequence text = "You have left the collab!";

            Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            toast.show();

            leaveCollab.setVisibility(INVISIBLE);
            joinCollab.setVisibility(VISIBLE);
            fab.setVisibility(INVISIBLE);

            editCollabTitle.setVisibility(INVISIBLE);
            editCollabSize.setVisibility(INVISIBLE);
            editCollabDescrip.setVisibility(INVISIBLE);
            editCollabStart.setVisibility(INVISIBLE);
            editCollabEnd.setVisibility(INVISIBLE);
            editCollabLocation.setVisibility(INVISIBLE);
            editCollabSkills.setVisibility(INVISIBLE);
            editCollabClasses.setVisibility(INVISIBLE);
            deleteCollab.setVisibility(INVISIBLE);

            leaveCollab.setEnabled(true);

            // repopulate members field locally
            membersArray.remove(userDetails.getUserName());
            membersArrayForRecyclerView.remove(userDetails.getUserName());
            membersArrayNicknames.remove(userDetails.getUserNickname());
            collabMembers.setText("");
            if (membersArrayNicknames != null){
                int len = membersArrayNicknames.size();
                for (int i = 0; i < len; i++){
                    collabMembers.append(membersArrayNicknames.get(i) + "\n");
                }
            }
        } else {
            CharSequence text = "Cannot leave!  Try again.";

            Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            toast.show();
            leaveCollab.setEnabled(true);
        }
    }

    @Override
    public void editComplete(Boolean success) {

    }

    @Override
    public void deleteComplete(Boolean success) {

    }

    //@author: Hugh Leow
    //@brief:
    //Interface function for ASYNC HTTP request from GetUserData.java
    //If data is successfully retrieved from the database, do checks to show correct buttons
    //If they are in the collaboration, show a leave instead of join, and also allow them to see the messaging button
    //Vice versa if they are not in the collaboration
    //If they are the owner, show them the appropriate buttons
    //Edit + Delete
    //@params: [Boolean success]
    //@pre condition: User data not retrieved from database, no buttons show
    //@post condition: User data retrieved, and relevant buttons are now visible to the user
    @Override
    public void downloadComplete(Boolean success) {
        if (success) {
            currentUsername = userDetails.getUserName();

            // if user is IN collab, show leave, else show join
            if (membersArray.contains(currentUsername)){
                leaveCollab.setVisibility(VISIBLE);
                fab.setVisibility(VISIBLE);
            }
            else {
                joinCollab.setVisibility(VISIBLE);
            }

            // if user is the owner, show edit/delete collab button
            if (getArguments().getString("owner").equals(userDetails.getUserName())){
                editCollabTitle.setVisibility(View.VISIBLE);
                editCollabSize.setVisibility(View.VISIBLE);
                editCollabDescrip.setVisibility(View.VISIBLE);
                editCollabStart.setVisibility(View.VISIBLE);
                editCollabEnd.setVisibility(View.VISIBLE);
                editCollabLocation.setVisibility(View.VISIBLE);
                editCollabSkills.setVisibility(View.VISIBLE);
                editCollabClasses.setVisibility(View.VISIBLE);
                deleteCollab.setVisibility(View.VISIBLE);
            }
        } else {
            // show error message to user
            Toast t = Toast.makeText(getContext(), "ERROR", Toast.LENGTH_LONG);
            t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
            t.show();
        }
    }

    //@author: Hugh Leow
    //@brief:
    //Interface function for ASYNC HTTP request from GetUserData.java
    //If data is successfully retrieved from the database, retrieve 'preferred names' of the members of the collaboration and display it accordingly
    //@params: [Boolean success]
    //@pre condition: Member data not retrieved, emails show instead of names
    //@post condition: Member data retrieved, and 'preferred names' now show
    @Override
    public void downloadProfileComplete(Boolean success) {
        if (success) {
            membersArrayForRecyclerView.add(memberDetails.getUserName());
            membersArrayNicknames.add(memberDetails.getUserNickname());
            collabMembers.append(memberDetails.getUserNickname() + "\n");
        }
    }

    //@author: Hugh Leow
    //@brief:
    //Interface function for ASYNC HTTP request from GetUserData.java
    //If data is successfully retrieved from the database, retrieve 'preferred names' of the owner of the collaboration and display it accordingly
    //@params: [Boolean success]
    //@pre condition: Owner data not retrieved, email shows instead of name
    //@post condition: Owner data retrieved, and 'preferred name' now shows
    @Override
    public void ownerDownloadComplete(Boolean success) {
        if (success) {
            collabOwner.setText(ownerDetails.getUserNickname());
        }
    }

}
