import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NeisAPI {
    private final String apiKey = "c75e35beb2744bd9a1b4971710b49cb4";
    String sidocode = "B10";
    String defaultcode = "7011569";
    String date = "20260115";

    public NeisAPI() {}
    public NeisAPI(String sidocode, String defaultcode, String date) {
        this.sidocode = sidocode;
        this.defaultcode = defaultcode;
        this.date = date;
    }

    String getMenu() {
        try {
            URL u = new URL("https://open.neis.go.kr/hub/mealServiceDietInfo" +
                "?KEY=" + apiKey +
                "&Type=json" +
                "&ATPT_OFCDC_SC_CODE=" + sidocode +
                "&SD_SCHUL_CODE=" + defaultcode +
                "&MLSV_YMD=" + date);

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
}
