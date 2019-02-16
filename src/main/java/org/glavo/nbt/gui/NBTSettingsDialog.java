package org.glavo.nbt.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.BeanPropertyUtils;
import org.glavo.nbt.util.Resources;

import java.util.*;

public final class NBTSettingsDialog extends Stage {
    private static NBTSettingsDialog dialog;
    private static final ResourceBundle resources = Resources.findResourceBundle(NBTSettingsDialog.class);

    private ChangeListener<String> listener = (observable, oldValue, newValue) -> {
        this.getScene().getStylesheets().remove(oldValue);
        this.getScene().getStylesheets().add(newValue);
    };

    public abstract class SettingItem implements PropertySheet.Item {
        public final String propertyName;
        public final Class<?> type;
        public final String category;
        public final String name;
        public final String description;

        private SettingItem(String propertyName, Class<?> type, String category) {
            this.propertyName = propertyName;
            this.type = type;
            this.category = resources.getString("SettingItems.Category." + category);
            this.name = resources.getString("SettingItems." + propertyName + ".Name");
            this.description = resources.getString("SettingItems." + propertyName + ".Description");
        }

        public abstract void applyChange(Object newValue);

        @Override
        public Class<?> getType() {
            return type;
        }

        @Override
        public String getCategory() {
            return category;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public Optional<ObservableValue<? extends Object>> getObservableValue() {
            return Optional.empty();
        }

    }

    public final SettingItem UIFontItem = new SettingItem("UIFont", Font.class, "Font") {
        @Override
        public void applyChange(Object newValue) {
            Settings.Global().setUIFont((Font) newValue);
        }

        @Override
        public Object getValue() {
            return changeList.getOrDefault(UIFontItem, Settings.Global().getUIFont());
        }

        @Override
        public void setValue(Object value) {
            if (value == null) {
                value = FontHelper.DefaultUIFont;
            }

            if (value.equals(Settings.Global().getUIFont())) {
                changeList.remove(UIFontItem);
            } else {
                changeList.put(UIFontItem, value);
            }
        }
    };

    public final SettingItem TextFontItem = new SettingItem("TextFont", Font.class, "Font") {
        @Override
        public void applyChange(Object newValue) {
            Settings.Global().setTextFont((Font) newValue);
        }

        @Override
        public Object getValue() {
            return changeList.getOrDefault(TextFontItem, Settings.Global().getTextFont());
        }

        @Override
        public void setValue(Object value) {
            if (value == null) {
                value = FontHelper.DefaultTextFont;
            }

            if (value.equals(Settings.Global().getTextFont())) {
                changeList.remove(TextFontItem);
            } else {
                changeList.put(TextFontItem, value);
            }
        }
    };

    public static NBTSettingsDialog dialog() {
        if (dialog == null) {
            dialog = new NBTSettingsDialog();
        }
        return dialog;
    }

    private ObservableMap<SettingItem, Object> changeList =
            FXCollections.observableMap(new TreeMap<>(Comparator.comparing(SettingItem::toString)));

    private NBTSettingsDialog() {
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle(resources.getString("Title"));
        this.setOnCloseRequest(event -> {
            clearChange();
        });

        BorderPane root = new BorderPane();

        /*
         * PropertySheet
         */
        PropertySheet propertySheet = new PropertySheet(FXCollections.observableArrayList(
                UIFontItem, TextFontItem
        )); // TODO
        propertySheet.getStyleClass().add(Settings.UI_CSS_CLASS);
        root.setCenter(propertySheet);

        /*
         * ButtonBar
         */
        ButtonBar buttonBar = new ButtonBar();

        Button okButton = new Button(resources.getString("ButtonBar.Buttons.Ok.Text"));
        Button cancelButton = new Button(resources.getString("ButtonBar.Buttons.Cancel.Text"));
        Button applyButton = new Button(resources.getString("ButtonBar.Buttons.Apply.Text"));

        ButtonBar.setButtonData(okButton, ButtonBar.ButtonData.OK_DONE);
        ButtonBar.setButtonData(cancelButton, ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonBar.setButtonData(applyButton, ButtonBar.ButtonData.APPLY);

        okButton.setOnAction(event -> {
            commitChange();
            Settings.save();
            this.close();
        });
        cancelButton.setOnAction(event -> clearChange());
        applyButton.setOnAction(event -> {
            commitChange();
            Settings.save();
            clearChange();
        });

        okButton.setDisable(true);
        cancelButton.setDisable(true);
        applyButton.setDisable(true);

        changeList.addListener((MapChangeListener<SettingItem, Object>) change -> {
            if (changeList.isEmpty()) {
                okButton.setDisable(true);
                cancelButton.setDisable(true);
                applyButton.setDisable(true);
            } else {
                okButton.setDisable(false);
                cancelButton.setDisable(false);
                applyButton.setDisable(false);
            }
        });

        Settings.addUIStyleClass(buttonBar, okButton, cancelButton, applyButton);

        buttonBar.getButtons().addAll(okButton, cancelButton, applyButton);

        root.setBottom(buttonBar);

        /*
         * Scene
         */
        Scene scene = new Scene(root, 500, 500);//TODO
        scene.getStylesheets().add(Settings.CssUrl.getValue());
        Settings.CssUrl.addListener(new WeakChangeListener<>(listener));
        this.setScene(scene);
    }

    public void commitChange() {
        changeList.forEach(SettingItem::applyChange);
    }

    public void clearChange() {
        this.changeList.clear();
    }
}
