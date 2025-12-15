package lecture.javalearnbot.AiFeatures;

import java.time.LocalDateTime;
//used for json serialization, to change a json file into a document object to be stored in tables
//did this because for some reason couldn't convert the json file directly into a Document object

public class DocumentMeta {
    public String title;
    public String category;
    public String source;
    public String path;
    public long timestamp;
    public String description;

}
