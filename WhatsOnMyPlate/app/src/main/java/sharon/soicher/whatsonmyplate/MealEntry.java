package sharon.soicher.whatsonmyplate;

public class MealEntry {
    private String foodName;
    private int weightInGrams;
    private int calories;
    private int protein;
    private int fat;
    private int carbs;

    public MealEntry() {} // Required for Firebase

    public MealEntry(String foodName, int weightInGrams, int calories, int protein, int fat, int carbs) {
        this.foodName = foodName;
        this.weightInGrams = weightInGrams;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
    }

    public String getFoodName() { return foodName; }
    public int getWeightInGrams() { return weightInGrams; }
    public int getCalories() { return calories; }
    public int getProtein() { return protein; }
    public int getFat() { return fat; }
    public int getCarbs() { return carbs; }
}