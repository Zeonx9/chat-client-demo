module com.ade.chatclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.net.http;
    requires lombok;

    exports com.ade.chatclient;
    opens com.ade.chatclient to javafx.fxml;

    exports com.ade.chatclient.view;
    opens com.ade.chatclient.view to javafx.fxml;

    exports com.ade.chatclient.model.entities;
    opens com.ade.chatclient.model.entities to javafx.fxml;
}