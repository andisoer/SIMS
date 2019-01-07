package com.soerdev.sims;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText loginEmail,loginPass;
    private FirebaseAuth mAuth;
    private String varEmail, varPass;
    private TextView daftarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        mAuth = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.email_user_login);
        loginPass = findViewById(R.id.sandi_user_login);
        TextView daftarBtn = findViewById(R.id.daftar);
        Button kirimnohape = findViewById(R.id.kirim_login);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Silahkan Tunggu . . .");
        progressDialog.setCancelable(true);

        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        daftarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, DaftarActivity.class);
                startActivity(intent);
            }
        });

        kirimnohape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                varEmail = loginEmail.getText().toString().trim();
                varPass = loginPass.getText().toString().trim();

                if(TextUtils.isEmpty(varEmail)){
                    Toast.makeText(LoginActivity.this, "Masukkan e - mail anda !", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(varPass)){
                    Toast.makeText(LoginActivity.this, "Masukkan kata sandi anda !", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(varEmail, varPass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this, "User Tidak Ditemukan", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });
    }
}
