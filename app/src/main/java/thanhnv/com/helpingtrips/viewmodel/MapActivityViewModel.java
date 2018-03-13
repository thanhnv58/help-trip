package thanhnv.com.helpingtrips.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;

import java.util.ArrayList;
import java.util.List;

import thanhnv.com.helpingtrips.data.database.UserDatabaseManager;
import thanhnv.com.helpingtrips.data.model.User;
import thanhnv.com.helpingtrips.data.remote.FirebaseUser;
import thanhnv.com.helpingtrips.util.Constants;
import thanhnv.com.helpingtrips.util.firebase.MyFireBase;
import thanhnv.com.helpingtrips.view.application.MyApplication;

/**
 * Created by Thanh on 3/7/2018.
 */

public class MapActivityViewModel {
    // observable for floating button change
    public ObservableInt mode = new ObservableInt(Constants.MODE_MAP);
    public ObservableBoolean hasLocation = new ObservableBoolean(true);

    private List<MyFireBase.OnGetUserListener> listeners;

    public List<User> followingFriends;

    public int friendNumber;

    public MapActivityViewModel() {
        if (MyApplication.lastLocation == null) {
            hasLocation.set(false);
        }
        getFollowingFriends();
    }

    public void getFollowingFriends() {
        followingFriends = UserDatabaseManager.getInstance().getFollowingFriends();
        friendNumber = followingFriends.size();
    }

    public void setListeners(List<MyFireBase.OnGetUserListener> listeners) {
        this.listeners = listeners;
    }

    public void showAllFollowingFriends() {
        int i = 0;

        for (User user : followingFriends) {
            MyFireBase.getUserById(user.getId() + "", listeners.get(i));
            i++;
        }

    }

    public String[] getListFollowingUserName() {
        List<String> listUser = new ArrayList<>();
        listUser.add(Constants.YOU);
        int size = MyApplication.yourFriends.size();

        for (int i = 0; i < size; i++) {
            if (MyApplication.yourFriends.get(i).isFollow()) {
                listUser.add(MyApplication.yourFriends.get(i).getName());
            }
        }

        String[] result = new String[listUser.size()];

        return listUser.toArray(result);
    }

    public void clickYou() {
        mode.set(Constants.MODE_YOU);
    }

    public void clickFriend() {
        mode.set(Constants.MODE_FRIEND);
    }

    public void clickMap() {
        mode.set(Constants.MODE_MAP);
    }

    public void pushYourLocation(double latitude, double longitude, MyFireBase.OnWriteUserListener listener) {
        hasLocation.set(true);
        FirebaseUser newUser = new FirebaseUser();
        newUser.setLatitude(latitude);
        newUser.setLongitude(longitude);
        MyFireBase.writeNewUserToDatabase(MyApplication.applicationId, newUser, listener);
    }
}
