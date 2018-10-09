package thanhnv.com.helpingtrips.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import thanhnv.com.helpingtrips.R;
import thanhnv.com.helpingtrips.databinding.DialogCreateUserBinding;
import thanhnv.com.helpingtrips.util.Utils;
import thanhnv.com.helpingtrips.view.handler.DialogCreateUserHandler;
import thanhnv.com.helpingtrips.viewmodel.DialogCreateUserViewModel;

/**
 * Created by Thanh on 3/7/2018.
 * CreateNewUserDialog
 */
public class CreateNewUserDialog implements DialogCreateUserHandler,
        DialogCreateUserViewModel.OnCreateNewUserViewModelListener {

    private DialogCreateUserViewModel viewModel;

    private Dialog dialog;
    private Context context;

    private OnCreateNewUserDialogListener onDialogCreateNewUserListener;

    public CreateNewUserDialog(Context context) {
        this.context = context;
        this.viewModel = new DialogCreateUserViewModel();
        this.viewModel.setOnCreateNewUserViewModelListener(this);

        // init dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        DialogCreateUserBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_create_user, null, false);
        binding.setViewModel(viewModel);
        binding.setHandler(this);

        dialog = new AlertDialog.Builder(context).setView(binding.getRoot()).create();
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return true;
            }
        });
    }

    public void setOnDialogCreateNewUserListener(OnCreateNewUserDialogListener listener) {
        this.onDialogCreateNewUserListener = listener;
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    // Handler when click on view
    @Override
    public void onClickCreateNewUser(View view) {
        if (!Utils.isNetworkAvailable(context)) {
            Utils.toastMessage(context, context.getResources().getString(R.string.open_network));
            return;
        }

        viewModel.createNewUser();
    }

    // listener DialogCreateUserViewModel create user
    @Override
    public void onSuccess(String newId) {
        if (onDialogCreateNewUserListener != null) {
            onDialogCreateNewUserListener.onSuccess(newId);
        }

        Utils.toastMessage(context, context.getResources().getString(R.string.create_new_user_success));
        dialog.dismiss();
    }

    @Override
    public void onFail() {
        dialog.dismiss();

        if (onDialogCreateNewUserListener != null) {
            onDialogCreateNewUserListener.onFail();
        }
    }

    /**
     * Created by Thanh on 3/7/2018.
     * OnCreateNewUserDialogListener
     */
    public interface OnCreateNewUserDialogListener {
        void onFail();
        void onSuccess(String newId);
    }
}
