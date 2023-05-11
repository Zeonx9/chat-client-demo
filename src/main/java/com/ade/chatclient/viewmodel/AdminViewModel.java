package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.AbstractViewModel;
import com.ade.chatclient.application.StartClientApp;
import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.RegisterData;
import com.ade.chatclient.model.ClientModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.time.LocalDate;

import static com.ade.chatclient.application.Views.LOG_IN_VIEW;

@Getter
public class AdminViewModel extends AbstractViewModel<ClientModel> {
    private final StringProperty empNameAndSurnameProperty = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> empBirthdateProperty = new SimpleObjectProperty<>();
    private final StringProperty empLoginProperty = new SimpleStringProperty();
    private final StringProperty resultLoginProperty = new SimpleStringProperty("");
    private final StringProperty resultPasswordProperty = new SimpleStringProperty("");


    public AdminViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);
    }

    public String getMyName() {
        return model.getMyself().getUsername();
    }

    public String getMyCompany() {
        return model.getCompany().getName();
    }

    public void logOut() {
        model.clearModel();
        StartClientApp.stop();
        viewHandler.openView(LOG_IN_VIEW);
    }

    public String register() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setLogin(empLoginProperty.getValue());
        authRequest.setCompanyId(model.getCompany().getId());

        String[] data = empNameAndSurnameProperty.getValue().split(" ");
        AuthRequest response = model.registerUser(new RegisterData(authRequest, data[0], data[1], empBirthdateProperty.getValue()));

        if (response == null) {
            resultLoginProperty.set("");
            resultPasswordProperty.set("");
            return "Result: Something went wrong...";
        }

        resultLoginProperty.set("Employee's login: " + response.getLogin());
        resultPasswordProperty.set("Employee's password: " + response.getPassword());
        return "Result: Successfully!";
    }
}
