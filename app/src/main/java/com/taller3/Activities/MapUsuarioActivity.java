package com.taller3.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taller3.Module.User;
import com.taller3.R;
import com.taller3.databinding.ActivityMainBinding;
import com.taller3.databinding.ActivityMapUsuarioBinding;

public class MapUsuarioActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapUsuarioBinding binding;
    private FusedLocationProviderClient mLocationClient;
    static final int LOCATION_REQUEST = 3;
    static final String permLocation = Manifest.permission.ACCESS_FINE_LOCATION;
    private LocationRequest locationRequest;
    private LocationCallback callback;
    LatLng location;
    Task<LocationSettingsResponse> task;
    Marker position;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference ref;
    private ValueEventListener myListener;
    User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_map_usuario);
        requestPermission(this,permLocation,"We need location",LOCATION_REQUEST);
        inflate();
        encontrarUsuario();
        checkGPS();


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User myUser= snapshot.getValue(User.class);
                    Log.i("TAG", "Encontró usuario: " + myUser.getName());
                    String name = myUser.getName();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DB","Error de consulta");
            }
        });

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });

        callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location loc = locationResult.getLastLocation();
                location = new LatLng(loc.getLatitude(), loc.getLongitude());
                if(position != null) {
                    position.remove();
                }
                position = mMap.addMarker(new MarkerOptions().position(location).title("Estas aqui"));
            }
        };
    }
    private void startLocationUpdates() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), permLocation) ==
                PackageManager.PERMISSION_GRANTED) {
            mLocationClient.requestLocationUpdates(locationRequest, callback, null);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }
    private void encontrarUsuario()
    {
        String idSent;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idSent = extras.getString("key");
            //The key argument here must match that used in the other activity
        }
        else
        {
            idSent = "";
        }
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User myUser2= snapshot.getValue(User.class);
                    if (dataSnapshot.getKey() == idSent)
                    {
                        Log.i("TAG", "Encontró usuario: " + myUser2.getName());
                        myUser = myUser2;
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DB","Error de consulta");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        initializeMarker();
        startLocationUpdates();
    }
    private void stopLocationUpdates(){
        mLocationClient.removeLocationUpdates(callback);
    }

    private void requestPermission(Activity context, String permission, String justification, int id){
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(context, justification, Toast.LENGTH_SHORT).show();
            }
            // request the permission.
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);
        }
    }
    private void initializeMarker()
    {
        if (ActivityCompat.checkSelfPermission(this, permLocation)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location loc) {
                    if (loc != null) {
                        location = new LatLng(loc.getLatitude(), loc.getLongitude());
                        position = mMap.addMarker(new MarkerOptions().position(location).title("Estas aqui"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                        mMap.getUiSettings().setZoomGesturesEnabled(true);
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
                    }
                }
            });
        }
    }
    private LocationRequest createLocationRequest(){
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
    private void checkGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        task = client.checkLocationSettings(builder.build());
    }
    private void inflate() {
        binding = ActivityMapUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = createLocationRequest();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("Users");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemClicked= item.getItemId();
        if(itemClicked== R.id.menuLogOut) {
            mAuth.signOut();
            Intent intent = new Intent(this, LogInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if (itemClicked == R.id.menuState){
            ref.child(user.getUid()).child("state").setValue("available");
        }
        return super.onOptionsItemSelected(item);
    }
}
