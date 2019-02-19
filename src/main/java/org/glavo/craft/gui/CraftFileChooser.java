package org.glavo.craft.gui;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.glavo.craft.util.Resources;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public final class CraftFileChooser {
    private static final ResourceBundle resources = Resources.findResourceBundle(CraftFileChooser.class);

    private static FileChooser fileChooser;
    private static DirectoryChooser directoryChooser;

    private static void initFileChooser() {
        fileChooser = new FileChooser();
        fileChooser.setTitle(resources.getString("FileChooser.Title"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        resources.getString("FileChooser.ExtensionFilters.All.Description"), "*.*"),
                new FileChooser.ExtensionFilter(
                        resources.getString("FileChooser.ExtensionFilters.All.Description"), "*.dat"),
                new FileChooser.ExtensionFilter(
                        resources.getString("FileChooser.ExtensionFilters.All.Description"), "*.dat")
        );
    }

    private static void initDirectoryChooser() {
        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(resources.getString("DirectoryChooser.Title"));
    }

    public static List<Path> chooseFiles(Window owner) {
        if (fileChooser == null) {
            initFileChooser();
        }
        List<File> files = fileChooser.showOpenMultipleDialog(owner);
        if (files == null) {
            return null;
        }
        int size = files.size();
        Path[] arr = new Path[size];
        for (int i = 0; i < size; i++) {
            arr[i] = files.get(i).toPath();
        }
        return Arrays.asList(arr);
    }

    public static Path chooseFolder(Window owner) {
        if (directoryChooser == null) {
            initDirectoryChooser();
        }
        File file = directoryChooser.showDialog(owner);
        return file == null ? null : file.toPath();
    }
}
