package lecture.javalearnbot.RAG;

import dev.langchain4j.model.openai.OpenAiChatModel;
import lecture.javalearnbot.RAG.RagHelperClasses.Hit;

import java.util.List;

public class AnswerGenerator {

    private final OpenAiChatModel chat;

    AnswerGenerator(OpenAiChatModel chat) {
        this.chat = chat;
    }

    public String GenerateAnswer (String question, List<Hit> hits, List<String> rewrites) {
        StringBuilder ctx = new StringBuilder();
        int i = 1;
        for (Hit hit : hits) {
            // Add metadata about where the chunk came from
            ctx.append("Index: = [")
                    .append(hit.getIndex())
                    .append("] Parent Document: =")
                    .append(hit.getHitSource())
                    .append(" | Path=")
                    .append(hit.getPath())
                    .append(" | Score=")
                    .append(hit.getScore())
                    .append("\n");
            // Add the actual text snippet
            ctx.append(hit.getSnippet())
                    .append("\n\n");
        }
        String prompt = """
                You are a helpful and accurate Java Learning Assistant for the application JavaLearnBot
                Using only information obtained from CONTEXT, answer the QUESTION
                If the answer is unsure or missing from the context, say you are unsure
                Cite context sources using bracket numbers from the index [1], [2], matching the context items, these bracket numbers
                should be included within the text from portions of text that you are deriving context from.
            
                CONTEXT:
                 %s
                 USER QUESTION:
                %s
                
                ALTERNATIVE REWRITES (for deeper understanding):
                %s
                """.formatted(ctx, question, String.join("\n", rewrites)) ;//string.join joins a list of strings into 1 string, putting a newline between each item

        String answer = chat.generate(prompt);
        return answer;
    }
}
