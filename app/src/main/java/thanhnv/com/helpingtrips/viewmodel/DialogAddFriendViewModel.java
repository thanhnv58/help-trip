package thanhnv.com.helpingtrips.viewmodel;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.text.Editable;
import android.text.TextWatcher;

import thanhnv.com.helpingtrips.common.TextWatcherAdapter;
import thanhnv.com.helpingtrips.data.database.UserDatabaseManager;
import thanhnv.com.helpingtrips.data.model.User;
import thanhnv.com.helpingtrips.util.Constants;
import thanhnv.com.helpingtrips.util.MarkerIconManager;
import thanhnv.com.helpingtrips.util.Utils;
import thanhnv.com.helpingtrips.view.adapter.MarkerAdapter;
import thanhnv.com.helpingtrips.view.application.MyApplication;

/**
 * Created by Thanh on 3/14/2018.
 * DialogAddFriendViewModel
 */
public class DialogAddFriendViewModel {
    public ObservableField<String> id = new ObservableField<>("");
    public ObservableField<String> name = new ObservableField<>("");
    public ObservableInt markerId = new ObservableInt(Constants.DEFAULT_MARKER_ICON_ID);

    public ObservableField<MarkerAdapter> markerAdapter = new ObservableField<>();

    private OnAddFriendListener onAddFriendListener;

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

    public DialogAddFriendViewModel() {
        markerAdapter.set(new MarkerAdapter(MarkerIconManager.getInstance().getListMarkerViewModel()));
    }

    public void addNewFriend() {
        if (id.get() == null || id.get().equals("")) {
            if (onAddFriendListener != null) {
                onAddFriendListener.onIdEmpty();
            }
            return;
        }

        if (Utils.parseToPrimaryKey(id.get()) == -1) {
            if (onAddFriendListener != null) {
                onAddFriendListener.onIdInvalid();
            }
            return;
        }

        if (name.get() == null || name.get().equals("")) {
            if (onAddFriendListener != null) {
                onAddFriendListener.onNameEmpty();
            }
            return;
        }

        final User user = new User(Utils.parseToPrimaryKey(id.get().toUpperCase()), name.get(), true, markerId.get());

        int resultInsert = UserDatabaseManager.getInstance().insertUser(user);

        if (resultInsert == UserDatabaseManager.DUPLICATE_ID) {
            if (onAddFriendListener != null) {
                onAddFriendListener.onDuplicateId();
            }
        } else if (resultInsert == UserDatabaseManager.SUCCESS) {
            MyApplication.yourFriends.add(user);
            if (onAddFriendListener != null) {
                onAddFriendListener.onSuccess(user);
            }
        }
    }

    public void setMarkerId(int markerId) {
        this.markerId.set(markerId);
    }

    public void setOnDialogAddFriendListener(OnAddFriendListener listener) {
        this.onAddFriendListener = listener;
    }

    /**
     * Created by Thanh on 3/14/2018.
     * OnAddFriendListener
     */
    public interface OnAddFriendListener {
        void onIdEmpty();
        void onIdInvalid();
        void onNameEmpty();
        void onDuplicateId();
        void onSuccess(User user);
    }
}
