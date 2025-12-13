//package lecture.javalearnbot.AiFeatures;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.io.*;
//import java.lang.reflect.Type;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//public class LogManager {
//    private final List<LogEntry> logs = new ArrayList<>();
//    private final File logFile = new File("logs.json");
//    private final Gson gson = new Gson();
//
//    public void addLog(String question, String answer) {
//        LogEntry entry = new LogEntry(question, answer);
//        logs.add(entry);
//        saveLogs(); // persist every time
//    }
//
//    //save logs to json
//    private void saveLogs() {
//        try (Writer writer = new FileWriter(logFile)) {
//            gson.toJson(logs, writer);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void loadLogs() {
//        if (!logFile.exists()) {
//            return;
//        }
//        try (Reader reader = new FileReader(logFile)) {
//            Type listType = new TypeToken<List<LogEntry>>(){}.getType();
//            List<LogEntry> savedLogs = gson.fromJson(reader, listType);
//            if (savedLogs != null) logs.addAll(savedLogs);
//
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    public List<LogEntry> getLogs() {
//        return logs;
//    }
//    public void clearLogs() {
//        logs.clear();
//    }
//}
