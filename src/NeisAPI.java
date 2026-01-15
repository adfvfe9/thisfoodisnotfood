import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NeisAPI {
    private final String apiKey = "c75e35beb2744bd9a1b4971710b49cb4";
    String sidocode = "B10";
    String defaultcode = "7011569"; // 미마고 기본값
    int year = 2026;

    public NeisAPI() {}
    public NeisAPI(String sidocode, String defaultcode, int year) {
        this.sidocode = sidocode;
        this.defaultcode = defaultcode;
        this.year = year;
    }

    String getMenu() {
        String fromDate = year + "0101"; // 20260101
        String toDate = year + "1231";   // 20261231

        try {
            URL u = new URL("https://open.neis.go.kr/hub/mealServiceDietInfo" +
                "?KEY=" + apiKey +
                "&Type=json" +
                "&pIndex=1" +
                "&pSize=1000" +
                "&ATPT_OFCDC_SC_CODE=" + sidocode +
                "&SD_SCHUL_CODE=" + defaultcode +
                "&MLSV_FROM_YMD=" + fromDate +
                "&MLSV_TO_YMD=" + toDate);

            HttpsURLConnection hcon = (HttpsURLConnection) u.openConnection();
            hcon.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(hcon.getInputStream(), StandardCharsets.UTF_8));

            String line;
            StringBuilder json = new StringBuilder();
            while ((line = br.readLine()) != null) {
                json.append(line);
            }

            return json.toString();
        } catch (MalformedURLException e) {
            System.out.println("Mal 에러남");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("io 에러남");
            e.printStackTrace();
        }
        return "급식을 불러올 수 없습니다.";
    }

    public void updateDB() {
        String jsonResult = this.getMenu();

        if (jsonResult.contains("INFO-200") || jsonResult.startsWith("급식을")) {
            System.out.println("데이터가 없거나 통신 에러로 저장을 건너뜁니다.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;


        try {
            Pattern datePattern = Pattern.compile("\"MLSV_YMD\"\\s*:\\s*\"([0-9]{8})\"");
            Pattern typePattern = Pattern.compile("\"MMEAL_SC_CODE\"\\s*:\\s*\"([0-9])\"");
            Pattern menuPattern = Pattern.compile("\"DDISH_NM\"\\s*:\\s*\"(.*?)\"");

            Matcher dateMatcher = datePattern.matcher(jsonResult);
            Matcher typeMatcher = typePattern.matcher(jsonResult);
            Matcher menuMatcher = menuPattern.matcher(jsonResult);

            conn = Connector.getConnection();
            String sql = "INSERT IGNORE INTO food (date, meal_type, menu, school_code) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            int count = 0;
            while (dateMatcher.find() && typeMatcher.find() && menuMatcher.find()) {
                String rawDate = dateMatcher.group(1);  // 20260115 (JSON에서 추출한 실제 급식 날짜)
                String mealCode = typeMatcher.group(1); // 1, 2, 3
                String rawMenu = menuMatcher.group(1);  // 메뉴 텍스트

                String dbDate = rawDate.substring(0, 4) + "-" + rawDate.substring(4, 6) + "-" + rawDate.substring(6, 8);

                String dbMealType = "L";
                if ("1".equals(mealCode)) dbMealType = "B";
                else if ("3".equals(mealCode)) dbMealType = "D";

                String cleanMenu = rawMenu.replace("<br/>", ", ").replaceAll("\\([0-9\\.]+\\)", "");

                pstmt.setString(1, dbDate);
                pstmt.setString(2, dbMealType);
                pstmt.setString(3, cleanMenu);
                pstmt.setString(4, this.defaultcode);

                pstmt.executeUpdate();
                count++;
            }

            pstmt = conn.prepareStatement("DELETE FROM food WHERE date < CURDATE()");
            int rows = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}