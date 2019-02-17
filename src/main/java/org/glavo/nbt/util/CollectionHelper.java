package org.glavo.nbt.util;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;

import java.util.ArrayList;
import java.util.function.Function;

public final class CollectionHelper {
    private CollectionHelper() {
    }

    /**
     * Store the return value to avoid the listener being recycled
     */
    public static <T, R> ListChangeListener<T> map(
            ObservableList<? extends T> source, ObservableList<? super R> target,
            Function<? super T, ? extends R> mapper,
            int targetOffset) {
        final ListChangeListener<T> listener = c -> {
            while (c.next()) {
                if (c.wasPermutated()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        target.set(
                                i + targetOffset,
                                mapper.apply(source.get(i))
                        );
                    }
                } else if (c.wasRemoved()) {
                    target.remove(
                            c.getFrom() + targetOffset,
                            c.getTo() + targetOffset);
                } else if (c.wasAdded()) {
                    ArrayList<R> l = new ArrayList<>();
                    for (T v : c.getAddedSubList()) {
                        l.add(mapper.apply(v));
                    }
                    target.addAll(c.getFrom() + targetOffset, l);
                }
            }
        };
        source.addListener(new WeakListChangeListener<>(listener));
        for (T e : source) {
            target.add(mapper.apply(e));
        }
        return listener;
    }

    public static <T, R> ListChangeListener<T> mapToEnd(
            ObservableList<? extends T> source, ObservableList<? super R> target,
            Function<? super T, ? extends R> mapper) {
        return map(source, target, mapper, target.size());
    }
}
