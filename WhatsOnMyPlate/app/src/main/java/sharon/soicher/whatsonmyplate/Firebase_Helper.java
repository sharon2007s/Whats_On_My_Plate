package sharon.soicher.whatsonmyplate;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.CompletableFuture;

public class Firebase_Helper {
    private Context context;

    private FirebaseAuth firebase_auth;
    private FirebaseDatabase database;



    /*
         A function to insert user to the realtime database.
        Input: profile
        Return value: none
     */
    private void addUserRealtimedatabase(UserProfile profile) {
        DatabaseReference usersreference = database.getReference("USERS");

        String uid = firebase_auth.getCurrentUser().getUid();

        usersreference.child(uid).setValue(false).addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "succeed", Toast.LENGTH_SHORT).show();
                })
                .addOnSuccessListener(e -> {
                    Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                });

        usersreference.child(uid).child("profile").setValue(profile).addOnCompleteListener(aVoid -> {
                    Toast.makeText(context, "succeed", Toast.LENGTH_SHORT).show();
                })
                .addOnSuccessListener(e -> {
                    Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                });
    }
//constructor
    public  Firebase_Helper(Context context){
        this.context=context;
        this.firebase_auth= FirebaseAuth.getInstance();
        this.database=FirebaseDatabase.getInstance();
    }

    /*
         A function to register a new user to the auth database.
        Input: profile, email, password
        Return value: user's id
     */
    public CompletableFuture<String> Register(UserProfile profile, String email, String password)
    {
        CompletableFuture<String> future = new CompletableFuture<>();

        firebase_auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FirebaseUser user = firebase_auth.getCurrentUser();
                String uid = user.getUid();
                future.complete(uid);
                addUserRealtimedatabase(profile);
            }
            else {
                Exception exception = task.getException();
                String errorMessage = "Register faild:" + (exception != null ? exception.getMessage() : "Unknown error");
                future.completeExceptionally(new Exception(errorMessage));
            }
        });
                return future;
    }
}
