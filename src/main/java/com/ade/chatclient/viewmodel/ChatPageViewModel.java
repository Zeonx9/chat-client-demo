package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.application.Views;
import com.ade.chatclient.application.structure.AbstractViewModel;
import com.ade.chatclient.application.util.BottomScroller;
import com.ade.chatclient.application.util.PaneSwitcher;
import com.ade.chatclient.application.util.ViewModelUtils;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.GroupRequest;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.GroupCreationDialog;
import com.ade.chatclient.view.GroupInfoDialog;
import com.ade.chatclient.view.UserInfoDialog;
import com.ade.chatclient.view.cellfactory.MessageListCellFactory;
import com.ade.chatclient.view.components.UserPhoto;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import lombok.Getter;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.ade.chatclient.application.util.ViewModelUtils.runLaterListener;

/**
 * Класс, связывающий model c ChatPageView.
 * Регистрирует 2 лисенера - "gotMessages" и "newMessagesInSelected"
 */
@Getter
public class ChatPageViewModel extends AbstractViewModel<ClientModel> {
    private final ListProperty<Message> messageListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObservableList<Node> chatIconNodes = FXCollections.observableArrayList();
    private final StringProperty messageTextProperty = new SimpleStringProperty();
    private final BooleanProperty showChatsButtonDisabled = new SimpleBooleanProperty(true);
    private final BooleanProperty showUsersButtonDisabled = new SimpleBooleanProperty(false);
    private final BooleanProperty showUserProfileDisabled = new SimpleBooleanProperty(false);
    private final BooleanProperty disableProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty infoAreaDisableProperty = new SimpleBooleanProperty(false);
    private final DoubleProperty infoAreaOpacityProperty = new SimpleDoubleProperty(100);
    private final DoubleProperty opacityProperty = new SimpleDoubleProperty(0);
    private final StringProperty selectedChatNameProperty = new SimpleStringProperty();
    private final StringProperty selectedChatInfoProperty = new SimpleStringProperty();
    private final StringProperty openViewNameProperty = new SimpleStringProperty();
    private final DoubleProperty fileUploadStatusProperty = new SimpleDoubleProperty(0);
    private BottomScroller<Message> scroller;
    private PaneSwitcher paneSwitcher;
    private File file = null;

    public static final String GOT_MESSAGES_EVENT = "gotMessages";
    public static final String NEW_MESSAGES_IN_SELECTED_EVENT = "newMessagesInSelected";
    public static final String GROUP_CHAT_NAME_UPDATE = "groupChatNameUpdate";
    public static final String GROUP_PHOTO = "groupPhoto";

    public ChatPageViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);

        model.addListener(GOT_MESSAGES_EVENT, runLaterListener(this::fillMessageHistory));
        model.addListener(NEW_MESSAGES_IN_SELECTED_EVENT, runLaterListener(this::newSelectedMessages));
        model.addListener(GROUP_CHAT_NAME_UPDATE, runLaterListener(this::groupChatNameUpdate));
        model.addListener(GROUP_PHOTO, runLaterListener(this::groupChatPhotoUpdate));

    }

    private void groupChatPhotoUpdate(PropertyChangeEvent event) {
        fillChatInfo();
    }

    private void groupChatNameUpdate(PropertyChangeEvent event) {
        synchronized (selectedChatNameProperty) {
            @SuppressWarnings("unchecked")
            String newChatName = (String) event.getNewValue();
            selectedChatNameProperty.setValue(newChatName);
        }
    }

    /**
     * Заменяет messageListProperty новым значением event и прокручивает список в конец, отобразив на экране только последние сообщения
     * @param event список сообщений из выбранного чата
     */
    private void fillMessageHistory(PropertyChangeEvent event) {
        synchronized (messageListProperty) {
            @SuppressWarnings("unchecked")
            List<Message> messages = (List<Message>) event.getNewValue();
            messageListProperty.clear();
            messageListProperty.addAll(messages);
            fillChatInfo();
            scroller.scrollDown();
        }
        opacityProperty.set(100);
        disableProperty.set(false);

        infoAreaDisableProperty.set(true);
        infoAreaOpacityProperty.set(0);
    }

    private void fillChatInfo() {
        synchronized (messageListProperty) {
            chatIconNodes.clear();

            selectedChatNameProperty.setValue(model.getSelectedChat().getChatName(model.getMyself().getId()));
            Boolean isPrivateChat = model.getSelectedChat().getIsPrivate();
            List<User> members = model.getSelectedChat().getMembers();

            if (isPrivateChat) {
                for (User user : members) {
                    if (!user.getId().equals(model.getMyself().getId())) {
                        selectedChatInfoProperty.set(user.getUsername());
                    }
                }
            } else {
                selectedChatInfoProperty.setValue(members.size() + " members");
            }

            chatIconNodes.clear();
            Objects.requireNonNull(UserPhoto.getPaneContent(model.getSelectedChat(), model.getMyself().getId(), 20, model::getPhotoById))
                    .thenAccept(children -> {
                        Platform.runLater(() -> {
                            chatIconNodes.clear();
                            chatIconNodes.addAll(children);
                        });
                    });
        }
    }

    /**
     * добавляет новое сообщение (отправленное или полученное) в messageListProperty
     * @param event новое сообщение
     */
    private void newSelectedMessages(PropertyChangeEvent event) {
        synchronized (messageListProperty) {
            @SuppressWarnings("unchecked")
            List<Message> selectedChatMessages = (List<Message>) event.getNewValue();
            messageListProperty.addAll(selectedChatMessages);
            scroller.scrollDown();
        }
    }

    public void addBottomScroller(ListView<Message> listView) {
        scroller = new BottomScroller<>(listView);
    }

    public void addPaneSwitcher(Pane placeHolder) {
        paneSwitcher = new PaneSwitcher(viewHandler, placeHolder);
    }

    /**
     * Метод осуществляет переключение на вью со всеми чатами пользователя
     */
    public void openChatPane() {
        paneSwitcher.switchTo(Views.ALL_CHATS_VIEW);
    }

    /**
     * Метод осуществляет переключение на вью со всеми пользователями в компании
     */
    public void openUsersPane() {
        paneSwitcher.switchTo(Views.ALL_USERS_VIEW);
    }

    /**
     * Метод осуществляет переключение на вью с личным кабинетом пользователя
     */
    public void openProfilePane() {
        viewHandler.getViewModelProvider().createProfileViewModel().setUser(model.getMyself());
        paneSwitcher.switchTo(Views.USER_SETTINGS_VIEW);
    }

    /**
     * Метод отправляет введенное пользователем сообщение, если оно длинное, то делит его на части
     */
    public void sendMessage() {
        if ((Objects.equals(messageTextProperty.getValue(), null) || messageTextProperty.get().isBlank()) && file == null) {
            return;
        }

        String message = (messageTextProperty.get() != null) ? messageTextProperty.get() : "";
        if (message.length() <= 250) {
            model.sendMessageToChat(message, file);
            file = null;
            fileUploadStatusProperty.set(0);
        }
        else {
            int startIndex = 0;
            while (startIndex < message.length()) {
                int endIndex = Math.min(startIndex + 250, message.length());
                if (endIndex < message.length() && message.charAt(endIndex) != ' ' && message.charAt(endIndex - 1) != ' ') {
                    int lastSpaceIndex = message.lastIndexOf(' ', endIndex);
                    if (lastSpaceIndex != -1 && lastSpaceIndex > startIndex)
                        endIndex = lastSpaceIndex;
                }

                model.sendMessageToChat(message.substring(startIndex, endIndex), file);
                file = null;
                fileUploadStatusProperty.set(0);
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                startIndex = endIndex;
            }
        }
        messageTextProperty.set("");
    }

    /**
     * Функция изменяет состояние кнопок на боковой панели, в зависимости от того, какое вью открыто
     * @param index номер вью, на которое пользователь перешел
     */
    public void changeButtonsParam(Integer index) {
        showUserProfileDisabled.set(index == 0);
        showUsersButtonDisabled.set(index == 1);
        showChatsButtonDisabled.set(index == 2);
        openViewNameProperty.set(index == 1 ? "Users" : index == 0 ? "Profile" : "Recent");
    }

    /**
     * Загружает фабрику ячеек для сообщений, используя MessageListCellFactory и fxml файл с описанием интерфейса одной ячейки
     * @return MessageListCellFactory - фабрика ячеек для сообщений
     */
    public ListCell<Message> getMessageCellFactory() {
        MessageListCellFactory factory = ViewModelUtils.loadCellFactory(
                MessageListCellFactory.class,
                "message-list-cell-factory.fxml"
        );
        factory.init(model.getMyself().getId(), model::getPhotoById);
        return factory;
    }

    /**
     * Метод осуществляет создание и открытие диалогового окна для просмотра информации о беседе
     */
    public void showDialog() {
        if (model.getSelectedChat().getIsPrivate()) {
            List<User> members = model.getSelectedChat().getMembers();
            for (User user: members)
                if (!user.equals(model.getMyself()))
                    viewHandler.getViewModelProvider().createProfileViewModel().setUser(user);

            UserInfoDialog userInfoDialog = UserInfoDialog.getInstance();
            userInfoDialog.setProfilePane(viewHandler);
            userInfoDialog.showAndWait();
        }
        else {
            GroupInfoDialog dialog = GroupInfoDialog.getInstance();
            dialog.init(new GroupInfoDialogModel(model));
            dialog.setImageRequest(model::getPhotoById);
            dialog.setChat(model.getSelectedChat());
            dialog.showAndWait();
        }
    }

    /**
     * Метод создает и запускает диалоговое окно для создания новой беседы, после чего получает результат работы диалогового окна и отправляет данные в модель для создания нового группового чата
     */
    public void showDialogAndWait() {
        GroupCreationDialog dialog = GroupCreationDialog.getInstance();
        dialog.init(new GroupCreationDialogModel());
        dialog.setImageRequest(model::getPhotoById);
        dialog.populateUserList(model.getAllUsers());

        Optional<GroupRequest> answer = dialog.showAndWait();
        answer.ifPresent(model::createGroupChat);
    }

    public void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите изображение");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Изображения (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            this.file = file;
            fileUploadStatusProperty.set(100);
        }
    }

}
