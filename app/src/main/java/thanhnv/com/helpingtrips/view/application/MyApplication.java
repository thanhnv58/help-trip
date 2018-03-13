package thanhnv.com.helpingtrips.view.application;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import thanhnv.com.helpingtrips.data.database.UserDatabaseManager;
import thanhnv.com.helpingtrips.data.model.User;
import thanhnv.com.helpingtrips.data.remote.FirebaseUser;
import thanhnv.com.helpingtrips.util.Utils;
import thanhnv.com.helpingtrips.util.firebase.MyFireBase;
import thanhnv.com.helpingtrips.view.sharedPreference.MySharedPreference;

/**
 * Created by Thanh on 3/1/2018.
 */

public class MyApplication extends Application {

    public static String applicationId;
    public static List<User> yourFriends;
    public static LatLng lastLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        // Create shared preferences
        MySharedPreference.getInstance().init(this);
        UserDatabaseManager.getInstance().init(this);

        applicationId = MySharedPreference.getInstance().getApplicationId();
        yourFriends = UserDatabaseManager.getInstance().getAllUser();

        if (applicationId != null) {
            if (Utils.isNetworkAvailable(this)) {
                MyFireBase.getUserById(applicationId, new MyFireBase.OnGetUserListener() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        if (user == null) {
                            return;
                        }

                        if (!Utils.checkLocation(user.getLatitude(), user.getLongitude())) {
                            return;
                        }

                        lastLocation = new LatLng(user.getLatitude(), user.getLongitude());
                    }

                    @Override
                    public void onFail(String error) {
                    }
                });
            }
        }
    }
}
