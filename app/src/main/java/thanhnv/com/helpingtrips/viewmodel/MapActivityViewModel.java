package thanhnv.com.helpingtrips.viewmodel;

import android.databinding.ObservableBoolean;

import java.util.ArrayList;
import java.util.List;

import thanhnv.com.helpingtrips.data.model.User;
import thanhnv.com.helpingtrips.data.remote.FirebaseUser;
import thanhnv.com.helpingtrips.util.firebase.MyFireBase;
import thanhnv.com.helpingtrips.view.application.MyApplication;

/**
 * Created by Thanh on 3/7/2018.
 * MapActivityViewModel
 */
public class MapActivityViewModel {
    //public ObservableBoolean hasLocation = new ObservableBoolean(false);
    public ObservableBoolean showBtnGo = new ObservableBoolean(false);
    public ObservableBoolean isPushing = new ObservableBoolean(false);
    public ObservableBoolean showButton = new ObservableBoolean(true);

    public List<User> followingFriends;

    public MapActivityViewModel() {
        followingFriends = new ArrayList<>();
        for (User u : MyApplication.yourFriends) {
            if (u.isFollow()) {
                followingFriends.add(u);
            }
        }
    }

    public void getFriendOnFirebase(List<MyFireBase.OnGetUserListener> listeners) {
        int i = 0;

        for (User user : followingFriends) {
            MyFireBase.getUserById(user.getId() + "", listeners.get(i));
            i++;
        }

    }

    public String[] getListFollowingUserName() {
        List<String> listUser = new ArrayList<>();
        int size = MyApplication.yourFriends.size();

        for (int i = 0; i < size; i++) {
            if (MyApplication.yourFriends.get(i).isFollow()) {
                listUser.add(MyApplication.yourFriends.get(i).getName());
            }
        }

        String[] result = new String[listUser.size()];

        return listUser.toArray(result);
    }

    public void pushStart() {
        if (!isPushing.get()) {
            isPushing.set(true);
        }
    }

    public void pushDone() {
        isPushing.set(false);
    }

    public void showButtonGo() {
        showBtnGo.set(true);
    }

    public void hideButtonGo() {
        showBtnGo.set(false);
    }

    public void pushYourLocation(double latitude, double longitude, MyFireBase.OnWriteUserListener listener) {
        //hasLocation.set(true);
        FirebaseUser newUser = new FirebaseUser();
        newUser.setLatitude(latitude);
        newUser.setLongitude(longitude);
        MyFireBase.writeNewUserToDatabase(MyApplication.applicationId, newUser, listener);
    }

    public int getMarkerId(int which) {
        return followingFriends.get(which).getIconId();
    }

    public int getFriendNumber() {
        if (followingFriends == null) {
            return 0;
        }
        return followingFriends.size();
    }

    public void settingFloatingButton() {
        showButton.set(!showButton.get());
    }
}
