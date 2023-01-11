package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ModelFactory;

public class ViewModelFactory {
    private final ModelFactory modelFactory;
    private CommandLineViewModel viewModel;

    public ViewModelFactory(ModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    public CommandLineViewModel getViewModel() {
        if (viewModel == null)
            viewModel = new CommandLineViewModel(modelFactory.getModel());

        return viewModel;
    }
}
