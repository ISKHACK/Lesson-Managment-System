import java.util.ArrayList;
import java.util.List;
 
public class CourseProgram {
 
    private String programName;
 
    // AGGREGATION: Lesson objects are created independently and are then
    // added to CourseProgram. They can exist without this CourseProgram.
    private final ArrayList<Lesson> lessons;
 
    public CourseProgram(String programName) {
        setProgramName(programName);
        lessons = new ArrayList<>();
    }
 
    public String getProgramName() {
        return programName;
    }
 
    public void setProgramName(String programName) {
        if (programName != null && !programName.trim().isEmpty()) {
            this.programName = programName;
        } else {
            this.programName = "Unnamed Program";
        }
    }
 
    public void addLesson(Lesson lesson) {
        if (lesson != null) {
            if (!lessons.contains(lesson)) lessons.add(lesson);
        }
    }
 
    public void removeLesson(int lessonId) {
        lessons.removeIf(lesson -> lesson.getId() == lessonId);
    }

    public int getLessonCount() {
        return lessons.size();
    }
 
    public List<Lesson> getLessons() {
        return new ArrayList<>(lessons);
    }
 
    public void displayProgramInfo() {
        System.out.println("Program: " + programName);
        System.out.println("Number of lessons: " + lessons.size());
 
        for (Lesson lesson : lessons) {
            System.out.println("- " + lesson.getSubject()
                    + " | Teacher: " + lesson.getTeacher());
        }
    }
}
