package thanhnv.com.helpingtrips.util;

import java.util.ArrayList;
import java.util.List;

import thanhnv.com.helpingtrips.R;
import thanhnv.com.helpingtrips.viewmodel.ItemMarkerViewModel;

/**
 * Created by Thanh on 3/14/2018.
 * MarkerIconManager
 */
public class MarkerIconManager {
    private static final MarkerIconManager INSTANTCE = new MarkerIconManager();

    public static MarkerIconManager getInstance() {
        return INSTANTCE;
    }

    private MarkerIconManager() {
    }

    public List<ItemMarkerViewModel> getListMarkerViewModel() {
        List<ItemMarkerViewModel> list = new ArrayList<>();
        list.add(new ItemMarkerViewModel(R.drawable.ic_marker_1));
        list.add(new ItemMarkerViewModel(R.drawable.ic_marker_2));
        list.add(new ItemMarkerViewModel(R.drawable.ic_marker_3));
        list.add(new ItemMarkerViewModel(R.drawable.ic_marker_4));
        list.add(new ItemMarkerViewModel(R.drawable.ic_marker_5));
        list.add(new ItemMarkerViewModel(R.drawable.ic_marker_6));
        list.add(new ItemMarkerViewModel(R.drawable.ic_marker_7));
        list.add(new ItemMarkerViewModel(R.drawable.ic_marker_8));
        list.add(new ItemMarkerViewModel(R.drawable.ic_marker_9));

        return list;
    }
}
