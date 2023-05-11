package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.RegisterData;
import com.ade.chatclient.model.ClientModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AdminViewModelTest {
    private AdminViewModel underTest;
    @Mock private ClientModel model;
    @Mock private ViewHandler handler;

    @BeforeEach
    void setUp() {
        underTest = new AdminViewModel(handler, model);
    }

    @Test
    void getMyName() {
        User myself = User.builder().username("Dasha").realName("Dasha").surname("Vav").id(1L).build();
        given(model.getMyself()).willReturn(myself);

        String myName = underTest.getMyName();

        assertEquals(myName, myself.getUsername());
    }

    @Test
    void getMyCompany() {
        Company company = Company.builder().name("name").build();
        given(model.getCompany()).willReturn(company);

        String myCompany = underTest.getMyCompany();

        assertEquals(myCompany, company.getName());
    }

    @Test
    void registerUnsuccessful() {
        underTest.getEmpLoginProperty().set("login");
        underTest.getEmpNameAndSurnameProperty().set("na me");
        underTest.getEmpBirthdateProperty().set(LocalDate.MIN);
        AuthRequest response = AuthRequest.builder().companyId(1L).login("login").build();
        given(model.getCompany()).willReturn(Company.builder().id(1L).build());
        given(model.registerUser(new RegisterData(response, "na", "me", LocalDate.MIN))).willReturn(null);

        String result = underTest.register();

        assertThat(underTest.getResultLoginProperty().get()).isEqualTo("");
        assertThat(underTest.getResultPasswordProperty().get()).isEqualTo("");
        assertEquals(result, "Result: Something went wrong...");
    }

    @Test
    void registerSuccessful() {
        underTest.getEmpLoginProperty().set("login");
        underTest.getEmpNameAndSurnameProperty().set("na me");
        underTest.getEmpBirthdateProperty().set(LocalDate.MIN);
        AuthRequest response = AuthRequest.builder().companyId(1L).login("login").build();
        given(model.getCompany()).willReturn(Company.builder().id(1L).build());
        given(model.registerUser(new RegisterData(response, "na", "me", LocalDate.MIN))).willReturn(response);

        String result = underTest.register();

        assertThat(underTest.getResultLoginProperty().get()).isEqualTo("Employee's login: login");
        assertThat(underTest.getResultPasswordProperty().get()).isEqualTo("Employee's password: null");
        assertEquals(result, "Result: Successfully!");
    }

}
