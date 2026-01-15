import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DBLoader {

    public List<String> getMenuListFromDB() {
        List<String> menuList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Connector.getConnection();

            String sql = "SELECT date, meal_type, menu FROM food ORDER BY date ASC LIMIT 30";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String date = rs.getString("date");      // 2026-01-15
                String type = rs.getString("meal_type"); // B, L, D
                String menu = rs.getString("menu");

                String typeKo = "점심";
                if ("B".equals(type)) typeKo = "아침";
                else if ("D".equals(type)) typeKo = "저녁";

                String line = date + "(" + typeKo + "): " + menu;

                menuList.add(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {}
        }

        return menuList;
    }
}