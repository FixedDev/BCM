package me.fixeddev.bcm.basic;

// Tagging interface
public interface DelegatedCommand<T extends ICommand> {

    T getDelegate();
    void setDelegate(T delegate);
}
