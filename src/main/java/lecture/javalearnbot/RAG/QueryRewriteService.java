package lecture.javalearnbot.RAG;

import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.Arrays;
import java.util.List;

public class QueryRewriteService {

    private final OpenAiChatModel chat;

    public QueryRewriteService(OpenAiChatModel chat) {
        this.chat = chat;
    }

    public List<String> rewrite(String question, int numberOfRewrites) {
        String prompt = """
                Generate %d diverse, short alternative questions that would help retrieve relevant context for: "%s".
                Return only the questions. No numbering, no extra text.
                """.formatted(numberOfRewrites, question);

        // call LLM to generate rewrites and store it in raw.
        String raw = chat.generate(prompt);

        //split by newline, clean whitespace, remove blankls, return only required number
        return Arrays.stream(raw.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .limit(numberOfRewrites)
                .toList();
    }
}
