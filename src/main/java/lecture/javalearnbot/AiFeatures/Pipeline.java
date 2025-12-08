package lecture.javalearnbot.AiFeatures;

import java.util.ArrayList;
import java.util.List;


//Main Pipeline class where main methods are stored. pom.xml dependencies for open,ai and langchain have to be added
/*
Things that still are required to be added
1.Constructor
2.Segmenter
3.Need to add AI and Langchain to PomXML so external libraries will have the packages and we can use the built in functions
4.OpenAI key
5.Rewrite method
6.Index Method
 */




public class Pipeline {

    private final int topK = 3;
    private final ChunkStorage chunkStore = new ChunkStorage();

    //addDocChunk adds an individual chunk into the documentChunks array in chunkStore
    public void addDocChunk(Document doc, String text, float[] vector, int index){
        ChunkStorage.DocumentChunk chunk = new ChunkStorage.DocumentChunk(doc, text, vector, index);
        chunkStore.add(chunk);
    }
    

    //is the method that will occur after user clicks run after inputting a query
    public Result run (String question) {
        List<String> rewrites = rewriteQuestion(question,3); //rewrites the query into 3 seperate questions to give to the AI

        //convert question into embedding vector
        float[] questionVector = embeddings.embed(question).content().vector();
        List<Hit> hits = toHits(chunkStore.findRelevantChunks(questionVector,topK)); //finds the topK most relevant chunks and stores it an a list as Hit Objects

        String answer = GenerateAnswer(question, hits, rewrites); //sends the original question, along with the hits and the list of rewrites to the prompt generator then to the AI,
        return new Result (hits, answer, rewrites);
    }


    //Creates and list of Hit objects after finding topK relevant chunks
    private List<Hit> toHits(List<ChunkStorage.ScoredChunk> scoredChunks) {
        List<Hit> hits = new ArrayList<>();
        for (ChunkStorage.ScoredChunk scoredChunk : scoredChunks) {
            ChunkStorage.DocumentChunk chunk = scoredChunk.chunk;

            hits.add(new Hit(
                    chunk.getParent().getTitle(), //created the hit class and returns the list of hits
                    chunk.getParent().getPath(),
                    chunk.getText(),
                    scoredChunk.score
            ));

        }
        return hits;
    }
}
