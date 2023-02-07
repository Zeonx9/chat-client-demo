package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ModelFactory;
import com.ade.chatclient.view.ViewHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Фабрика вью-моделей  требует ссылку на фабрику моделей.
 * Инициализирует вью-модель только при наличии ссылки на ViewHandler, которая позволяет перевключиться на другое вью
 */
@Getter
@RequiredArgsConstructor
public class ViewModelProvider {
    private LogInViewModel logInViewModel;
    private ChatPageViewModel chatPageViewModel;
    private final ModelFactory modelFactory;
    private AllUsersViewModel allUsersViewModel;


    /**
     * метод передающий ссылку на ViewHandler в объекты вью-моделей
     * инициализирует вью-модели
     * @param viewHandler требуется для переключения между вью
     */
    public void instantiateViewModels(ViewHandler viewHandler) {
        logInViewModel = new LogInViewModel(viewHandler, modelFactory.getModel());
        chatPageViewModel = new ChatPageViewModel(viewHandler, modelFactory.getModel());
        allUsersViewModel = new AllUsersViewModel(viewHandler, modelFactory.getModel());
    }
}
