package thanhnv.com.helpingtrips.util;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Thanh on 3/1/2018.
 * Utils
 */
public class Utils {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } else {
            return false;
        }
    }

    public static boolean isGPSAvaiable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void toastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void snackbarMessage(Activity activity, String message) {
        View parentLayout = activity.findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    public static boolean checkLocation(Double latitude, Double longitude) {
        return !(latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180);
    }

    public static String parseToAppId(String decimalId) {
        try {
            return Integer.toHexString(Integer.valueOf(decimalId)).toUpperCase();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String parseToAppId(int decimalId) {
        try {
            return Integer.toHexString(decimalId).toUpperCase();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static int parseToPrimaryKey(String hexId) {
        try {
            return Integer.valueOf(hexId, 16);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
