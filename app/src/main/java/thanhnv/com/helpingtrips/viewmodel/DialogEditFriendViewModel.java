package thanhnv.com.helpingtrips.viewmodel;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.text.Editable;
import android.text.TextWatcher;

import thanhnv.com.helpingtrips.common.TextWatcherAdapter;
import thanhnv.com.helpingtrips.data.database.UserDatabaseManager;
import thanhnv.com.helpingtrips.data.model.User;
import thanhnv.com.helpingtrips.util.MarkerIconManager;
import thanhnv.com.helpingtrips.view.adapter.MarkerAdapter;

/**
 * Created by Thanh on 3/15/2018.
 * DialogEditFriendViewModel
 */
public class DialogEditFriendViewModel {

    public ObservableField<String> id = new ObservableField<>();
    public ObservableField<String> name = new ObservableField<>();
    public ObservableInt markerId = new ObservableInt();
    private boolean isFollow;

    public ObservableField<MarkerAdapter> markerAdapter = new ObservableField<>();

    public TextWatcher nameWatcher = new TextWatcherAdapter() {
        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
            if (!name.get().equals(s.toString())) {
                name.set(s.toString());
            }
        }
    };

    private OnDialogEditFriendListener onDialogEditFriendListener;

    public DialogEditFriendViewModel(User user) {
        name.set(user.getName());
        id.set(user.getId() + "");
        markerId.set(user.getIconId());
        isFollow = user.isFollow();

        markerAdapter.set(new MarkerAdapter(MarkerIconManager.getInstance().getListMarkerViewModel()));
    }

    public void setMarkerId(int markerId) {
        this.markerId.set(markerId);
    }

    public void setOnEditFriendDialogListener(OnDialogEditFriendListener listener) {
        this.onDialogEditFriendListener = listener;
    }

    public void onClickUpdate() {
        if (name.get() == null || name.get().equals("")) {
            onDialogEditFriendListener.onNameEmpty();
            return;
        }

        User user = new User(Integer.valueOf(id.get()), name.get(), isFollow, markerId.get());
        int result = UserDatabaseManager.getInstance().updateUser(user);

        if (result == -1) {
            if (onDialogEditFriendListener != null) {
                onDialogEditFriendListener.onFail();
            }
        } else {
            onDialogEditFriendListener.onSuccess(user);
        }
    }

    /**
     * Created by Thanh on 3/15/2018.
     * OnDialogEditFriendListener
     */
    public interface OnDialogEditFriendListener {
        void onFail();
        void onSuccess(User user);
        void onNameEmpty();
    }
}
