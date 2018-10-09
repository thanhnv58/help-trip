package thanhnv.com.helpingtrips.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import thanhnv.com.helpingtrips.R;
import thanhnv.com.helpingtrips.data.database.UserDatabaseManager;
import thanhnv.com.helpingtrips.data.model.User;
import thanhnv.com.helpingtrips.view.adapter.viewholder.ItemUserViewHolder;
import thanhnv.com.helpingtrips.view.application.MyApplication;
import thanhnv.com.helpingtrips.view.dialog.EditFriendDialog;
import thanhnv.com.helpingtrips.view.handler.ItemUserHandler;
import thanhnv.com.helpingtrips.viewmodel.ItemUserViewModel;

/**
 * Created by Thanh on 3/6/2018.
 * UserAdapter
 */
public class UserAdapter extends RecyclerView.Adapter<ItemUserViewHolder> {
    private List<ItemUserViewModel> items;
    private Context context;

    public UserAdapter(Context context, List<ItemUserViewModel> listData) {
        this.context = context;
        this.items = listData;
    }

    @Override
    public ItemUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ItemUserViewHolder(inflater.inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemUserViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        ItemUserHandler handler = new ItemUserHandler() {
            @Override
            public void onClickFollow(View view) {
                followFriend(position);
            }

            @Override
            public void onClickDelete(View view) {
                deleteYourFriend(position);

            }

            @Override
            public void onClickItem(View view) {
                showDialogUpdateFriend(position);
            }
        };

        holder.binding.setViewModel(items.get(position));
        holder.binding.setHandler(handler);

        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void showDialogUpdateFriend(final int position) {
        final EditFriendDialog dialog = new EditFriendDialog(context, items.get(position).getUser());
        dialog.setOnDialogEditFriendListener(new EditFriendDialog.OnDialogEditFriendListener() {
            @Override
            public void onSuccess(User user) {
                items.get(position).setUser(user);
                MyApplication.yourFriends.get(position).updateUser(user);
            }
        });
        dialog.show();
    }

    private void deleteYourFriend(int position) {
        items.get(position).deleteUser();
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
        MyApplication.yourFriends.remove(position);
    }

    private void followFriend(int position) {
        boolean isFollow = !items.get(position).isFollow.get();
        items.get(position).isFollow.set(isFollow);
        MyApplication.yourFriends.get(position).setFollow(isFollow);
        UserDatabaseManager.getInstance().updateUser(position);
    }
}
