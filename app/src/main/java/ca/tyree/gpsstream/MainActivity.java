package ca.tyree.gpsstream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSONS_FINE_LOCATION = 99;
    TextView td_lat, td_long, td_alti, td_accur, td_speed, td_locationGeocode, tl_batterySaver, tl_locationStream;
    Switch sw_locationUpdate, sw_batterySaver;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    boolean updatebyDefault = false;



//Program Start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Variables TextData (td)
        td_lat = findViewById(R.id.td_lat);
        td_long = findViewById(R.id.td_long);
        td_alti = findViewById(R.id.td_alti);
        td_accur = findViewById(R.id.td_accur);
        td_speed = findViewById(R.id.td_speed);
        td_locationGeocode = findViewById(R.id.td_locationGeocode);
        //SwitchLabels
        tl_locationStream = findViewById(R.id.tl_locationStream);
        tl_batterySaver = findViewById(R.id.tl_batterySaver);
        //Switch Variables (sw)
        sw_locationUpdate = findViewById(R.id.sw_locationUpdate);
        sw_batterySaver = findViewById(R.id.sw_batterySaver);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUIValues(locationResult.getLastLocation());
            }
        };
        sw_batterySaver.setChecked(true);
        sw_locationUpdate.setChecked(false);
        sw_batterySaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sw_batterySaver.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tl_batterySaver.setText("Battery Saver Off");
                }
                else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tl_batterySaver.setText("Battery Saver On");
                }
            }
        });
        sw_locationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sw_locationUpdate.isChecked()) {
                    stopLocationUpdates();
                }
                else {
                    startLocationUpdates();
                }
            }
        });
    }





//Stop
    private void stopLocationUpdates() {
        tl_locationStream.setText("Location Stream Off");
        td_long.setText("null");
        td_lat.setText("null");
        td_speed.setText("null");
        td_accur.setText("null");
        td_alti.setText("null");
        td_locationGeocode.setText("null");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }




//Start
    private void startLocationUpdates() {
        tl_locationStream.setText("Location Stream On");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
        updateGPS();
    }





//RequestPerms
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateGPS();
            }
                else {
                Toast.makeText(this, "This App Requires Location Services Permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
        }
    }





//Program Entry
    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValues(location);
                }
            });
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSONS_FINE_LOCATION);
            }
        }
    }






//Update Values
    private void updateUIValues(Location location) {
        td_lat.setText(String.valueOf(location.getLatitude()));
        td_long.setText(String.valueOf(location.getLongitude()));
        td_accur.setText(String.valueOf(location.getAccuracy()));
        //Check Altitude
        if (location.hasAltitude()){
            td_alti.setText(String.valueOf(location.getAltitude()));
        }
        else {
            td_alti.setText("Unavailable");
        }
        //Check Speed
        if (location.hasSpeed()){
            td_speed.setText(String.valueOf(location.getSpeed()));
        }
        else {
            td_alti.setText("Unavailable");
        }
        //Get Address From Coords
        Geocoder geocoder = new Geocoder(MainActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            td_locationGeocode.setText(addresses.get(0).getAddressLine(0));
        }
        catch (Exception e) {
            td_locationGeocode.setText("Unavailable");
        }
    }
}