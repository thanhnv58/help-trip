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
import thanhnv.com.helpingtrips.view.handler.DialogCreateUserHandler;
import thanhnv.com.helpingtrips.viewmodel.DialogCreateUserViewModel;

/**
 * Created by Thanh on 3/7/2018.
 */

public class CreateUserDialog implements DialogCreateUserHandler, DialogCreateUserViewModel.OnCreateNewUserListener {

    private DialogCreateUserViewModel viewModel;
    private Dialog dialog;
    private DialogCreateUserBinding binding;
    private Context context;
    private OnCreateUserDialogListener listener;

    public CreateUserDialog(Context context, OnCreateUserDialogListener listener) {
        this.context = context;
        this.listener = listener;
        this.viewModel = new DialogCreateUserViewModel();

        // init dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_create_user, null, false);
        binding.setViewModel(viewModel);
        binding.setHandler(this);

        dialog = new AlertDialog.Builder(context)
                .setView(binding.getRoot())
                .create();

        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    dialog.getOwnerActivity().finish();
                }
                return true;
            }
        });
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    // Handler when click on view
    @Override
    public void onClickCreateNewUser(View view) {
        viewModel.onClickCreateBtn(context, this);
    }

    // listener DialogCreateUserViewModel create user
    @Override
    public void onSuccess() {
        if (listener != null) {
            listener.onSuccess();
        }
        dialog.dismiss();
    }

    @Override
    public void onFail() {
        if (listener != null) {
            listener.onFail();
        }
        dialog.dismiss();
    }

    public interface OnCreateUserDialogListener {
        void onSuccess();
        void onFail();
    }
}
