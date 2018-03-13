package thanhnv.com.helpingtrips.util.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import thanhnv.com.helpingtrips.data.remote.FirebaseUser;

/**
 * Created by Thanh on 3/1/2018.
 */

public class MyFireBase {
    private static final String FB_USER_KEY = "users";
    private static final String FB_LASTEST_ID = "lastestId";

    public static void writeNewUserToDatabase(String userId, FirebaseUser newUser, final OnWriteUserListener listener) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(FB_USER_KEY).child(userId).setValue(newUser);

        // listener for write to database
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            // write success
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess();
            }

            // write fail
            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFail();
            }
        });
    }

    public static void getUserById(String userId, final OnGetUserListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child(FB_USER_KEY).child(userId);

        myRef.addValueEventListener(new ValueEventListener() {
            // get user success
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = dataSnapshot.getValue(FirebaseUser.class);
                listener.onSuccess(user);
            }

            // get user fail
            @Override
            public void onCancelled(DatabaseError error) {
                listener.onFail(error.getMessage());
            }
        });
    }

//    public static Single<FirebaseUser> getUserByIdRx(final String userId) {
//        return Single.create(new SingleOnSubscribe<FirebaseUser>() {
//            @Override
//            public void subscribe(final SingleEmitter<FirebaseUser> emitter) throws Exception {
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference().child(FB_USER_KEY).child(userId);
//
//                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    // get user success
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        FirebaseUser user = dataSnapshot.getValue(FirebaseUser.class);
//                        emitter.onSuccess(user);
//                    }
//
//                    // get user fail
//                    @Override
//                    public void onCancelled(DatabaseError error) {
//                        emitter.onError(new Exception(error.getMessage()));
//                    }
//                });
//            }
//        });
//    }

    public static void createNewUserIdTransaction(final OnCreateNewUserIdListener listener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FB_LASTEST_ID);

        databaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class);
                if (currentValue == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentValue + 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                // Transaction completed
                listener.onCreateSuccess(dataSnapshot.getValue(long.class) + "");
            }
        });
    }

    public interface OnGetUserListener {
        void onSuccess(FirebaseUser user);
        void onFail(String error);
    }

    public interface OnCreateNewUserIdListener {
        void onCreateSuccess(String value);
    }

    public interface OnWriteUserListener {
        void onSuccess();

        void onFail();
    }
}
