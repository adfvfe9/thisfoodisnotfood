import java.sql.*;
import java.util.*;

public class Main {
    static Scanner s = new Scanner(System.in);
    public static void main(String[] args) {
        Connection conn = Connector.getConnection();
        System.out.println(new NeisAPI().getMenu());
    }
}
