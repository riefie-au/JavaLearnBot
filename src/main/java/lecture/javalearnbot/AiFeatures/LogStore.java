package lecture.javalearnbot.AiFeatures;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class LogStore {
    private final List<LogEntry> buffer = new ArrayList<>();
    private final Path logDir = Path.of(".logs"); //creates path objects that points to logs folder
    private final File csvFile = logDir.resolve("chat_log.csv").toFile(); //makes csvFile point to the directory of LogDir and chat_log.csv

    public LogStore() { //constructor that creates a  log directory
        try { Files.createDirectories(logDir); } //creates the folder logDir
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public void add(LogEntry entry) {
        buffer.add(entry);
    }

    public void saveToCSV(){
        boolean newFile = !csvFile.exists(); //create a boolean value to check if the file exists or not
        //so if the the csvFile doesnt exist its true, and if it does exists its false
        try(CSVWriter writer = new CSVWriter(new FileWriter(csvFile,true))){
            //csvFile is created here and placed in the .logs directory, its because of the line previously where the file object CSV file represents chat_log.csv in .logs even though it doesnt exist yet
            if(newFile){
                writer.writeNext(new String[]{"timestamp", "question","answer"});
            }
            for (LogEntry logEntry : buffer) {
                writer.writeNext(new String[] {
                        logEntry.getTimestamp().toString(),
                        logEntry.getQuestion(),
                        logEntry.getAnswer()
                });
            }
            buffer.clear();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<LogEntry> loadFromCSV() {
        List<LogEntry> list = new ArrayList<>();

        if (!csvFile.exists()) {
            return list; // nothing to load
        }

        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] next;
            boolean skipHeader = true;

            while ((next = reader.readNext()) != null) {

                if (skipHeader) {  // skip first row
                    skipHeader = false;
                    continue;
                }

                String timestamp = next[0];
                String question  = next[1];
                String answer    = next[2];

                list.add(new LogEntry(question, answer));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }



}
