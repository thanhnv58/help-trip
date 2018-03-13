package thanhnv.com.helpingtrips.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;

import thanhnv.com.helpingtrips.data.remote.FirebaseUser;
import thanhnv.com.helpingtrips.util.Utils;
import thanhnv.com.helpingtrips.util.firebase.MyFireBase;
import thanhnv.com.helpingtrips.view.application.MyApplication;
import thanhnv.com.helpingtrips.view.sharedPreference.MySharedPreference;

/**
 * Created by Thanh on 3/2/2018.
 */

public class DialogCreateUserViewModel implements MyFireBase.OnCreateNewUserIdListener {

    public ObservableBoolean isCreating = new ObservableBoolean(false);

    private OnCreateNewUserListener listener;

    public void onClickCreateBtn(Context context, OnCreateNewUserListener listener) {
        if (!Utils.notificationConnectNetwork(context)) {
            return;
        }
        this.listener = listener;
        isCreating.set(true);
        MyFireBase.createNewUserIdTransaction(this);
    }

    @Override
    public void onCreateSuccess(final String newUserId) {
        // write new user to database
        MyFireBase.writeNewUserToDatabase(newUserId, new FirebaseUser(), new MyFireBase.OnWriteUserListener() {
            @Override
            public void onSuccess() {
                MySharedPreference.getInstance().setApplicationId(newUserId);
                MyApplication.applicationId = MySharedPreference.getInstance().getApplicationId();
                isCreating.set(false);
                listener.onSuccess();
            }

            @Override
            public void onFail() {
                listener.onFail();
            }
        });
    }

    public interface OnCreateNewUserListener {
        void onSuccess();
        void onFail();
    }
}
