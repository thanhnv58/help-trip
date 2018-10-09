package thanhnv.com.helpingtrips.viewmodel;

import android.databinding.ObservableBoolean;

import thanhnv.com.helpingtrips.data.remote.FirebaseUser;
import thanhnv.com.helpingtrips.util.firebase.MyFireBase;

/**
 * Created by Thanh on 3/2/2018.
 * DialogCreateUserViewModel
 */
public class DialogCreateUserViewModel implements MyFireBase.OnCreateNewUserIdListener {

    public ObservableBoolean isCreating = new ObservableBoolean(false);

    private OnCreateNewUserViewModelListener onCreateNewUserViewModelListener;

    public DialogCreateUserViewModel() {
    }

    public void setOnCreateNewUserViewModelListener(OnCreateNewUserViewModelListener listener) {
        this.onCreateNewUserViewModelListener = listener;
    }

    public void createNewUser() {
        isCreating.set(true);
        MyFireBase.createNewUserIdTransaction(this);
    }

    @Override
    public void onCreateSuccess(final String newUserId) {
        // write new user to database
        MyFireBase.writeNewUserToDatabase(newUserId, new FirebaseUser(), new MyFireBase.OnWriteUserListener() {
            @Override
            public void onSuccess() {
                if (onCreateNewUserViewModelListener != null) {
                    onCreateNewUserViewModelListener.onSuccess(newUserId);
                }

                isCreating.set(false);
            }

            @Override
            public void onFail() {
                if (onCreateNewUserViewModelListener != null) {
                    onCreateNewUserViewModelListener.onFail();
                }
            }
        });
    }

    /**
     * Created by Thanh on 3/2/2018.
     * OnCreateNewUserViewModelListener
     */
    public interface OnCreateNewUserViewModelListener {
        void onSuccess(String newId);
        void onFail();
    }
}
