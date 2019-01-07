package com.soerdev.sims;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.soerdev.sims.models.Admin;
import com.soerdev.sims.utils.Constants;

public class AddChatActivity extends AppCompatActivity {

    private RecyclerView rvAddChat;

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private DatabaseReference mChatReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);

        Toolbar toolbar_addchat = (Toolbar) findViewById(R.id.toolbar_addchat);

        setSupportActionBar(toolbar_addchat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        // mChatReference = FirebaseDatabase.getInstance().getReference().child(Constants.CHATS);

        rvAddChat = (RecyclerView)findViewById(R.id.rvAddChat);
        rvAddChat.setLayoutManager(linearLayoutManager);

        tampilAdminorPengurus();
    }

    private void tampilAdminorPengurus() {
        Query query = FirebaseDatabase.getInstance().getReference().child(Constants.USER_LOC);

        FirebaseRecyclerOptions<Admin> adminFirebaseRecyclerOptions =
                new FirebaseRecyclerOptions.Builder<Admin>()
                .setQuery(query.orderByChild("privillage").equalTo("admin"), new SnapshotParser<Admin>() {
                    @NonNull
                    @Override
                    public Admin parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new Admin(snapshot.child(Constants.USER_EMAIL).getValue().toString(),
                                snapshot.child(Constants.USERNAME_LOC).getValue().toString());
                    }
                })
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Admin, listHolder>(adminFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull listHolder holder, final int position,@NonNull Admin admin) {
                final String uid_admin = getRef(position).getKey();

                holder.setEmailUser(admin.getEmailUser());
                holder.setNamaUser(admin.getNamaUser());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //String uid_chats = mChatReference.push().getKey();

                    }
                });
            }

            @NonNull
            @Override
            public listHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_admin, viewGroup, false);

                return new listHolder(view);
            }
        };
        rvAddChat.setAdapter(firebaseRecyclerAdapter);
    }

    public static class listHolder extends RecyclerView.ViewHolder{
        View mView;

        public listHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setEmailUser(String emailUser){
            TextView email_user = (TextView)mView.findViewById(R.id.nama_admin);
            email_user.setText(emailUser);
        }

        public void setNamaUser(String namaUser){
            TextView nama_user = (TextView)mView.findViewById(R.id.email_admin);
            nama_user.setText(namaUser);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}
