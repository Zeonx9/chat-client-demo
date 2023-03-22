package com.ade.chatclient.application;

public class AbstractView<VM extends AbstractViewModel<?>> {
    protected VM viewModel;

    /**
     * метод, который должен быть вызван после конструктора вью (его вызывает система)
     * внутри него должен проходить binding property
     * @param viewModel ссылка на управляющую вью-модель
     */
    public void init(VM viewModel) {
        this.viewModel = viewModel;
    }

    public VM getViewModel() {
        return viewModel;
    }
}
