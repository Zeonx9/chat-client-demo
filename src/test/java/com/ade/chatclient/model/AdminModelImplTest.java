package com.ade.chatclient.model;

import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.RegisterData;
import com.ade.chatclient.model.impl.AdminModelImpl;
import com.ade.chatclient.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class AdminModelImplTest {
    private AdminModelImpl adminModel;
    private AdminRepository adminRepositoryMock;

    @BeforeEach
    public void setUp() {
        adminRepositoryMock = Mockito.mock(AdminRepository.class);
        adminModel = new AdminModelImpl(adminRepositoryMock);
    }

    @Test
    public void testRegisterUser() {
        AuthRequest expectedAuthRequest = AuthRequest.builder().login("username").login("password").build();
        when(adminRepositoryMock.registerUser(any(RegisterData.class))).thenReturn(expectedAuthRequest);

        RegisterData registerData = RegisterData.builder().authRequest(expectedAuthRequest).build();
        AuthRequest result = adminModel.registerUser(registerData);

        assertEquals(expectedAuthRequest, result);
        verify(adminRepositoryMock).registerUser(registerData);
    }

    @Test
    public void testGetMyself() {
        User expectedUser = User.builder().username("login").build();
        when(adminRepositoryMock.getMyself()).thenReturn(expectedUser);

        User result = adminModel.getMyself();

        assertEquals(expectedUser, result);
    }

    @Test
    public void testGetCompany() {
        Company expectedCompany = Company.builder().id(1L).name("company").build();
        when(adminRepositoryMock.getCompany()).thenReturn(expectedCompany);

        Company result = adminModel.getCompany();

        assertEquals(expectedCompany, result);
    }

    @Test
    public void testClearModel() {
        adminModel.clearModel();

        verify(adminRepositoryMock).clear();
    }
}
