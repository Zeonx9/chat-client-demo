package com.ade.chatclient.view.components;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserPhoto {

    public static void setPaneContent(ObservableList<Node> pane, Chat chat, Long selfId) {
        if (!ifIconPresent(chat)) {
            Circle circle = new Circle(20, Color.rgb(145, 145, 145));
            Label label = new Label(prepareInitialsToBeShown(chat, selfId));
            label.setStyle("-fx-text-fill: #FFFFFF");
            pane.addAll(circle, label);
        }
    }

    public static void setPaneContent(ObservableList<Node> pane, User user) {
        if (!ifIconPresent(user)) {
            Circle circle = new Circle(20, Color.rgb(145, 145, 145));
            Label label = new Label(prepareInitialsToBeShown(user));
            label.setStyle("-fx-text-fill: #FFFFFF");
            pane.addAll(circle, label);
        }
    }

    private static boolean ifIconPresent(User user) {
        return false;
    }

    private static boolean ifIconPresent(Chat chat) {
        return false;
    }

    private static String prepareInitialsToBeShown(Chat chat, Long id) {
        String[] chatName;

        if (chat.getGroup() != null) {
            chatName = chat.getGroup().getName().split(" ");
        }
        else{
            List<String> memberNames = new ArrayList<>();
            chat.getMembers().forEach(member -> {
                if (!Objects.equals(member.getId(), id))
                    memberNames.add(member.getRealName() + " " + member.getSurname());
            });
            chatName = String.join(", ", memberNames).split(" ");
        }

        StringBuilder result = new StringBuilder();
        for (String s : chatName) {
            result.append(Character.toUpperCase(s.charAt(0)));
        }
        return result.toString();
    }

    private static String prepareInitialsToBeShown(User user) {
        return Character.toUpperCase(user.getRealName().charAt(0)) + "" +
                Character.toUpperCase(user.getSurname().charAt(0));
    }
}



