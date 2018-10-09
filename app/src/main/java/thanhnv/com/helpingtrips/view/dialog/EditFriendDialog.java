package thanhnv.com.helpingtrips.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import thanhnv.com.helpingtrips.R;
import thanhnv.com.helpingtrips.data.model.User;
import thanhnv.com.helpingtrips.databinding.DialogEditFriendBinding;
import thanhnv.com.helpingtrips.util.Utils;
import thanhnv.com.helpingtrips.view.adapter.viewholder.ItemMarkerViewHolder;
import thanhnv.com.helpingtrips.view.handler.DialogEditFriendHandler;
import thanhnv.com.helpingtrips.viewmodel.DialogEditFriendViewModel;

/**
 * Created by Thanh on 3/15/2018.
 * EditFriendDialog
 */
public class EditFriendDialog implements DialogEditFriendHandler,
        DiscreteScrollView.OnItemChangedListener<ItemMarkerViewHolder>,
        DiscreteScrollView.ScrollStateChangeListener<ItemMarkerViewHolder>,
        DialogEditFriendViewModel.OnDialogEditFriendListener {
    private Dialog dialog;
    private DialogEditFriendViewModel viewModel;
    private Context context;

    private OnDialogEditFriendListener onDialogEditFriendListener;

    public EditFriendDialog(final Context context, User user) {
        this.context = context;
        viewModel = new DialogEditFriendViewModel(user);
        viewModel.setOnEditFriendDialogListener(this);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        DialogEditFriendBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_edit_friend, null, false);
        binding.setViewModel(viewModel);
        binding.setHandler(this);

        dialog = new AlertDialog.Builder(context).setView(binding.getRoot()).create();
        dialog.setCanceledOnTouchOutside(true);

        binding.discreteMarker.addOnItemChangedListener(this);
        binding.discreteMarker.addScrollStateChangeListener(this);

        binding.executePendingBindings();
    }

    public void setOnDialogEditFriendListener(OnDialogEditFriendListener listener) {
        this.onDialogEditFriendListener = listener;
    }

    public void show() {
        dialog.show();
    }

    @Override
    public void onClickUpdate(View view) {
        viewModel.onClickUpdate();
    }

    @Override
    public void onCurrentItemChanged(@Nullable ItemMarkerViewHolder holder, int adapterPosition) {
        if (holder != null) {
            holder.showText();
        }
    }

    @Override
    public void onScrollStart(@NonNull ItemMarkerViewHolder holder, int adapterPosition) {
        holder.hideText();
    }

    @Override
    public void onScrollEnd(@NonNull ItemMarkerViewHolder currentItemHolder, int adapterPosition) {
        viewModel.setMarkerId(adapterPosition);
    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable ItemMarkerViewHolder currentHolder, @Nullable ItemMarkerViewHolder newCurrent) {

    }

    // Handle edit friend dialog view model
    @Override
    public void onFail() {
        Utils.toastMessage(context, context.getResources().getString(R.string.update_fail));
    }

    @Override
    public void onSuccess(User user) {
        if (onDialogEditFriendListener != null) {
            onDialogEditFriendListener.onSuccess(user);
        }

        Utils.toastMessage(context, context.getResources().getString(R.string.update_successfully));
        dialog.dismiss();
    }

    @Override
    public void onNameEmpty() {
        Utils.toastMessage(context, context.getResources().getString(R.string.error_name_empty));
    }

    /**
     * Created by Thanh on 3/15/2018.
     * OnDialogEditFriendListener
     */
    public interface OnDialogEditFriendListener {
        void onSuccess(User user);
    }
}
