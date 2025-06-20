package sharon.soicher.whatsonmyplate;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CompletableFuture;

public class Firebase_Helper {
    private static Context context;

    private FirebaseAuth firebase_auth;
    private static FirebaseDatabase database;



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

    /*
       A function to login user.
       Input: email, password
       Return value: user's id
    */
    public CompletableFuture<String> login(String email, String password) {
        CompletableFuture<String> future = new CompletableFuture<>();

        firebase_auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebase_auth.getCurrentUser();
                        String uid = user.getUid();
                        future.complete(uid);
                    } else {
                        Exception exception = task.getException();
                        String errorMessage = "Login failed: " + (exception != null ? exception.getMessage() : "Unknown error");
                        future.completeExceptionally(new Exception(errorMessage));
                    }
                });

        return future;
    }

    public CompletableFuture<String> getUserName(String userId)
    {
        CompletableFuture<String> future = new CompletableFuture<>();
        DatabaseReference userNameReference = database.getReference("USERS").child(userId).child("profile").child("name");
        userNameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String userName = snapshot.getValue(String.class);
                    future.complete(userName);
                }
                else {
                    future.complete(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(new Exception("Faild " + userId + ": " + error.getMessage()));
            }
        });

        return future;
    }
    public static void addMealFromAPI(String userId, NutritionAPI.FoodNutritionData foodData, String mealType) {
        DatabaseReference mealsReference = database.getReference("USERS").child(userId).child("MEALS").child(mealType);

        String mealId = mealsReference.push().getKey(); // Generate unique meal ID
        if (mealId != null) {
            MealEntry mealEntry = new MealEntry(foodData.getName(), (int) foodData.getWeightInGrams(),
                    (int) foodData.getCalories(), (int) foodData.getProtein(),
                    (int) foodData.getFat(), (int) foodData.getCarbs());
            mealsReference.child(mealId).setValue(mealEntry)
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Meal saved!", Toast.LENGTH_SHORT).show());
            updateCalorieConsumed(userId, (int) foodData.getCalories());

        }
    }
    public static void addExercise(String userId, String exerciseType, int durationTime, int caloriesBurned) {
        // Get the database reference for the user's EXERCISE node
        DatabaseReference exerciseReference = database.getReference("USERS")
                .child(userId)
                .child("EXERCISE");

        // Generate a new unique key for the exercise
        String exerciseId = exerciseReference.push().getKey();

        if (exerciseId != null) {
            // Construct an ExerciseEntry object (you may need to create this class)
            ExerciseEntry exerciseEntry = new ExerciseEntry(exerciseType, durationTime, caloriesBurned);

            // Save the exercise entry in the database
            exerciseReference.child(exerciseId).setValue(exerciseEntry)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(context, "Exercise logged successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Error logging exercise.", Toast.LENGTH_SHORT).show());
        }
    }
    public static void addSteps(String userId, int stepsGoal, int currentSteps) {
        // reference to USERS -> <userId> -> STEPS
        DatabaseReference stepsReference = database.getReference("USERS")
                .child(userId)
                .child("STEPS");

        StepsEntry stepsEntry = new StepsEntry(stepsGoal, currentSteps);

        stepsReference.setValue(stepsEntry)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Steps updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error updating steps.", Toast.LENGTH_SHORT).show());
    }
    public static void addWaterIntake(String userId, int waterIntake) {
        // Get reference to USERS -> <userId> -> WATERTRACKER node
        DatabaseReference waterReference = database.getReference("USERS")
                .child(userId)
                .child("WATERTRACKER");

        // Create a WaterTrackerEntry instance with the current water intake
        WaterTrackerEntry waterTrackerEntry = new WaterTrackerEntry(waterIntake);

        waterReference.setValue(waterTrackerEntry)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Water intake updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error updating water intake.", Toast.LENGTH_SHORT).show());
    }

    public static void updateCalorieConsumed(String userId, int addedCalories) {
        // Reference to the cumulative calorie count for the user
        DatabaseReference totalRef = database.getReference("USERS")
                .child(userId)
                .child("calorieConsumed");

        totalRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer currentTotal = currentData.getValue(Integer.class);
                if (currentTotal == null) {
                    currentData.setValue(addedCalories);
                } else {
                    currentData.setValue(currentTotal + addedCalories);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                // Optionally handle errors or log the successful update here.
            }
        });
    }
}
