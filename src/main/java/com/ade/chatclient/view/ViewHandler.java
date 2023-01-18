package com.ade.chatclient.view;

import com.ade.chatclient.viewmodel.ViewModelProvider;
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
    private final ViewModelProvider viewModelProvider;

    public ViewHandler(Stage stage, ViewModelProvider viewModelProvider) {
        this.viewModelProvider = viewModelProvider;
        viewModelProvider.InstantiateViewModels(this);
        this.stage = stage;
    }

    // starts the command line view main loop
    public void start() throws IOException {
        openLogInView();
    }

    public void openLogInView() throws IOException {
        openView("login");
    }
    public void openChatPageView() throws IOException {
        openView("chatPage");
    }

    private void openView(String viewName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = null;
        boolean isFullScreen = false;

        if (Objects.equals(viewName, "login")) {
            fxmlLoader.setLocation(getClass().getResource("log-in-view.fxml"));
            root = fxmlLoader.load();
            LogInView view = fxmlLoader.getController();
            view.init(viewModelProvider.getLogInViewModel());
        }

        else if (Objects.equals(viewName, "chatPage")) {
            fxmlLoader.setLocation(getClass().getResource("chat-page-view.fxml"));
            root = fxmlLoader.load();
            ChatPageView view = fxmlLoader.getController();
            view.init(viewModelProvider.getChatPageViewModel());
            isFullScreen = true;
        }

        assert root != null;
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(isFullScreen);
        stage.show();
    }
}
