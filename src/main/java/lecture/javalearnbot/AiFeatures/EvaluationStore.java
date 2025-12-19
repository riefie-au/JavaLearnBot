package lecture.javalearnbot.AiFeatures;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
The Goal of this class is to store ev


 */




public class EvaluationStore {
    //Path to evaluation directory
    private static final Path evaluationDirectory = Path.of(".evaluations");
    //obtaining the evaluations .csv file and storing it in a bariable
    private static final File evaluationFile = evaluationDirectory.resolve("evaluations.csv").toFile();


    //creating the evaluation directory and if the evaluationFile doesnt exist create a new file
    static {
        try {
            Files.createDirectories(evaluationDirectory);
            if (!evaluationFile.exists()) {
                evaluationFile.createNewFile();
                // write the headers of the evaluatikon file into the the CSV
                try (CSVWriter writer = new CSVWriter(new FileWriter(evaluationFile))) {
                    writer.writeNext(new String[]{"timestamp","question","answer","score","label"});
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //Goal is to loadEvaluations is to return an evaluationLogEntry list in order to load it into the table in the Evaluations Page
    public static List<EvaluationLogEntry> loadEvaluations() {
        //the list that we want to return
        List<EvaluationLogEntry> list = new ArrayList<>();


        //try with resource so we dont have to close the reader
        try (CSVReader reader = new CSVReader(new FileReader(evaluationFile))) {
            String[] cell;
            boolean firstLine = true; //set first line as true so it doesnt read the first line
            while ((cell = reader.readNext()) != null) {
                if (firstLine)
                {
                    firstLine = false; //after reading first line, set the boolean to null
                    continue;
                }

                //store the values read from cells in the specific String
                String timestampStr = cell[0];
                String question = cell[1];
                String answer = cell[2];
                int score = Integer.parseInt(cell[3]);
                String label = cell[4];

                //use the info from the CSV to create the evaluationLogEntry object and use the evaluate
                //method to add the score and label
                EvaluationLogEntry eval = new EvaluationLogEntry(question, answer);
                eval.evaluate(label, score);

                //add it to the list
                list.add(eval);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return list; //return it to be loaded in the EvaluationController
    }

    public static void saveEvaluation(EvaluationLogEntry eval) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(evaluationFile, true))) {
            String[] row = new String[]{
                    eval.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    eval.getQuestion(),
                    eval.getAnswer(),
                    String.valueOf(eval.getScore()),
                    eval.getLabel()
            };
            writer.writeNext(row);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}