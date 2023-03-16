package com.ade.chatclient.application;

import com.ade.chatclient.view.AllChatsView;
import com.ade.chatclient.view.AllUsersView;
import com.ade.chatclient.view.ChatPageView;
import com.ade.chatclient.view.LogInView;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;


/**
 * Перечисление, которое содержит константы описывающие View или Pane в приложении.
 * Каждая константа описывает свой метод инициализации, в который передаются необходимые параметры
 */
@AllArgsConstructor
public enum Views {
    LOG_IN_VIEW(
            "log-in-view",
            (initializer) -> {
                LogInView view = initializer.getLoader().getController();
                view.init(initializer.getViewHandler().getViewModelProvider().getLogInViewModel());
            }
    ),
    ALL_USERS_VIEW(
            "all-users-view",
            (initializer) -> {
                AllUsersView view = initializer.getLoader().getController();
                view.init(initializer.getViewModelProvider().getAllUsersViewModel());
                view.getViewModel().setPlaceHolder(initializer.getParent());
            }
    ),
    ALL_CHATS_VIEW(
            "all-chats-view",
            (initializer) -> {
                AllChatsView view = initializer.getLoader().getController();
                view.init(initializer.getViewModelProvider().getAllChatsViewModel());
            }
    ),
    CHAT_PAGE_VIEW(
            "chat-page-view",
            (initializer) -> {
                ChatPageView view = initializer.getLoader().getController();
                view.init(initializer.getViewModelProvider().getChatPageViewModel());
//                пример, как тут подгрузить панель
                initializer.getViewHandler().openPane(ALL_CHATS_VIEW, view.getSwitchPane());
            }
    );
    /**
     * Содержит имя связанного fxml файла без расширения ".fxml"
     */
    final String fxmlFileName;
    /**
     * функциональный объект инициализатор, он должен загрузить контролер из fxmlLoader,
     * и вызвать инициализатор для этого контроллера (view.init(...)), если вью содержит панели,
     * то необходимо подгрузить и панели используя переданный viewHandler
     * Все необходимые параметры должны быть упакованы и переданы в объекте ViewInitializer
     */
    final Consumer<ViewInitializer> initAction;
}
