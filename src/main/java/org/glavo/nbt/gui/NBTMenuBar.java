package org.glavo.nbt.gui;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import org.glavo.nbt.util.Resources;

import java.util.ResourceBundle;

public final class NBTMenuBar extends MenuBar {
    private static final ResourceBundle resources = Resources.findResourceBundle(NBTMenuBar.class);

    private static final KeyCombination OpenFileAccelerator = KeyCombination.keyCombination("Shortcut+O");
    private static final KeyCombination OpenFolderAccelerator = KeyCombination.keyCombination("Shortcut+K");
    private static final KeyCombination SettingsAccelerator = KeyCombination.keyCombination("Shortcut+Alt+S");

    final NBTEditorApp app;

    public NBTMenuBar(NBTEditorApp app) {
        this.app = app;
        this.getStyleClass().add(Settings.UI_CSS_CLASS);
        addFileMenu();
        addEditMenu();
    }

    private void addFileMenu() {
        Menu menu = new Menu(resources.getString("FileMenu.Name"));

        /*
         * OpenFile Item
         */
        MenuItem openFileItem = new MenuItem(resources.getString("FileMenu.Items.OpenFile.Name")); // TODO
        openFileItem.setAccelerator(OpenFileAccelerator);

        /*
         * OpenFolder Item
         */
        MenuItem openFolderItem = new MenuItem(resources.getString("FileMenu.Items.OpenFolder.Name")); // TODO
        openFolderItem.setAccelerator(OpenFolderAccelerator);

        /*
         * Settings Item
         */
        MenuItem settingsItem = new MenuItem(resources.getString("FileMenu.Items.Settings.Name"));
        settingsItem.setAccelerator(SettingsAccelerator);
        settingsItem.setOnAction(event -> NBTSettingsDialog.dialog().showAndWait());

        /*
         * Exit Item
         */
        MenuItem exitItem = new MenuItem(resources.getString("FileMenu.Items.Exit.Name")); // TODO
        exitItem.setOnAction(event -> Platform.exit());

        menu.getItems().addAll(
                openFileItem, openFolderItem,
                new SeparatorMenuItem(),
                settingsItem,
                new SeparatorMenuItem(),
                exitItem
        );
        this.getMenus().add(menu);
    }

    private void addEditMenu() {
        Menu menu = new Menu(resources.getString("EditMenu.Name"));

        // TODO

        this.getMenus().add(menu);
    }

    private void addWindowMenu() {
        Menu menu = new Menu(resources.getString("WindowMenu.Name"));

        // TODO

        this.getMenus().add(menu);
    }
}
