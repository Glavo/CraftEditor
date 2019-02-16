package org.glavo.nbt.gui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.glavo.nbt.util.Resources;

import java.util.ResourceBundle;

public final class NBTMenuBar extends MenuBar {
    private static final ResourceBundle resources = Resources.findResourceBundle(NBTMenuBar.class);

    final NBTEditorApp app;

    public NBTMenuBar(NBTEditorApp app) {
        this.app = app;
        this.getStyleClass().add(Settings.UI_CSS_CLASS);
        addFileMenu();
        addEditMenu();
        addWindowMenu();
        addHelpMenu();
    }


    /*
     * File Menu
     */
    private static final KeyCombination OpenFileAccelerator = KeyCombination.keyCombination("Shortcut+O");
    private static final KeyCombination OpenFolderAccelerator = KeyCombination.keyCombination("Shortcut+K");
    private static final KeyCombination SettingsAccelerator = KeyCombination.keyCombination("Shortcut+Alt+S");

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

    /*
     * Window Menu
     */
    private void addWindowMenu() {
        Menu menu = new Menu(resources.getString("WindowMenu.Name"));
        /*
         * New Window Item
         */
        MenuItem newWindowItem = new MenuItem(resources.getString("WindowMenu.Items.NewWindow.Name"));
        newWindowItem.setOnAction(event -> {
            try {
                new NBTEditorApp().start(new Stage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        /*
         * Close Window Item
         */
        MenuItem closeWindowItem = new MenuItem(resources.getString("WindowMenu.Items.CloseWindow.Name"));
        closeWindowItem.setOnAction(event -> app.getStage().close());


        /*
         * Next Window Item
         */
        MenuItem nextWindowItem = new MenuItem(resources.getString("WindowMenu.Items.NextWindow.Name"));// TODO

        /*
         * Previous Window Item
         */
        MenuItem previousWindowItem = new MenuItem(resources.getString("WindowMenu.Items.PreviousWindow.Name"));// TODO

        menu.getItems().addAll(
                newWindowItem, closeWindowItem,
                new SeparatorMenuItem(),
                nextWindowItem, previousWindowItem
        );
        this.getMenus().add(menu);
    }

    private void addHelpMenu() {
        Menu menu = new Menu(resources.getString("HelpMenu.Name"));

        /*
         * About Item
         */
        MenuItem aboutItem = new MenuItem(resources.getString("HelpMenu.Items.About.Name"));// TODO
        aboutItem.setOnAction(event -> {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);

            BorderPane pane = new BorderPane();
            pane.setOnMouseClicked(e -> stage.close());

            Hyperlink text = new Hyperlink("AAAAA\nBBBBB");
            pane.setCenter(text);

            Scene scene = new Scene(pane);
            scene.setFill(Color.TRANSPARENT);

            stage.setScene(scene);
            stage.setTitle("About");
            stage.show();
        });

        menu.getItems().addAll(aboutItem);

        this.getMenus().add(menu);
    }
}
