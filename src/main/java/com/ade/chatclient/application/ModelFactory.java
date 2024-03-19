package com.ade.chatclient.application;

import com.ade.chatclient.model.*;
import com.ade.chatclient.model.impl.AdminModelImpl;
import com.ade.chatclient.model.impl.AuthorizationModelImpl;
import com.ade.chatclient.model.impl.ClientModelImpl;
import lombok.RequiredArgsConstructor;

/**
 * Фабрика для моделей в приложении, на данный момент в приложении всего одна модель, но со временем она может
 * усложниться и придется разбить на 2 модели или более,
 * этот класс будет предоставлять доступ к моделям для вью-моделей
 */
@RequiredArgsConstructor
public class ModelFactory {
    private final ApiFactory apiFactory;
    private final RepositoryFactory repositoryFactory;
    private ClientModel clientModel;
    private AuthorizationModel authorizationModel;
    private AdminModel adminModel;

    /**
     * Пока что позволяет получить только одну модель, и реализует "ленивую загрузку",
     * что значит создается только один объект
     * @return модель для приложения
     */
    public ClientModel getModel() {
        if (clientModel == null) {
            clientModel = new ClientModelImpl(
                    apiFactory.provideStompSessionApi(),
                    repositoryFactory.provideMessageRepository(),
                    repositoryFactory.provideChatRepository(),
                    repositoryFactory.provideUsersRepository(),
                    repositoryFactory.provideSelfRepository(),
                    repositoryFactory.provideFileRepository()
            );
        }
        return clientModel;
    }

    public AuthorizationModel getAuthorizationModel() {
        if (authorizationModel == null) {
            authorizationModel = new AuthorizationModelImpl(
                    apiFactory.provideAuthorizationApi(),
                    repositoryFactory.provideSelfRepository(),
                    repositoryFactory.provideAdminRepository()
            );
        }
        return authorizationModel;
    }

    public AdminModel getAdminModel() {
        if (adminModel == null) {
            adminModel = new AdminModelImpl(
                    repositoryFactory.provideAdminRepository()
            );
        }
        return adminModel;
    }
}
