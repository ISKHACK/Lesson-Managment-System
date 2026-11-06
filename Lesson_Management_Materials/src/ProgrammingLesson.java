public class ProgrammingLesson extends Lesson implements Practiceable, HomeworkAssignable {
 
    private String language;
    private boolean computerRequired;
 
    public ProgrammingLesson(String subject, String teacher, int duration, int roomNumber,
                             String language, boolean computerRequired) {
        super(subject, teacher, duration, roomNumber);
        setLanguage(language);
        setComputerRequired(computerRequired);
    }
 
    public String getLanguage() {
        return language;
    }
 
    public void setLanguage(String language) {
        if (language != null && !language.trim().isEmpty()) {
            this.language = language;
        } else {
            this.language = "Java";
        }
    }
 
    public boolean isComputerRequired() {
        return computerRequired;
    }
 
    public void setComputerRequired(boolean computerRequired) {
        this.computerRequired = computerRequired;
    }
 
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Language: " + language);
        System.out.println("Computer Required: " + computerRequired);
    }
 
    @Override
    public void prepareLesson() {
        System.out.println("Preparing code examples and programming tasks.");
    }
 
    @Override
    public void conductLesson() {
        System.out.println("Conducting a programming lesson in " + language + ".");
    }
 
    @Override
    public void performPractice() {
        System.out.println("Programming practice: students write and test code.");
    }
 
    @Override
    public void assignHomework() {
        System.out.println("Programming homework: create a small " + language + " program.");
    }
 
    public void writeCode() {
        System.out.println("Students are writing " + language + " code.");
    }
 
    public void runProgram() {
        System.out.println("The program is running.");
    }
}
