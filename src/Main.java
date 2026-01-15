import java.sql.*;

public class Main {
    public static void main(String[] args) {
        Connection conn = Connector.getConnection();
        System.out.println(new NeisAPI().getMenu());
    }
}
