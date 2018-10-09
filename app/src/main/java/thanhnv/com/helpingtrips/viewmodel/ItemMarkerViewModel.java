package thanhnv.com.helpingtrips.viewmodel;

import android.databinding.ObservableInt;

/**
 * Created by Thanh on 3/14/2018.
 * ItemMarkerViewModel
 */
public class ItemMarkerViewModel {
    public ObservableInt markerDrawable = new ObservableInt();

    public ItemMarkerViewModel(int markerIconSource) {
        this.markerDrawable.set(markerIconSource);
    }

}
