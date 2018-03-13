package thanhnv.com.helpingtrips.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import thanhnv.com.helpingtrips.R;
import thanhnv.com.helpingtrips.data.remote.FirebaseUser;
import thanhnv.com.helpingtrips.databinding.ActivityMapBinding;
import thanhnv.com.helpingtrips.util.Constants;
import thanhnv.com.helpingtrips.util.Utils;
import thanhnv.com.helpingtrips.util.firebase.MyFireBase;
import thanhnv.com.helpingtrips.view.application.MyApplication;
import thanhnv.com.helpingtrips.view.handler.MapActivityHandler;
import thanhnv.com.helpingtrips.viewmodel.MapActivityViewModel;

/**
 * Created by Thanh on 3/6/2018.
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        MapActivityHandler, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {
    private static final String TAG = "MapActivity";

    // Location permission granted
    private boolean locationPermissionGranted = false;

    // Google configuration
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    //private Location lastKnowLocation;
    private Marker[] markers; // list following friend marker
    private Marker myMarker; // your marker
    private Polyline polyline; // draw directory

    //private LocationRequest locationRequest; // condition for location change
    private int selectedFriend = -1;

    // Google firebase configuration
    private List<MyFireBase.OnGetUserListener> gettingUserListeners = new ArrayList<>();

    // View model
    private MapActivityViewModel viewModel;
    private ActivityMapBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new MapActivityViewModel();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        binding.setHandler(this);
        binding.setViewModel(viewModel);

        // constructor for friend's marker
        initFriendLocationUpdateListener();
        viewModel.setListeners(gettingUserListeners);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // custom info window of marker
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            // Return null here, so that getInfoContents() is called next.
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return createWindowInfoMarker(marker);
            }
        });

        mMap.setOnMarkerClickListener(this);

        mMap.setOnMapClickListener(this);

        // Prompt the user for permission.
        getLocationPermission();

        // Get all friend and show on map
        viewModel.showAllFollowingFriends();

        // show your marker
        showYourMarker();

        // update your location
        //initLocationUpdateRequest();
        //requestUpdateLocation();
    }

    @SuppressLint("MissingPermission")
    private void showYourMarker() {
        if (MyApplication.lastLocation == null) {
            Utils.toastMessage(this, MapActivity.this.getResources().getString(R.string.need_push_location));
            return;
        }

        MarkerOptions options = new MarkerOptions().title(Constants.YOU).position(MyApplication.lastLocation);
        myMarker = mMap.addMarker(options);
        myMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyApplication.lastLocation, Constants.DEFAULT_ZOOM));
    }

    // Click on map
    @Override
    public void onMapClick(LatLng latLng) {
        viewModel.clickMap();
        selectedFriend = -1;
    }

    // Click on marker
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();

        // click yourself
        if (marker.getTitle().equals(Constants.YOU)) {
            viewModel.clickYou();
        } else { // click your friend
            viewModel.clickFriend();
            selectedFriend = Integer.valueOf(marker.getSnippet());
        }
        return true;
    }

    private View createWindowInfoMarker(Marker marker) {
        // Inflate the layouts for the info window, title and snippet.
        View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        TextView title = infoWindow.findViewById(R.id.title);
        title.setText(marker.getTitle());
        return infoWindow;
    }

    private void initFriendLocationUpdateListener() {
        markers = new Marker[viewModel.friendNumber];
        for (int i = 0; i < viewModel.friendNumber; i++) {
            final int finalI = i;
            gettingUserListeners.add(new MyFireBase.OnGetUserListener() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    updateFriendLocation(user, finalI);
                }

                @Override
                public void onFail(String error) {
                    Log.e(TAG, "GET user FROM firebase: " + error);
                }
            });
        }
    }

    private void updateFriendLocation(FirebaseUser user, int which) {
        if (user == null) {
            return;
        }

        if (!Utils.checkLocation(user.getLatitude(), user.getLongitude())) {
            return;
        }

        LatLng friendLocation = new LatLng(user.getLatitude(), user.getLongitude());

        if (markers[which] == null) {
            MarkerOptions options = new MarkerOptions()
                    .title(viewModel.followingFriends.get(which).getName())
                    .snippet(which + "")
                    .position(friendLocation);
            markers[which] = mMap.addMarker(options);
//            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//            Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
//            Canvas canvas1 = new Canvas(bmp);
//
//            Paint color = new Paint();
//            color.setTextSize(35);
//            color.setColor(Color.BLACK);
//
//            canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker_1), 0,0, color);
//            canvas1.drawText("User Name!", 30, 40, color);

            markers[which].setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_1));
        } else {
            markers[which].hideInfoWindow();
            markers[which].setPosition(friendLocation);
            if (which == selectedFriend) {
                markers[which].showInfoWindow();
                viewModel.clickFriend();
            }
        }
    }

    /*private void initLocationUpdateRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(Constants.DEFAULT_TIME_UPDATE_LOCATION);
        locationRequest.setFastestInterval(Constants.DEFAULT_TIME_UPDATE_LOCATION);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    private void requestUpdateLocation() {
        if (!locationPermissionGranted) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                lastKnowLocation = location;
                LatLng latLng = new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());

                if (myMarker == null) {
                    // create new your location
                    myMarker = mMap.addMarker(new MarkerOptions()
                            .title("You")
                            .position(latLng));
                    myMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_ZOOM));
                    mMap.setMyLocationEnabled(true);
                } else {
                    //Update your location
                    myMarker.setPosition(latLng);
                }
            }
        }
    };*/

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.LOCATION_PERMISION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.LOCATION_PERMISION_REQUEST_CODE:
                locationPermissionGranted = true;
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionGranted = false;
                            break;
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClickFloatingButton(View view) {
        switch (viewModel.mode.get()) {
            case Constants.MODE_FRIEND:
                if (Utils.notificationConnectNetwork(this)) {
                    directoryToFriend();
                }
                break;
            case Constants.MODE_MAP:
                openUserListDialog();
                break;
            case Constants.MODE_YOU:
                if (Utils.notificationConnectNetwork(this)) {
                    pushYourLocation();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClickFloatingBtnPush(View view) {
        if (Utils.notificationConnectNetwork(this)) {
            pushYourLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void pushYourLocation() {
        if (!locationPermissionGranted) {
            getLocationPermission();
            return;
        }

        if (!Utils.notificationConnectNetwork(this)) {
            return;
        }

        final MyFireBase.OnWriteUserListener pushYourLocationListener = new MyFireBase.OnWriteUserListener() {
            @Override
            public void onSuccess() {
                Utils.toastMessage(MapActivity.this, "Push successfully");
            }

            @Override
            public void onFail() {
                Utils.toastMessage(MapActivity.this, "Push fail!!!");
            }
        };

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    LatLng lastLatLng = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                    MyApplication.lastLocation = lastLatLng;
                    viewModel.pushYourLocation(lastLatLng.latitude, lastLatLng.longitude, pushYourLocationListener);
                    if (myMarker == null) {
                        MarkerOptions options = new MarkerOptions().title(Constants.YOU).position(MyApplication.lastLocation);
                        myMarker = mMap.addMarker(options);
                        myMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyApplication.lastLocation, Constants.DEFAULT_ZOOM));
                        if (locationPermissionGranted) {
                            mMap.setMyLocationEnabled(true);
                        }
                    } else {
                        myMarker.setPosition(MyApplication.lastLocation);
                    }
                }  else {
                    if (Utils.openGPS(MapActivity.this)) {
                        Utils.toastMessage(MapActivity.this, "Something went wrong!");
                    }
                }
            }
        });
    }

    private void openUserListDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mMap == null) {
                    return;
                }

                if (which == 0) { // you
                    if (myMarker != null) {
                        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(myMarker.getPosition(), Constants.DEFAULT_ZOOM);
                        mMap.animateCamera(location);
                        myMarker.showInfoWindow();
                    } else {
                        Utils.toastMessage(MapActivity.this, MapActivity.this.getResources().getString(R.string.need_push_location));
                    }
                } else {
                    if (markers[which - 1] != null) { // your friend
                        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(markers[which - 1].getPosition(), Constants.DEFAULT_ZOOM);
                        mMap.animateCamera(location);
                        markers[which - 1].showInfoWindow();
                        viewModel.clickFriend();
                        selectedFriend = Integer.valueOf(markers[which - 1].getSnippet());
                    } else {
                        Utils.toastMessage(MapActivity.this, MapActivity.this.getResources().getString(R.string.your_friend_not_push_location));
                    }
                }
            }
        };

        String[] followingFriends = viewModel.getListFollowingUserName();

        // Display the dialog.
        new AlertDialog.Builder(this)
                .setTitle("Following friends (" + viewModel.friendNumber + ")")
                .setItems(followingFriends, listener)
                .show();
    }

    private void directoryToFriend() {
        if (selectedFriend == -1) {
            return;
        }
        if (polyline != null) {
            polyline.remove();
        }

        if (MyApplication.lastLocation == null) {
            Utils.toastMessage(this, MapActivity.this.getResources().getString(R.string.need_push_location));
            viewModel.clickMap();
            return;
        }

        LatLng destPosition = new LatLng(markers[selectedFriend].getPosition().latitude, markers[selectedFriend].getPosition().longitude);

        DirectionCallback callback = new DirectionCallback() {
            @Override
            public void onDirectionSuccess(Direction direction, String rawBody) {
                if (direction.isOK()) {
                    Route route = direction.getRouteList().get(0);
                    ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                    polyline = mMap.addPolyline(DirectionConverter.createPolyline(MapActivity.this, directionPositionList, 5, MapActivity.this.getResources().getColor(R.color.colorPolyline)));
                    viewModel.clickMap();
                } else {
                    Utils.toastMessage(MapActivity.this, MapActivity.this.getResources().getString(R.string.directoration_error));
                    Log.d(TAG, direction.getStatus());
                    Log.d(TAG, direction.getErrorMessage());
                }
            }

            @Override
            public void onDirectionFailure(Throwable t) {
                Utils.toastMessage(MapActivity.this, "Cant load direction!");
            }
        };

        GoogleDirection.withServerKey(getResources().getString(R.string.GOOGLE_MAP_API_KEY))
                .from(MyApplication.lastLocation)
                .to(destPosition)
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.HIGHWAYS)
                .execute(callback);
    }
}
