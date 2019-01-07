package com.soerdev.sims;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import com.soerdev.sims.app.AppController;
import com.soerdev.sims.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DaftarActivity extends AppCompatActivity {


    private String varemailUser, varpassUser, varkonfPass;

    private TextInputEditText emailDaftar, sandiDaftar, namaUser;
    private ProgressDialog progressDialog;

    private String URL_DAFTAR = "https://backendservice.000webhostapp.com/backSIMS/register.php";

    int success;
    ConnectivityManager connectivityManager;

    private static String TAG = DaftarActivity.class.getSimpleName();

    private String TAG_SUCCESS = "success";
    private String TAG_MESSAGE = "message";

    private String KEY_USERNAME = "username";
    private String KEY_PASSWORD = "password";
    private String KEY_CONFIRMPASS = "con_pass";

    String json_obj_req = "json_obj_req";

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

                connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);{
                    if(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager.getActiveNetworkInfo().isConnected()){
                        cekNIntent();
                    }else{
                        Toast.makeText(getApplicationContext(), "Tidak Ada Koneksi Internet !", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Tunggu Sebentar . . .");
        progressDialog.setCancelable(false);
    }

    private void cekNIntent() {
        varemailUser = emailDaftar.getText().toString().trim();
        varpassUser = sandiDaftar.getText().toString().trim();
        varkonfPass = namaUser.getText().toString().trim();

        if(TextUtils.isEmpty(varemailUser)){
            Toast.makeText(DaftarActivity.this, "Masukkan E - mail Anda !", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(varpassUser)){
            Toast.makeText(DaftarActivity.this, "Masukkan Kata Sandi Anda !", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(varkonfPass)){
            Toast.makeText(DaftarActivity.this, "Ketik Ulang Kata Sandi Anda !", Toast.LENGTH_SHORT).show();
        }
        else{
            if(varpassUser.length() < 8 ){
                Toast.makeText(DaftarActivity.this, "Kata Sandi Minimal 8 Karakter !", Toast.LENGTH_SHORT).show();
            }
            else{
                progressDialog.show();
                buatAkun();
            }
        }
    }

    private void buatAkun() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DAFTAR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response:" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    success = jsonObject.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        Log.e("Registration Success !", jsonObject.toString());

                        Toast.makeText(getApplicationContext(), jsonObject.getString(TAG_MESSAGE) + ", silahkan login !", Toast.LENGTH_SHORT).show();

                        emailDaftar.setText("");
                        sandiDaftar.setText("");
                        namaUser.setText("");

                        Intent intent = new Intent(DaftarActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString(TAG_MESSAGE) + ", coba lagi !", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                Toast.makeText(DaftarActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, error.getMessage().toString());
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_USERNAME, varemailUser);
                params.put(KEY_PASSWORD, varpassUser);
                params.put(KEY_CONFIRMPASS, varkonfPass);
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, json_obj_req);

        /*
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

        */
    }
}
