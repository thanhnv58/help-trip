package thanhnv.com.helpingtrips.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import thanhnv.com.helpingtrips.data.model.User;
import thanhnv.com.helpingtrips.data.remote.FirebaseUser;
import thanhnv.com.helpingtrips.util.Utils;
import thanhnv.com.helpingtrips.util.firebase.MyFireBase;
import thanhnv.com.helpingtrips.util.sharedpreference.MySharedPreference;
import thanhnv.com.helpingtrips.view.adapter.UserAdapter;
import thanhnv.com.helpingtrips.view.application.MyApplication;

/**
 * Created by Thanh on 3/1/2018.
 * MainViewModel
 */
public class MainViewModel {
    public ObservableField<String> applicationId = new ObservableField<>("null");

    public ObservableField<UserAdapter> adapter = new ObservableField<>();
    public ObservableField<RecyclerView.LayoutManager> layoutManager = new ObservableField<>();
    public ObservableBoolean isPushing = new ObservableBoolean(false);
    public ObservableBoolean showAddFriend = new ObservableBoolean(true);
    public ObservableBoolean isAutoPushLocation = new ObservableBoolean(false);

    private List<ItemUserViewModel> items = new ArrayList<>();

    public MainViewModel(Context context) {
        applicationId.set(Utils.parseToAppId(MyApplication.applicationId));

        List<User> users = MyApplication.yourFriends;
        for (int i = 0; i < users.size(); i++) {
            items.add(new ItemUserViewModel(users.get(i)));
        }

        UserAdapter userAdapter = new UserAdapter(context, items);
        adapter.set(userAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        layoutManager.set(linearLayoutManager);
    }

    public void pushLocationToServer(final LatLng latLng, final OnPushLocationListener listener) {
        FirebaseUser newUser = new FirebaseUser();
        newUser.setLatitude(latLng.latitude);
        newUser.setLongitude(latLng.longitude);

        MyFireBase.writeNewUserToDatabase(MyApplication.applicationId, newUser, new MyFireBase.OnWriteUserListener() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.pushSuccess();
                }
            }

            @Override
            public void onFail() {
                if (listener != null) {
                    listener.pushFail();
                }
            }
        });
    }

    public void pushStart() {
        if (!isPushing.get()) {
            isPushing.set(true);
        }
    }

    public void pushDone() {
        isPushing.set(false);
    }

    public void addFriend(User user) {
        items.add(new ItemUserViewModel(user));
    }

    public void settingShowAddFriend() {
        showAddFriend.set(!showAddFriend.get());
    }

    public void createNewUser(String newId) {
        applicationId.set(Utils.parseToAppId(newId));
        MyApplication.applicationId = newId;
        MySharedPreference.getInstance().setApplicationId(newId);
    }

    public int getSizeItems() {
        return items.size();
    }

    /**
     * Created by Thanh on 3/1/2018.
     * OnPushLocationListener
     */
    public interface OnPushLocationListener {
        void pushSuccess();
        void pushFail();
    }
}
