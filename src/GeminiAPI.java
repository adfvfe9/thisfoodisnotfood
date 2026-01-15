import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GeminiAPI {
    private static final String API_KEY = "AIzaSyDz8dJdOn68gF3gl1xvZz1QejXe4LsRL9M";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + API_KEY;

    String getMenu(String menus) {
        try {
            String jsonBody = "{\n" +
                "  \"contents\": [{\n" +
                "    \"parts\": [{\n" +
                "      \"text\": \"" + escapeJson(question) + "\"\n" +
                "    }]\n" +
                "  }]\n" +
                "}";

            // 3. HTTP 요청 생성
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

            // 4. 요청 전송 및 응답 수신
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();

                // 5. 응답 파싱 (JSON 라이브러리 없이 텍스트 추출)
                String answer = extractTextFromResponse(responseBody);

                System.out.println("--- 답변 ---");
                System.out.println(answer);
                System.out.println("------------");

            } else {
                System.err.println("요청 실패! 상태 코드: " + response.statusCode());
                System.err.println("에러 메시지: " + response.body());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // JSON 문자열 깨짐 방지를 위한 간단한 이스케이프 처리 함수
    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r");
    }

    // 정규식(Regex)을 사용하여 JSON 응답에서 "text" 부분만 추출하는 함수
    // 라이브러리 없이 값을 꺼내기 위한 "꼼수"입니다.
    private static String extractTextFromResponse(String jsonResponse) {
        // Gemini 응답 구조: "text": "실제 답변 내용"
        // 단순 파싱이므로 답변 내에 복잡한 패턴이 있으면 완벽하지 않을 수 있습니다.
        try {
            String marker = "\"text\": \"";
            int startIndex = jsonResponse.indexOf(marker);

            if (startIndex == -1) return "답변을 찾을 수 없습니다 (Raw Response 확인 필요).";

            startIndex += marker.length();

            // 답변이 끝나는 지점 찾기 (이스케이프된 따옴표 제외하고 진짜 닫는 따옴표 찾기)
            // 간단한 구현을 위해 다음 따옴표를 찾습니다.
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

            // JSON 이스케이프 문자(\n, \", 등)를 다시 원래대로 복구
            return extracted.replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");

        } catch (Exception e) {
            return "파싱 오류: " + e.getMessage();
        }
    }
}