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
import thanhnv.com.helpingtrips.databinding.DialogAddFriendBinding;
import thanhnv.com.helpingtrips.util.Utils;
import thanhnv.com.helpingtrips.view.adapter.viewholder.ItemMarkerViewHolder;
import thanhnv.com.helpingtrips.view.handler.DialogAddFriendHandler;
import thanhnv.com.helpingtrips.viewmodel.DialogAddFriendViewModel;

/**
 * Created by Thanh on 3/14/2018.
 * AddFriendDialog
 */
public class AddFriendDialog implements DialogAddFriendHandler,
        DiscreteScrollView.OnItemChangedListener<ItemMarkerViewHolder>,
        DiscreteScrollView.ScrollStateChangeListener<ItemMarkerViewHolder>,
        DialogAddFriendViewModel.OnAddFriendListener {

    private Dialog dialog;
    private DialogAddFriendViewModel viewModel;

    private Context context;
    private OnDialogAddFriendListener addFriendListener;

    public AddFriendDialog(Context context) {
        this.context = context;
        viewModel = new DialogAddFriendViewModel();
        viewModel.setOnDialogAddFriendListener(this);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        DialogAddFriendBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_add_friend, null, false);
        binding.setViewModel(viewModel);
        binding.setHandler(this);

        dialog = new AlertDialog.Builder(context).setView(binding.getRoot()).create();
        dialog.setCanceledOnTouchOutside(true);

        binding.discreteMarker.setSlideOnFling(true);
        binding.discreteMarker.addOnItemChangedListener(this);
        binding.discreteMarker.addScrollStateChangeListener(this);

        binding.executePendingBindings();
    }

    public void setOnDialogAddFriendListener(OnDialogAddFriendListener listener) {
        this.addFriendListener = listener;
    }

    public void show() {
        dialog.show();
    }

    @Override
    public void onClickAddFriend(View view) {
        viewModel.addNewFriend();
    }

    @Override
    public void onCurrentItemChanged(@Nullable ItemMarkerViewHolder holder, int adapterPosition) {
        //viewHolder will never be null, because we never remove items from adapter's list
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

    // Handle dialog show......
    @Override
    public void onIdEmpty() {
        Utils.toastMessage(context, context.getResources().getString(R.string.error_id_empty));
    }

    @Override
    public void onIdInvalid() {
        Utils.toastMessage(context, context.getResources().getString(R.string.error_id_invalid));
    }

    @Override
    public void onNameEmpty() {
        Utils.toastMessage(context, context.getResources().getString(R.string.error_name_empty));
    }

    @Override
    public void onDuplicateId() {
        Utils.toastMessage(context, context.getResources().getString(R.string.error_duplicate_id));
    }

    @Override
    public void onSuccess(User user) {
        if (addFriendListener != null) {
            addFriendListener.onSuccess(user);
        }
        Utils.toastMessage(context, context.getResources().getString(R.string.add_friend_success));
        dialog.dismiss();
    }

    /**
     * Created by Thanh on 3/14/2018.
     * OnDialogAddFriendListener
     */
    public interface OnDialogAddFriendListener {
        void onSuccess(User user);
    }
}
