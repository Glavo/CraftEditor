package org.glavo.nbt.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class NBTTab extends Tab {
    private final Label label = new Label();

    public NBTTab() {
        this(null);
    }

    public NBTTab(String text) {
        this(text, null);
    }

    public NBTTab(String text, Node content) {
        this.getStyleClass().add(Settings.UI_CSS_CLASS);
        this.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                throw new AssertionError("Cannot set NBTTab.text");
            }
        });
        label.setText(text);

        //
        // https://stackoverflow.com/questions/41473987/how-to-drag-and-drop-tabs-of-the-same-tabpane
        //
        label.setOnDragDetected(e -> {
            Dragboard dragboard = label.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent c = new ClipboardContent();
            c.putString(this.toString());
            dragboard.setContent(c);
            dragboard.setDragView(label.snapshot(null, null));
            NBTTabPane.setCurrentDraggingTab(this);
        });
        label.setOnDragOver(e -> {
            if (NBTTabPane.getCurrentDraggingTab() != null &&
                    NBTTabPane.getCurrentDraggingTab().getGraphic() != label) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });
        label.setOnDragDropped(e -> {
            NBTTab currentDraggingTab = NBTTabPane.getCurrentDraggingTab();
            if (currentDraggingTab != null &&
                    currentDraggingTab.getGraphic() != label) {
                ObservableList<Tab> tabs = this.getTabPane().getTabs();
                int index = tabs.indexOf(this);
                NBTTabPane.getCurrentDraggingTab().getTabPane().getTabs().remove(currentDraggingTab);
                tabs.add(index, currentDraggingTab);
                currentDraggingTab.getTabPane().getSelectionModel().select(currentDraggingTab);
                e.consume();
            }
        });
        label.setOnDragDone(e -> {
            NBTTabPane.setCurrentDraggingTab(null);
            e.getDragboard().clear();
        });
        this.setGraphic(label);
    }

    public Node getNBTTabGraphic() {
        return this.label.getGraphic();
    }

    public void setNBTTabGraphic(Node value) {
        this.label.setGraphic(value);
    }

    public String getNBTTabText() {
        return this.label.getText();
    }

    public void setNBTTabText(String value) {
        this.label.setText(value);
    }
}
