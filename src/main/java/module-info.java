module lecture.javalearnbot {
    requires javafx.controls; // standard javafx modules
    requires javafx.fxml;
    requires langchain4j.open.ai; // must add langchain4j references
    requires langchain4j.core;
    requires langchain4j;
    requires org.apache.logging.log4j; // must add log4j references
    requires org.slf4j; // must add slf4j
    requires java.net.http; // needed if HttpTimeoutException occurs
    requires com.fasterxml.jackson.core;
    requires java.desktop;
    requires com.google.gson;
    requires com.opencsv;
    requires io.github.cdimascio.dotenv.java; // needed if assistant is null


    opens lecture.javalearnbot to javafx.fxml;
    exports lecture.javalearnbot;
    exports lecture.javalearnbot.RAG;
    opens lecture.javalearnbot.RAG to javafx.fxml;
    exports lecture.javalearnbot.Evaluation;
    opens lecture.javalearnbot.Evaluation to javafx.fxml;
    exports lecture.javalearnbot.Log;
    opens lecture.javalearnbot.Log to javafx.fxml;
    exports lecture.javalearnbot.RAG.RagHelperClasses;
    opens lecture.javalearnbot.RAG.RagHelperClasses to javafx.fxml;
    exports lecture.javalearnbot.Utility;
    opens lecture.javalearnbot.Utility to javafx.fxml;
    exports lecture.javalearnbot.Controllers;
    opens lecture.javalearnbot.Controllers to javafx.fxml;
}