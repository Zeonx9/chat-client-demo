package com.ade.chatclient.application;

import com.ade.chatclient.viewmodel.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Фабрика вью-моделей требует ссылку на фабрику моделей.
 * Инициализирует вью-модель только при наличии ссылки на ViewHandler, которая позволяет перевключиться на другое вью
 */
@Getter
@RequiredArgsConstructor
@Slf4j
public class ViewModelProvider {
    private LogInViewModel logInViewModel;
    private ChatPageViewModel chatPageViewModel;
    private final ModelFactory modelFactory;
    private AllUsersViewModel allUsersViewModel;
    private AllChatsViewModel allChatsViewModel;
    private UserSettingsViewModel userProfileViewModel;
    private ProfileViewModel profileViewModel;
    private AdminViewModel adminViewModel;

    private ViewHandler viewHandler;

    /**
     * метод передающий ссылку на ViewHandler в объекты вью-моделей
     * инициализирует вью-модели
     * @param viewHandler требуется для переключения между вью
     */
    public void instantiateViewModels(ViewHandler viewHandler) {
        this.viewHandler = viewHandler;
        logInViewModel = new LogInViewModel(viewHandler, modelFactory.getAuthorizationModel());
        chatPageViewModel = new ChatPageViewModel(viewHandler, modelFactory.getModel());
        allUsersViewModel = new AllUsersViewModel(viewHandler, modelFactory.getModel());
        allChatsViewModel = new AllChatsViewModel(viewHandler, modelFactory.getModel());
        userProfileViewModel = new UserSettingsViewModel(viewHandler, modelFactory.getModel());
        profileViewModel = new ProfileViewModel(viewHandler, modelFactory.getModel());
        adminViewModel = new AdminViewModel(viewHandler, modelFactory.getAdminModel());
    }

    public ProfileViewModel createProfileViewModel() {
        profileViewModel = new ProfileViewModel(viewHandler, modelFactory.getModel());
        return profileViewModel;
    }

    /**
     * Запускает загрузку данных в ClientModel
     */
    public void runClientModel() {
        modelFactory.getModel().runModel();
    }
}
