package thanhnv.com.helpingtrips.util;

import thanhnv.com.helpingtrips.R;

/**
 * Created by Thanh on 3/8/2018.
 * Constants
 */
public class Constants {

    public static final int MODE_YOU = 1;
    public static final int MODE_FRIEND = 2;
    public static final int MODE_MAP = 3;
    //public static final String YOU = "You";
    public static final int LOCATION_PERMISION_REQUEST_CODE = 1234;

    // configuration google map
    public static final float DEFAULT_ZOOM = 13;
    public static final float DEFAULT_TILT = 45;
    public static final int DEFAULT_MARKER_ICON_ID = 0;

    public static final int[] LIST_ICON = new int[] {
            R.drawable.ic_marker_1, R.drawable.ic_marker_2, R.drawable.ic_marker_3,
            R.drawable.ic_marker_4, R.drawable.ic_marker_5, R.drawable.ic_marker_6,
            R.drawable.ic_marker_7, R.drawable.ic_marker_8, R.drawable.ic_marker_9
    };

    public static final int REQUEST_LOCATION = 199;
    public static final long GAP_OF_PUSH_AUTO_SECOND = 180; // seconds
}
