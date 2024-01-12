package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.application.structure.AbstractChildViewModel;
import com.ade.chatclient.application.util.ListViewSelector;
import com.ade.chatclient.application.util.ViewModelUtils;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.dtos.GroupRequest;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.GroupCreationDialog;
import com.ade.chatclient.view.cellfactory.ChatListCellFactory;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;

import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyChangeEvent;
import java.util.Optional;

import static com.ade.chatclient.application.util.ViewModelUtils.listReplacer;
import static com.ade.chatclient.application.util.ViewModelUtils.runLaterListener;

/**
 * Класс, который связывает model с AllChatsView.
 * Регистрирует 4 лисенера - "gotChats", "NewChatCreated","selectedChatModified" и "chatReceivedMessages"
 */
@Getter
@Setter
public class AllChatsViewModel extends AbstractChildViewModel<ClientModel> {
    private final ListProperty<Chat> chatListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    // TODO Исправить звук

//    private final String mediaPath = "src/main/resources/com/ade/chatclient/sounds/sound.mp3";
//    private MediaPlayer mediaPlayer = new MediaPlayer(new Media(new File(mediaPath).toURI().toString()));
    private Boolean isSearching = false;
    private Chat selected;
    public ListViewSelector<Chat> selector;

    public AllChatsViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);

        //заменяет значение chatListProperty новым значением (лист чатов)
        model.addListener("gotChats", runLaterListener(listReplacer(chatListProperty)));
        model.addListener("NewChatCreated", runLaterListener(this::newChatCreated));
        model.addListener("selectedChatModified", runLaterListener(this::selectedChatModified));
        model.addListener("chatReceivedMessages", runLaterListener(this::raiseChat));
    }


    /**
     * Перемещает чат в начало списка чатов. Если этот чат не открыт в данный момент, то воспроизводит звук уведомления.
     * Если в чет пришло новое сообщения, а он в это момент не открыт, то счетчик новых сообщений увеличится
     * @param event существующий чат
     */
    private void raiseChat(PropertyChangeEvent event) {
        if (isSearching) return;
        synchronized (chatListProperty) {
            Chat chat = (Chat) event.getNewValue();
            chatListProperty.remove(chat);
            chatListProperty.add(0, chat);
            selector.select(0);
            if ((boolean) event.getOldValue()) {
                selected = chat;
            }
            else {
//                playSound();
            }
        }
    }

    /**
     * Добавляет новый чат в начало списка чатов
     * @param event новый чат
     */
    private void newChatCreated(PropertyChangeEvent event) {
        if (isSearching) return;
        synchronized (chatListProperty) {
            Chat chat = (Chat) event.getNewValue();
            chatListProperty.add(0, chat);
            selected = chat;
            selector.select(0);
        }
    }

    /**
     * Обновляет счетчик непрочитанных сообщений для чата
     * @param evt существующий чат
     */
    private void selectedChatModified(PropertyChangeEvent evt) {
        if (isSearching) return;
        synchronized (chatListProperty) {
            Chat chat = (Chat) evt.getNewValue();
            int index = chatListProperty.indexOf(chat);
            chatListProperty.set(index, chat);
            selected = chat;
            selector.select(model.getSelectedChat());
        }
    }

    /**
     * Метод вызывается при переключении на AllChatsView, обновляет состояние боковых кнопок и список чатов
     */
    @Override
    public void actionInParentOnOpen() {
        viewHandler.getViewModelProvider().getChatPageViewModel().changeButtonsParam(2);
        model.getMyChatsAfterSearching();
    }

    /**
     * Загружает фабрику ячеек для чатов, используя ChatListCellFactory и fxml файл с описанием интерфейса одной ячейки
     * @return ChatListCellFactory - фабрика ячеек для чатов
     */
    public ListCell<Chat> getChatListCellFactory() {
        ChatListCellFactory factory = ViewModelUtils.loadCellFactory(
                ChatListCellFactory.class,
                "chat-list-cell-factory.fxml"
        );
        factory.init(model.getMyself().getId());
        selector.select(selected);
        return factory;
    }

    /**
     * Метод создает и запускает диалоговое окно для создания новой беседы, после чего получает результат работы диалогового окна и отправляет данные в модель для создания нового группового чата
     */
    public void showDialogAndWait() {
        GroupCreationDialog dialog = GroupCreationDialog.getInstance();
        dialog.init(new GroupCreationDialogModel());
        dialog.populateUserList(model.getAllUsers());

        Optional<GroupRequest> answer = dialog.showAndWait();
        answer.ifPresent(model::createGroupChat);
    }


    /**
     * При изменении текста newText в поле для поиска вызывает
     * метод model для поиска чатов, если текст не пустой, иначе завершает поиск
     * @param newText измененный текст
     */
    public void onTextChanged(String newText) {
        if (newText == null || newText.isBlank()) {
            isSearching = false;
            model.getMyChatsAfterSearching();
            selector.select(selected);
            return;
        }
        isSearching = true;
        synchronized (chatListProperty) {
            chatListProperty.clear();
            chatListProperty.addAll(model.searchChat(newText));
        }
    }

    /**
     * Метод срабатывающий при нажатии на ячейку ListView со всеми чатами пользователя, осуществляет выбор этого чата для дальнейшего отображения истории сообщений из него
     */
    public void onMouseClickedListener(MouseEvent mouseEvent) {
        @SuppressWarnings("unchecked")
        Chat changedChat = ((ListView<Chat>) mouseEvent.getSource()).getSelectionModel().getSelectedItem();
        if (changedChat == null || changedChat.equals(model.getSelectedChat())) {
            return;
        }
        selected = changedChat;
        model.setSelectChat(changedChat);
    }


    /**
     * Создает объект класса ListViewSelector, который необходим для корректного отображения выбранного чата в лист ListView
     * @param listView список чатов
     */
    public void addSelector(ListView<Chat> listView) {
        selector = new ListViewSelector<>(listView);
    }

//    /**
//     * Метод для проигрывания звука уведомления о новом сообщении
//     */
//    private void playSound(){
//        mediaPlayer.seek(Duration.ZERO);
//        mediaPlayer.play();
//    }
}
