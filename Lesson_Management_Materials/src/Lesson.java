public abstract class Lesson implements Comparable<Lesson> {
    private int id;
 
    private String subject;
    private String teacher;
    private int duration;
    private int roomNumber;
 
    // COMPOSITION: every Lesson owns its LessonMaterial object.
    // The material is created inside Lesson and belongs to this lesson.
    private final java.util.List<LessonMaterial> materials = new java.util.ArrayList<>();
 
    public Lesson() {
        this("Unknown", "Unknown", 45, 0);
    }
 
    public Lesson(String subject, String teacher, int duration, int roomNumber) {
        setSubject(subject);
        setTeacher(teacher);
        setDuration(duration);
        setRoomNumber(roomNumber);
 

    }
 
    // Assigned once by LessonService when the lesson is added.
    public int getId() { return id; }
    public void setId(int id) {
        if (this.id == 0 && id > 0) this.id = id;
    }
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Lesson)) return false;
        Lesson lesson = (Lesson) other;
        return id > 0 && id == lesson.id;
    }
    @Override
    public int hashCode() { return id > 0 ? Integer.hashCode(id) : System.identityHashCode(this); }
    @Override
    public int compareTo(Lesson other) { return Integer.compare(id, other.id); }

    public String getSubject() {
        return subject;
    }
 
    public void setSubject(String subject) {
        if (subject != null && !subject.trim().isEmpty()) {
            this.subject = subject;
        } else {
            this.subject = "Unknown";
        }
    }
 
    public String getTeacher() {
        return teacher;
    }
 
    public void setTeacher(String teacher) {
        if (teacher != null && !teacher.trim().isEmpty()) {
            this.teacher = teacher;
        } else {
            this.teacher = "Unknown";
        }
    }
 
    public int getDuration() {
        return duration;
    }
 
    public void setDuration(int duration) {
        if (duration > 0) {
            this.duration = duration;
        } else if (this.duration == 0) {
            this.duration = 45;
        }
    }
 
    public int getRoomNumber() {
        return roomNumber;
    }
 
    public void setRoomNumber(int roomNumber) {
        if (roomNumber >= 0) {
            this.roomNumber = roomNumber;
        } else if (this.roomNumber == 0) {
            this.roomNumber = 0;
        }
    }
 
    public void displayInfo() {
        System.out.println("Subject: " + subject);
        System.out.println("Teacher: " + teacher);
        System.out.println("Duration: " + duration + " minutes");
        System.out.println("Room: " + roomNumber);
    }
 
    // Composition: each lesson owns its materials.
    public java.util.List<LessonMaterial> getMaterials() {
        return java.util.Collections.unmodifiableList(materials);
    }
    public void addMaterial(String title, String content) {
        materials.add(new LessonMaterial(title, content));
    }
    public boolean removeMaterial(int index) {
        if (index < 0 || index >= materials.size()) return false;
        materials.remove(index);
        return true;
    }

    public abstract void prepareLesson();
 
    public abstract void conductLesson();
}
