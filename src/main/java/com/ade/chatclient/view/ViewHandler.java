package com.ade.chatclient.view;

import com.ade.chatclient.viewmodel.ViewModelFactory;

// a class that manipulates the views
// it also starts the first view routine
public class ViewHandler {
    private final ViewModelFactory vmFactory;
    private CommandLineView view;

    public ViewHandler(ViewModelFactory vmFactory) {
        this.vmFactory = vmFactory;
    }

    // lazy getter that only initializes view once
    // it might change later
    public CommandLineView getView() {
        if (view == null)
            view = new CommandLineView(vmFactory.getViewModel());
        return view;
    }

    // starts the command line view main loop
    public void start() {
        getView().runMainLoop();
    }
}
