package com.ade.chatclient.view.components;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Slf4j
public class UserPhoto {

    public static void setPaneContent(ObservableList<Node> pane, Chat chat, Long selfId, int size, Function<String, CompletableFuture<Image>> imageRequest) {
        if (ifIconPresent(chat)) {
            chat.getMembers().forEach(member -> {
                if (!Objects.equals(member.getId(), selfId))
                    UserPhoto.setPaneContent(pane, member, size, imageRequest);
            });
        }
        else {
            Circle circle = new Circle(size, Color.rgb(145, 145, 145));
            Label label = new Label(prepareInitialsToBeShown(chat, selfId));
            label.setStyle("-fx-text-fill: #FFFFFF");
            pane.addAll(circle, label);
        }
    }

    public static void setPaneContent(ObservableList<Node> pane, User user, int size, Function<String, CompletableFuture<Image>> imageRequest) {
        if (!ifIconPresent(user)) {
            Circle circle = new Circle(size, Color.rgb(145, 145, 145));
            Label label = new Label(prepareInitialsToBeShown(user));
            label.setStyle("-fx-text-fill: #FFFFFF");
            pane.addAll(circle, label);
        } else {
            CompletableFuture<Image> future = imageRequest.apply(user.getThumbnailPhotoId());
            future.thenAccept(image -> {
//                log.info("got image with id = " + user.getThumbnailPhotoId());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(size * 2);
                imageView.setFitHeight(size * 2);
                Circle circle = new Circle(size);
                circle.setCenterX(size);
                circle.setCenterY(size);
                imageView.setClip(circle);
                Platform.runLater(() -> pane.addAll(imageView));
            });
        }
    }

    private static boolean ifIconPresent(User user) {
        return user.getThumbnailPhotoId() != null;
    }

    private static boolean ifIconPresent(Chat chat) {
        return chat.getIsPrivate();
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
        String raw = thisOrEmpty(user.getRealName()) + thisOrEmpty(user.getSurname());
        if (raw.isBlank()) {
            return String.valueOf(Character.toUpperCase(user.getUsername().charAt(0)));
        }
        return raw.trim();
    }

    private static String thisOrEmpty(String value) {
        return value == null ? "" : value.isEmpty() ? "" : String.valueOf(Character.toUpperCase(value.charAt(0)));
    }
}



