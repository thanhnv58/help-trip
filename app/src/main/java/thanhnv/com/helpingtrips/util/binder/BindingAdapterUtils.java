package thanhnv.com.helpingtrips.util.binder;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import thanhnv.com.helpingtrips.R;
import thanhnv.com.helpingtrips.util.Constants;
import thanhnv.com.helpingtrips.view.adapter.MarkerAdapter;
import thanhnv.com.helpingtrips.view.adapter.UserAdapter;

/**
 * Created by Thanh on 3/6/2018.
 * BindingAdapterUtils
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

    @BindingAdapter({"isVisible"})
    public static void showFloatingButton(final FloatingActionButton btn, boolean show) {
        if (show) {
            btn.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(btn.getContext(), R.anim.anim_open);
            btn.startAnimation(animation);
        } else {
            Animation animation = AnimationUtils.loadAnimation(btn.getContext(), R.anim.anim_off);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    btn.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            btn.startAnimation(animation);
        }
    }

    @BindingAdapter({"src"})
    public static void setMarkerSrc(ImageView imageView, int source) {
        imageView.setImageResource(source);
    }

    @BindingAdapter({"mode"})
    public static void setModeForMap(FloatingActionButton button, int mode) {
        switch (mode) {
            case Constants.MODE_FRIEND:
                button.setImageBitmap(textAsBitmap(Color.WHITE));
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

    @BindingAdapter({"pushing"})
    public static void setProgressBar(ProgressBar button, boolean isPushing) {
        if (isPushing) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }
    }

    @BindingAdapter({"clickFriend"})
    public static void clickFriend(final FloatingActionButton btn, boolean showBtnGo) {
        if (showBtnGo) {
            btn.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(btn.getContext(), R.anim.anim_open);
            btn.startAnimation(animation);
        } else {
            Animation animation = AnimationUtils.loadAnimation(btn.getContext(), R.anim.anim_off);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    btn.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            btn.startAnimation(animation);
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

    @BindingAdapter({"position"})
    public static void setPosition(DiscreteScrollView listView, int position) {
        listView.smoothScrollToPosition(position);
    }

    @BindingAdapter({"adapter"})
    public static void setAdapterScrollView(DiscreteScrollView discreteMarker, MarkerAdapter adapter) {
        discreteMarker.setAdapter(adapter);
        discreteMarker.setSlideOnFling(true);
        discreteMarker.setItemTransitionTimeMillis(150);
        discreteMarker.setItemTransformer(new ScaleTransformer.Builder().setMinScale(0.8f).build());
    }

    private static Bitmap textAsBitmap(int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize((float) 40);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText("GO") + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText("GO", 0, baseline, paint);
        return image;
    }
}
