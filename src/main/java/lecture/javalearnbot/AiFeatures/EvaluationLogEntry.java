package lecture.javalearnbot.AiFeatures;

import java.time.LocalDateTime;
import java.util.List;

public class EvaluationLogEntry extends LogEntry {

    private String label;
    private int score;

    public EvaluationLogEntry(String question, String answer) {
        super(question, answer);
    }

    public void evaluate(String label, int score) {
        this.label = label;
        this.score = score;
    }

    public String getLabel() { return label; }
    public int getScore() { return score; }

    @Override
    public String getType() {
        return "EVALUATION";
    }
}