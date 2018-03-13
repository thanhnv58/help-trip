package thanhnv.com.helpingtrips.data.repository;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import thanhnv.com.helpingtrips.data.model.User;

/**
 * Created by Thanh on 3/5/2018.
 */

@Dao
public interface UserRepository {

    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE id IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " + "last_name LIKE :last LIMIT 1")
//    User findByName(String first, String last);

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);

    @Query("DELETE FROM USER WHERE id = (:userId)")
    void delete(int userId);

    @Update
    int update(User user);

    @Query("SELECT * FROM USER WHERE is_follow = 1")
    List<User> getFollowingFriends();
}
