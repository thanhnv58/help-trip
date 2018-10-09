package thanhnv.com.helpingtrips.view.adapter.viewholder;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import thanhnv.com.helpingtrips.databinding.ItemMarkerBinding;

/**
 * Created by Thanh on 3/14/2018.
 * ItemMarkerViewHolder
 */
public class ItemMarkerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private RecyclerView parentRecyclerView;
    public ItemMarkerBinding binding;

    public ItemMarkerViewHolder(View view, RecyclerView recyclerView) {
        super(view);

        binding = DataBindingUtil.bind(view);
        binding.container.setOnClickListener(this);

        parentRecyclerView = recyclerView;
    }

    public void showText() {
        int parentHeight = (binding.container).getHeight();
        float scale = (parentHeight) / (float) binding.markerImage.getHeight();
        binding.markerImage.setPivotX(binding.markerImage.getWidth() * 0.5f);
        binding.markerImage.setPivotY(0);
        binding.markerImage.animate().scaleX(scale)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                    }
                })
                .scaleY(scale).setDuration(200)
                .start();
    }

    public void hideText() {
        binding.markerImage.setAlpha((float) 0.7);
        binding.markerImage.animate().scaleX(1f).scaleY(1f)
                .setDuration(200)
                .start();
    }

    @Override
    public void onClick(View v) {
        parentRecyclerView.smoothScrollToPosition(getAdapterPosition());
    }

}
