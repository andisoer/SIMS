package com.soerdev.sims;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.soerdev.sims.utils.Constants;

import java.util.concurrent.TimeUnit;

public class DaftarActivity extends AppCompatActivity {


    private String varemailUser, varpassUser, varNamaUser;
    private TextInputEditText emailDaftar, sandiDaftar, namaUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_hp);

        mAuth = FirebaseAuth.getInstance();

        emailDaftar = findViewById(R.id.emailUserDaftar);
        sandiDaftar = findViewById(R.id.sandiUserDaftar);
        namaUser = findViewById(R.id.namaUser);

        Button kirimKode = findViewById(R.id.daftarAkun);

        kirimKode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cekNIntent();
            }
        });

    }

    private void cekNIntent() {
        varemailUser = emailDaftar.getText().toString().trim();
        varpassUser = sandiDaftar.getText().toString().trim();
        varNamaUser = namaUser.getText().toString().trim();

        if(TextUtils.isEmpty(varemailUser)){
            Toast.makeText(DaftarActivity.this, "Masukkan E - mail Anda !", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(varpassUser)){
            Toast.makeText(DaftarActivity.this, "Masukkan Kata Sandi Anda !", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(varNamaUser)){
            Toast.makeText(DaftarActivity.this, "Masukkan Nama Anda !", Toast.LENGTH_SHORT).show();
        }
        else{
            if(varpassUser.length() < 8 ){
                Toast.makeText(DaftarActivity.this, "Kata Sandi Minimal 8 Karakter !", Toast.LENGTH_SHORT).show();
            }
            else{
                progressDialog();
                buatAkun();
            }
        }
    }

    private void buatAkun() {
        mAuth.createUserWithEmailAndPassword(varemailUser, varpassUser)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            UserRegClass userRegClass = new UserRegClass(varemailUser, varNamaUser, Constants.Privillage);
                            FirebaseDatabase.getInstance().getReference("User")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .setValue(userRegClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(DaftarActivity.this, "Autentikasi Berhasil", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(DaftarActivity.this, HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(DaftarActivity.this, "Terjadi Kesalahan, Silahkan Coba Lagi . . .", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(DaftarActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void progressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Tunggu Sebentar . . .");
        progressDialog.show();
        progressDialog.setCancelable(true);
    }
}
