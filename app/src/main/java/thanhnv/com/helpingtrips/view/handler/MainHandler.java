package thanhnv.com.helpingtrips.view.handler;

import android.view.View;

/**
 * Created by Thanh on 3/1/2018.
 * MainHandler
 */
public interface MainHandler {

    void onClickPushLocation(View view);
    void onClickAddFriend(View view);
    void onClickGoMap(View view);
    void onAutoPushLocation(View view, boolean checked);

}
