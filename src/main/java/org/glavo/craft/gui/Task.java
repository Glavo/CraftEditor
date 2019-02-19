package org.glavo.craft.gui;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Task<T> extends javafx.concurrent.Task<T> {

    public static <T> Task<T> of(Supplier<T> f) {
        return new Task<T>() {
            @Override
            protected T call() {
                return f.get();
            }
        };
    }

    @SuppressWarnings("unchecked")
    public void onSucceeded(Consumer<? super T> callback) {
        Objects.requireNonNull(callback);
        super.setOnSucceeded(
                e -> callback.accept((T) e.getSource().getValue()));
    }

    public void onFailed(Consumer<? super Throwable> callback) {
        Objects.requireNonNull(callback);
        super.setOnFailed(event -> {
            Throwable err = event.getSource().getException();
            callback.accept(err);
        });
    }

    public void startInNewThread() {
        new Thread(this).start();
    }
}