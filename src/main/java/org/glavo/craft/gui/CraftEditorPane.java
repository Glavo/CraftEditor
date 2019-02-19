package org.glavo.craft.gui;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public final class CraftEditorPane extends BorderPane {
    final CraftEditorApp app;

    public CraftEditorPane(CraftEditorApp app) {
        this.app = app;
        this.getStyleClass().add(Settings.UI_CSS_CLASS);

        CraftMenuBar menuBar = new CraftMenuBar(app);
        VBox topBar = new VBox(menuBar);

        CraftTabPane tabPane = new CraftTabPane(app);

        this.setTop(topBar);
        this.setCenter(tabPane);
    }
}
