package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ModelFactory;
import com.ade.chatclient.view.ViewHandler;
import lombok.Getter;

// factory that created viewModels linking them to models
@Getter
public class ViewModelProvider {
    private LogInViewModel logInViewModel;
    private ChatPageViewModel chatPageViewModel;
    private final ModelFactory modelFactory;

    public ViewModelProvider(ModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    public void InstantiateViewModels(ViewHandler viewHandler) {
        logInViewModel = new LogInViewModel(viewHandler, modelFactory.getModel());
        chatPageViewModel = new ChatPageViewModel(viewHandler, modelFactory.getModel());
    }
}
