package com.soerdev.sims;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.soerdev.sims.app.AppController;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AbsenActivity extends AppCompatActivity implements LocationListener {

    ImageView img_absen;
    CardView submitAbsen, pilihGambar, lihatpeta;
    RelativeLayout enabledBTN, disableBTN;
    TextView lokasi1, lokasi2;
    Uri Fileurl, gmmIntentUri;
    Bitmap bitmap, decoded;
    LocationManager locationManager;
    SharedPreferences sharedPreferences;

    int success;
    int REQUEST_CAMERA = 0;
    int bitmap_size = 60;

    boolean clickableUpload = false;

    double latti, longi;

    private static final String TAG = AbsenActivity.class.getSimpleName();

    private String UPLOAD_URL = "https://backendservice.000webhostapp.com/backSIMS/upload_pict.php";

    private String username;
    private int id;

    private static String TAG_SUCCESS = "success";
    private static String TAG_MESSAGE = "message";

    private String deviceID;

    private String KEY_ID = "id";
    private String KEY_IMAGE = "image";
    private String KEY_NAMA = "username";
    private String KEY_KOORDINAT = "koordinat";
    private String KEY_DEVICE_ID = "id_device";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen_si);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_absen);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submitAbsen = (CardView) findViewById(R.id.submitAbsen);

        pilihGambar = (CardView) findViewById(R.id.ambilPhoto);

        img_absen = (ImageView) findViewById(R.id.img_absen);

        lokasi1 = (TextView) findViewById(R.id.lokasi1);
        lokasi2 = (TextView) findViewById(R.id.lokasi2);

        enabledBTN = findViewById(R.id.enabledCardView);
        disableBTN = findViewById(R.id.disabledCardView);

        sharedPreferences = getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);

        id = (sharedPreferences.getInt(KEY_ID, 0));
        username = (sharedPreferences.getString(KEY_NAMA, ""));

        deviceID  = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);

        checkGambar();
        //checkGPSisOnOff();

        submitAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadGambar();
            }
        });

        pilihGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Fileurl = getOutPutMediaFileURI();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Fileurl);
                startActivityForResult(intent, REQUEST_CAMERA);
                locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                getLocation();

            }
        });

        lihatpeta = (CardView) findViewById(R.id.ambilLokasi);

    }

    private void checkGPSisOnOff() {
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            enabledGPS();
        }
    }

    private void enabledGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS anda non - aktif, apakah anda ingin meng - aktifkanya ?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private Uri getOutPutMediaFileURI() {
        return Uri.fromFile(getOutPutMediaFile());
    }

    private static File getOutPutMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DeKa");
        if(!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdirs()){
                Log.e("Monitoring", "Oops, Failed Create Monitoring Directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_DeKa_" + timeStamp + ".jpg");

        return mediaFile;
    }

    private void getLocation (){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            ProgressDialog dialog = ProgressDialog.show(this, null, "Lihat Lokasi . . .", false, false);

            if (location !=null){
                dialog.dismiss();
                latti = location.getLatitude();
                longi = location.getLongitude();

                lokasi1.setText(""+latti);
                lokasi2.setText(""+longi);
            }
            else {
                lokasi2.setText("Nyalakan GPS Anda !");
                lokasi1.setText("");

                loc();
            }
        }
    }

    private void loc(){

        getLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case  1:
                getLocation();
                break;
        }
    }

    private void checkGambar() {
        if(img_absen.getDrawable() != null){
            submitAbsen.setEnabled(true);
            submitAbsen.setClickable(true);
            clickableUpload = true;
            enabledBTN.setVisibility(View.VISIBLE);
            disableBTN.setVisibility(View.INVISIBLE);
        }
        else{
            submitAbsen.setEnabled(false);
            submitAbsen.setClickable(false);
            clickableUpload = false;
            enabledBTN.setVisibility(View.INVISIBLE);
            disableBTN.setVisibility(View.VISIBLE);
        }
    }

    private void uploadGambar() {
        final ProgressDialog dialog = ProgressDialog.show(this, null, "Unggah Gambar . . .", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response: " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    success = jsonObject.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        Log.e("v Add", jsonObject.toString());

                        Toast.makeText(AbsenActivity.this, jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(AbsenActivity.this, jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();

                Toast.makeText(AbsenActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, error.getMessage().toString());
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                String koordinat = "geo:"+latti+","+longi;

                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_IMAGE, getStringImage(decoded));
                params.put(KEY_NAMA, username);
                params.put(KEY_KOORDINAT, koordinat);
                params.put(KEY_DEVICE_ID, deviceID);
                Log.e(TAG, "" + params);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImages = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImages;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult", "requestCode" +requestCode+ ", resultCode" + requestCode);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_CAMERA){
                try{
                    Log.e("CAMERA", Fileurl.getPath());

                    bitmap = BitmapFactory.decodeFile(Fileurl.getPath());
                    setToImageView(getResizedBitmap(bitmap, 512));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void setToImageView(Bitmap bmp){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        img_absen.setImageBitmap(decoded);
        checkGambar();
    }

    public Bitmap getResizedBitmap(Bitmap image, int max_size){
        int width = image.getWidth();
        int height = image.getHeight();
        Bitmap gambar = image;

        float bitmapRatio = (float)width / (float)height;
        if(bitmapRatio > 1){
            width = max_size;
            height = (int)(width / bitmapRatio);
        }else{
            height = max_size;
            width = (int)(height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(gambar, width, height, true);
    }

    @Override
    public void onLocationChanged(Location location) {
        final double longitude = location.getLongitude();
        final double lantitude = location.getLatitude();

        lokasi1.setText(""+lantitude);
        lokasi2.setText(""+longitude);

        /*
        lihatpeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri gmmIntentUri = Uri.parse("geo:"+lantitude+","+longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        */
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
