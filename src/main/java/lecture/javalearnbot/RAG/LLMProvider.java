package lecture.javalearnbot.RAG;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LLMProvider {
    private final OpenAiChatModel chat;
    private final OpenAiEmbeddingModel embeddings;
    private final Properties cfg = new Properties();


    public LLMProvider() {
        try(InputStream input = LLMProvider.class.getResourceAsStream("/config.properties")) {
            if(input == null) {
               throw new RuntimeException();
            }
            cfg.load(input);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String OPENAI_API_KEY = cfg.getProperty("MY_API_KEY");
        String chatModel = cfg.getProperty("llm.model");
        String embeddingModel = cfg.getProperty("embedding.model");

        //String OPENAI_API_KEY = System.getenv("MY_API_KEY");

        chat = OpenAiChatModel.builder()
                .apiKey(OPENAI_API_KEY) // .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(chatModel)
                .temperature(0.2)
                .build();

        embeddings = OpenAiEmbeddingModel.builder()
                .apiKey(OPENAI_API_KEY) // .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(embeddingModel)
                .build();
    }

    public OpenAiChatModel getChat() {
        return chat;
    }

    public OpenAiEmbeddingModel getEmbeddings() {
        return embeddings;
    }
}
