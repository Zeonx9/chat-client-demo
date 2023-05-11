package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.model.ClientModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserProfileViewModelTest {
    private UserProfileViewModel underTest;
    @Mock private ClientModel model;
    @Mock private ViewHandler handler;
    @BeforeEach
    void setUp() {
        underTest = new UserProfileViewModel(handler, model);
    }

    @Test
    void setUserPersonalData() {
        User myself = User.builder().username("login").realName("realName").surname("surname").dateOfBirth(LocalDate.now()).build();
        Company company = Company.builder().id(1L).name("company").build();
        given(model.getMyself()).willReturn(myself);
        given(model.getCompany()).willReturn(company);

        underTest.setUserPersonalData();

        assertThat(underTest.getFullNameProperty().get()).isEqualTo("realName surname");
        assertThat(underTest.getBirthDateProperty().get()).isEqualTo(myself.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        assertThat(underTest.getUserNameProperty().get()).isEqualTo(myself.getUsername());
        assertThat(underTest.getCompanyNameProperty().get()).isEqualTo(company.getName());
    }

    @Test
    void setUserPersonalDataIsNull() {
        User myself = User.builder().username(null).realName(null).surname(null).dateOfBirth(null).build();
        Company company = Company.builder().id(null).name(null).build();
        given(model.getMyself()).willReturn(myself);
        given(model.getCompany()).willReturn(company);

        underTest.setUserPersonalData();

        assertThat(underTest.getFullNameProperty().get()).isEqualTo("-");
        assertThat(underTest.getBirthDateProperty().get()).isEqualTo("-");
        assertThat(underTest.getUserNameProperty().get()).isEqualTo(myself.getUsername());
        assertThat(underTest.getCompanyNameProperty().get()).isEqualTo(company.getName());
    }

}
