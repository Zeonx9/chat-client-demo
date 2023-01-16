package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ModelFactory;

// factory that created viewModels linking them to models
public class ViewModelFactory {
    private final CommandLineViewModel viewModel;
    private final LogInViewModel logInViewModel;

    public ViewModelFactory(ModelFactory modelFactory) {
        logInViewModel = new LogInViewModel(modelFactory.getModel());
        viewModel = new CommandLineViewModel(modelFactory.getModel());
    }

    public CommandLineViewModel getCmdViewModel() {
        return viewModel;
    }

    public LogInViewModel getLogInViewModel() {
        return logInViewModel;
    }
}
