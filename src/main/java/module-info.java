// этот файл нуже для того, чтобы подключить javafx к проекту, он говорит виртуальной машине java
// чтобы она подключила необходимые библиотеки

module com.ade.chatclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.net.http;
    requires lombok;
    requires java.desktop;
    requires spring.messaging;
    requires spring.websocket;
    requires org.slf4j;


    exports com.ade.chatclient;
    opens com.ade.chatclient to javafx.fxml;

    exports com.ade.chatclient.dtos;
    opens com.ade.chatclient.dtos to javafx.fxml;

    exports com.ade.chatclient.view;
    opens com.ade.chatclient.view to javafx.fxml;

    exports com.ade.chatclient.domain;
    opens com.ade.chatclient.domain to javafx.fxml;

    exports com.ade.chatclient.viewmodel;
    opens com.ade.chatclient.viewmodel to javafx.fxml;

    exports com.ade.chatclient.model;
    opens com.ade.chatclient.model to javafx.fxml;

    exports com.ade.chatclient.application;
    opens com.ade.chatclient.application to javafx.fxml;

    exports com.ade.chatclient.view.cellfactory;
    opens com.ade.chatclient.view.cellfactory to javafx.fxml;

    exports com.ade.chatclient.application.util;
    opens com.ade.chatclient.application.util to javafx.fxml;

    exports com.ade.chatclient.application.structure;
    opens com.ade.chatclient.application.structure to javafx.fxml;

    exports com.ade.chatclient.api;
    opens com.ade.chatclient.api to javafx.fxml;

    exports com.ade.chatclient.repository;
    opens com.ade.chatclient.repository to javafx.fxml;
}