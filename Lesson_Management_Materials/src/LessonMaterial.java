import java.util.ArrayList;
import java.util.List;

public class LessonMaterial {
    private String title;
    private String content;

    public LessonMaterial(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String toString() { return title + ": " + content; }
}
