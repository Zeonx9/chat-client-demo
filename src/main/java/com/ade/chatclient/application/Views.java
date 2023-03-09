package com.ade.chatclient.application;

import com.ade.chatclient.view.AllUsersView;
import com.ade.chatclient.view.ChatPageView;
import com.ade.chatclient.view.LogInView;
import javafx.fxml.FXMLLoader;
import lombok.AllArgsConstructor;

import java.util.function.BiConsumer;

@AllArgsConstructor
public enum Views {
    LOG_IN_VIEW(
            "log-in-view",
            (loader, viewHandler) -> {
                LogInView view = loader.getController();
                view.init(viewHandler.getViewModelProvider().getLogInViewModel());
            }
    ),
    ALL_USERS_VIEW(
            "all-users-view",
            (loader, viewHandler) -> {
                AllUsersView view = loader.getController();
                view.init(viewHandler.getViewModelProvider().getAllUsersViewModel());
            }
    ),
    CHAT_PAGE_VIEW(
            "chat-page-view",
            (loader, viewHandler) -> {
                ChatPageView view = loader.getController();
                view.init(viewHandler.getViewModelProvider().getChatPageViewModel());
//                пример, как тут подгрузить панель
//                viewHandler.openPane(ALL_USERS_VIEW, view.getChatsPane());
            }
    );
    /**
     * Содержит имя связанного fxml файла без расширения ".fxml"
     */
    final String fxmlFileName;
    /**
     * функциональный объект инициализатор, он должен загрузить контролер из fxmlLoader,
     * и вызвать инициализатор для этого контроллера (view.init(...)), если вью содержит панели,
     * то необходимо подгрузить и панели используя переданный
     */
    final BiConsumer<FXMLLoader, ViewHandler> viewInitializer;
}
