public class EnglishLesson extends Lesson implements Practiceable, HomeworkAssignable {
 
    private String level;
    private String lessonType;
 
    public EnglishLesson(String subject, String teacher, int duration, int roomNumber,
                         String level, String lessonType) {
        super(subject, teacher, duration, roomNumber);
        setLevel(level);
        setLessonType(lessonType);
    }
 
    public String getLevel() {
        return level;
    }
 
    public void setLevel(String level) {
        if (level != null && !level.trim().isEmpty()) {
            this.level = level;
        } else {
            this.level = "Beginner";
        }
    }
 
    public String getLessonType() {
        return lessonType;
    }
 
    public void setLessonType(String lessonType) {
        if (lessonType != null && !lessonType.trim().isEmpty()) {
            this.lessonType = lessonType;
        } else {
            this.lessonType = "General";
        }
    }
 
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Level: " + level);
        System.out.println("Lesson Type: " + lessonType);
    }
 
    @Override
    public void prepareLesson() {
        System.out.println("Preparing vocabulary and grammar materials.");
    }
 
    @Override
    public void conductLesson() {
        System.out.println("Conducting an English " + lessonType + " lesson.");
    }
 
    @Override
    public void performPractice() {
        System.out.println("English practice: students practice speaking and vocabulary.");
    }
 
    @Override
    public void assignHomework() {
        System.out.println("English homework: review vocabulary and grammar.");
    }
 
    public void practiceSpeaking() {
        System.out.println("Students are practicing speaking.");
    }
 
    public void learnWords() {
        System.out.println("Students are learning new words.");
    }
}
