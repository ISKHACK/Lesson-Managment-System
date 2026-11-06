import java.util.*;

public class LessonService {
    private final List<Lesson> lessons = new ArrayList<>();
    private final Set<Lesson> uniqueLessons = new HashSet<>();
    private final Map<Integer, Lesson> byId = new HashMap<>();
    private final Map<String, List<Lesson>> bySubject = new HashMap<>();
    private final Map<String, Student> students = new HashMap<>();
    private final Map<String, Teacher> teachers = new HashMap<>();
    private final Map<Integer, Set<Integer>> enrolled = new HashMap<>();
    private final Map<Integer, Map<Integer, String>> grades = new HashMap<>();
    private final Queue<LessonRequest> requests = new ArrayDeque<>();
    private final List<CourseProgram> programs = new ArrayList<>();
    private int nextUserId = 1;
    private int nextLessonId = 1;
    private int nextRequestId = 1;

    public Student registerStudent(String name, String password, int schoolGrade, String favoriteSubject) {
        String key = name.toLowerCase();
        if (students.containsKey(key) || teachers.containsKey(key)) return null;
        Student student = new Student(nextUserId++, name, password, schoolGrade, favoriteSubject);
        students.put(key, student);
        enrolled.put(student.getId(), new HashSet<>());
        return student;
    }
    public Teacher registerTeacher(String name, String password, String subject) {
        String key = name.toLowerCase();
        if (teachers.containsKey(key) || students.containsKey(key)) return null;
        Teacher teacher = new Teacher(nextUserId++, name, password, subject);
        teachers.put(key, teacher);
        return teacher;
    }
    // Used only when loading existing users from the text files.
    public void loadStudent(int id, String name, String password, int grade, String subject) {
        String key = name.toLowerCase();
        if (students.containsKey(key) || teachers.containsKey(key)) return;
        Student student = new Student(id, name, password, grade, subject);
        students.put(key, student);
        enrolled.put(id, new HashSet<>());
        if (id >= nextUserId) nextUserId = id + 1;
    }
    public void loadTeacher(int id, String name, String password, String subject) {
        String key = name.toLowerCase();
        if (students.containsKey(key) || teachers.containsKey(key)) return;
        teachers.put(key, new Teacher(id, name, password, subject));
        if (id >= nextUserId) nextUserId = id + 1;
    }
    public Student studentLogin(String name, String password) {
        Student s = students.get(name.toLowerCase());
        return s != null && s.checkPassword(password) ? s : null;
    }
    public Teacher teacherLogin(String name, String password) {
        Teacher t = teachers.get(name.toLowerCase());
        return t != null && t.checkPassword(password) ? t : null;
    }
    public Collection<Student> students() { return students.values(); }
    public Collection<Teacher> teachers() { return teachers.values(); }

    private void rebuildSubjects() {
        bySubject.clear();
        for (Lesson l : lessons) bySubject.computeIfAbsent(l.getSubject(), k -> new ArrayList<>()).add(l);
    }
    public Lesson createLesson(Teacher teacher, String detail, int minutes, int room) {
        Lesson lesson;
        if (teacher.getSubject().equalsIgnoreCase("Math"))
            lesson = new MathLesson("Math", teacher.getNickname(), minutes, room, detail, 5);
        else if (teacher.getSubject().equalsIgnoreCase("English"))
            lesson = new EnglishLesson("English", teacher.getNickname(), minutes, room, detail, "General");
        else lesson = new ProgrammingLesson("Programming", teacher.getNickname(), minutes, room, detail, true);
        lesson.setId(nextLessonId++);
        lessons.add(lesson);
        uniqueLessons.add(lesson);
        byId.put(lesson.getId(), lesson);
        rebuildSubjects();
        return lesson;
    }
    // Restore existing lessons and relationships from TXT files.
    public void loadLesson(int id, String subject, String teacher, int minutes, int room, String detail) {
        if (id <= 0 || minutes <= 0 || room < 0 || byId.containsKey(id)) return;
        Teacher owner = teachers.get(teacher.toLowerCase());
        if (owner == null || !owner.getSubject().equalsIgnoreCase(subject)) return;
        Lesson l;
        if (subject.equals("Math")) l = new MathLesson(subject, teacher, minutes, room, detail, 5);
        else if (subject.equals("English")) l = new EnglishLesson(subject, teacher, minutes, room, detail, "General");
        else if (subject.equals("Programming")) l = new ProgrammingLesson(subject, teacher, minutes, room, detail, true);
        else return;
        l.setId(id);
        lessons.add(l);
        uniqueLessons.add(l);
        byId.put(id, l);
        rebuildSubjects();
        if (id >= nextLessonId) nextLessonId = id + 1;
    }
    public void loadEnrollment(int studentId, int lessonId) {
        if (studentExists(studentId) && byId.containsKey(lessonId))
            enrolled.get(studentId).add(lessonId);
    }
    private boolean studentExists(int id) {
        for (Student s : students.values()) if (s.getId() == id) return true;
        return false;
    }
    public void loadRequest(int id, int studentId, int lessonId) {
        if (id <= 0 || !studentExists(studentId) || !byId.containsKey(lessonId)) return;
        if (enrolled.get(studentId).contains(lessonId)) return;
        for (LessonRequest r : requests)
            if (r.getId() == id || (r.getStudentId() == studentId && r.getLessonId() == lessonId)) return;
        requests.add(new LessonRequest(id, studentId, lessonId));
        if (id >= nextRequestId) nextRequestId = id + 1;
    }
    public void loadGrade(int lessonId, int studentId, String value) {
        if (enrolled.getOrDefault(studentId, Collections.emptySet()).contains(lessonId)
                && Arrays.asList("A+", "A", "B+", "B", "C+", "C", "D", "F").contains(value))
            grades.computeIfAbsent(lessonId, k -> new HashMap<>()).put(studentId, value);
    }
    public void loadProgram(String name, String ids) {
        CourseProgram p = new CourseProgram(name);
        if (!ids.isEmpty()) for (String part : ids.split(",")) {
            try {
                Lesson l = byId.get(Integer.parseInt(part));
                if (l != null) p.addLesson(l);
            } catch (NumberFormatException ignored) { }
        }
        programs.add(p);
    }
    public Collection<LessonRequest> requests() { return new ArrayList<>(requests); }
    public Map<Integer, Set<Integer>> enrollments() { return enrolled; }
    public Map<Integer, Map<Integer, String>> grades() { return grades; }
    public List<CourseProgram> programs() { return programs; }

    public Lesson getLesson(int id) { return byId.get(id); }
    public List<Lesson> allLessons() {
        List<Lesson> result = new ArrayList<>(lessons);
        Collections.sort(result);
        return result;
    }
    public List<Lesson> findSubject(String subject) {
        return new ArrayList<>(bySubject.getOrDefault(subject, Collections.emptyList()));
    }
    public List<Lesson> teacherLessons(Teacher teacher) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson l : lessons) if (l.getTeacher().equalsIgnoreCase(teacher.getNickname())) result.add(l);
        return result;
    }
    public boolean updateLesson(int id, int minutes, int room) {
        Lesson l = byId.get(id);
        if (l == null || minutes <= 0 || room < 0) return false;
        l.setDuration(minutes);
        l.setRoomNumber(room);
        return true;
    }
    public boolean deleteLesson(int id) {
        Lesson l = byId.remove(id);
        if (l == null) return false;
        lessons.remove(l);
        uniqueLessons.remove(l);
        requests.removeIf(r -> r.getLessonId() == id);
        grades.remove(id);
        for (Set<Integer> ids : enrolled.values()) ids.remove(id);
        for (CourseProgram p : programs) p.removeLesson(id);
        rebuildSubjects();
        return true;
    }
    public boolean request(Student student, int lessonId) {
        if (!byId.containsKey(lessonId) || enrolled.get(student.getId()).contains(lessonId)) return false;
        for (LessonRequest r : requests)
            if (r.getStudentId() == student.getId() && r.getLessonId() == lessonId) return false;
        requests.add(new LessonRequest(nextRequestId++, student.getId(), lessonId));
        return true;
    }
    public List<LessonRequest> pending(Teacher teacher) {
        List<LessonRequest> result = new ArrayList<>();
        for (LessonRequest r : requests) {
            Lesson l = byId.get(r.getLessonId());
            if (l != null && l.getTeacher().equalsIgnoreCase(teacher.getNickname())) result.add(r);
        }
        return result;
    }
    public boolean decide(Teacher teacher, int requestId, boolean approve) {
        List<LessonRequest> pending = pending(teacher);
        for (LessonRequest r : pending) if (r.getId() == requestId) {
            // Queue.poll() removes the selected request after earlier ones are preserved.
            Queue<LessonRequest> rest = new ArrayDeque<>();
            while (!requests.isEmpty()) {
                LessonRequest next = requests.poll();
                if (next.getId() != requestId) rest.add(next);
            }
            requests.addAll(rest);
            if (approve) enrolled.get(r.getStudentId()).add(r.getLessonId());
            return true;
        }
        return false;
    }
    public String studentName(int id) {
        for (Student s : students.values()) if (s.getId() == id) return s.getNickname();
        return "Unknown";
    }
    public List<Student> enrolledStudents(int lessonId) {
        List<Student> result = new ArrayList<>();
        for (Student s : students.values())
            if (enrolled.get(s.getId()).contains(lessonId)) result.add(s);
        return result;
    }
    public List<Lesson> studentLessons(Student student) {
        List<Lesson> result = new ArrayList<>();
        for (Integer id : enrolled.get(student.getId())) if (byId.containsKey(id)) result.add(byId.get(id));
        return result;
    }
    public boolean setGrade(Teacher teacher, int lessonId, int studentId, String grade) {
        Lesson lesson = byId.get(lessonId);
        if (lesson == null || !lesson.getTeacher().equalsIgnoreCase(teacher.getNickname())) return false;
        if (!Arrays.asList("A+", "A", "B+", "B", "C+", "C", "D", "F").contains(grade)) return false;
        if (!enrolled.getOrDefault(studentId, Collections.emptySet()).contains(lessonId)) return false;
        grades.computeIfAbsent(lessonId, k -> new HashMap<>()).put(studentId, grade);
        return true;
    }
    public String grade(int studentId, int lessonId) {
        return grades.getOrDefault(lessonId, Collections.emptyMap()).getOrDefault(studentId, "Not graded");
    }
    public boolean deleteStudent(int studentId) {
        Student found = null;
        for (Student s : students.values()) if (s.getId() == studentId) found = s;
        if (found == null) return false;
        students.remove(found.getNickname().toLowerCase());
        enrolled.remove(studentId);
        requests.removeIf(r -> r.getStudentId() == studentId);
        for (Map<Integer, String> scores : grades.values()) scores.remove(studentId);
        return true;
    }
    public boolean deleteTeacher(int teacherId) {
        Teacher found = null;
        for (Teacher t : teachers.values()) if (t.getId() == teacherId) found = t;
        if (found == null) return false;
        for (Lesson l : new ArrayList<>(teacherLessons(found))) deleteLesson(l.getId());
        teachers.remove(found.getNickname().toLowerCase());
        return true;
    }
    public CourseProgram createProgram(String name) {
        CourseProgram p = new CourseProgram(name);
        for (Lesson l : lessons) p.addLesson(l);
        programs.add(p);
        return p;
    }
    public List<Lesson> sorted(int type) {
        List<Lesson> result = new ArrayList<>(lessons);
        if (type == 1) result.sort(Comparator.comparing(Lesson::getSubject));
        if (type == 2) result.sort(Comparator.comparingInt(Lesson::getDuration));
        if (type == 3) result.sort(Comparator.comparing(Lesson::getTeacher));
        return result;
    }
    public void statistics() {
        int total = 0;
        for (Lesson l : lessons) total += l.getDuration();
        System.out.println("Students: " + students.size() + ", Teachers: " + teachers.size()
                + ", Lessons: " + lessons.size() + ", Pending requests: " + requests.size());
        System.out.println("Average lesson duration: " + (lessons.isEmpty() ? 0 : total / lessons.size()) + " minutes");
    }
}
