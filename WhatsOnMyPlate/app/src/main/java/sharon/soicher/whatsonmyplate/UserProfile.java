package sharon.soicher.whatsonmyplate;

public class UserProfile {
    private String name;
    private int age;
    private String gender;
    private float weight;
    private float height;
    private String goals;

    // Default constructor (required for Firebase)
    public UserProfile() { }

    // Constructor with six parameters
    public UserProfile(String name, int age, String gender, float weight, float height, String goals) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.goals = goals;
    }

    // Getters and setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public float getWeight() {
        return weight;
    }
    public void setWeight(float weight) {
        this.weight = weight;
    }
    public float getHeight() {
        return height;
    }
    public void setHeight(float height) {
        this.height = height;
    }
    public String getGoals() {
        return goals;
    }
    public void setGoals(String goals) {
        this.goals = goals;
    }
}