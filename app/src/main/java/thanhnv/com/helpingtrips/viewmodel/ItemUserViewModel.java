package thanhnv.com.helpingtrips.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import thanhnv.com.helpingtrips.data.database.UserDatabaseManager;
import thanhnv.com.helpingtrips.data.model.User;

/**
 * Created by Thanh on 3/6/2018.
 */

public class ItemUserViewModel {
    public ObservableField<String> id = new ObservableField<>("");
    public ObservableField<String> name = new ObservableField<>("");
    public ObservableBoolean isFollow = new ObservableBoolean(false);

    public ItemUserViewModel(User user) {
        id.set(user.getId() + "");
        name.set(user.getName());
        isFollow.set(user.isFollow());
    }

    public void deleteUser() {
        UserDatabaseManager.getInstance().deleteUser(Integer.valueOf(id.get()));
    }
}
