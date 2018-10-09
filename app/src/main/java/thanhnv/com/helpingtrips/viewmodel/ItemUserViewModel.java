package thanhnv.com.helpingtrips.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import thanhnv.com.helpingtrips.data.database.UserDatabaseManager;
import thanhnv.com.helpingtrips.data.model.User;
import thanhnv.com.helpingtrips.util.Constants;
import thanhnv.com.helpingtrips.util.Utils;

/**
 * Created by Thanh on 3/6/2018.
 * ItemUserViewModel
 */
public class ItemUserViewModel {
    public ObservableField<String> id = new ObservableField<>("");
    public ObservableField<String> name = new ObservableField<>("");
    public ObservableBoolean isFollow = new ObservableBoolean(false);
    private ObservableInt markerId = new ObservableInt();
    public ObservableInt imageMarker = new ObservableInt();

    ItemUserViewModel(User user) {
        setUser(user);
    }

    public void deleteUser() {
        UserDatabaseManager.getInstance().deleteUser(Utils.parseToPrimaryKey(id.get()));
    }

    public User getUser() {
        return new User(Utils.parseToPrimaryKey(id.get()), name.get(), isFollow.get(), markerId.get());
    }

    public void setUser(User user) {
        id.set(Utils.parseToAppId(user.getId()));
        name.set(user.getName());
        isFollow.set(user.isFollow());
        markerId.set(user.getIconId());
        imageMarker.set(Constants.LIST_ICON[user.getIconId()]);
    }
}
