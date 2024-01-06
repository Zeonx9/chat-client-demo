package com.ade.chatclient.application;

import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.model.ClientModelImpl;
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

    /**
     * Пока что позволяет получить только одну модель, и реализует "ленивую загрузку",
     * что значит создается только один объект
     * @return модель для приложения
     */
    public ClientModel getModel() {
        if (clientModel == null) {
            clientModel = new ClientModelImpl(
                    apiFactory.getRequestHandler(),
                    apiFactory.provideAuthorizationApi(),
                    apiFactory.provideStompSessionApi(),
                    repositoryFactory.provideMessageRepository(),
                    repositoryFactory.provideChatRepository()
            );
        }
        return clientModel;
    }
}
