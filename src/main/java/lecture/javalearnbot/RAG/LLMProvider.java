package lecture.javalearnbot.RAG;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;

import java.util.Properties;

public class LLMProvider {
    private final OpenAiChatModel chat;
    private final OpenAiEmbeddingModel embeddings;
    private final Properties cfg = new Properties();

    public LLMProvider() {
        String OPENAI_API_KEY = System.getenv("MY_API_KEY");

        chat = OpenAiChatModel.builder()
                .apiKey(OPENAI_API_KEY) // .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(cfg.getProperty("llm.model","gpt-4o-mini"))
                .temperature(0.2)
                .build();

        embeddings = OpenAiEmbeddingModel.builder()
                .apiKey(OPENAI_API_KEY) // .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(cfg.getProperty("embedding.model","text-embedding-3-small"))
                .build();
    }

    public OpenAiChatModel getChat() {
        return chat;
    }

    public OpenAiEmbeddingModel getEmbeddings() {
        return embeddings;
    }
}
