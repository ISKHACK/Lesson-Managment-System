import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner input = new Scanner(System.in);
    private static LessonService service = new LessonService();

    private static String text(String message) {
        while (true) {
            System.out.print(message);
            String value = input.nextLine().trim();
            if (!value.isEmpty() && !value.contains("\t")) return value;
            System.out.println("This field is required (no tab characters).");
        }
    }
    private static int number(String message) {
        while (true) {
            try { return Integer.parseInt(text(message)); }
            catch (NumberFormatException e) { System.out.println("Enter a whole number."); }
        }
    }
    private static int positive(String message) {
        while (true) {
            int n = number(message);
            if (n > 0) return n;
            System.out.println("Enter a positive number.");
        }
    }
    private static void printLessons(List<Lesson> lessons) {
        if (lessons.isEmpty()) System.out.println("No lessons found.");
        for (Lesson l : lessons) {
            System.out.println("#" + l.getId() + " | " + l.getSubject() + " | " + l.getTeacher()
                    + " | " + l.getDuration() + " min | Room " + l.getRoomNumber());
        }
    }
    private static void printMaterials(Lesson lesson) {
        if (lesson.getMaterials().isEmpty()) {
            System.out.println("No materials yet."); return;
        }
        for (int i = 0; i < lesson.getMaterials().size(); i++)
            System.out.println((i + 1) + ". " + lesson.getMaterials().get(i));
    }
    private static String materialText(String message) {
        while (true) {
            String value = text(message);
            if (!value.contains("\n") && !value.contains("\r")) return value;
        }
    }
    private static String subject() {
        while (true) {
            System.out.println("1. Math   2. English   3. Programming");
            int choice = number("Subject: ");
            if (choice == 1) return "Math";
            if (choice == 2) return "English";
            if (choice == 3) return "Programming";
            System.out.println("Choose 1, 2, or 3.");
        }
    }
    private static void studentAccess() {
        System.out.println("\n1. Login   2. Register   0. Back");
        int choice = number("Choose: ");
        if (choice == 0) return;
        Student s;
        if (choice == 2) {
            String nickname = text("Nickname: ");
            String password = text("Password: ");
            int grade;
            do { grade = number("School grade (1-12): "); }
            while (grade < 1 || grade > 12);
            s = service.registerStudent(nickname, password, grade, subject());
            if (s == null) { System.out.println("Nickname already exists."); return; }
            TextFiles.save(service);
            System.out.println("Student registered! ID: " + s.getId());
        } else if (choice == 1) {
            s = service.studentLogin(text("Nickname: "), text("Password: "));
            if (s == null) { System.out.println("Wrong nickname or password."); return; }
        } else return;
        studentMenu(s);
    }
    private static void studentMenu(Student student) {
        while (true) {
            System.out.println("\nSTUDENT: " + student.getNickname());
            System.out.println("1. View lessons\n2. Search by subject\n3. Request enrollment\n4. My lessons and grades\n5. My profile\n6. View my lesson materials\n0. Logout");
            int c = number("Choose: ");
            if (c == 0) return;
            if (c == 1) printLessons(service.allLessons());
            if (c == 2) printLessons(service.findSubject(subject()));
            if (c == 3) {
                printLessons(service.allLessons());
                int id = positive("Lesson ID: ");
                System.out.println(service.request(student, id) ? "Request sent." : "Invalid lesson or already requested/enrolled.");
                TextFiles.save(service);
                }
            if (c == 4) {
                List<Lesson> list = service.studentLessons(student);
                printLessons(list);
                for (Lesson l : list) System.out.println("Lesson #" + l.getId() + " | Grade: " + service.grade(student.getId(), l.getId()));
            }
            if (c == 5) System.out.println(student);
            if (c == 6) {
                List<Lesson> mine = service.studentLessons(student);
                printLessons(mine);
                if (mine.isEmpty()) continue;
                int id = positive("Enrolled lesson ID: ");
                Lesson selected = null;
                for (Lesson l : mine) if (l.getId() == id) selected = l;
                if (selected == null) System.out.println("You are not enrolled in this lesson.");
                else printMaterials(selected);
            }
        }
    }
    private static void teacherAccess() {
        System.out.println("\n1. Login   2. Register   0. Back");
        int choice = number("Choose: ");
        if (choice == 0) return;
        Teacher t;
        if (choice == 2) {
            t = service.registerTeacher(text("Nickname: "), text("Password: "), subject());
            if (t == null) { System.out.println("Nickname already exists."); return; }
            TextFiles.save(service);
            System.out.println("Teacher registered! ID: " + t.getId());
        } else if (choice == 1) {
            t = service.teacherLogin(text("Nickname: "), text("Password: "));
            if (t == null) { System.out.println("Wrong nickname or password."); return; }
        } else return;
        teacherMenu(t);
    }
    private static void teacherMenu(Teacher teacher) {
        while (true) {
            System.out.println("\nTEACHER: " + teacher.getNickname() + " (" + teacher.getSubject() + ")");
            System.out.println("1. My lessons\n2. Create lesson\n3. Edit lesson\n4. Enrollment requests\n5. Enrolled students\n6. Give or change grade\n7. Lesson activities\n8. Add lesson material\n9. View or remove material\n0. Logout");
            int c = number("Choose: ");
            if (c == 0) return;
            if (c == 1) printLessons(service.teacherLessons(teacher));
            if (c == 2) {
                String detail = text("Topic / level / programming language: ");
                int minutes = positive("Duration (minutes): ");
                int room = positive("Room number: ");
                Lesson l = service.createLesson(teacher, detail, minutes, room);
                System.out.println("Created lesson #" + l.getId());
                TextFiles.save(service);
            }
            if (c == 3) {
                printLessons(service.teacherLessons(teacher));
                int id = positive("Lesson ID: ");
                Lesson l = service.getLesson(id);
                if (l == null || !l.getTeacher().equalsIgnoreCase(teacher.getNickname())) {
                    System.out.println("That is not your lesson."); continue;
                }
                boolean ok = service.updateLesson(id, positive("New duration: "), positive("New room: "));
                System.out.println(ok ? "Updated." : "Update failed.");
                if (ok) TextFiles.save(service);
            }
            if (c == 4) {
                List<LessonRequest> requests = service.pending(teacher);
                if (requests.isEmpty()) { System.out.println("No pending requests."); continue; }
                for (int i = 0; i < requests.size(); i++) {
                    LessonRequest r = requests.get(i);
                    System.out.println((i + 1) + ". " + service.studentName(r.getStudentId())
                            + " -> Lesson #" + r.getLessonId());
                }
                int selected = number("Request number (0 to cancel): ");
                if (selected < 1 || selected > requests.size()) continue;
                int action = number("1. Approve  2. Reject  0. Cancel: ");
                if (action == 1 || action == 2) {
                    service.decide(teacher, requests.get(selected - 1).getId(), action == 1);
                    TextFiles.save(service);
                    System.out.println(action == 1 ? "Approved." : "Rejected.");
                }
            }
            if (c == 5 || c == 6) {
                printLessons(service.teacherLessons(teacher));
                int id = positive("Your lesson ID: ");
                Lesson l = service.getLesson(id);
                if (l == null || !l.getTeacher().equalsIgnoreCase(teacher.getNickname())) {
                    System.out.println("That is not your lesson."); continue;
                }
                List<Student> students = service.enrolledStudents(id);
                if (students.isEmpty()) { System.out.println("No enrolled students."); continue; }
                for (Student s : students) System.out.println(s.getId() + " | " + s.getNickname()
                        + " | Grade: " + service.grade(s.getId(), id));
                if (c == 6) {
                    int studentId = positive("Student ID: ");
                    String grade = text("Grade (A+, A, B+, B, C+, C, D, F): ").toUpperCase();
                    boolean ok = service.setGrade(teacher, id, studentId, grade);
                    System.out.println(ok ? "Grade saved." : "Invalid student or grade.");
                    if (ok) TextFiles.save(service);
                }
            }
            if (c == 8 || c == 9) {
                printLessons(service.teacherLessons(teacher));
                int id = positive("Your lesson ID: ");
                Lesson l = service.getLesson(id);
                if (l == null || !l.getTeacher().equalsIgnoreCase(teacher.getNickname())) {
                    System.out.println("That is not your lesson."); continue;
                }
                if (c == 8) {
                    String title = materialText("Material title: ");
                    String content = materialText("Material content (one line): ");
                    l.addMaterial(title, content);
                    TextFiles.save(service);
                    System.out.println("Material added and saved.");
                } else {
                    printMaterials(l);
                    int n = number("Material number to remove (0 to keep): ");
                    if (n > 0) {
                        if (l.removeMaterial(n - 1)) {
                            TextFiles.save(service);
                            System.out.println("Material removed.");
                        } else System.out.println("Invalid material number.");
                    }
                }
            }
            if (c == 7) {
                printLessons(service.teacherLessons(teacher));
                int id = positive("Lesson ID: ");
                Lesson l = service.getLesson(id);
                if (l == null || !l.getTeacher().equalsIgnoreCase(teacher.getNickname())) continue;
                l.prepareLesson();
                l.conductLesson();
                if (l instanceof Practiceable) ((Practiceable) l).performPractice();
                if (l instanceof HomeworkAssignable) ((HomeworkAssignable) l).assignHomework();
            }
        }
    }
    private static void adminMenu() {
        if (!text("Admin password: ").equals("admin123")) { System.out.println("Wrong password."); return; }
        while (true) {
            System.out.println("\nADMIN");
            System.out.println("1. All lessons\n2. All students\n3. All teachers\n4. Delete lesson\n5. Delete student\n6. Delete teacher\n7. Sort lessons\n8. Statistics\n9. Create course program\n0. Logout");
            int c = number("Choose: ");
            if (c == 0) return;
            if (c == 1) printLessons(service.allLessons());
            if (c == 2) {
                if (service.students().isEmpty()) System.out.println("No students.");
                for (Student s : service.students()) System.out.println(s);
            }
            if (c == 3) {
                if (service.teachers().isEmpty()) System.out.println("No teachers.");
                for (Teacher t : service.teachers()) System.out.println(t);
            }
            if (c == 4) {
                printLessons(service.allLessons());
                boolean ok = service.deleteLesson(positive("Lesson ID to delete: "));
                System.out.println(ok ? "Deleted." : "Not found.");
                if (ok) TextFiles.save(service);
            }
            if (c == 5) {
                boolean ok = service.deleteStudent(positive("Student ID to delete: "));
                if (ok) TextFiles.save(service);
                System.out.println(ok ? "Deleted." : "Not found.");
            }
            if (c == 6) {
                boolean ok = service.deleteTeacher(positive("Teacher ID to delete: "));
                if (ok) TextFiles.save(service);
                System.out.println(ok ? "Deleted with their lessons." : "Not found.");
            }
            if (c == 7) {
                System.out.println("1. Subject  2. Duration  3. Teacher");
                int type = number("Sort by: ");
                if (type >= 1 && type <= 3) printLessons(service.sorted(type));
            }
            if (c == 8) service.statistics();
            if (c == 9) {
                CourseProgram program = service.createProgram(text("Program name: "));
                program.displayProgramInfo();
                TextFiles.save(service);
            }
        }
    }
    public static void main(String[] args) {
        TextFiles.showDataFolder();
        TextFiles.load(service);
        while (true) {
            System.out.println("\n=== LESSON MANAGEMENT SYSTEM ===");
            System.out.println("1. Student  2. Teacher  3. Admin  0. Exit");
            int choice = number("Choose: ");
            if (choice == 0) { System.out.println("Goodbye!"); return; }
            if (choice == 1) studentAccess();
            if (choice == 2) teacherAccess();
            if (choice == 3) adminMenu();
        }
    }
}
