package sharon.soicher.whatsonmyplate;

public class DiaryEntry {
    private String id;
    private String date;
    private String text;
    private String photoUrl;

    public DiaryEntry() {} // Empty constructor for Firebase

    public DiaryEntry(String id, String date, String text, String photoUrl) {
        this.id = id;
        this.date = date;
        this.text = text;
        this.photoUrl = photoUrl;
    }

    public String getId() { return id; }
    public String getDate() { return date; }
    public String getText() { return text; }
    public String getPhotoUrl() { return photoUrl; }
}