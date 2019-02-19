package org.glavo.craft.gui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.glavo.craft.util.IOHelper;
import org.glavo.craft.util.Resources;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

public final class RecentFiles {
    public static final ObservableList<Path> recentFiles =
            FXCollections.observableArrayList();

    public static final int RECENT_FINES_MAX = 20;

    static {
        recentFiles.addListener((ListChangeListener<Path>) c -> {
            int size = recentFiles.size();
            if (size > RECENT_FINES_MAX) {
                recentFiles.remove(size - 1);
            }
        });
    }

    public static void add(Path p) {
        Objects.requireNonNull(p);
        recentFiles.add(0, p);
    }

    public static void remove(Path p) {
        recentFiles.remove(p);
    }

    public static void load() {
        load(Resources.RecentFilesPath);
    }

    public static void load(Path p) {
        try (Stream<String> list = Files.lines(p)) {
            list.filter(s -> s != null && !s.isEmpty())
                    .map(IOHelper::toPath)
                    .filter(Objects::nonNull)
                    .forEach(recentFiles::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        saveTo(Resources.RecentFilesPath);
    }

    public static void saveTo(Path p) {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(p))) {
            recentFiles.forEach(writer::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}















