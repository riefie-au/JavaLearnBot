package lecture.javalearnbot.AiFeatures;

import com.google.gson.Gson;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lecture.javalearnbot.ChatController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.time.LocalDateTime;


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
    private java.io.File docs = new java.io.File("src/main/resources/docs");;
    private final Properties cfg = new Properties();
    private final OpenAiChatModel chat;
    private final OpenAiEmbeddingModel embeddings;
    private final int topK = 5;
    private final ChunkStorage chunkStore = new ChunkStorage();
    private final Segmenter segmenter = new Segmenter(800, 80);
    private final Gson gson = new Gson();
    private final LocalDateTime time = LocalDateTime.now();

    public Pipeline() {
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

        //List<Hit> rerankedHits = rerankAndDiversify(hits);
        List<Hit> rerankedHits = rerank(hits);
        String answer = GenerateAnswer(question, rerankedHits, rewrites); //sends the original question, along with the hits and the list of rewrites to the prompt generator then to the AI,
        return new Result (rerankedHits, answer, rewrites);
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


    public List<String> rewriteQuestion(String question, int numberOfRewrites) {
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

    //Creates and list of Hit objects after finding topK relevant chunks
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

    public void indexDocs(){
        if (docs == null || !docs.exists()) {
            return; //skip if folder is missing
        }

        try {
            var files = Files.walk(docs.toPath()) //converts file object to path, walk will visit every file and folder inside it
                    .filter(p -> !Files.isDirectory(p)) //skips directories
                    .filter(p -> p.toString().endsWith(".txt") || p.toString().endsWith(".docs"))
                    .map(Path::toFile)
                    .toList();
            // keep only files ending with .txt or docs, convert each path object to a string and collect all of them into a string list

            for(File file : files){
                String title = file.getName();
                String category = "general";
                String description = "No description";
                File metaFile = new File(docs,file.getName()+".meta");

                //check if the metadata file for that file exists
                if(metaFile.exists()){
                    String json = Files.readString(metaFile.toPath());
                    DocumentMeta meta = gson.fromJson(json,DocumentMeta.class);
                    title = meta.title;
                    category = meta.category;
                    description = meta.description;

                }

                String text = Files.readString(file.toPath());
                List<String> chunkTexts = segmenter.segment(text);

                //creating document object for each file found
                Document doc = new Document(
                        title, //document title
                        category,      //category placeholder
                        "local",        //source
                        file.getPath(), //path reference
                        file.lastModified(), //timestamp
                        text            //pass the full text context
                );

                int index = 0;
                for (String chunkText : chunkTexts) {
                    float[] vector = embeddings.embed(chunkText).content().vector();
                    ChunkStorage.DocumentChunk chunk = new ChunkStorage.DocumentChunk(doc, chunkText,vector,index++);
                    chunkStore.add(chunk); //generate metadata and vector embeddings for chunk
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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



    //purpose of ingest is to take a file from the user, copy it into the directory, then create
    //an associated metadata file for it, then passing
    public void ingest(File file, String title, String category, String description) throws IOException {
        if (file == null || !file.exists()) {
            return;
        }
        try {
            File target = new File(docs, title); //copy the file into the docs folder
            if (!target.exists()) {
                Files.copy(file.toPath(), target.toPath());
            }
            File metadataFile = new File(docs, file.getName() + ".meta.json"); //create the metadata file
            if (!metadataFile.exists()) {
                String metadataJson = String.format("""
                        {
                        "docTitle": "%s",
                        "category": "%s",
                        "description": "%s",
                        "lastModified":  "%s"
                        }
                        
                        """, title, category,description,time.toString());
                Files.writeString(metadataFile.toPath(), metadataJson);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


//        public void indexSingleFile (File file, String title,String category) throws IOException {
//            String text = Files.readString(file.toPath()); //read the file
//            List<String> chunkTexts = segmenter.segment(text);
//
//            Document doc = new Document(
//                    title != null ? title : file.getName(),
//                    category != null ? category : "general",
//                    "local",
//                    file.getPath(),
//                    file.lastModified()
//            );
//            int index = 0;
//            for (String ChunkText : chunkTexts) {
//                float[] vector = embeddings.embed(ChunkText).content().vector();
//                ChunkStorage.DocumentChunk chunk = new ChunkStorage.DocumentChunk(doc, ChunkText, vector, index++);
//                chunkStore.add(chunk);
//            }
//        }


    //load docs folder from projects resources folder and makes it accessible as a file object in java
//    public void loadDocsFolder() {
//        try{
//            docs = new File(getClass().getClassLoader().getResource("docs").toURI());
//            }
//        catch (Exception e){
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }

    public ChunkStorage getChunkStore() {
        return chunkStore;
    }
    private static class DocumentMeta {
        String title;
        String category;
        String source;
        String description;
    }
}





