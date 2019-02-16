package org.glavo.nbt.gui;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public final class NBTEditorPane extends BorderPane {
    final NBTEditorApp app;

    public NBTEditorPane(NBTEditorApp app) {
        this.app = app;
        this.getStyleClass().add(Settings.UI_CSS_CLASS);

        NBTMenuBar menuBar = new NBTMenuBar(app);
        VBox topBar = new VBox(menuBar);

        NBTTabPane tabPane = new NBTTabPane(app);

        this.setTop(topBar);
        this.setCenter(tabPane);
    }
}
