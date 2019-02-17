package org.glavo.nbt.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import org.glavo.nbt.TagType;
import org.glavo.nbt.io.NBTReader;
import org.glavo.nbt.util.Resources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.ResourceBundle;

public class NBTTree extends TreeItem<NBTTree> {
    public final ObjectProperty<TagType> typeProperty = new SimpleObjectProperty<>();

    public final StringProperty nameProperty = new SimpleStringProperty("");

    public final ObjectProperty<Object> valueProperty = new SimpleObjectProperty<>();

    public final ObservableBooleanValue hasChildren = Bindings.createBooleanBinding(
            () -> typeProperty.get() == null || typeProperty.get().hasChildren,
            typeProperty
    );

    public final ObservableStringValue text = Bindings.createStringBinding(
            () -> {
                TagType type = typeProperty.getValue();
                String name = nameProperty.getValue();
                Object value = valueProperty.getValue();
                if (type == null) {
                    return "";
                }
                if (name == null) {
                    name = String.valueOf(this.getParent().getChildren().indexOf(this));
                }
                if (type.hasChildren) {
                    return name; // TODO
                }
                if (value instanceof String) {
                    return name + ": \"" + value + "\""; // TODO
                }
                if (value instanceof byte[] || value instanceof int[] || value instanceof char[]) {
                    return name; // TODO
                }
                return name + ": " + value;
            },
            nameProperty, valueProperty, getChildren()
    );

    public NBTTree() {
        setValue(this);
        this.graphicProperty().bind(Bindings.createObjectBinding(
                () -> typeProperty.get() == null ? null : new ImageView(typeProperty.get().icon), typeProperty)
        );
    }

    public static NBTTree buildTree(Path path) throws IOException {
        try (NBTReader reader = NBTReader.open(path)) {
            NBTTree tree = buildTree(reader);
            int i = reader.read();
            if (i == -1) {
                tree.setExpanded(true);
                return tree;
            }
            NBTTree root = new NBTTree();
            root.getChildren().add(tree);

            do {
                TagType tag = TagType.tagOf((byte) i);
                if (tag == null) {
                    throw new IllegalFileFormatException();
                }
                root.getChildren().add(buildTree(tag, reader));
            } while ((i = reader.read()) != -1);
            root.setExpanded(true);
            return root;
        }
    }

    public static NBTTree buildTree(NBTReader reader) {
        try {
            TagType tag = reader.readTag();
            if (tag == null) {
                throw new IllegalFileFormatException();
            }
            return buildTree(tag, reader);
        } catch (IOException e) {
            IllegalFileFormatException ex = new IllegalFileFormatException();
            ex.initCause(e);
            throw ex;
        }
    }

    public static NBTTree buildTree(TagType tag, NBTReader reader) throws IOException {
        if (tag == TagType.TAG_End) {
            return null;
        }

        return buildTreeWithName(tag, reader.readUTF(), reader);
    }

    public static NBTTree buildTreeWithName(TagType tag, String name, NBTReader reader) throws IOException {
        NBTTree tree = new NBTTree();
        tree.typeProperty.setValue(tag);
        switch (tag) {
            case TAG_End:
                return null;
            case TAG_Byte:
                tree.nameProperty.setValue(name);
                tree.valueProperty.setValue(reader.readByte());
                break;
            case TAG_Short:
                tree.nameProperty.setValue(name);
                tree.valueProperty.setValue(reader.readShort());
                break;
            case TAG_Int:
                tree.nameProperty.setValue(name);
                tree.valueProperty.setValue(reader.readInt());
                break;
            case TAG_Long:
                tree.nameProperty.setValue(name);
                tree.valueProperty.setValue(reader.readLong());
                break;
            case TAG_Float:
                tree.nameProperty.setValue(name);
                tree.valueProperty.setValue(reader.readFloat());
                break;
            case TAG_Double:
                tree.nameProperty.setValue(name);
                tree.valueProperty.setValue(reader.readDouble());
                break;
            case TAG_Byte_Array:
                tree.nameProperty.setValue(name);
                int s0 = reader.readInt();
                byte[] arr0 = new byte[s0];
                for (int i = 0; i < s0; i++) {
                    arr0[i] = reader.readByte();
                }
                tree.valueProperty.setValue(arr0);
                break;
            case TAG_String:
                tree.nameProperty.setValue(name);
                tree.valueProperty.setValue(reader.readUTF());
                break;
            case TAG_List:
                tree.nameProperty.setValue(name);
                TagType elemTag = reader.readTag();
                if (elemTag == null) {
                    throw new IllegalFileFormatException();
                }
                int s1 = reader.readInt();
                NBTTree[] arr1 = new NBTTree[s1];
                for (int i = 0; i < s1; i++) {
                    arr1[i] = buildTreeWithName(elemTag, null, reader);
                }
                tree.getChildren().addAll(arr1);
                break;
            case TAG_Compound:
                tree.nameProperty.setValue(name);
                NBTTree t;
                while ((t = buildTree(reader)) != null) {
                    tree.getChildren().add(t);
                }
                break;
            case TAG_Int_Array:
                tree.nameProperty.setValue(name);
                int s2 = reader.readInt();
                int[] arr2 = new int[s2];
                for (int i = 0; i < s2; i++) {
                    arr2[i] = reader.readInt();
                }
                tree.valueProperty.setValue(arr2);
                break;
            case TAG_Long_Array:
                tree.nameProperty.setValue(name);
                int s3 = reader.readInt();
                long[] arr3 = new long[s3];
                for (int i = 0; i < s3; i++) {
                    arr3[i] = reader.readInt();
                }
                tree.valueProperty.setValue(arr3);
                break;
        }
        return tree;
    }

    @Override
    public String toString() {
        return text.getValue();
    }

}

class NBTTreeCellFactory implements Callback<TreeView<NBTTree>, TreeCell<NBTTree>> {
    public static final DataFormat FORMAT = new DataFormat("application/craft-editor-nbt-tree-item");
    private static final String DROP_HINT_STYLE = "-fx-border-color: #eea82f; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 3";

    private TreeCell<NBTTree> dropZone = null;
    private NBTTree draggedItem = null;

    @Override
    public TreeCell<NBTTree> call(TreeView<NBTTree> treeView) {
        TreeCell<NBTTree> cell = new TreeCell<NBTTree>() {
            @Override
            protected void updateItem(NBTTree item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    graphicProperty().unbind();
                    textProperty().unbind();
                    setGraphic(null);
                    setText(null);
                } else {
                    graphicProperty().bind(item.graphicProperty());
                    textProperty().bind(item.text);
                }
            }
        };

        cell.setOnDragDetected(event -> {
            draggedItem = (NBTTree) cell.getTreeItem();
            if (draggedItem.getParent() == null) {
                return;
            }
            Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(FORMAT, draggedItem);
            db.setContent(content);
            db.setDragView(cell.snapshot(null, null));
            event.consume();
        });
        cell.setOnDragOver(event -> {
            if (!event.getDragboard().hasContent(FORMAT)) {
                return;
            }
            NBTTree item = (NBTTree) cell.getTreeItem();
            if (draggedItem == null || item == null || draggedItem == item) {
                return;
            }
            if (draggedItem.getParent() == null) {
                if (dropZone != null) {
                    dropZone.setStyle("");
                }
                return;
            }

            event.acceptTransferModes(TransferMode.MOVE);
            if (!Objects.equals(dropZone, cell)) {
                if (dropZone != null) {
                    dropZone.setStyle("");
                }
                this.dropZone = cell;
                dropZone.setStyle(DROP_HINT_STYLE);
            }
        });
        cell.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (!db.hasContent(FORMAT)) {
                return;
            }

            NBTTree item = (NBTTree) cell.getTreeItem();
            NBTTree droppedItemParent = (NBTTree) draggedItem.getParent();

            droppedItemParent.getChildren().remove(draggedItem);

            if (Objects.equals(droppedItemParent, item)) {
                item.getChildren().add(0, draggedItem);
                treeView.getSelectionModel().select(draggedItem);
            } else {
                int indexInParent = item.getParent().getChildren().indexOf(item);
                item.getParent().getChildren().add(indexInParent + 1, draggedItem);
            }
            treeView.getSelectionModel().select(draggedItem);
            event.setDropCompleted(true);
        });
        cell.setOnDragDone(event -> {
            if (dropZone != null) {
                dropZone.setStyle("");
            }
        });
        return cell;
    }
}

class IllegalFileFormatException extends NBTException {
    private static final ResourceBundle resources = Resources.findResourceBundle(IllegalFileFormatException.class);

    @Override
    public void showDialog() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(resources.getString("Dialog.Title"));
        alert.setHeaderText(resources.getString("Dialog.HeaderText"));
        alert.setContentText(resources.getString("Dialog.ContentText"));
        alert.showAndWait();
    }
}