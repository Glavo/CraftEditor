package org.glavo.nbt.util;

import javafx.scene.image.Image;
import org.glavo.nbt.gui.NBTEditorApp;
import org.glavo.nbt.gui.Settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.jar.Manifest;

public final class Resources {
    public static final Path HomePath;
    public static final Path SettingsPath;

    public static final Manifest manifest;

    public static ResourceBundle findResourceBundle(Class<?> cls) {
        return ResourceBundle.getBundle(cls.getName());
    }

    public static Image findImage(String path) {
        return new Image(NBTEditorApp.class.getResource("icons/" + path).toExternalForm());
    }

    public static URL find(String path) {
        return NBTEditorApp.class.getResource(path);
    }

    public static InputStream open(String path) {
        return Resources.class.getResourceAsStream(path);
    }

    static {
        Path userHome = Paths.get(System.getProperty("user.home"));
        HomePath = userHome.resolve(".CraftEditor").toAbsolutePath();
        if (Files.notExists(HomePath)) {
            try {
                Files.createDirectories(HomePath);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        SettingsPath = HomePath.resolve("setting.properties");
        try {
            if (Files.notExists(SettingsPath)) {
                Files.createFile(SettingsPath);
                try (Writer writer = Files.newBufferedWriter(SettingsPath)) {
                    Settings.Default.saveTo(writer);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        try (InputStream in = open("/META-INF/MANIFEST.MF")) {
            manifest = new Manifest(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
