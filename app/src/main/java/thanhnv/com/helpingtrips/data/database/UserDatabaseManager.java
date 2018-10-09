package thanhnv.com.helpingtrips.data.database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

import thanhnv.com.helpingtrips.data.model.User;
import thanhnv.com.helpingtrips.view.application.MyApplication;

/**
 * Created by Thanh on 3/5/2018.
 * UserDatabaseManager
 */
public class UserDatabaseManager {
    public static final int DUPLICATE_ID = 1;
    public static final int SUCCESS = 0;

    private UserDatabase userDatabase = null;

    private static final UserDatabaseManager INSTANCE = new UserDatabaseManager();

    public static UserDatabaseManager getInstance() {
        return INSTANCE;
    }

    private UserDatabaseManager() {
    }

    public void init(Context context) {
        userDatabase = Room.databaseBuilder(context, UserDatabase.class, "user").allowMainThreadQueries().build();
    }

    public int insertUser(final User user) {
        if (userDatabase == null) {
            return -1;
        }
        try {
            userDatabase.userRepository().insertAll(user);
            return SUCCESS;
        } catch (SQLiteConstraintException e) {
            return DUPLICATE_ID;
        }
    }

    public List<User> getAllUser() {
        if (userDatabase == null) {
            return null;
        }

        return userDatabase.userRepository().getAll();
    }

    public void deleteUser(int userId) {
        if (userDatabase == null) {
            return;
        }
        userDatabase.userRepository().delete(userId);
    }

    public void updateUser(int position) {
        if (userDatabase == null) {
            return;
        }
        userDatabase.userRepository().update(MyApplication.yourFriends.get(position));
    }

    public int updateUser(User user) {
        if (userDatabase == null) {
            return -1;
        }
        return userDatabase.userRepository().update(user);
    }
}
