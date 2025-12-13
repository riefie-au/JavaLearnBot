package lecture.javalearnbot.AiFeatures;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EvaluationRecord {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String question;
    private final String answer;
    private final List<String> rewrites;
    private final double score;
    private final String label;
    private final String notes;

    public EvaluationRecord(String question, String answer,List<String> rewrites, double score, String label, String notes) {
        this.question = question;
        this.answer = answer;
        this.rewrites = rewrites;
        this.score = score;
        this.label = label;
        this.notes = notes;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getNotes() {
        return notes;
    }

    public String getLabel() {
        return label;
    }

    public double getScore() {
        return score;
    }

    public List<String> getRewrites() {
        return rewrites;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }
}
