//package lecture.javalearnbot.AiFeatures;
//
//import io.github.cdimascio.dotenv.Dotenv;
//
//public class ApiKeyLoader {
//    public static String getApiKey() {
//        Dotenv dotenv = Dotenv.load(); // loads .env from project root
//        String key = dotenv.get("OPENAI_API_KEY");
//        if (key == null || key.isBlank()) {
//            throw new RuntimeException(".env OPENAI_API_KEY not found!");
//        }
//        return key;
//    }
//}