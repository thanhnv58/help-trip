package thanhnv.com.helpingtrips.view.sharedPreference;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import thanhnv.com.helpingtrips.view.application.MyApplication;

/**
 * Created by Thanh on 3/1/2018.
 */

public class MySharedPreference {
    private static final String APPLICATION_ID = "APPLICATION_ID";

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

    public void clearData() {
        sharedPreferences.edit().putString(APPLICATION_ID, null).apply();
        MyApplication.applicationId = null;
    }

}
