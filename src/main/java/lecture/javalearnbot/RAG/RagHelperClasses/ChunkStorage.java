package lecture.javalearnbot.RAG.RagHelperClasses;

import java.util.ArrayList;
import java.util.List;

public class ChunkStorage {

    // The purpose of this class is to store pieces of text alongside their embedding vectors

    public static class DocumentChunk {
        public final String chunkId; // unique identifier for each chunk segment
        public Document parent; // identifies chunks parent
        public String text;
        public float[] vector;

        public DocumentChunk(Document parent, String text, float[] vector, int index) {
            this.chunkId = parent.getDocumentID() + index; // uses parentID and an index to create a unique value "intro_to_oop_1733680200333_chunk_0"
            this.parent = parent;
            this.text = text;
            this.vector = vector;
        }

        public String getChunkId() { return chunkId; }
        public Document getParent() { return parent; }
        public void setParent(Document parent) { this.parent = parent; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public float[] getVector() { return vector; }
        public void setVector(float[] vector) { this.vector = vector; }
    }

    private final List<DocumentChunk> documentChunks = new ArrayList<DocumentChunk>(); // holds every chunk processed in the system so far

    public List<DocumentChunk> getDocumentChunks() {
        return documentChunks;
    }

    public List<ScoredChunk> findRelevantChunks(float[] questionVector, int topK) {
        List<ScoredChunk> scoredList = new ArrayList<>();

        for (DocumentChunk documentChunk : documentChunks) {
            double score = cosine(questionVector, documentChunk.getVector());
            scoredList.add(new ScoredChunk(documentChunk, score));
        }

        // sorts the list by highest score first
        scoredList.sort((a, b) -> Double.compare(b.score, a.score));

        List<ScoredChunk> topKChunks = new ArrayList<ScoredChunk>();
        for (int i = 0; i < Math.min(topK, scoredList.size()); i++) {
            ScoredChunk sc = scoredList.get(i);
            topKChunks.add(sc); // returns the chunk value from the scored list
        }
        return topKChunks; // the list of topK most relevant ScoredChunks returned back to the user.
    }

    // calculates cosine similarity between two vectors
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

    // helper class to keep data about chunks that have been scored
    public static class ScoredChunk {
        public DocumentChunk chunk;
        public double score;

        ScoredChunk(DocumentChunk chunk, double score) {
            this.chunk = chunk;
            this.score = score;
        }
    }

    public int getChunkCountForDocument(Document doc) {
        int count = 0;
        for (DocumentChunk c : documentChunks) {
            if (c.getParent().equals(doc)) {
                count++;
            }
        }
        return count;
    }

    public void add(DocumentChunk chunk) {
        documentChunks.add(chunk);
    }

    // ========== NEW DELETE METHOD ==========
    /**
     * Removes all chunks belonging to a specific document
     * @param documentID The unique ID of the document to remove
     * @return Number of chunks removed
     */
    public int removeDocument(String documentID) {
        if (documentID == null || documentID.isEmpty()) {
            return 0;
        }

        int initialSize = documentChunks.size();

        // Remove all chunks where the parent document has matching ID
        documentChunks.removeIf(chunk -> {
            Document parent = chunk.getParent();
            return parent != null && documentID.equals(parent.getDocumentID());
        });

        int removedCount = initialSize - documentChunks.size();

        if (removedCount > 0) {
            System.out.println("✓ Removed " + removedCount + " chunks for document ID: " + documentID);
        } else {
            System.out.println("⚠ No chunks found for document ID: " + documentID);
        }

        return removedCount;
    }

    /**
     * Alternative method to remove by Document object
     * @param doc The document to remove
     * @return Number of chunks removed
     */
    public int removeDocument(Document doc) {
        if (doc == null) {
            return 0;
        }
        return removeDocument(doc.getDocumentID());
    }

    /**
     * Check if a document has chunks in storage
     * @param documentID The document ID to check
     * @return Number of chunks found for this document
     */
    public int countChunksForDocument(String documentID) {
        if (documentID == null || documentID.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (DocumentChunk chunk : documentChunks) {
            Document parent = chunk.getParent();
            if (parent != null && documentID.equals(parent.getDocumentID())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get all chunks for a specific document
     * @param documentID The document ID
     * @return List of chunks belonging to this document
     */
    public List<DocumentChunk> getChunksForDocument(String documentID) {
        List<DocumentChunk> result = new ArrayList<>();
        if (documentID == null || documentID.isEmpty()) {
            return result;
        }

        for (DocumentChunk chunk : documentChunks) {
            Document parent = chunk.getParent();
            if (parent != null && documentID.equals(parent.getDocumentID())) {
                result.add(chunk);
            }
        }
        return result;
    }

    /**
     * Clear all chunks from storage
     */
    public void clearAll() {
        int size = documentChunks.size();
        documentChunks.clear();
        System.out.println("✓ Cleared all " + size + " chunks from storage");
    }
}
