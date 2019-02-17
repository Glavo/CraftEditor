package org.glavo.nbt.gui;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import org.controlsfx.dialog.ExceptionDialog;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

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

    public final BooleanProperty refreshable = new SimpleBooleanProperty(false);

    public void refresh() {
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

    public static NBTTab open(Path path) {
        Objects.requireNonNull(path);
        NBTTab tab = new NBTTab(path.getFileName().toString());
        if (Files.isDirectory(path)) {
            // TODO
        } else {
            TreeView<NBTTree> tree = new TreeView<>();
            tree.setCellFactory(new NBTTreeCellFactory());

            ProgressIndicator progressIndicator = new ProgressIndicator();
            progressIndicator.setMaxWidth(75);
            progressIndicator.setMaxHeight(75);
            BorderPane pane = new BorderPane();
            pane.setCenter(progressIndicator);
            Task<Object> task = Task.of(() -> {
                try {
                    NBTTree t = NBTTree.buildTree(path);
                    Platform.runLater(() -> {
                        tree.setRoot(t);
                        tab.setContent(tree);
                    });
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                return null;
            });
            task.setOnFailed(event -> {
                tab.getTabPane().getTabs().remove(tab);
                Platform.runLater(() -> {
                    Throwable ex = event.getSource().getException();
                    if (ex instanceof NBTException) {
                        ((NBTException) ex).showDialog();
                    } else {
                        new ExceptionDialog(ex).showAndWait();
                    }
                });
            });
            task.startInNewThread();
            tab.setContent(pane);
        }
        return tab;
    }
}
