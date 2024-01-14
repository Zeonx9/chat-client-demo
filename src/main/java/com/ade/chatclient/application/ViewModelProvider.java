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
    private UserSettingsViewModel userProfileViewModel;
    private ProfileViewModel profileViewModel;
    private AdminViewModel adminViewModel;
    private BackgroundService backgroundService;

    /**
     * метод передающий ссылку на ViewHandler в объекты вью-моделей
     * инициализирует вью-модели
     * @param viewHandler требуется для переключения между вью
     */
    public void instantiateViewModels(ViewHandler viewHandler) {
        logInViewModel = new LogInViewModel(viewHandler, modelFactory.getModel());
        chatPageViewModel = new ChatPageViewModel(viewHandler, modelFactory.getModel());
        allUsersViewModel = new AllUsersViewModel(viewHandler, modelFactory.getModel());
        allChatsViewModel = new AllChatsViewModel(viewHandler, modelFactory.getModel());
        userProfileViewModel = new UserSettingsViewModel(viewHandler, modelFactory.getModel());
        profileViewModel = new ProfileViewModel(viewHandler, modelFactory.getModel());
        adminViewModel = new AdminViewModel(viewHandler, modelFactory.getModel());

    }

    /**
     * Ленивая инициализация Фоновой службы
     * @return фоновую службу
     */
    public BackgroundService getBackgroundService() {
        if (backgroundService == null) {
            backgroundService = new BackgroundService(modelFactory.getModel());
        }
        return backgroundService;
    }
}
