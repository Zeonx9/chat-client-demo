package com.ade.chatclient.repository;

import com.ade.chatclient.api.AdminApi;
import com.ade.chatclient.application.ApiFactory;
import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.RegisterData;
import com.ade.chatclient.repository.impl.AdminRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminRepositoryImplTest {
    AdminRepository underTest;
    @Mock ApiFactory apiFactory;
    @Mock AdminApi adminApi;

    @BeforeEach
    void setUp() {
        when(apiFactory.provideAdminApi()).thenReturn(adminApi);
        underTest = new AdminRepositoryImpl(apiFactory.provideAdminApi());
        underTest.setCompany(Company.builder().id(1L).name("Company").build());
        underTest.setMyself(User.builder().id(1L).build());
    }

    @Test
    void registerUser() throws ExecutionException, InterruptedException {
        RegisterData testData = RegisterData.builder().surname("Dasha").realName("Vav").authRequest(AuthRequest.builder().login("dasha").build()).build();
        AuthRequest expectedResult = AuthRequest.builder().login("dasha").password("0000").build();
        when(adminApi.registerUser(testData)).thenReturn(expectedResult);

        AuthRequest result = underTest.registerUser(testData);

        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(adminApi).registerUser(testData);
    }

    @Test
    void clear() {
        underTest.clear();

        assertNull(underTest.getCompany());
        assertNull(underTest.getMyself());
    }
}
