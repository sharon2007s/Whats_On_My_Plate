package sharon.soicher.whatsonmyplate;

public class ExerciseEntry {
    private String exerciseType;
    private int durationTime; // in minutes
    private int caloriesBurned;

    // Default constructor required for calls to DataSnapshot.getValue(ExerciseEntry.class)
    public ExerciseEntry() {}

    public ExerciseEntry(String exerciseType, int durationTime, int caloriesBurned) {
        this.exerciseType = exerciseType;
        this.durationTime = durationTime;
        this.caloriesBurned = caloriesBurned;
    }

    // Getters and Setters
    public String getExerciseType() { return exerciseType; }
    public void setExerciseType(String exerciseType) { this.exerciseType = exerciseType; }

    public int getDurationTime() { return durationTime; }
    public void setDurationTime(int durationTime) { this.durationTime = durationTime; }

    public int getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(int caloriesBurned) { this.caloriesBurned = caloriesBurned; }
}