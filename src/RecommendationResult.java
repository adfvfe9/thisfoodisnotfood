import java.time.*;
import java.time.temporal.ChronoUnit;

public class RecommendationResult {
    private String date;      // 날짜
    private String menuName;  // 메뉴 이름
    private String reason;    // 추천 이유

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public RecommendationResult(String date, String menuName, String reason) {
        this.date = date;
        this.menuName = menuName;
        this.reason = reason;
    }

    @Override
    public String toString() {
        String dDayStr;

        try {
            LocalDate targetDate = LocalDate.parse(this.date);
            LocalDate today = LocalDate.now();

            long daysBetween = ChronoUnit.DAYS.between(today, targetDate);

            if (daysBetween > 0) {
                dDayStr = "(D-" + daysBetween + ")"; // 미래 (남은 기간)
            } else {
                dDayStr = "(D-Day)"; // 당일
            }

        } catch (Exception e) {
            dDayStr = "(" + this.date + ")";
            e.printStackTrace();
        }

        return CurrentUser.getInstance().getName() + "님을 위한 추천 메뉴 : " + menuName + " " + dDayStr + " / " + reason;
    }
}