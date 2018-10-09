package thanhnv.com.helpingtrips.util.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

import thanhnv.com.helpingtrips.util.Utils;

/**
 * Created by Thanh on 3/1/2018.
 * MySharedPreference
 */
public class MySharedPreference {
    private static final String APPLICATION_ID = "APPLICATION_ID";
    private static final String MY_LATITUDE = "MY_LATITUDE";
    private static final String MY_LONGITUDE = "MY_LONGITUDE";

    private SharedPreferences sharedPreferences;

    private static final MySharedPreference INSTANCE = new MySharedPreference();

    public static MySharedPreference getInstance() {
        return INSTANCE;
    }

    private MySharedPreference() {

    }

    public void init(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getApplicationId() {
        return sharedPreferences.getString(APPLICATION_ID, null);
    }

    public void setApplicationId(String userId) {
        sharedPreferences.edit().putString(APPLICATION_ID, userId).apply();
    }

    public LatLng getMyLatLng() {
        Double latitude = (double) sharedPreferences.getFloat(MY_LATITUDE, 1000);
        Double longitude = (double) sharedPreferences.getFloat(MY_LONGITUDE, 1000);

        if (Utils.checkLocation(latitude, longitude)) {
            return new LatLng(latitude, longitude);
        } else {
            return null;
        }
    }

    public void setMyLocation(LatLng latLng) {
        sharedPreferences.edit()
                .putFloat(MY_LATITUDE, (float) latLng.latitude)
                .putFloat(MY_LONGITUDE, (float) latLng.longitude)
                .apply();
    }

//    public void clearData() {
//        sharedPreferences.edit().putString(APPLICATION_ID, null).apply();
//        MyApplication.applicationId = null;
//    }

}
