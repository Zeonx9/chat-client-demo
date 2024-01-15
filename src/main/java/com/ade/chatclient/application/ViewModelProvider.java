package com.ade.chatclient.application;

import com.ade.chatclient.viewmodel.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Фабрика вью-моделей требует ссылку на фабрику моделей.
 * Инициализирует вью-модель только при наличии ссылки на ViewHandler, которая позволяет перевключиться на другое вью
 */
@Getter
@RequiredArgsConstructor
public class ViewModelProvider {
    private LogInViewModel logInViewModel;
    private ChatPageViewModel chatPageViewModel;
    private final ModelFactory modelFactory;
    private AllUsersViewModel allUsersViewModel;
    private AllChatsViewModel allChatsViewModel;
    private UserProfileViewModel userProfileViewModel;
    private AdminViewModel adminViewModel;

    /**
     * метод передающий ссылку на ViewHandler в объекты вью-моделей
     * инициализирует вью-модели
     * @param viewHandler требуется для переключения между вью
     */
    public void instantiateViewModels(ViewHandler viewHandler) {
        logInViewModel = new LogInViewModel(viewHandler, modelFactory.getAuthorizationModel());
        chatPageViewModel = new ChatPageViewModel(viewHandler, modelFactory.getModel());
        allUsersViewModel = new AllUsersViewModel(viewHandler, modelFactory.getModel());
        allChatsViewModel = new AllChatsViewModel(viewHandler, modelFactory.getModel());
        userProfileViewModel = new UserProfileViewModel(viewHandler, modelFactory.getModel());
        adminViewModel = new AdminViewModel(viewHandler, modelFactory.getAdminModel());
    }

    /**
     * Запускает загрузку данных в ClientModel
     */
    public void runClientModel() {
        modelFactory.getModel().runModel();
    }
}
