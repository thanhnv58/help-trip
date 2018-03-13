package thanhnv.com.helpingtrips.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import thanhnv.com.helpingtrips.R;
import thanhnv.com.helpingtrips.data.database.UserDatabaseManager;
import thanhnv.com.helpingtrips.view.adapter.viewholder.ItemUserViewHolder;
import thanhnv.com.helpingtrips.view.application.MyApplication;
import thanhnv.com.helpingtrips.view.handler.ItemUserHandler;
import thanhnv.com.helpingtrips.viewmodel.ItemUserViewModel;

/**
 * Created by Thanh on 3/6/2018.
 */

public class UserAdapter extends RecyclerView.Adapter<ItemUserViewHolder> {
    private List<ItemUserViewModel> items;

    public UserAdapter(List<ItemUserViewModel> listData) {
        items = listData;
    }

    @Override
    public ItemUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ItemUserViewHolder(inflater.inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemUserViewHolder holder, final int position) {
        ItemUserHandler handler = new ItemUserHandler() {
            @Override
            public void onClickFollow(View view) {
                boolean isFollow = !items.get(position).isFollow.get();
                items.get(position).isFollow.set(isFollow);
                MyApplication.yourFriends.get(position).setFollow(isFollow);
                UserDatabaseManager.getInstance().updateUser(position);
            }

            @Override
            public void onClickDelete(View view) {
                items.get(position).deleteUser();
                items.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, items.size());
                MyApplication.yourFriends.remove(position);
            }
        };

        holder.binding.setViewModel(items.get(position));
        holder.binding.setViewModel(items.get(position));
        holder.binding.setHandler(handler);

        holder.binding.executePendingBindings(); // update the view now
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
