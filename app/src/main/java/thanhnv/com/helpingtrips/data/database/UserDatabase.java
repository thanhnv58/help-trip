package thanhnv.com.helpingtrips.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import thanhnv.com.helpingtrips.data.model.User;
import thanhnv.com.helpingtrips.data.repository.UserRepository;

/**
 * Created by Thanh on 3/5/2018.
 */

@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserRepository userRepository();
}
