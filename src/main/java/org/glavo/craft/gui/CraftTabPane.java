package org.glavo.craft.gui;

import javafx.scene.control.TabPane;
import javafx.scene.input.TransferMode;

public final class CraftTabPane extends TabPane {
    private static CraftTab currentDraggingTab;

    final CraftEditorApp app;

    public CraftTabPane(CraftEditorApp app) {
        this.app = app;
        this.getStyleClass().add(Settings.UI_CSS_CLASS);
        this.setOnDragOver(e -> {
            if (currentDraggingTab != null &&
                    currentDraggingTab.getTabPane() != this) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });

        this.setOnDragDropped(e -> {
            if (currentDraggingTab != null &&
                    currentDraggingTab.getTabPane() != this) {
                currentDraggingTab.getTabPane().getTabs().remove(currentDraggingTab);
                this.getTabs().add(currentDraggingTab);
                this.getSelectionModel().select(currentDraggingTab);
            }
        });
    }


    public synchronized static CraftTab getCurrentDraggingTab() {
        return currentDraggingTab;
    }

    public synchronized static void setCurrentDraggingTab(CraftTab currentDraggingTab) {
        CraftTabPane.currentDraggingTab = currentDraggingTab;
    }
}
