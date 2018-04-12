package eu.dkaratzas.starwarspedia.api;

public interface ApiCallback<T> {
    void onResponse(T result);

    void onCancel();
}