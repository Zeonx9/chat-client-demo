package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.application.structure.AbstractChildViewModel;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.components.UserPhoto;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.ade.chatclient.application.util.ViewModelUtils.runLaterListener;

@Getter
public class ProfileViewModel extends AbstractChildViewModel<ClientModel> {
    private final StringProperty fullNameProperty = new SimpleStringProperty();
    private final StringProperty loginProperty = new SimpleStringProperty();
    private final StringProperty statusProperty = new SimpleStringProperty();
    private final StringProperty birthDateProperty = new SimpleStringProperty();
    private final StringProperty phoneNumberProperty = new SimpleStringProperty();
    private final StringProperty companyNameProperty = new SimpleStringProperty();
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

        UserPhoto.setPaneContent(chatIconNodes, user, 40, model::getPhotoById);
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
