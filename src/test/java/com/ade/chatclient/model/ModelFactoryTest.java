package com.ade.chatclient.model;

import com.ade.chatclient.application.RequestHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ModelFactoryTest {


    @Mock RequestHandler handler;
    private ModelFactory underTest;

    @BeforeEach
    void setUp() {
        underTest = new ModelFactory("str");
    }

    @Test
    void getModel() {
        //given

        //when
        underTest.getModel();

        //then
        assertThat(underTest.getModel()).isEqualTo(new ClientModelManager(handler));
    }
}