package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.application.structure.AbstractChildViewModel;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.components.UserPhoto;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.ade.chatclient.application.util.ViewModelUtils.runLaterListener;

@Getter
@Slf4j
public class ProfileViewModel extends AbstractChildViewModel<ClientModel> {
    private final StringProperty fullNameProperty = new SimpleStringProperty();
    private final StringProperty loginProperty = new SimpleStringProperty();
    private final StringProperty statusProperty = new SimpleStringProperty();
    private final StringProperty birthDateProperty = new SimpleStringProperty();
    private final StringProperty phoneNumberProperty = new SimpleStringProperty();
    private final StringProperty companyNameProperty = new SimpleStringProperty();
    private final DoubleProperty opacityProperty = new SimpleDoubleProperty();
    private final ObservableList<Node> chatIconNodes = FXCollections.observableArrayList();
    @Setter
    private User user;

    public static final String CHANGED_USER_INFO = "changeUserInfo";

    public ProfileViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);

        model.addListener(CHANGED_USER_INFO, runLaterListener(this::setUserInfo));
    }


    private void setUserInfo(PropertyChangeEvent event) {
        user = (User) event.getNewValue();
        setUserPersonalData();
    }

    /**
     * Метод получает из модели объект класса User пользователя и устанавливает личную информацию на экран
     */
    public void setUserPersonalData() {
        fullNameProperty.set(prepareFullName(user));
        loginProperty.set(user.getUsername());
        statusProperty.set(user.getIsOnline() == Boolean.TRUE ? "online" : "last seen recently");
        birthDateProperty.set(formatDOB(user.getDateOfBirth()));
        phoneNumberProperty.set(user.getPhoneNumber());
        companyNameProperty.set(model.getCompany().getName());

        chatIconNodes.clear();
        UserPhoto.getPaneContent(user, 40, model::getPhotoById)
                .thenAccept(children -> {
                    Platform.runLater(() -> {
                        log.info("set user Personal data {}: {}", user.getUsername(), user.getThumbnailPhotoId());
                        chatIconNodes.clear();
                        chatIconNodes.addAll(children);
                        if (user.getIsOnline() != null && user.getIsOnline()) {
                            opacityProperty.set(100);
                        }
                        else {
                            opacityProperty.set(0);
                        }
                        log.info("after set, pane children size: {}", chatIconNodes.size());
                    });
                });

//        UserPhoto.setPaneContent(chatIconNodes, user, 40, model::getPhotoById);
    }


    /**
     * @param user объект класса User - пользователь
     * @return имя и фамилию пользователя, если она указана
     */
    private String prepareFullName(User user) {
        String raw = thisOrEmpty(user.getRealName()) + " " + thisOrEmpty(user.getSurname());
        if (raw.isBlank()) {
            return "-";
        }
        return raw.trim();
    }

    /**
     * @param date дата рождения пользователя
     * @return дату рождения в формате "dd.MM.yyyy"
     */
    private String formatDOB(LocalDate date) {
        if (date == null) {
            return "-";
        }
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private String thisOrEmpty(String value) {
        return value != null ? value : "";
    }

}
