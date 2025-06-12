package sharon.soicher.whatsonmyplate;

public class UserProfile {
    private String name;
    private int age;
    private String gender;
    private float height;
    private float weight;
    private String goals;
   // true = kg/cm, false = lbs/in

    public UserProfile() {} // Empty constructor for Firebase

    public UserProfile(String name, int age, String gender, float height, float weight, String goals) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.goals = goals;

    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public float getHeight() { return height; }
    public float getWeight() { return weight; }
    public String getGoals() { return goals; }
}