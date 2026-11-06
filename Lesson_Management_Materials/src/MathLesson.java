public class MathLesson extends Lesson implements Practiceable, HomeworkAssignable {
 
    private String topic;
    private int numberOfProblems;
 
    public MathLesson(String subject, String teacher, int duration, int roomNumber,
                      String topic, int numberOfProblems) {
        super(subject, teacher, duration, roomNumber);
        setTopic(topic);
        setNumberOfProblems(numberOfProblems);
    }
 
    public String getTopic() {
        return topic;
    }
 
    public void setTopic(String topic) {
        if (topic != null && !topic.trim().isEmpty()) {
            this.topic = topic;
        } else {
            this.topic = "General Mathematics";
        }
    }
 
    public int getNumberOfProblems() {
        return numberOfProblems;
    }
 
    public void setNumberOfProblems(int numberOfProblems) {
        if (numberOfProblems >= 0) {
            this.numberOfProblems = numberOfProblems;
        }
    }
 
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Topic: " + topic);
        System.out.println("Problems: " + numberOfProblems);
    }
 
    @Override
    public void prepareLesson() {
        System.out.println("Preparing formulas and math exercises.");
    }
 
    @Override
    public void conductLesson() {
        System.out.println("Conducting a mathematics lesson on " + topic + ".");
    }
 
    @Override
    public void performPractice() {
        System.out.println("Math practice: students solve exercises.");
    }
 
    @Override
    public void assignHomework() {
        System.out.println("Math homework: solve " + numberOfProblems + " problems.");
    }
 
    public void solveProblem() {
        System.out.println("Students are solving math problems.");
    }
 
    public void giveHomework() {
        System.out.println("Math homework was given.");
    }
}
