
public class Teacher {
    private final int id;
    private final String nickname;
    private final String password;
    private final String subject;

    public Teacher(int id, String nickname, String password, String subject) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.subject = subject;
    }
    public int getId() { return id; }
    public String getNickname() { return nickname; }
    public String getSubject() { return subject; }
    String getPasswordForFile() { return password; }
    public boolean checkPassword(String p) { return password.equals(p); }
    @Override public boolean equals(Object o) { return o instanceof Teacher && id == ((Teacher) o).id; }
    @Override public int hashCode() { return Integer.hashCode(id); }
    @Override public String toString() { return id + " | " + nickname + " | " + subject; }
}
