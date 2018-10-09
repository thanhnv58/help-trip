package thanhnv.com.helpingtrips.view.application;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import thanhnv.com.helpingtrips.data.database.UserDatabaseManager;
import thanhnv.com.helpingtrips.data.model.User;
import thanhnv.com.helpingtrips.util.sharedpreference.MySharedPreference;

/**
 * Created by Thanh on 3/1/2018.
 * MyApplication
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
            lastLocation = MySharedPreference.getInstance().getMyLatLng();
        }
    }
}
