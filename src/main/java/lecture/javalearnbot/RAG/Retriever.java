package lecture.javalearnbot.RAG;


import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lecture.javalearnbot.RAG.RagHelperClasses.ChunkStorage;
import lecture.javalearnbot.RAG.RagHelperClasses.Hit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Retriever {
    private final ChunkStorage chunkStore;
    private final OpenAiEmbeddingModel embeddingModel;
    private final int topK;

    public Retriever(ChunkStorage chunkStore, OpenAiEmbeddingModel embeddingModel, int topK) {
        this.chunkStore = chunkStore;
        this.embeddingModel = embeddingModel;
        this.topK = topK;
    }

    public List<Hit> retrieve(String question){
        float[] questionVector = embeddingModel.embed(question).content().vector();
        List<Hit> hits = toHits(chunkStore.findRelevantChunks(questionVector,topK)); //finds the topK most relevant chunks and stores it an a list as Hit Objects
        //List<Hit> rerankedHits = rerankAndDiversify(hits);
        List<Hit> rerankedHits = rerank(hits);
        return rerankedHits;
    }

    private List<Hit> toHits(List<ChunkStorage.ScoredChunk> scoredChunks) {
        List<Hit> hits = new ArrayList<>();
        int index = 1;
        for (ChunkStorage.ScoredChunk scoredChunk : scoredChunks) {
            ChunkStorage.DocumentChunk chunk = scoredChunk.chunk;

            hits.add(new Hit(
                    index++,
                    chunk.getParent().getTitle(), //created the hit class and returns the list of hits
                    chunk.getParent().getPath(),
                    chunk.getText(),
                    scoredChunk.score
            ));
        }
        return hits;
    }

    public List<Hit> rerank (List<Hit> hits) {
        if (hits == null || hits.isEmpty()) return hits; //check if list is empty
        hits.sort(new Comparator<Hit>(){ //call sort method on hits, create new comparator class, compare method compares two hits as the same time
            @Override
            public int compare(Hit o1, Hit o2) {
                return Double.compare(o2.getScore(), o1.getScore());
            }
        });
        return hits;
    }

}
