package com.connectapp.user.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.connectapp.user.R;
import com.connectapp.user.adapter.FriendsListAdapter;
import com.connectapp.user.data.UserClass;
import com.connectapp.user.chatmodel.ChatContact;
import com.connectapp.user.chatmodel.ChatContactList;
import com.connectapp.user.util.AlertDialogCallBack;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatContactsActivity extends AppCompatActivity implements ServerResponseCallback {

    private Context mContext;

    private RecyclerView recyclerView;

    //Firebase and GoogleApiClient
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private VolleyTaskManager volleyTaskManager;

    private LinearLayout ll_searchLayout;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_contacts);
        setupActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("  ConnectApp Chat");
        // Assign context
        mContext = ChatContactsActivity.this;
        // Iniialize volley class
        volleyTaskManager = new VolleyTaskManager(mContext);
        // Initialize UI components
        initView();
        // Verify Firebase Login
        verifyUserLogin();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewContacts);

        // ll_searchLayout = (LinearLayout) findViewById(R.id.ll_searchLayout);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void setupActionBar() {

        // Inflate a "Done/Cancel" custom action bar view.
        final LayoutInflater inflater = (LayoutInflater) this
                .getSupportActionBar().getThemedContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(R.layout.actionbar_chat_contacts, null);
        customActionBarView.findViewById(R.id.title);
        final ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM |
                        ActionBar.DISPLAY_SHOW_HOME |
                        ActionBar.DISPLAY_SHOW_TITLE);

        actionBar.setCustomView(customActionBarView,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        ll_searchLayout = (LinearLayout) customActionBarView.findViewById(R.id.ll_searchLayout);
    }

    /**
     * Verify user is logged in
     */
    private void verifyUserLogin() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // User could not be verified.
            // startActivity(new Intent(this, SigninActivity.class));
            Util.showCallBackMessageWithOkCallback(mContext, "Your gmail account cannot be verified at the moment.", new AlertDialogCallBack() {
                @Override
                public void onSubmit() {
                    finish();
                }

                @Override
                public void onCancel() {

                }
            });

        } else {
            // User verified.
            fetchChatUserContacts();
        }
    }


    private void fetchChatUserContacts() {

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("userID", "" + Util.fetchUserClass(mContext).getUserId());
        volleyTaskManager.doFetchChatContacts(requestMap, true);

    }

    @Override
    public void onSuccess(JSONObject resultJsonObject) {

        if (resultJsonObject.optString("code").trim().equalsIgnoreCase("200")) {
            try {
                JSONArray dataJsonArray = resultJsonObject.optJSONArray("data");
                ArrayList<ChatContact> chatContacts = new ArrayList<>();
                HashMap<String, ChatContact> contactArrayMap = new HashMap<>();
                for (int i = 0; i < dataJsonArray.length(); i++) {
                    JSONObject studentJsonObj = dataJsonArray.optJSONObject(i);
                    ChatContact chatContact = new ChatContact();
                    chatContact.name = studentJsonObj.optString("mu_name");
                    chatContact.emailId = studentJsonObj.optString("mu_email");
                    chatContact.mobile = studentJsonObj.optString("mu_phone");
                    chatContact.userID = studentJsonObj.optString("mu_id");
                    chatContact.studentFirebaseId = studentJsonObj.optString("mu_firebaseID");
                    chatContact.profileImgURL = studentJsonObj.optString("mu_img_url");
                    chatContact.firebaseInstanceID = studentJsonObj.optString("mu_firebase_instance");
                    chatContacts.add(chatContact);
                    contactArrayMap.put("" + chatContact.emailId, chatContact);
                }
                ChatContactList studentList = Util.fetchStudentList(mContext);
                if (studentList == null) {
                    //First Time Student List Fetch
                    studentList = new ChatContactList();
                    studentList.studentsArrayList = chatContacts;
                    studentList.studentArrayMap = contactArrayMap;
                    Util.saveStudentList(mContext, studentList);
                } else if (studentList != null) {
                    // Not the First Time
                    Log.e("NotFirstTime", "Not the First Time");

                    ArrayList<ChatContact> oldList = studentList.studentsArrayList;
                    ArrayList<ChatContact> newList = chatContacts;
                    ArrayList<ChatContact> commonList = new ArrayList<>();
                    ArrayList<ChatContact> newAddedStudentsList = new ArrayList<>();
                    for (ChatContact student1 : oldList) {
                        Log.e("Unreadcount", "Unreadcount: " + student1.unreadMsgCount);
                        for (ChatContact student2 : newList) {
                            if (student1.emailId.equalsIgnoreCase(student2.emailId)) {
                                //This gives the common elements
                                commonList.add(student1);
                            }
                        }
                    }


                    ArrayList<Integer> results = new ArrayList<>();

                    // Loop arrayList2 items
                    for (ChatContact person2 : newList) {
                        // Loop arrayList1 items
                        boolean found = false;
                        for (ChatContact person1 : commonList) {
                            if (person2.emailId.equalsIgnoreCase(person1.emailId)) {
                                found = true;
                            }
                        }
                        if (!found) {
                            newAddedStudentsList.add(person2);
                        }
                    }
                   /* //Now get New Elements
                    for(Student student1:newList){
                        for(Student student2:commonList){
                            if(!(student1.emailId.trim().equalsIgnoreCase(student2.emailId.trim()))){
                                // This gives new elements that are added
                                newAddedStudentsList.add(student1);
                            }
                        }
                    }*/
                    Log.e("New Files", "Common Files Size: " + commonList.size());
                    Log.e("New Files", "New Files Size: " + newAddedStudentsList.size());

                    //Now Final List is
                    ArrayList<ChatContact> finalList = new ArrayList<>(commonList);
                    finalList.addAll(newAddedStudentsList);
                    Log.e("Final List", "Final List: " + finalList);
                    Log.e("Final List", "Final List Size: " + finalList.size());

                    studentList.studentsArrayList = finalList;
                    Util.saveStudentList(mContext, studentList);
                }

                //Set filter for Search
                final ArrayList<ChatContact> studentNewList = studentList.studentsArrayList;
                ll_searchLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, SearchViewActivity.class);
                        intent.putExtra("students", studentNewList);
                        startActivity(intent);
                    }
                });

                // Set Students List View and Adapter
                Log.e("studentsArrayList", "studentsArrayList Size: " + studentList.studentsArrayList.size());
                FriendsListAdapter adapter = new FriendsListAdapter(mContext, studentList.studentsArrayList);
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
                //list.setAdapter( adapter );
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new FriendsListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, ChatContact obj, int position) {
                        //Toast.makeText(mContext, "Position: " + position, Toast.LENGTH_SHORT).show();

                        UserClass userClass = Util.fetchSelectedUserClass(mContext);
                        if (userClass == null)
                            userClass = new UserClass();

                        userClass.selectedStudent = obj;
                        Util.saveSelectedUserClass(mContext, userClass);
                        ChatContactList studentList1 = Util.fetchStudentList(mContext);
                        if (studentList1.studentsArrayList.get(position).unreadMsgCount > 0) {
                            studentList1.studentsArrayList.get(position).unreadMsgCount = 0;
                            Util.saveStudentList(mContext, studentList1);
                        }

                        Intent intent = new Intent(mContext, ChatActivity.class);
                        intent.putExtra("name", obj.name);
                        intent.putExtra("email", obj.emailId);
                        intent.putExtra("studentId", obj.userID);
                        intent.putExtra("firebaseId", obj.studentFirebaseId);
                        intent.putExtra("firebaseInstanceID", obj.firebaseInstanceID);
                        startActivity(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Util.showMessageWithOk(ChatContactsActivity.this, "Something went wrong. Please try again later.");
            }
        } else {
            Util.showCallBackMessageWithOkCallback(ChatContactsActivity.this, "Something went wrong. Please try again later.", new AlertDialogCallBack() {
                @Override
                public void onSubmit() {
                    // Exit
                    finish();
                }

                @Override
                public void onCancel() {
                }
            });
        }
    }

    @Override
    public void onError(VolleyError error) {

    }
}
