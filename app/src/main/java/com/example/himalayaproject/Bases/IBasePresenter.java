package com.example.himalayaproject.Bases;

public interface IBasePresenter<T> {
    void registViewCallback(T t);
    void unRegistViewCallback(T t);
}
