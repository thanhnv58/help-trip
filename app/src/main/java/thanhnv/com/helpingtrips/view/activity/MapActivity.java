package thanhnv.com.helpingtrips.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import thanhnv.com.helpingtrips.view.dialog.ListFriendDialog;
import thanhnv.com.helpingtrips.view.handler.MapActivityHandler;
import thanhnv.com.helpingtrips.viewmodel.MapActivityViewModel;

/**
 * Created by Thanh on 3/6/2018.
 * MapActivity
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        MapActivityHandler, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {
    // Location permission granted
    private boolean locationPermissionGranted = false;

    // Google configuration
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Marker[] friendMarkers;
    private Polyline polyline;

    // logic of app
    private int selectedFriend = -1;
    private boolean isShowMyMarker = false;
    private boolean isPushLocation = false;
    private boolean isDirectoring = false;
    private boolean isOpenGPS = false;
    private boolean oneTime = true;

    // Google firebase configuration
    private List<MyFireBase.OnGetUserListener> gettingUserListeners = new ArrayList<>();

    // View model and data binding
    private MapActivityViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new MapActivityViewModel();

        ActivityMapBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        binding.setHandler(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        // constructor for friend's marker
        initFriendLocationFirebaseListener();

        // construtor google map
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
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

        // Prompt the user for permission.
        getLocationPermission();

        // Get my location
        if (!Utils.isGPSAvaiable(this)) {
            isShowMyMarker = true;
            isOpenGPS = false;
            enableLoc();
        } else {
            isOpenGPS = true;
            showMyLocation();
        }

        if (locationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerClickListener(this);

        mMap.setOnMapClickListener(this);

        // Get all friend from fire base and show on map
        viewModel.getFriendOnFirebase(gettingUserListeners);
    }

    @Override
    public void onClickFloatingButton(View view) {
        showListFriendDialog();
    }

    @Override
    public void onClickFloatingBtnPush(View view) {
        if (viewModel.isPushing.get()) {
            return;
        }

        if (!Utils.isNetworkAvailable(this)) {
            Utils.snackbarMessage(this, getResources().getString(R.string.open_network));
            return;
        }

        if (!Utils.isGPSAvaiable(this)) {
            isPushLocation = true;
            isOpenGPS = false;
            enableLoc();
        } else {
            isOpenGPS = true;
            viewModel.pushStart();
            pushYourLocation();
        }
    }

    @Override
    public void onClickFloatingBtnGo(View view) {
        if (isDirectoring) {
            return;
        }

        if (!Utils.isNetworkAvailable(this)) {
            Utils.snackbarMessage(this, getResources().getString(R.string.open_network));
            return;
        }

        if (selectedFriend == -1) {
            return;
        }

        if (polyline != null) {
            polyline.remove();
        }

        isDirectoring = true;
        directoryToFriend();
    }

    @Override
    public void onMapClick(LatLng latLng) {

        if (viewModel.showButton.get() && selectedFriend == -1) { // show + hide
            viewModel.settingFloatingButton();
        } else if (viewModel.showButton.get() && selectedFriend != -1) { // show + show
            viewModel.settingFloatingButton();
            viewModel.hideButtonGo();
            selectedFriend = -1;
        } else if (!viewModel.showButton.get() && selectedFriend == -1) { // hide + hide
            viewModel.settingFloatingButton();
        } else if (!viewModel.showButton.get() && selectedFriend != -1) { // hide + show
            viewModel.hideButtonGo();
            selectedFriend = -1;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();

        viewModel.showButtonGo();
        if (!viewModel.showButton.get()) {
            viewModel.settingFloatingButton();
        }

        selectedFriend = Integer.valueOf(marker.getSnippet());

        return true;
    }

    @SuppressLint("MissingPermission")
    public void getMyLocation(final OnGetMyLocationListener listener) {
        if (!locationPermissionGranted) {
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    LatLng lastLatLng = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                    if (listener != null) {
                        listener.onSuccess(lastLatLng);
                    }
                } else {
                    if (listener != null) {
                        listener.onFail();
                    }
                }
            }
        });
    }

    private void moveCameraToLocation(CameraPosition cameraPosition) {
        if (mMap == null) {
            return;
        }

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private Marker showMarker(Marker marker, MarkerOptions options, LatLng latLng, BitmapDescriptor markerIcon) {
        if (mMap == null) {
            return null;
        }

        if (marker == null) {
            marker = mMap.addMarker(options);
            marker.setIcon(markerIcon);
        } else {
            marker.setPosition(latLng);
            if (marker.isInfoWindowShown()) {
                marker.showInfoWindow();
            }
        }
        return marker;
    }

    private View createWindowInfoMarker(Marker marker) {
        // Inflate the layouts for the info window, title and snippet.
        @SuppressLint("InflateParams")
        View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        TextView title = infoWindow.findViewById(R.id.title);
        title.setText(marker.getTitle());
        return infoWindow;
    }

    private void initFriendLocationFirebaseListener() {
        int friendNumber = viewModel.getFriendNumber();
        friendMarkers = new Marker[friendNumber];
        for (int i = 0; i < friendNumber; i++) {
            final int finalI = i;

            gettingUserListeners.add(new MyFireBase.OnGetUserListener() {
                // data on firebase has changed
                @Override
                public void onSuccess(FirebaseUser user) {
                    updateFriendMarker(user, finalI);
                }

                @Override
                public void onFail(String error) {
                }
            });
        }
    }

    private void updateFriendMarker(FirebaseUser user, int which) {
        if (mMap == null) {
            return;
        }

        if (user == null) {
            return;
        }

        if (!Utils.checkLocation(user.getLatitude(), user.getLongitude())) {
            return;
        }

        LatLng friendLocation = new LatLng(user.getLatitude(), user.getLongitude());

        if (friendMarkers[which] == null) {
            MarkerOptions options = new MarkerOptions()
                    .title(viewModel.followingFriends.get(which).getName())
                    .snippet(which + "")
                    .position(friendLocation);
            int markerId = viewModel.getMarkerId(which);
            BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(Constants.LIST_ICON[markerId]);

            friendMarkers[which] = showMarker(friendMarkers[which], options, null, markerIcon);

            if (!isOpenGPS && MyApplication.lastLocation == null && oneTime) {
                oneTime = false;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(friendLocation)
                        .tilt(Constants.DEFAULT_TILT)
                        .zoom(Constants.DEFAULT_ZOOM)
                        .bearing(0)
                        .build();
                moveCameraToLocation(cameraPosition);
            }
        } else {
            friendMarkers[which] = showMarker(friendMarkers[which], null, friendLocation, null);
        }
    }

    private void showMyLocation() {
        getMyLocation(new OnGetMyLocationListener() {
            @Override
            public void onSuccess(LatLng latLng) {
                MyApplication.lastLocation = latLng;

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .tilt(Constants.DEFAULT_TILT)
                        .zoom(Constants.DEFAULT_ZOOM)
                        .bearing(0)
                        .build();
                moveCameraToLocation(cameraPosition);
            }

            @Override
            public void onFail() {
                if (isOpenGPS) {
                    showMyLocation();
                } else {
                    if (MyApplication.lastLocation != null) {
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(MyApplication.lastLocation)
                                .tilt(Constants.DEFAULT_TILT)
                                .zoom(Constants.DEFAULT_ZOOM)
                                .bearing(0)
                                .build();

                        moveCameraToLocation(cameraPosition);
                    } else {
                        Utils.toastMessage(MapActivity.this, getResources().getString(R.string.need_push_location));
                    }
                }
            }
        });
    }

    private void pushYourLocation() {
        getMyLocation(new OnGetMyLocationListener() {
            @Override
            public void onSuccess(LatLng latLng) {

                MyApplication.lastLocation = latLng;

                MyFireBase.OnWriteUserListener pushYourLocationListener = new MyFireBase.OnWriteUserListener() {
                    @Override
                    public void onSuccess() {
                        viewModel.pushDone();
                        Utils.toastMessage(MapActivity.this, getResources().getString(R.string.push_location_success));
                    }

                    @Override
                    public void onFail() {
                        viewModel.pushDone();
                        Utils.toastMessage(MapActivity.this, getResources().getString(R.string.push_location_fail));
                    }
                };

                viewModel.pushYourLocation(latLng.latitude, latLng.longitude, pushYourLocationListener);
            }

            @Override
            public void onFail() {
                if (isOpenGPS) {
                    pushYourLocation();
                } else {
                    viewModel.pushDone();
                    Utils.toastMessage(MapActivity.this, getResources().getString(R.string.need_push_location));
                }
            }
        });
    }

    private void showListFriendDialog() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectFriend(which);
            }
        };

        ListFriendDialog dialog = new ListFriendDialog(this, viewModel.getListFollowingUserName(), listener);
        dialog.show();
    }

    private void selectFriend(int which) {
        if (mMap == null) {
            return;
        }

        // select yourself
        if (friendMarkers[which] != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(friendMarkers[which].getPosition())
                    .tilt(Constants.DEFAULT_TILT)
                    .zoom(Constants.DEFAULT_ZOOM)
                    .bearing(0)
                    .build();

            moveCameraToLocation(cameraPosition);
            friendMarkers[which].showInfoWindow();

            viewModel.showButtonGo();
            selectedFriend = Integer.valueOf(friendMarkers[which].getSnippet());
        } else {
            Utils.toastMessage(this, getResources().getString(R.string.your_friend_not_push_location));
        }
    }

    private void directoryToFriend() {
        getMyLocation(new OnGetMyLocationListener() {
            @Override
            public void onSuccess(LatLng latLng) {
                MyApplication.lastLocation = latLng;
                directionService();
            }

            @Override
            public void onFail() {
                directionService();
            }
        });
    }

    private void directionService() {
        if (MyApplication.lastLocation == null) {
            Utils.toastMessage(this, getResources().getString(R.string.need_push_location));
            return;
        }

        LatLng destPosition = new LatLng(friendMarkers[selectedFriend].getPosition().latitude,
                friendMarkers[selectedFriend].getPosition().longitude);

        DirectionCallback callback = new DirectionCallback() {
            @Override
            public void onDirectionSuccess(Direction direction, String rawBody) {
                if (direction.isOK()) {
                    Route route = direction.getRouteList().get(0);
                    ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                    polyline = mMap.addPolyline(
                            DirectionConverter.createPolyline(MapActivity.this,
                                    directionPositionList, 5,
                                    MapActivity.this.getResources().getColor(R.color.colorPolyline)));
                    isDirectoring = false;
                } else {
                    Utils.toastMessage(MapActivity.this,
                            MapActivity.this.getResources().getString(R.string.direction_error));
                }
            }

            @Override
            public void onDirectionFailure(Throwable t) {
                Utils.toastMessage(MapActivity.this,
                        MapActivity.this.getResources().getString(R.string.direction_error));
            }
        };

        GoogleDirection.withServerKey(getResources().getString(R.string.GOOGLE_MAP_API_KEY))
                .from(MyApplication.lastLocation)
                .to(destPosition)
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.HIGHWAYS)
                .execute(callback);
    }

    /**
     * Created by Thanh on 3/6/2018.
     * OnGetMyLocationListener
     */
    private interface OnGetMyLocationListener {
        void onSuccess(LatLng latLng);
        void onFail();
    }

    private void enableLoc() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapActivity.this, Constants.REQUEST_LOCATION);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_LOCATION && isShowMyMarker) {
            isShowMyMarker = false;
            if (resultCode == RESULT_OK) {
                isOpenGPS = true;
            }
            showMyLocation();
        }

        if (requestCode == Constants.REQUEST_LOCATION && isPushLocation) {
            isPushLocation = false;
            if (resultCode == RESULT_OK) {
                isOpenGPS = true;
                viewModel.pushStart();
                pushYourLocation();
            } else {
                Utils.toastMessage(this, getResources().getString(R.string.cant_get_location));
            }

        }
    }
}
