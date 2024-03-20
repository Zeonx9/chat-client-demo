package com.ade.chatclient.model;

import com.ade.chatclient.api.AuthorizationApi;
import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthResponse;
import com.ade.chatclient.model.impl.AuthorizationModelImpl;
import com.ade.chatclient.repository.AdminRepository;
import com.ade.chatclient.repository.SelfRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthorizationModelImplTest {
    private AuthorizationModelImpl authorizationModel;
    private AuthorizationApi authApiMock;
    private SelfRepository selfRepositoryMock;
    private AdminRepository adminRepositoryMock;

    @BeforeEach
    public void setUp() {
        authApiMock = Mockito.mock(AuthorizationApi.class);
        selfRepositoryMock = Mockito.mock(SelfRepository.class);
        adminRepositoryMock = Mockito.mock(AdminRepository.class);

        authorizationModel = new AuthorizationModelImpl(authApiMock, selfRepositoryMock, adminRepositoryMock);
    }

    @Test
    public void testAuthorizeSuccessAdmin() throws ExecutionException, InterruptedException {
        User user = User.builder().build();
        Company company = Company.builder().build();
        AuthResponse authResponse = new AuthResponse("token", user, company, true);
        when(authApiMock.authorize(anyString(), anyString())).thenReturn(authResponse);

        boolean result = authorizationModel.authorize("login", "password");

        assertTrue(result);

        verify(adminRepositoryMock).setMyself(authResponse.getUser());
        verify(adminRepositoryMock).setCompany(authResponse.getCompany());
        verify(authApiMock).setHandlerAuthToken(authResponse.getToken());
    }

    @Test
    public void testAuthorizeFailure() throws ExecutionException, InterruptedException {
        when(authApiMock.authorize(anyString(), anyString())).thenThrow(new RuntimeException("Test exception"));

        boolean result = authorizationModel.authorize("incorrect_login", "incorrect_password");

        assertFalse(result);

        verify(authApiMock, never()).setHandlerAuthToken(anyString());
    }

    @Test
    public void testAuthorizeSuccessGeneralUser() throws ExecutionException, InterruptedException {
        User user = User.builder().build();
        Company company = Company.builder().build();
        AuthResponse authResponse = new AuthResponse("token", user, company, false);
        when(authApiMock.authorize(anyString(), anyString())).thenReturn(authResponse);

        boolean result = authorizationModel.authorize("login", "password");

        assertTrue(result);

        verify(selfRepositoryMock).setMyself(authResponse.getUser());
        verify(selfRepositoryMock).setCompany(authResponse.getCompany());
        verify(authApiMock).setHandlerAuthToken(authResponse.getToken());
    }
}
