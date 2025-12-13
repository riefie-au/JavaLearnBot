package lecture.javalearnbot.AiFeatures;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;

public class EvaluationStore {
    private final List<EvaluationRecord> records = new ArrayList<>();
    private final Path evaluationDirectory = Path.of(".evaluations"); //this is a path object pointing to evaluations

    public EvaluationStore() {
        try {
            Files.createDirectories(evaluationDirectory); //creating a directory inside evalatuions
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void add (EvaluationRecord r) {
        records.add(r); //adding an evaluationRecord object into the list of records
    }

    public void exportAsCSV() {
        File file = evaluationDirectory.resolve("evaluations.csv").toFile(); //makes the file object point to evaluations CSV with was created from the constructor
        boolean isNewFile = !file.exists();

        try (CSVWriter writer = new CSVWriter(new FileWriter(file,true))) { //create a csvWriter object that wraps a filewriter
            if (isNewFile) {
                String[] header = {"timestamp", "question", "answer", "rewrites", "score", "label", "notes"};
                writer.writeNext(header);
            }

                for (EvaluationRecord r : records) {
                    String[] row = {
                            r.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            r.getQuestion(),
                            r.getAnswer(),
                            r.getRewrites().toString(),
                            String.valueOf(r.getScore()),
                            r.getLabel(),
                            r.getNotes()
                    };
                    writer.writeNext(row);
                }
                records.clear();
            }
        catch (IOException e) {throw new RuntimeException(e);
        }





    }

}
