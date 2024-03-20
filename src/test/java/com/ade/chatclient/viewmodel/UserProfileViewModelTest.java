package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.model.ClientModel;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserProfileViewModelTest extends RunPlatform {
    private ProfileViewModel underTest;
    @Mock
    private ClientModel model;
    @Mock
    private ViewHandler handler;

    @BeforeEach
    void setUp() {
        underTest = new ProfileViewModel(handler, model);

    }

    @Test
    void setUserPersonalData() {
        User myself = User.builder()
                .id(1L)
                .username("login")
                .realName("realName")
                .surname("surname")
                .dateOfBirth(LocalDate.now())
                .thumbnailPhotoId("photo")
                .build();
        Image mockImage = Mockito.mock(Image.class);
        Company company = Company.builder().id(1L).name("company").build();

        given(model.getCompany()).willReturn(company);
        given(model.getPhotoById(myself.getThumbnailPhotoId())).willReturn(CompletableFuture.completedFuture(mockImage));

        underTest.setUser(myself);
        underTest.setUserPersonalData();

        assertThat(underTest.getFullNameProperty().get()).isEqualTo("realName surname");
        assertThat(underTest.getBirthDateProperty().get()).isEqualTo(myself.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        assertThat(underTest.getLoginProperty().get()).isEqualTo(myself.getUsername());
        assertThat(underTest.getCompanyNameProperty().get()).isEqualTo(company.getName());
    }

    @Test
    void setUserPersonalDataIsNull() {
        User myself = User.builder()
                .username("name")
                .realName(null)
                .surname(null)
                .dateOfBirth(null)
                .thumbnailPhotoId(null)
                .build();
        Company company = Company.builder().id(null).name(null).build();

        given(model.getCompany()).willReturn(company);

        underTest.setUser(myself);
        underTest.setUserPersonalData();

        assertThat(underTest.getFullNameProperty().get()).isEqualTo("-");
        assertThat(underTest.getBirthDateProperty().get()).isEqualTo("-");
        assertThat(underTest.getLoginProperty().get()).isEqualTo(myself.getUsername());
        assertThat(underTest.getCompanyNameProperty().get()).isEqualTo(company.getName());
    }

}
