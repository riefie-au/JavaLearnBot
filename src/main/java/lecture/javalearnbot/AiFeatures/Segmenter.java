package lecture.javalearnbot.AiFeatures;

import java.util.ArrayList;
import java.util.List;

public class Segmenter {
    private final int chunkSize;
    private final int overlap;

    public Segmenter(int chunkSize, int overlap) {
        this.chunkSize = chunkSize; //maximum characters each chunk will contain
        this.overlap = overlap; //number of characters the next chunk will reuse from previous chunk to avoid losing context
    } //when we create a segmenter object we pass the chunk size and the overlap


    public List<String> segment(String text) {
        List<String> chunks = new ArrayList<String>(); //creates empty list to store chunks
        int start = 0; //keeps track of current starting position in the text
        int length = text.length(); //total length of input text
        while (start < length) {
            int end = Math.min(start + chunkSize, length);
            String chunk = text.substring(start, end);
            chunks.add(chunk);

            start = start + chunkSize-overlap;
        }
        return chunks;
    }
}
