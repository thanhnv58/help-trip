package thanhnv.com.helpingtrips.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Thanh on 3/5/2018.
 * User
 */
@Entity
public class User {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "is_follow")
    private boolean isFollow;

    @ColumnInfo(name = "icon_id")
    private int iconId;

    public User() {
    }

    public User(int id, String name, boolean isFollow, int iconId) {
        this.id = id;
        this.name = name;
        this.isFollow = isFollow;
        this.iconId = iconId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public void updateUser(User user) {
        this.name = user.getName();
        this.iconId = user.getIconId();
    }
}
