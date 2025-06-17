package sharon.soicher.whatsonmyplate;

public class WaterTrackerEntry {
    private int waterIntake; // Can also add a timestamp if needed

    // Default constructor required for Firebase
    public WaterTrackerEntry() { }

    public WaterTrackerEntry(int waterIntake) {
        this.waterIntake = waterIntake;
    }

    public int getWaterIntake() {
        return waterIntake;
    }

    public void setWaterIntake(int waterIntake) {
        this.waterIntake = waterIntake;
    }
}