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
    requires java.desktop; // needed if assistant is null

    opens lecture.javalearnbot to javafx.fxml;
    exports lecture.javalearnbot;
    exports lecture.javalearnbot.AiFeatures;
    opens lecture.javalearnbot.AiFeatures to javafx.fxml;
}