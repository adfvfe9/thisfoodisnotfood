public class CurrentUser {
    private static CurrentUser c; // 유일한 인스턴스를 담을 변수

    private String id;
    private String pw;
    private String name;
    private String prefer;
    private String bulho;

    private CurrentUser() {}

    public static CurrentUser getInstance() {
        if (c == null) {
            c = new CurrentUser();
        }
        return c;
    }

    public static void setAll(String id, String pw, String name, String prefer, String bulho) {
        CurrentUser user = getInstance();

        user.id = id;
        user.pw = pw;
        user.name = name;
        user.prefer = prefer;
        user.bulho = bulho;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPw() { return pw; }
    public void setPw(String pw) { this.pw = pw; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrefer() { return prefer; }
    public void setPrefer(String prefer) { this.prefer = prefer; }

    public String getBulho() { return bulho; }
    public void setBulho(String bulho) { this.bulho = bulho; }
}