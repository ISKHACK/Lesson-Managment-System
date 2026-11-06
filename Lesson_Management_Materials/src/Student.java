
public class Student {
    private final int id;
    private final String nickname;
    private final String password;
    private final int schoolGrade;
    private final String favoriteSubject;

    public Student(int id, String nickname, String password, int schoolGrade, String favoriteSubject) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.schoolGrade = schoolGrade;
        this.favoriteSubject = favoriteSubject;
    }
    public int getId() { return id; }
    public String getNickname() { return nickname; }
    String getPasswordForFile() { return password; }
    public boolean checkPassword(String p) { return password.equals(p); }
    public int getSchoolGrade() { return schoolGrade; }
    public String getFavoriteSubject() { return favoriteSubject; }
    @Override public boolean equals(Object o) { return o instanceof Student && id == ((Student) o).id; }
    @Override public int hashCode() { return Integer.hashCode(id); }
    @Override public String toString() { return id + " | " + nickname + " | School grade: " + schoolGrade + " | " + favoriteSubject; }
}
