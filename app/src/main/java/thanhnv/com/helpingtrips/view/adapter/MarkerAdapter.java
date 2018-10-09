package thanhnv.com.helpingtrips.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import thanhnv.com.helpingtrips.R;
import thanhnv.com.helpingtrips.view.adapter.viewholder.ItemMarkerViewHolder;
import thanhnv.com.helpingtrips.viewmodel.ItemMarkerViewModel;

/**
 * Created by Thanh on 3/14/2018.
 * MarkerAdapter
 */
public class MarkerAdapter extends RecyclerView.Adapter<ItemMarkerViewHolder> {

    private RecyclerView parentRecycler;
    private List<ItemMarkerViewModel> items;

    public MarkerAdapter(List<ItemMarkerViewModel> items) {
        this.items = items;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parentRecycler = recyclerView;
    }

    @Override
    public ItemMarkerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ItemMarkerViewHolder(layoutInflater.inflate(R.layout.item_marker, parent, false), parentRecycler);
    }

    @Override
    public void onBindViewHolder(ItemMarkerViewHolder holder, int position) {
        holder.binding.setViewModel(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
