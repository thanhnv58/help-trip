package thanhnv.com.helpingtrips.util;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import thanhnv.com.helpingtrips.R;

/**
 * Created by Thanh on 3/1/2018.
 */
public class Utils {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isOpenGPS(Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }

    public static boolean openGPS(Context context) {
        if (!isOpenGPS(context)) {
            toastMessage(context, context.getResources().getString(R.string.open_gps));
            return false;
        }
        return true;
    }

    public static boolean notificationConnectNetwork(Context context) {
        if (!isNetworkAvailable(context)) {
            toastMessage(context, "Connect network!!!");
            return false;
        }
        return true;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void toastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean checkLocation(Double latitude, Double longitude) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            return false;
        }
        return true;
    }
}
