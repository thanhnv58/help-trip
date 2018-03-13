package thanhnv.com.helpingtrips.util.binder;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import thanhnv.com.helpingtrips.R;
import thanhnv.com.helpingtrips.util.Constants;
import thanhnv.com.helpingtrips.view.adapter.UserAdapter;

/**
 * Created by Thanh on 3/6/2018.
 */

public class BindingAdapterUtils {

    @BindingAdapter({"adapter"})
    public static void setAdapter(RecyclerView recyclerView, UserAdapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter({"layoutManager"})
    public static void setLayoutManager(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
    }

    @BindingAdapter({"isVisible"})
    public static void isFollow(CheckBox checkBox, boolean isFollow) {
        if (isFollow) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
    }

    @BindingAdapter({"mode"})
    public static void setModeForMap(FloatingActionButton button, int mode) {
        switch (mode) {
            case Constants.MODE_FRIEND:
                button.setImageBitmap(textAsBitmap("GO", 40, Color.WHITE));
                break;
            case Constants.MODE_MAP:
                button.setImageResource(R.drawable.ic_user_list);
                break;
            case Constants.MODE_YOU:
                button.setImageResource(R.drawable.ic_push_location);
                break;
            default:
                break;
        }
    }

    @BindingAdapter({"hasLocation"})
    public static void setVisiable(FloatingActionButton button, Boolean hasLocation) {
        if (hasLocation) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
        }
    }

    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }
}
