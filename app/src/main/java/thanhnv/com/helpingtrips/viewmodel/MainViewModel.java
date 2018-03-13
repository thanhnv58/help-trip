package thanhnv.com.helpingtrips.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import thanhnv.com.helpingtrips.data.database.UserDatabaseManager;
import thanhnv.com.helpingtrips.data.model.User;
import thanhnv.com.helpingtrips.data.remote.FirebaseUser;
import thanhnv.com.helpingtrips.util.Utils;
import thanhnv.com.helpingtrips.util.firebase.MyFireBase;
import thanhnv.com.helpingtrips.view.activity.MainActivity;
import thanhnv.com.helpingtrips.view.adapter.UserAdapter;
import thanhnv.com.helpingtrips.view.application.MyApplication;

/**
 * Created by Thanh on 3/1/2018.
 */

public class MainViewModel {
    public ObservableField<String> applicationId = new ObservableField<>("null");
    public ObservableField<String> id = new ObservableField<>("");
    public ObservableField<String> name = new ObservableField<>("");

    public ObservableField<UserAdapter> adapter = new ObservableField<>();
    public ObservableField<RecyclerView.LayoutManager> layoutManager = new ObservableField<>();
    public List<ItemUserViewModel> items = new ArrayList<>();

    public TextWatcher idWatcher = new TextWatcherAdapter() {
        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
            if (!id.get().equals(s.toString())) {
                id.set(s.toString());
            }
        }
    };

    public TextWatcher nameWatcher = new TextWatcherAdapter() {
        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
            if (!name.get().equals(s.toString())) {
                name.set(s.toString());
            }
        }
    };

    private Context context;

    public MainViewModel(Context context) {
        this.context = context;

        initViewModel();
    }

    private void initViewModel() {
        applicationId.set(MyApplication.applicationId);

        List<User> users = MyApplication.yourFriends;
        for (int i = 0; i < users.size(); i++) {
            items.add(new ItemUserViewModel(users.get(i)));
        }

        adapter.set(new UserAdapter(items));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        //linearLayoutManager.setReverseLayout(true);
        layoutManager.set(linearLayoutManager);
    }

    public void pushLocationToServer(LatLng latLng) {
        FirebaseUser newUser = new FirebaseUser();
        newUser.setLatitude(latLng.latitude);
        newUser.setLongitude(latLng.longitude);

        final MyFireBase.OnWriteUserListener updateCallback = new MyFireBase.OnWriteUserListener() {
            @Override
            public void onSuccess() {
                Utils.toastMessage(context, "Push location successfully");
            }

            @Override
            public void onFail() {
                Utils.toastMessage(context, "Pushing failed!!!");
            }
        };

        MyFireBase.writeNewUserToDatabase(MyApplication.applicationId, newUser, updateCallback);
    }

    public void addNewFriend(MainActivity.AddFriendSuccess addFriendSuccess, OnAddFriend listener) {
        final User user = new User();
        user.setId(Integer.parseInt(id.get()));
        user.setName(name.get());
        user.setFollow(true);

        int resultInsert = UserDatabaseManager.getInstance().insertUser(user);

        if (resultInsert == UserDatabaseManager.DUPLICATE_ID) {
            Utils.toastMessage(context, "Duplicate friend id!");
        } else if (resultInsert == UserDatabaseManager.SUCCESS) {
            // update list item on adapter
            items.add(new ItemUserViewModel(user));

            // update list item on application
            MyApplication.yourFriends.add(user);

            addFriendSuccess.onSuccess();

            LinearLayoutManager lm = (LinearLayoutManager) layoutManager.get();
            lm.scrollToPositionWithOffset(0, 0);
            layoutManager.set(lm);

            listener.onSuccess();
            // reset data
            id.set("");
            name.set("");
        }
    }

    public class TextWatcherAdapter implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override public void afterTextChanged(Editable s) {
        }
    }

    public interface OnAddFriend {
        void onSuccess();
    }
}
