package com.ade.chatclient.application.structure;

/**
 * Класс от которого наследутся все классы слоя Вью, включая фрамгменты.
 * @param <VM> - определяет связаунную с этим вью вью-модель.
 */
public abstract class AbstractView<VM extends AbstractViewModel<?>> {
    protected VM viewModel;

    /**
     * метод, который должен быть вызван после конструктора вью (его вызывает система)
     * внутри него должен проходить binding property
     * @param viewModel ссылка на управляющую вью-модель
     */
    public final void init(VM viewModel) {
        this.viewModel = viewModel;
        initialize();
    }

    /**
     * этот метод инициализирует специфическое для подкласса состояние, и выполняет binding
     */
    protected abstract void initialize();

    public final VM getViewModel() {
        return viewModel;
    }
}
