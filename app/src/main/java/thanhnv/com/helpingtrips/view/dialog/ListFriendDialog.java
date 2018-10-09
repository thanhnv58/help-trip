package thanhnv.com.helpingtrips.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Thanh on 3/17/2018.
 * ListFriendDialog
 */
public class ListFriendDialog {
    private Dialog dialog;

    public ListFriendDialog(Context context, String[] listData, DialogInterface.OnClickListener listener) {
        dialog = new AlertDialog.Builder(context)
                        .setTitle("Following friends (" + listData.length + ")")
                        .setItems(listData, listener).create();
    }

    public void show() {
        dialog.show();
    }
}
