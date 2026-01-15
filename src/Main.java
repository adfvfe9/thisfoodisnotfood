import java.sql.*;
import java.util.*;

public class Main {
    static Scanner s = new Scanner(System.in);
    public static void main(String[] args) {
        Connection conn = Connector.getConnection();    // 앞으로 db 연결하려고 커넥션 객체만들면 이거
        NeisAPI napi = new NeisAPI();
        GeminiAPI gapi = new GeminiAPI();
        DBLoader dbl = new DBLoader();
        LoginService lg = new LoginService();
        RegisterService rs = new RegisterService();
        napi.updateDB();

        /**
         * 1. 실행 시 회원가입 / 로그인 선택 창 띄우기
         * 2. 메뉴 선택기 창 띄우기
         * 3. 종료
         *
         * 대충 이순서로 갈것같음 ㅇㅇ
        */

        // 아래는 테스트용

        do {
            System.out.print("1. 회원가입 / 2. 로그인 : ");
            int n = Integer.parseInt(s.nextLine());
            if (n==1) rs.SignUp();
            else if (n==2) {
                lg.Login();
                break;
            }
        } while (true);

        while (true) {
            RecommendationResult rr = gapi.getRecommendation(CurrentUser.getInstance().getPrefer(), CurrentUser.getInstance().getBulho(), dbl.getMenuListFromDB());
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + rr);
            break;

        }
    }
}
