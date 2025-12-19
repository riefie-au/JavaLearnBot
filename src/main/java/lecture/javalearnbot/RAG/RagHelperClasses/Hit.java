package lecture.javalearnbot.RAG.RagHelperClasses;

public class Hit {
    private final int index;
    private final String hitSource; //the source title of the document chunk that was hit
    private final String path;
    private final String snippet;
    private final double score;     // similarity score

    public Hit(int index, String hitSource, String path, String snippet, double score) {
        this.index = index;
        this.hitSource = hitSource;
        this.path = path;
        this.snippet = snippet;
        this.score = score;
    }


    public int getIndex() {
        return index;
    }
    public String getHitSource() {return hitSource;}
    public String getPath() { return path; }
    public String getSnippet() { return snippet; }
    public double getScore() { return score; }
}
