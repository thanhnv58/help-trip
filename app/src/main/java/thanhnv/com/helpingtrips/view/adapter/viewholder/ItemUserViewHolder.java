package thanhnv.com.helpingtrips.view.adapter.viewholder;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import thanhnv.com.helpingtrips.databinding.ItemUserBinding;

/**
 * Created by Thanh on 3/6/2018.
 */

public class ItemUserViewHolder extends RecyclerView.ViewHolder {
    public ItemUserBinding binding;

    public ItemUserViewHolder(View view) {
        super(view);
        binding = DataBindingUtil.bind(view);
    }
}
