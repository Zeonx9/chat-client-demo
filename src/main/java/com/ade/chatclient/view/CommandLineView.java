package com.ade.chatclient.view;
import com.ade.chatclient.viewmodel.CommandLineViewModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Scanner;

// this view handles user input and presents the information to user
// view is bound with viewModel
@Getter
@Setter
public class CommandLineView {
    // fields that hold the current state of the view
    String myName;
    Long myId;
    List<String> myChats;

    // reference to the VM
    private final CommandLineViewModel viewModel;

    public CommandLineView(CommandLineViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.bindToView(this);
    }

    // here the view operates
    public void runMainLoop() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Everything is ready!\nEnter your name please:");
        myName = scanner.nextLine();

        System.out.println("Logging you in ...");
        // notify the viewModel about changes, will be replaced by data binding
        viewModel.namePropertyChanged();

        System.out.println("Logged successfully! your ID is: " + myId);
        System.out.println("\nFetching your chats ...");
        System.out.println("Every chat has its ID, please use it to select the chat you want\n");

        myChats.forEach(System.out::println);
        if (myChats.isEmpty())
            System.out.println("You have no chats, start a new one with somebody)");

        System.out.println("\nThat's all for now, thank you for trying our chat application!)");
    }
}
