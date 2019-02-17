package org.glavo.nbt.gui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import org.glavo.nbt.util.IOHelper;
import org.glavo.nbt.util.Resources;

import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

public final class NBTEditorApp extends Application {

    public static final String APP_NAME = "CraftEditor";

    public static final String VERSION = Resources.manifest.getMainAttributes().getValue(APP_NAME + "-Version");

    static final ObservableList<NBTEditorApp> apps =
            FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

    public static final int DEFAULT_WIDTH;
    public static final int DEFAULT_HEIGHT;

    static {
        DisplayMode mode = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDisplayMode();

        if (mode.getWidth() >= 2880 && mode.getHeight() >= 1800) {
            DEFAULT_WIDTH = 1920;
            DEFAULT_HEIGHT = 750;
        } else {
            DEFAULT_WIDTH = 750;
            DEFAULT_HEIGHT = 500;
        }
    }

    private Stage stage;

    public final StringProperty titleProperty = new SimpleStringProperty();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Settings.Global();
        this.stage = primaryStage;

        NBTEditorPane pane = new NBTEditorPane(this);
        Scene scene = new Scene(pane, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        enableDragAndDrop(scene);
        primaryStage.setScene(scene);
        Settings.applySettingsFor(stage);
        primaryStage.titleProperty().bind(
                Bindings.createStringBinding(
                        () -> titleProperty.getValue() == null ? APP_NAME : APP_NAME + " - " + titleProperty.getValue(),
                        titleProperty
                )
        );
        primaryStage.setOnShowing(event -> {
            apps.add(this);
        });
        primaryStage.setOnCloseRequest(event -> apps.remove(this));
        primaryStage.show();
    }

    public void openFiles(List<? extends Path> p) {
        NBTTabPane tabPane = getTabPane();
        int size = p.size();
        NBTTab[] tabs = new NBTTab[size];
        for (int i = 0; i < size; i++) {
            tabs[i] = NBTTab.open(p.get(i));
        }
        tabPane.getTabs().addAll(tabs);
    }

    public Stage getStage() {
        return stage;
    }

    public NBTEditorPane getPane() {
        return (NBTEditorPane) this.stage.getScene().getRoot();
    }

    public NBTTabPane getTabPane() {
        return (NBTTabPane) getPane().getCenter();
    }

    private void enableDragAndDrop(Scene scene) {
        scene.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });

        // Dropping over surface
        scene.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;

                openFiles(IOHelper.mapFileList(db.getFiles()));
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
}
