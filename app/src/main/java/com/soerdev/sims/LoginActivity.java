package com.soerdev.sims;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.soerdev.sims.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    // private FirebaseAuth mAuth;

    private TextView daftarBtn;

    private String TAG = LoginActivity.class.getSimpleName();
    private String TAG_SUCCESS = "success";
    private String TAG_MESSAGE = "message";
    private String TAG_ID = "id";
    private String TAG_USERNAME = "username";
    private String TAG_PASS = "password";
    private String URL_LOGIN = "https://backendservice.000webhostapp.com/backSIMS/login.php";
    private String varEmail, varPass, id;

    int success;

    ConnectivityManager connectivityManager;

    Boolean session = false;

    Intent mIntent;

    private SharedPreferences sharedPreferences;

    private ProgressDialog progressDialog;
    private TextInputEditText loginEmail,loginPass;

    public static final String my_shared_preferences = "sims_preferences";
    public static final String session_status = "session_status";

    String json_obj_req = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        //mAuth = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.email_user_login);
        loginPass = findViewById(R.id.sandi_user_login);
        TextView daftarBtn = findViewById(R.id.daftar);
        Button loginSubmit = findViewById(R.id.kirim_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Silahkan Tunggu . . .");
        progressDialog.setCancelable(false);

        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager.getActiveNetworkInfo().isConnected()){

            }else{
                Toast.makeText(getApplicationContext(), "Internet Tidak Terhubung", Toast.LENGTH_SHORT).show();
            }
        }

        sharedPreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedPreferences.getBoolean(session_status, false);
        id = sharedPreferences.getString(TAG_ID, null);
        varEmail = sharedPreferences.getString(TAG_USERNAME, null);

        if (session){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra(TAG_ID, id);
            intent.putExtra(TAG_USERNAME, varEmail);
            startActivity(intent);
            finish();
        }

        /*
        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        */
        daftarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, DaftarActivity.class);
                startActivity(intent);
            }
        });

        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager.getActiveNetworkInfo().isConnected()){
                    cekLoginForm();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Tidak Ada Koneksi !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cekLoginForm() {
        varEmail = loginEmail.getText().toString().trim();
        varPass = loginPass.getText().toString().trim();

        if(TextUtils.isEmpty(varEmail)){
            Toast.makeText(LoginActivity.this, "Masukkan username anda !", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(varPass)){
            Toast.makeText(LoginActivity.this, "Masukkan kata sandi anda !", Toast.LENGTH_SHORT).show();
        }
        else{
            progressDialog.show();
            loginAkun();
            /*
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
                    });*/
        }
    }

    private void loginAkun() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response: " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    success = jsonObject.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        Log.e("Registration Success !", jsonObject.toString());

                        Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_ID, id);
                        editor.putString(TAG_USERNAME, varEmail); //ganti dengan username
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra(TAG_ID, id);
                        intent.putExtra(TAG_USERNAME, varEmail); //ganti dengan username
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString(TAG_MESSAGE) + ", coba lagi", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error" + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String>getParams(){
                Map<String, String> params = new HashMap<String, String>();

                params.put(TAG_USERNAME, varEmail); //ganti dengan username
                params.put(TAG_PASS, varPass);
                Log.e(TAG, "" + params);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, json_obj_req);
    }
}
