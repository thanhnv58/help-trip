package thanhnv.com.helpingtrips.data.remote;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Thanh on 3/1/2018.
 * FirebaseUser
 */
@IgnoreExtraProperties
public class FirebaseUser {
    private Double latitude;
    private Double longitude;

    public FirebaseUser() {
        latitude = 1000.0;
        longitude = 1000.0;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
