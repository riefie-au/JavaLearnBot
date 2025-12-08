package lecture.javalearnbot.AiFeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkStorage {

    //The purpose of this class is to store pieces of text alongside their embedding vectors

    public static class DocumentChunk {
        public final String chunkId; //unique identifier for each chunk segment
        public Document parent; //identifies chunks parent
        public String text;
        public float[] vector;

        public DocumentChunk(Document parent, String text, float[] vector, int index){
            this.chunkId = parent.getDocumentID() + index; //uses parentID and an index to create a unique value "intro_to_oop_1733680200333_chunk_0"
            this.parent = parent;
            this.text = text;
            this.vector = vector;
        }

        public String getChunkId() {return chunkId;}
        public Document getParent() {return parent;}
        public void setParent(Document parent) {this.parent = parent;}
        public String getText() {return text;}
        public void setText(String text) {this.text = text;}
        public float[] getVector() {return vector;}
        public void setVector(float[] vector) {this.vector = vector;}
    }

    private final List<DocumentChunk> documentChunks = new ArrayList<DocumentChunk>(); //holds every chunk processed in the system so far


    public List<ScoredChunk> findRelevantChunks (float[] questionVector, int topK) {
        List<ScoredChunk> scoredList = new ArrayList<>();

        for (DocumentChunk documentChunk : documentChunks) {
            double score = cosine(questionVector, documentChunk.getVector());
            scoredList.add(new ScoredChunk(documentChunk,score));
        }

        //sorts the list by highest score first
        scoredList.sort((a,b)-> Double.compare(b.score,a.score));

        List<ScoredChunk> topKChunks = new ArrayList<ScoredChunk>();
        for (int i = 0; i < topK; i++) {
            ScoredChunk sc = scoredList.get(i);
            topKChunks.add(sc); //returns the chunk value from the scored list
        }
        return topKChunks; //the list of topK most relevant ScoredChunks returned back to the user.
    }



    //calculates cosine similarity between two vectors
    private static double cosine(float[] a, float[] b) {
        double dot = 0, na = 0, nb = 0;
        int n = Math.min(a.length, b.length);
        for (int i = 0; i < n; i++) {
            dot += a[i] * b[i];     // dot product of a and b
            na += a[i] * a[i];      // magnitude squared of a
            nb += b[i] * b[i];      // magnitude squared of b
        }
        double denom = Math.sqrt(na) * Math.sqrt(nb); // product of magnitudes
        return denom == 0 ? 0 : dot / denom;           // cosine similarity formula
    }


    //helper class to keep data about chunks that have been scored
    private static class ScoredChunk {
        DocumentChunk chunk;
        double score;
        ScoredChunk(DocumentChunk chunk, double score) {
            this.chunk = chunk;
            this.score = score;
        }
    }

}
