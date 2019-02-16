package org.glavo.nbt.gui;

import javafx.scene.control.TabPane;
import javafx.scene.input.TransferMode;

public final class NBTTabPane extends TabPane {
    private static NBTTab currentDraggingTab;

    final NBTEditorApp app;

    public NBTTabPane(NBTEditorApp app) {
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


    public synchronized static NBTTab getCurrentDraggingTab() {
        return currentDraggingTab;
    }

    public synchronized static void setCurrentDraggingTab(NBTTab currentDraggingTab) {
        NBTTabPane.currentDraggingTab = currentDraggingTab;
    }
}
