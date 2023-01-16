package com.ade.chatclient.view;

import com.ade.chatclient.viewmodel.ViewModelFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

// a class that manipulates the views
// it also starts the first view routine
public class ViewHandler {

    private final Stage stage;
    private final ViewModelFactory vmFactory;

    public ViewHandler(Stage stage, ViewModelFactory vmFactory) {
        this.vmFactory = vmFactory;
        this.stage = stage;
    }

    // starts the command line view main loop
    public void start() throws IOException {
        openView("login");
    }

    private void openView(String viewName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = null;

        if (Objects.equals(viewName, "cmd")) {
            CommandLineView view = new CommandLineView(vmFactory.getCmdViewModel());
            view.runMainLoop();
        }
        else if (Objects.equals(viewName, "login")) {
            fxmlLoader.setLocation(getClass().getResource("log-in-view.fxml"));
            root = fxmlLoader.load();
            LogInView view = fxmlLoader.getController();
            view.init(vmFactory.getLogInViewModel());
        }
        else if (Objects.equals(viewName, "chatPage")) {
            fxmlLoader.setLocation(getClass().getResource("chat-page.fxml"));
            root = fxmlLoader.load();
            Objects view = fxmlLoader.getController();
        }

        assert root != null;
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
