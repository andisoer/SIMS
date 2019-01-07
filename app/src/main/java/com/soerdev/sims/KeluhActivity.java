package com.soerdev.sims;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class KeluhActivity extends AppCompatActivity {

    private EditText mChatEDT;
    private ImageButton ibChatSend;

    private String varUIDCurrentUser;
    private String varUsername;

    private FloatingActionButton fabaddChat;

    private FirebaseAuth.AuthStateListener mfirebaseAuthListener;
    private DatabaseReference chatReference, userReference;
    private FirebaseListAdapter chatAdapater;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_keluhan);

        Toolbar toolbar = (Toolbar)findViewById(R.id.keluhan_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        varUIDCurrentUser = mAuth.getCurrentUser().getUid();

        fabaddChat = findViewById(R.id.addChatfab);

        fabaddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KeluhActivity.this, AddChatActivity.class);
                startActivity(intent);
            }
        });
    }



}
