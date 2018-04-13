package eu.dkaratzas.starwarspedia.api;

public interface StarWarsApiCallback<T> {
    void onResponse(T result);

    void onCancel();
}