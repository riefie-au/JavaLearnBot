package lecture.javalearnbot.RAG;

import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lecture.javalearnbot.RAG.RagHelperClasses.ChunkStorage;
import lecture.javalearnbot.RAG.RagHelperClasses.Hit;
import lecture.javalearnbot.RAG.RagHelperClasses.Result;

import java.util.List;

public class Pipeline {
    private final QueryRewriteService rewriter;
    private final Retriever retriever;
    private final AnswerGenerator answerGenerator;
    private int amountOfRewrites = 3;
    private int amountOfRetrievalChunks = 5;
    private final OpenAiEmbeddingModel embeddingModel;
    private final ChunkStorage chunkStore ;


    public Pipeline(){
        LLMProvider llm = new LLMProvider();
        this.chunkStore = new ChunkStorage();
        this.embeddingModel = llm.getEmbeddings();
        this.rewriter = new QueryRewriteService(llm.getChat());
        this.retriever = new Retriever(chunkStore, llm.getEmbeddings(), amountOfRetrievalChunks);
        this.answerGenerator = new AnswerGenerator(llm.getChat());
    }

    public Result run(String question){
        List<String> rewrites = rewriter.rewrite(question,amountOfRewrites);
        List<Hit> hits = retriever.retrieve(question);
        String answer = answerGenerator.GenerateAnswer(question, hits, rewrites);
        return new Result(hits,answer,rewrites);
    }


    public int getNumberOfRewrites() {
        return amountOfRewrites;
    }

    public void setNumberOfRewrites(int numberOfRewrites) {
        this.amountOfRewrites = numberOfRewrites;
    }

    public QueryRewriteService getRewriter() {
        return rewriter;
    }

    public Retriever getRetriever() {
        return retriever;
    }

    public AnswerGenerator getAnswerGenerator() {
        return answerGenerator;
    }

    public int getAmountOfRewrites() {
        return amountOfRewrites;
    }

    public ChunkStorage getChunkStore() {
        return chunkStore;
    }

    public OpenAiEmbeddingModel getEmbeddingModel() {
        return embeddingModel;
    }
}
