package sharon.soicher.whatsonmyplate;

public class StepsEntry {
    private int stepsGoal;
    private int currentSteps;

    // Default constructor is required for calls to DataSnapshot.getValue(StepsEntry.class)
    public StepsEntry() { }

    public StepsEntry(int stepsGoal, int currentSteps) {
        this.stepsGoal = stepsGoal;
        this.currentSteps = currentSteps;
    }

    // Getters and Setters
    public int getStepsGoal() {
        return stepsGoal;
    }

    public void setStepsGoal(int stepsGoal) {
        this.stepsGoal = stepsGoal;
    }

    public int getCurrentSteps() {
        return currentSteps;
    }

    public void setCurrentSteps(int currentSteps) {
        this.currentSteps = currentSteps;
    }
}
