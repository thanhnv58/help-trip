package thanhnv.com.helpingtrips.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import thanhnv.com.helpingtrips.R;
import thanhnv.com.helpingtrips.databinding.ActivityMainBinding;
import thanhnv.com.helpingtrips.util.Constants;
import thanhnv.com.helpingtrips.util.Utils;
import thanhnv.com.helpingtrips.view.application.MyApplication;
import thanhnv.com.helpingtrips.view.dialog.CreateUserDialog;
import thanhnv.com.helpingtrips.view.handler.MainHandler;
import thanhnv.com.helpingtrips.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity implements MainHandler {
    private ActivityMainBinding dataBinding;
    private MainViewModel viewModel;

    private boolean locationPermissionGranted = false;

    private FusedLocationProviderClient fusedLocationProviderClient = null;

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

        // constructor FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // get location permission
        getLocationPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //dataBinding.recyclerView.smoothScrollToPosition(viewModel.items.size());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void pushLocation(View view) {
        if (!locationPermissionGranted) {
            getLocationPermission();
            return;
        }

        if (!Utils.notificationConnectNetwork(this)) {
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location lastKnowLocation = task.getResult();
                            MyApplication.lastLocation = new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
                            viewModel.pushLocationToServer(MyApplication.lastLocation);
                        }  else {
                            if (Utils.openGPS(MainActivity.this)) {
                                Utils.toastMessage(MainActivity.this, "Something went wrong!");
                            }
                        }
                    }
                });
    }

    @Override
    public void onClickGoMap(View view) {
        if (!Utils.notificationConnectNetwork(this)) {
            return;
        }

        // go to Map
        startActivity(new Intent(this, MapActivity.class));
    }

    @Override
    public void onClickAddFriend(final View view) {
        AddFriendSuccess addFriendSuccess = new AddFriendSuccess() {
            @Override
            public void onSuccess() {
                Utils.hideKeyboard(MainActivity.this);
            }
        };

        viewModel.addNewFriend(addFriendSuccess, new MainViewModel.OnAddFriend() {
            @Override
            public void onSuccess() {
                Utils.toastMessage(MainActivity.this, MainActivity.this.getResources().getString(R.string.add_friend_success));
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

    private void checkExistedUser() {
        if (MyApplication.applicationId == null) {
            showDialogCreateUser();
        }
    }

    private void showDialogCreateUser() {
        CreateUserDialog dialog = new CreateUserDialog(this, new CreateUserDialog.OnCreateUserDialogListener() {
            @Override
            public void onSuccess() {
                viewModel.applicationId.set(MyApplication.applicationId);
            }

            @Override
            public void onFail() {
                Utils.toastMessage(MainActivity.this, "Some thing went wrong!");
                finish();
            }
        });
        dialog.show();
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

    public interface AddFriendSuccess {
        void onSuccess();
    }
}
