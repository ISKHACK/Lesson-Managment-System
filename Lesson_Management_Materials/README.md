# Lesson Management System — corrected TXT saving

This is the SAME university Java project and menus. The only functional changes are in `TextFiles.java` and the startup line in `Main.java`.

## How to run
Open `src/Main.java` in IntelliJ IDEA and run Main. The first line prints the exact **TXT data folder** used by the program. You can also run from terminal:

```
javac -d build src/*.java
java -cp build Main
```

## Data files store CURRENT RECORDS, not history
- `students.txt`: `1 Iskhak Math 12 1234` => ID, nickname, favorite subject, school grade, password
- `teachers.txt`: `2 Isko Programming 1234` => ID, nickname, teaching subject, password
- `lessons.txt`: `1<TAB>Programming<TAB>Isko<TAB>60<TAB>201<TAB>Java` => lesson ID, subject, teacher, minutes, room, detail
- `enrollments.txt`: `1<TAB>2` => student ID, lesson ID
- `grades.txt`: `2<TAB>1<TAB>A+` => lesson ID, student ID, letter grade
- `requests.txt`: `1<TAB>3<TAB>1` => request ID, student ID, lesson ID (pending only)
- `programs.txt`: course program name then tab and comma-separated lesson IDs

Students and teachers use simple spaces, as requested; the remaining files use tabs, so lesson topics and program names can include spaces.

`BufferedReader` reads the records when Main starts. The data is loaded into the existing ArrayList, HashMap, HashSet, and Queue collections. After registration, creating or updating lessons, requesting or approving enrollment, grading, or deleting records, `BufferedWriter` rewrites the TXT files with the **current data**. It does not append an operation log. New IDs resume from the highest loaded IDs.

## Test existing saved accounts
- Student: Iskhak / 1234 (Math, grade 12)
- Teacher: Isko / 1234 (Programming)
- Student: Sara / demo123
- Teacher: Aida / demo123
- Admin password: admin123

The sample grade `A+` belongs to student Iskhak in lesson 2. Edit TXT files **only when the app is closed**. Never use real passwords in this simple university demo: passwords are plain text.

## Troubleshooting
If you don't see a new record, check the `TXT data folder:` printed at startup. The app now finds the project root based on `src/Main.java` and is not sensitive to IntelliJ starting from `src/` or `build/`.

`src/` Java code; `data/` current records; `docs/` UML and report; `evidence/` earlier workflow examples.


## Teacher-uploaded lesson materials (TXT)
Teachers log in, select **8. Add lesson material**, choose one of their lessons,
then enter a material **title** and **content** (plain text on one line).
Select **9. View or remove material** to review or remove existing entries.
Enrolled students choose **6. View my lesson materials** to see the notes.
The new `data/materials.txt` stores **lesson ID, material title, content** separated
by tabs. `BufferedReader` loads these records when the application starts;
`BufferedWriter` saves the current materials together with the other records.
No external libraries or additional service classes were introduced.
This supports plain-text teaching notes, links, exercises and references, not
file uploads of PDFs, images or video. Avoid tabs inside content.

The UML diagram in docs/ shows classes, methods, attributes, inheritance,
interfaces, composition, aggregation and dependencies in a textbook UML layout.
