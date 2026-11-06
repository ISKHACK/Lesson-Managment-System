import java.io.*;
import java.nio.file.*;

public class TextFiles {
    // Locate the project folder even if IntelliJ starts in src/ or build/.
    private static final Path ROOT = findProjectFolder();
    private static final Path DATA = ROOT.resolve("data");
    private static final Path STUDENTS = DATA.resolve("students.txt");
    private static final Path TEACHERS = DATA.resolve("teachers.txt");
    private static final Path LESSONS = DATA.resolve("lessons.txt");
    private static final Path ENROLLMENTS = DATA.resolve("enrollments.txt");
    private static final Path REQUESTS = DATA.resolve("requests.txt");
    private static final Path GRADES = DATA.resolve("grades.txt");
    private static final Path PROGRAMS = DATA.resolve("programs.txt");
    private static final Path MATERIALS = DATA.resolve("materials.txt");

    private static Path findProjectFolder() {
        Path current = Paths.get("").toAbsolutePath().normalize();
        Path found = searchParents(current);
        if (found != null) return found;
        try {
            Path classes = Paths.get(TextFiles.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            found = searchParents(classes);
            if (found != null) return found;
        } catch (Exception ignored) { }
        return current; // Works if launched from a new empty project folder.
    }

    private static Path searchParents(Path path) {
        for (int i = 0; path != null && i < 8; i++, path = path.getParent()) {
            if (Files.exists(path.resolve("src/Main.java"))) return path;
        }
        return null;
    }

    public static void showDataFolder() {
        System.out.println("TXT data folder: " + DATA.toAbsolutePath());
    }

    public static void load(LessonService service) {
        try {
            if (Files.exists(STUDENTS)) {
                try (BufferedReader reader = Files.newBufferedReader(STUDENTS)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] data = line.trim().split("\\s+");
                        if (data.length != 5) continue;
                        try {
                            int id = Integer.parseInt(data[0]);
                            int grade = Integer.parseInt(data[3]);
                            if (id > 0 && grade >= 1 && grade <= 12)
                                service.loadStudent(id, data[1], data[4], grade, data[2]);
                        } catch (NumberFormatException ignored) {
                            System.out.println("Skipping invalid student row.");
                        }
                    }
                }
            }
            if (Files.exists(TEACHERS)) {
                try (BufferedReader reader = Files.newBufferedReader(TEACHERS)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] data = line.trim().split("\\s+");
                        if (data.length != 4) continue;
                        try {
                            int id = Integer.parseInt(data[0]);
                            if (id > 0) service.loadTeacher(id, data[1], data[3], data[2]);
                        } catch (NumberFormatException ignored) {
                            System.out.println("Skipping invalid teacher row.");
                        }
                    }
                }
            }
            // Read lessons after teachers, then relationships after lessons.
            if (Files.exists(LESSONS)) try (BufferedReader reader = Files.newBufferedReader(LESSONS)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] d = line.split("\t", -1);
                    if (d.length != 6) continue;
                    try { service.loadLesson(Integer.parseInt(d[0]), d[1], d[2],
                            Integer.parseInt(d[3]), Integer.parseInt(d[4]), d[5]); }
                    catch (NumberFormatException ignored) { System.out.println("Skipping invalid lesson row."); }
                }
            }
            // Read materials after lessons so each record has an existing owner.
            if (Files.exists(MATERIALS)) try (BufferedReader reader = Files.newBufferedReader(MATERIALS)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] d = line.split("\t", -1);
                    if (d.length != 3) continue;
                    try {
                        int lessonId = Integer.parseInt(d[0]);
                        Lesson lesson = service.getLesson(lessonId);
                        if (lesson != null && !d[1].isEmpty() && !d[2].isEmpty())
                            lesson.addMaterial(d[1], d[2]);
                    } catch (NumberFormatException ignored) { }
                }
            }
            if (Files.exists(ENROLLMENTS)) try (BufferedReader reader = Files.newBufferedReader(ENROLLMENTS)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] d = line.split("\t", -1);
                    if (d.length != 2) continue;
                    try { service.loadEnrollment(Integer.parseInt(d[0]), Integer.parseInt(d[1])); }
                    catch (NumberFormatException ignored) { System.out.println("Skipping invalid enrollment row."); }
                }
            }
            if (Files.exists(REQUESTS)) try (BufferedReader reader = Files.newBufferedReader(REQUESTS)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] d = line.split("\t", -1);
                    if (d.length != 3) continue;
                    try { service.loadRequest(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2])); }
                    catch (NumberFormatException ignored) { System.out.println("Skipping invalid request row."); }
                }
            }
            if (Files.exists(GRADES)) try (BufferedReader reader = Files.newBufferedReader(GRADES)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] d = line.split("\t", -1);
                    if (d.length != 3) continue;
                    try { service.loadGrade(Integer.parseInt(d[0]), Integer.parseInt(d[1]), d[2]); }
                    catch (NumberFormatException ignored) { System.out.println("Skipping invalid grade row."); }
                }
            }
            if (Files.exists(PROGRAMS)) try (BufferedReader reader = Files.newBufferedReader(PROGRAMS)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] d = line.split("\t", -1);
                    if (d.length == 2 && !d[0].isEmpty()) service.loadProgram(d[0], d[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read user files: " + e.getMessage());
        }
    }

    public static void save(LessonService service) {
        try {
            Files.createDirectories(STUDENTS.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(STUDENTS)) {
                for (Student s : service.students()) {
                    writer.write(s.getId() + " " + s.getNickname() + " " + s.getFavoriteSubject()
                            + " " + s.getSchoolGrade() + " " + s.getPasswordForFile());
                    writer.newLine();
                }
            }
            try (BufferedWriter writer = Files.newBufferedWriter(TEACHERS)) {
                for (Teacher t : service.teachers()) {
                    writer.write(t.getId() + " " + t.getNickname() + " " + t.getSubject()
                            + " " + t.getPasswordForFile());
                    writer.newLine();
                }
            }
            try (BufferedWriter writer = Files.newBufferedWriter(LESSONS)) {
                for (Lesson l : service.allLessons()) {
                    String detail;
                    if (l instanceof MathLesson) detail = ((MathLesson) l).getTopic();
                    else if (l instanceof EnglishLesson) detail = ((EnglishLesson) l).getLevel();
                    else detail = ((ProgrammingLesson) l).getLanguage();
                    writer.write(l.getId() + "\t" + l.getSubject() + "\t" + l.getTeacher()
                            + "\t" + l.getDuration() + "\t" + l.getRoomNumber() + "\t" + detail);
                    writer.newLine();
                }
            }
            try (BufferedWriter writer = Files.newBufferedWriter(MATERIALS)) {
                for (Lesson lesson : service.allLessons()) {
                    for (LessonMaterial material : lesson.getMaterials()) {
                        writer.write(lesson.getId() + "\t" + material.getTitle() + "\t" + material.getContent());
                        writer.newLine();
                    }
                }
            }
            try (BufferedWriter writer = Files.newBufferedWriter(ENROLLMENTS)) {
                for (java.util.Map.Entry<Integer, java.util.Set<Integer>> e : service.enrollments().entrySet())
                    for (int lessonId : e.getValue()) {
                        writer.write(e.getKey() + "\t" + lessonId);
                        writer.newLine();
                    }
            }
            try (BufferedWriter writer = Files.newBufferedWriter(REQUESTS)) {
                for (LessonRequest r : service.requests()) {
                    writer.write(r.getId() + "\t" + r.getStudentId() + "\t" + r.getLessonId());
                    writer.newLine();
                }
            }
            try (BufferedWriter writer = Files.newBufferedWriter(GRADES)) {
                for (java.util.Map.Entry<Integer, java.util.Map<Integer, String>> e : service.grades().entrySet())
                    for (java.util.Map.Entry<Integer, String> g : e.getValue().entrySet()) {
                        writer.write(e.getKey() + "\t" + g.getKey() + "\t" + g.getValue());
                        writer.newLine();
                    }
            }
            try (BufferedWriter writer = Files.newBufferedWriter(PROGRAMS)) {
                for (CourseProgram p : service.programs()) {
                    StringBuilder ids = new StringBuilder();
                    for (Lesson l : p.getLessons()) {
                        if (ids.length() > 0) ids.append(",");
                        ids.append(l.getId());
                    }
                    writer.write(p.getProgramName() + "\t" + ids);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Could not save user files: " + e.getMessage());
        }
    }
}
