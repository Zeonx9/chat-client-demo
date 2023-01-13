package com.ade.chatclient.view;
import com.ade.chatclient.model.entities.Message;
import com.ade.chatclient.viewmodel.CommandLineViewModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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
    List<ArrayList<String>> myChats;
    List<Long> myChatsId;
    Long idChat;
    List<Message> chatHistory;
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
        myChatsId.forEach(System.out::println);
        if (myChats.isEmpty())
            System.out.println("You have no chats, start a new one with somebody)");

        System.out.println("\nEnter chat id: ");
        idChat = scanner.nextLong();

        bindBack();
        bind();

        System.out.println("History of chart: " + idChat);
        chatHistory.forEach(System.out::println);

        System.out.println("\nThat's all for now, thank you for trying our chat application!)");
    }

    public void bindBack() {
        viewModel.setMyName(myName);
    }

    public void bind() {
        myName = viewModel.getMyName();
        myId = viewModel.getMyId();
        myChats = viewModel.getMyChats();
        myChatsId = viewModel.getMyChatId();
        idChat = viewModel.getMyIdChat();
        chatHistory = viewModel.getMyChatHistory();
    }
}
