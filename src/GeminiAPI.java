import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GeminiAPI {
    private static final String API_KEY = "AIzaSyBni0yHnquigbQTxa8DoDyWWAlQtCFa4_Q";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + API_KEY;

    public RecommendationResult getRecommendation(String userPreference, String userBulho, List<String> lunchList) {

        StringBuilder menuListString = new StringBuilder();
        for (String menu : lunchList) {
            menuListString.append(menu).append(", ");
        }

        String prompt = "너는 급식 추천 AI야.\n" +
            "사용자의 선호: " + userPreference + "\n" +
            "사용자의 불호: " + userBulho + "\n" +
            "이번 달 급식 메뉴 목록: [" + menuListString.toString() + "]\n\n" +
            "위 급식 메뉴 목록 중에서 사용자의 취향에 가장 잘 맞는 메뉴를 딱 하나만 골라줘.\n" +
            "답변은 반드시 아래 형식으로 딱 한 줄만 작성해. 부가 설명 하지 마.\n" +
            "형식: 날짜|||메뉴이름|||추천이유\n" +
            "예시: 2026-01-15|||돈까스|||바삭한 튀김을 좋아해서 추천함";


        try {
            String jsonBody = "{\n" +
                "  \"contents\": [{\n" +
                "    \"parts\": [{\n" +
                "      \"text\": \"" + escapeJson(prompt) + "\"\n" +
                "    }]\n" +
                "  }]\n" +
                "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String rawText = extractTextFromResponse(response.body());

                if (rawText.contains("|||")) {
                    String[] parts = rawText.split("\\|\\|\\|");
                    if (parts.length >= 3) {
                        String date = parts[0].trim();
                        String menu = parts[1].trim();
                        String reason = parts[2].trim();

                        return new RecommendationResult(date, menu, reason);
                    }
                }
                return new RecommendationResult("날짜없음", "분석 실패: " + rawText, "AI가 형식을 지키지 않았습니다.");

            } else {
                System.err.println("요청 실패: " + response.statusCode());
                return new RecommendationResult("에러", "통신 에러", response.body());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new RecommendationResult("에러", "예외 발생", e.getMessage());
        }
    }

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r");
    }

    private static String extractTextFromResponse(String jsonResponse) {
        try {
            String marker = "\"text\": \"";
            int startIndex = jsonResponse.indexOf(marker);

            if (startIndex == -1) return "응답 없음";

            startIndex += marker.length();
            int endIndex = startIndex;
            boolean isEscaped = false;

            for (int i = startIndex; i < jsonResponse.length(); i++) {
                char c = jsonResponse.charAt(i);
                if (c == '\\') {
                    isEscaped = !isEscaped;
                } else if (c == '"' && !isEscaped) {
                    endIndex = i;
                    break;
                } else {
                    isEscaped = false;
                }
            }
            String extracted = jsonResponse.substring(startIndex, endIndex);
            return extracted.replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
        } catch (Exception e) {
            return "파싱 오류";
        }
    }
}