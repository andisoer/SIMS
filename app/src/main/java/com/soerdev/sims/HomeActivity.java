package com.soerdev.sims;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.soerdev.sims.models.Admin;
import com.soerdev.sims.utils.Constants;

public class HomeActivity extends AppCompatActivity {

    private ActionBarDrawerToggle swipe;
    private NavigationView navigationView;
    private CardView absen, nilai, spp, keluhan;
    private ImageView panahPilihProfil;
    private TextView nama_profile_nav_header, sekolah_profile_nav_header, nama_siswa_dashboard, nama_sekolah_dashboard;
    private DrawerLayout drawerLayout;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserReference, mProfileAddedReference;

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private String varUserNameNow, varUserUidNow;

    private RelativeLayout relativeLayoutPilihProfil;
    private LinearLayout listPilihProfil;

    private RecyclerView rvPilihProfile;

    private View mViewHeader;

    private String nama_siswa_header, nama_sekolah_header, uid_profile_added;

    private boolean pickProfileshow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        absen = (CardView) findViewById(R.id.absensi) ;

        Toolbar toolbarhome = findViewById(R.id.home_toolbar);
        drawerLayout = findViewById(R.id.activity_home);

        navigationView = findViewById(R.id.navigasi_drawerhome);

        nama_siswa_dashboard = (TextView)findViewById(R.id.nama_siswa_dashboard);
        nama_sekolah_dashboard = (TextView)findViewById(R.id.nama_sekolah_dashboard);

        mAuth = FirebaseAuth.getInstance();

        navigationView.inflateMenu(R.menu.menu_navdraw);
        mViewHeader = navigationView.inflateHeaderView(R.layout.header_navigasi);

        nama_profile_nav_header = (TextView)mViewHeader.findViewById(R.id.nama_added_profile_header);
        sekolah_profile_nav_header = (TextView)mViewHeader.findViewById(R.id.sekolah_added_profile_header);

        relativeLayoutPilihProfil = (RelativeLayout) mViewHeader.findViewById(R.id.relativePilihProfil);
        listPilihProfil = (LinearLayout)findViewById(R.id.linearlayoutlistProfil);

        setSupportActionBar(toolbarhome);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipe = new ActionBarDrawerToggle(HomeActivity.this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(swipe);
        swipe.syncState();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        rvPilihProfile = (RecyclerView)findViewById(R.id.rvPilihProfil);
        rvPilihProfile.setLayoutManager(linearLayoutManager);

        animasi();
        getUserName();
        tampilData();

        relativeLayoutPilihProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihProfil();
            }
        });

        keluhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, KeluhActivity.class);
                startActivity(intent);
            }
        });

        /*
        spp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, EsPePeActivity.class);
                startActivity(intent);
            }
        });
        */

        absen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(HomeActivity.this,AbsenActivity.class));
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.Logout:
                        showDialog();
                        break;
                }

                return true;
            }
        });

    }

    private void animasi() {
        absen = findViewById(R.id.absensi);
        nilai = findViewById(R.id.nilai);
        spp = findViewById(R.id.espepe);
        keluhan = findViewById(R.id.keluhan);

        absen.setTranslationY(200);
        nilai.setTranslationY(200);

        spp.setTranslationY(-200);
        keluhan.setTranslationY(-200);

        absen.setAlpha(0);
        nilai.setAlpha(0);
        spp.setAlpha(0);
        keluhan.setAlpha(0);

        absen.animate().alpha(1).translationY(0).setDuration(500).setStartDelay(200).start();
        nilai.animate().alpha(1).translationY(0).setDuration(500).setStartDelay(200).start();
        spp.animate().alpha(1).translationY(0).setDuration(500).setStartDelay(200).start();
        keluhan.animate().alpha(1).translationY(0).setDuration(500).setStartDelay(200).start();
    }

    private void tampilData() {
        Query query = FirebaseDatabase.getInstance().getReference().child(Constants.USER_LOC); // ganti dengan path profile added

        FirebaseRecyclerOptions<Admin> adminFirebaseRecyclerOptions = // ganti <Admin> model profile added
                new FirebaseRecyclerOptions.Builder<Admin>()
                .setQuery(query, new SnapshotParser<Admin>() {
                    @NonNull
                    @Override
                    public Admin parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new Admin(snapshot.child(Constants.USER_EMAIL).getValue().toString(), //ganti dengan nama profile added
                                snapshot.child(Constants.USERNAME_LOC).getValue().toString()); // ganti dengan sekolah profile added
                    }
                })
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Admin, listProfileHeader>(adminFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull listProfileHeader listProfileHeader, int position, @NonNull Admin admin) {
                listProfileHeader.setEmailUser(admin.getEmailUser()); //ganti dengan set nama profile added
                listProfileHeader.setNamaUser(admin.getNamaUser()); //ganti dengan set sekolha profile added

                uid_profile_added = getRef(position).getKey();

                listProfileHeader.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mUserReference.child(uid_profile_added).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                nama_siswa_header = (String)dataSnapshot.child(Constants.USERNAME_LOC).getValue(); // ganti child() dengan nama_siswa/profile
                                nama_sekolah_header = (String)dataSnapshot.child(Constants.USER_EMAIL).getValue(); // ganti child() dengan nama_sekolah_siswa/profile

                                nama_profile_nav_header.setText(nama_siswa_header);
                                sekolah_profile_nav_header.setText(nama_sekolah_header);

                                nama_siswa_dashboard.setText(nama_siswa_header);
                                nama_sekolah_dashboard.setText(nama_sekolah_header);

                                drawerLayout.closeDrawers();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public listProfileHeader onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_profile_added, viewGroup, false);

                return new listProfileHeader(view);
            }
        };
        rvPilihProfile.setAdapter(firebaseRecyclerAdapter);

    }

    public static class listProfileHeader extends RecyclerView.ViewHolder{
        View mView;

        public listProfileHeader(@NonNull View itemView) {
            super(itemView);
            this.mView = itemView;
        }

        public void setNamaUser(String namaUser){ //ganti dengan nama profil added
            TextView nama_profil_added = (TextView)mView.findViewById(R.id.nama_profile_added);
            nama_profil_added.setText(namaUser);
        }

        public void setEmailUser(String emailUser){ // ganti dengan sekolah profil added
            TextView sekolah_profil_added = (TextView)mView.findViewById(R.id.sekolah_profile_added);
            sekolah_profil_added.setText(emailUser);
        }

    }

    private void pilihProfil() {
        if(!pickProfileshow){
            navigationView.getMenu().clear();
            listPilihProfil.setVisibility(View.VISIBLE);
            panahPilihProfil = (ImageView)navigationView.findViewById(R.id.pickArrowProfil);
            panahPilihProfil.setImageResource(R.drawable.ic_action_up);
            pickProfileshow = true;
        }else{
            navigationView.getMenu().clear();
            listPilihProfil.setVisibility(View.INVISIBLE);
            navigationView.inflateMenu(R.menu.menu_navdraw);
            panahPilihProfil = (ImageView)navigationView.findViewById(R.id.pickArrowProfil);
            panahPilihProfil.setImageResource(R.drawable.ic_action_down);
            pickProfileshow = false;
        }
    }

    private void getUserName() {
        mUserReference = FirebaseDatabase.getInstance().getReference(Constants.USER_LOC);
        varUserUidNow = mAuth.getCurrentUser().getUid();

        mUserReference.child(varUserUidNow).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                varUserNameNow = (String) dataSnapshot.child(Constants.USERNAME_LOC).getValue();

                Toast.makeText(HomeActivity.this, "Selamat Datang " +varUserNameNow, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        popDialog.setTitle("Keluar")
                .setMessage("Anda Ingin Keluar ?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAuth.signOut();
                        finish();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                popDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(swipe.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
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
