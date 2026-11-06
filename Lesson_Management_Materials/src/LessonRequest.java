import java.util.Objects;
public class LessonRequest {
    private final int id;
    private final int studentId;
    private final int lessonId;
    public LessonRequest(int id, int studentId, int lessonId) {
        this.id = id; this.studentId = studentId; this.lessonId = lessonId;
    }
    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public int getLessonId() { return lessonId; }
    @Override public boolean equals(Object o) { return o instanceof LessonRequest && id == ((LessonRequest)o).id; }
    @Override public int hashCode() { return Objects.hash(id); }
}
