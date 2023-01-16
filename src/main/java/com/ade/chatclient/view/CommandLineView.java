package com.ade.chatclient.view;
import com.ade.chatclient.model.entities.Chat;
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
    List<Chat> myChats;
    Long selectedChatId;

    List<String[]> selectedChatMessages;

    // reference to the VM
    private final CommandLineViewModel viewModel;

    public CommandLineView(CommandLineViewModel viewModel) {
        this.viewModel = viewModel;
    }

    // here the view operates
    public void runMainLoop() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Everything is ready!\nEnter your name please:");
        myName = scanner.nextLine();

        System.out.println("Logging you in ...");
        // notify the viewModel about changes, will be replaced by data binding
        bindBack();
        bind();

        System.out.println("Logged successfully! your ID is: " + myId);
        System.out.println("\nFetching your chats ...");
        System.out.println("Every chat has its ID, please use it to select the chat you want\n");

        myChats.forEach(System.out::println);

        System.out.println("\nEnter chat id: ");
        selectedChatId = scanner.nextLong();

        bindBack();
        bind();

        System.out.println("History of chart: " + selectedChatId);
        if (selectedChatMessages != null) {
            selectedChatMessages.forEach((s) -> System.out.println(s[1] + " by " + s[3]));
            if (selectedChatMessages.isEmpty())
                System.out.println("No messages here yet..");
        }
        else {
            System.out.println("didn't exist");
        }
        System.out.println("\nThat's all for now, thank you for trying our chat application!)");
    }

    public void bindBack() {
        viewModel.setMyName(myName);
        viewModel.setSelectedChat(selectedChatId);
    }

    public void bind() {
        if (myName == null) {
            myName = viewModel.getMyName();
            myId = viewModel.getMyId();
        }
        myChats = viewModel.getMyChats();

        if (selectedChatId != null) {
            selectedChatMessages = viewModel.getSelectedChatMessages();
        }
    }
}
