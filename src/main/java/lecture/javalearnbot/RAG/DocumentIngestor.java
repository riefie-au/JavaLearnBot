package lecture.javalearnbot.RAG;

import com.google.gson.Gson;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lecture.javalearnbot.RAG.RagHelperClasses.ChunkStorage;
import lecture.javalearnbot.RAG.RagHelperClasses.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class DocumentIngestor {
    private java.io.File docs = new java.io.File("src/main/resources/docs");
    private final LocalDateTime time = LocalDateTime.now();
    private final Segmenter segmenter = new Segmenter(800, 80);
    private final Gson gson = new Gson();
    private final OpenAiEmbeddingModel embeddingModel;
    private final ChunkStorage chunkStore;

    public DocumentIngestor(OpenAiEmbeddingModel embeddingModel, ChunkStorage chunkStore) {
        this.chunkStore = chunkStore;
        this.embeddingModel = embeddingModel;
    }

    public void indexDocs(){
        if (docs == null || !docs.exists()) {
            return; //skip if folder is missing
        }
        try {
            var files = Files.walk(docs.toPath()) //converts file object to path, walk will visit every file and folder inside it
                    .filter(p -> !Files.isDirectory(p)) //skips directories
                    .filter(p -> p.toString().endsWith(".txt"))
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
                    DocumentMeta meta = gson.fromJson(json, DocumentMeta.class);
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
                        file.getPath(),//path reference
                        description,
                        file.lastModified() //timestamp
                );

                int index = 0;
                for (String chunkText : chunkTexts) {
                    float[] vector = embeddingModel.embed(chunkText).content().vector();
                    ChunkStorage.DocumentChunk chunk = new ChunkStorage.DocumentChunk(doc, chunkText,vector,index++);
                    chunkStore.add(chunk); //generate metadata and vector embeddings for chunk
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

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

    public ChunkStorage getChunkStore() {
        return chunkStore;
    }

    public static class DocumentMeta {
       public String title;
       public String category;
       String source;
       public String description;
     }

}
