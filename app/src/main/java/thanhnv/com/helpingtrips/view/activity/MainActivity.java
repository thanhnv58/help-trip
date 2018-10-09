package thanhnv.com.helpingtrips.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.Timed;
import thanhnv.com.helpingtrips.R;
import thanhnv.com.helpingtrips.data.model.User;
import thanhnv.com.helpingtrips.databinding.ActivityMainBinding;
import thanhnv.com.helpingtrips.util.Constants;
import thanhnv.com.helpingtrips.util.Utils;
import thanhnv.com.helpingtrips.util.sharedpreference.MySharedPreference;
import thanhnv.com.helpingtrips.view.application.MyApplication;
import thanhnv.com.helpingtrips.view.dialog.AddFriendDialog;
import thanhnv.com.helpingtrips.view.dialog.CreateNewUserDialog;
import thanhnv.com.helpingtrips.view.handler.MainHandler;
import thanhnv.com.helpingtrips.viewmodel.MainViewModel;

/**
 * Created by Thanh on 3/6/2018.
 * MainActivity
 */
public class MainActivity extends AppCompatActivity implements MainHandler, View.OnClickListener {
    private MainViewModel viewModel;
    private ActivityMainBinding dataBinding;

    private boolean locationPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private boolean isPushLocation = false; // check onResultActivity
    private boolean isAutoPush = false;

    private Disposable timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new MainViewModel(this);

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        dataBinding.setHandler(this);
        dataBinding.setViewModel(viewModel);
        dataBinding.executePendingBindings();

        // check the first using
        checkExistedUser();

        // get location permission
        getLocationPermission();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        dataBinding.container.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        viewModel.settingShowAddFriend();
    }

    private void checkExistedUser() {
        if (MyApplication.applicationId == null) {
            showDialogCreateUser();
        }
    }

    private void showDialogCreateUser() {
        CreateNewUserDialog dialog = new CreateNewUserDialog(this);

        dialog.setOnDialogCreateNewUserListener(new CreateNewUserDialog.OnCreateNewUserDialogListener() {
            @Override
            public void onFail() {
                finish();
            }

            @Override
            public void onSuccess(String newId) {
                viewModel.createNewUser(newId);
            }
        });

        dialog.show();
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
                        resolvable.startResolutionForResult(MainActivity.this, Constants.REQUEST_LOCATION);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    public void onClickPushLocation(View view) {
        if (viewModel.isPushing.get()) {
            return;
        }

        if (!locationPermissionGranted) {
            getLocationPermission();
            return;
        }

        if (!Utils.isNetworkAvailable(this)) {
            Utils.snackbarMessage(this, getResources().getString(R.string.open_network));
            return;
        }

        if (!Utils.isGPSAvaiable(MainActivity.this)) {
            isPushLocation = true;
            enableLoc();
        } else {
            viewModel.pushStart();
            pushLocation();
        }
    }

    @Override
    public void onClickAddFriend(final View view) {
        final AddFriendDialog dialog = new AddFriendDialog(this);
        dialog.setOnDialogAddFriendListener(new AddFriendDialog.OnDialogAddFriendListener() {
            @Override
            public void onSuccess(User user) {
                viewModel.addFriend(user);
                dataBinding.recyclerView.smoothScrollToPosition(viewModel.getSizeItems());
            }
        });
        dialog.show();
    }

    @Override
    public void onClickGoMap(View view) {
        if (!Utils.isNetworkAvailable(this)) {
            Utils.snackbarMessage(this, getResources().getString(R.string.open_network));
            return;
        }

        // go to Map
        startActivity(new Intent(this, MapActivity.class));
    }

    @Override
    public void onAutoPushLocation(View view, boolean checked) {
        if (viewModel.isAutoPushLocation.get()) {
            // view model: true - switch: false
            if (!checked) {
                offPushAutomatically();
                Utils.toastMessage(this, getResources().getString(R.string.off_push_auto));
            }
        } else {
            // view model: false - switch: true
            if (checked) {
                // check gps is open
                if (!Utils.isGPSAvaiable(this)) {
                    // show dialog gps
                    isAutoPush = true;
                    enableLoc();
                } else {
                    onPushAutomatically();
                }
            } else {
                Utils.toastMessage(MainActivity.this, getResources().getString(R.string.cant_get_location));
            }
        }
    }

    private void pushLocation() {
        // get your location then push to server
        getYourLocation(new OnGetLocationListener() {
            @Override
            public void onSuccess(LatLng latLng) {
                MyApplication.lastLocation = latLng;

                viewModel.pushLocationToServer(latLng, new MainViewModel.OnPushLocationListener() {
                    @Override
                    public void pushSuccess() {
                        viewModel.pushDone();
                        Utils.toastMessage(MainActivity.this, getResources().getString(R.string.push_location_success));
                    }

                    @Override
                    public void pushFail() {
                        viewModel.pushDone();
                        Utils.toastMessage(MainActivity.this, getResources().getString(R.string.push_location_fail));
                    }
                });
            }

            @Override
            public void onFail() {
                // push again....
                pushLocation();
            }
        });
    }

    private void pushLocationBackground() {
        //Log.d("thanhnv1234", "push background");
        // get your location then push to server
        getYourLocation(new OnGetLocationListener() {
            @Override
            public void onSuccess(LatLng latLng) {
                MyApplication.lastLocation = latLng;
                viewModel.pushLocationToServer(latLng, null);
            }

            @Override
            public void onFail() {
                // push again....
                pushLocationBackground();
            }
        });
    }

    private void onPushAutomatically() {
        Utils.toastMessage(this, getResources().getString(R.string.on_push_auto));

        viewModel.isAutoPushLocation.set(true);

        timer = Observable.interval(0, Constants.GAP_OF_PUSH_AUTO_SECOND, TimeUnit.SECONDS)
                .timeInterval()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Timed<Long>>() {
                    @Override
                    public void accept(@NonNull Timed<Long> longTimed) throws Exception {
                        // push your location
                        pushLocationBackground();
                    }
                });
    }

    private void offPushAutomatically() {
        viewModel.isAutoPushLocation.set(false);
        if (timer != null) {
            timer.dispose();
        }
    }

    @SuppressLint("MissingPermission")
    private void getYourLocation(final OnGetLocationListener listener) {
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location lastKnowLocation = task.getResult();
                            LatLng latLng = new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());

                            if (listener != null) {
                                listener.onSuccess(latLng);
                            }
                        } else {
                            if (listener != null) {
                                listener.onFail();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    @Override
    protected void onStop() {
        if (MyApplication.lastLocation != null) {
            MySharedPreference.getInstance().setMyLocation(MyApplication.lastLocation);
        }
        unregisterReceiver(mGpsSwitchStateReceiver);
        super.onStop();
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
        if (requestCode == Constants.REQUEST_LOCATION && isPushLocation) {
            isPushLocation = false;
            if (resultCode == RESULT_OK) {
                viewModel.pushStart();
                pushLocation();
            } else {
                Utils.toastMessage(MainActivity.this, getResources().getString(R.string.cant_get_location));
            }
        }

        if (requestCode == Constants.REQUEST_LOCATION && isAutoPush) {
            isAutoPush = false;
            if (resultCode == RESULT_OK) {
                onPushAutomatically();
            } else {
                dataBinding.toggle.setChecked(false);
            }
        }
    }

    private BroadcastReceiver mGpsSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                if (viewModel.isAutoPushLocation.get()) {
                    dataBinding.toggle.setChecked(false);
                }
            }
        }
    };

    /**
     * Created by Thanh on 3/6/2018.
     * OnGetLocationListener
     */
    private interface OnGetLocationListener {
        void onSuccess(LatLng latLng);
        void onFail();
    }
}
