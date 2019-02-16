package org.glavo.nbt.gui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class NBTEditorApp extends Application {
    static final ObservableList<NBTEditorApp> apps =
            FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

    private Stage stage;
    private ChangeListener<String> listener = (observable, oldValue, newValue) -> {
        stage.getScene().getStylesheets().remove(oldValue);
        stage.getScene().getStylesheets().add(newValue);
    };

    @Override
    public void start(Stage primaryStage) throws Exception {
        Settings.Global();
        this.stage = primaryStage;

        NBTEditorPane pane = new NBTEditorPane(this);
        Scene scene = new Scene(pane, 500, 500);
        scene.getStylesheets().add(Settings.CssUrl.getValue());
        Settings.CssUrl.addListener(new WeakChangeListener<>(listener));
        primaryStage.setScene(scene);
        primaryStage.setOnShowing(event -> {
            apps.add(this);
        });
        primaryStage.setOnCloseRequest(event -> apps.remove(this));
        primaryStage.show();
    }

    public Stage getStage() {
        return stage;
    }
}
