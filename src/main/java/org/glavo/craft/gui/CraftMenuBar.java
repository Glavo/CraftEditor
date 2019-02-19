package org.glavo.craft.gui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.HyperlinkLabel;
import org.glavo.craft.util.CollectionHelper;
import org.glavo.craft.util.IOHelper;
import org.glavo.craft.util.NodeHelper;
import org.glavo.craft.util.Resources;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static org.glavo.craft.util.Resources.findImage;

public final class CraftMenuBar extends MenuBar {
    private static final ResourceBundle resources = Resources.findResourceBundle(CraftMenuBar.class);

    final CraftEditorApp app;

    public CraftMenuBar(CraftEditorApp app) {
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

    private class RecentFileItem extends MenuItem {
        private final Path path;

        RecentFileItem(Path path) {
            Objects.requireNonNull(path);
            this.path = path;
            this.setText(IOHelper.pathToURIString(path));
            this.setOnAction(event -> {
                if (Files.notExists(path)) {
                    Dialog<ButtonType> notFound = new Dialog<>();
                    notFound.setHeaderText(resources.getString("FileMenu.Items.OpenRecent.NotFoundDialog.HeaderText"));
                    notFound.setContentText(String.format(resources.getString("FileMenu.Items.OpenRecent.NotFoundDialog.ContextText"), path));

                    ButtonType okButton =
                            new ButtonType(
                                    resources.getString("FileMenu.Items.OpenRecent.NotFoundDialog.ButtonTypes.Ok.Name"),
                                    ButtonBar.ButtonData.OK_DONE
                            );
                    ButtonType removeButton =
                            new ButtonType(resources.getString("FileMenu.Items.OpenRecent.NotFoundDialog.ButtonTypes.RemoveFromList.Name"),
                                    ButtonBar.ButtonData.FINISH
                            );

                    notFound.getDialogPane().getButtonTypes().addAll(okButton, removeButton);

                    notFound.showAndWait().ifPresent(t -> {
                        if (t == removeButton) {
                            RecentFiles.remove(path);
                        }
                    });
                }
                //TODO
            });
        }
    }

    private void addFileMenu() {
        Menu menu = new Menu(resources.getString("FileMenu.Name"));

        /*
         * OpenFile Item
         */
        MenuItem openFileItem = new MenuItem(resources.getString("FileMenu.Items.OpenFile.Name"));
        openFileItem.setAccelerator(OpenFileAccelerator);
        openFileItem.setOnAction(event -> {
            List<Path> paths = CraftFileChooser.chooseFiles(app.getStage());
            if (paths != null) {
                app.openFiles(paths);
            }
        });

        /*
         * OpenFolder Item
         */
        MenuItem openFolderItem = new MenuItem(resources.getString("FileMenu.Items.OpenFolder.Name")); // TODO
        openFolderItem.setAccelerator(OpenFolderAccelerator);

        /*
         * OpenRecent Item
         */
        Menu openRecent = new Menu(resources.getString("FileMenu.Items.OpenRecent.Name")); // TODO
        ListChangeListener<Path> l = CollectionHelper.mapToEnd(
                RecentFiles.recentFiles, openRecent.getItems(),
                RecentFileItem::new
        );
        NodeHelper.save(this, l);

        /*
         * Settings Item
         */
        MenuItem settingsItem = new MenuItem(resources.getString("FileMenu.Items.Settings.Name"));
        settingsItem.setAccelerator(SettingsAccelerator);
        settingsItem.setOnAction(event -> CraftSettingsDialog.dialog().showAndWait());

        /*
         * Exit Item
         */
        MenuItem exitItem = new MenuItem(resources.getString("FileMenu.Items.Exit.Name")); // TODO
        exitItem.setOnAction(event -> Platform.exit());

        menu.getItems().addAll(
                openFileItem, openFolderItem, openRecent,
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
    private static final Image BulletIcon = findImage("bullet.png");

    private static final KeyCombination NextWindowAccelerator = KeyCombination.keyCombination("Shortcut+Alt+]");
    private static final KeyCombination PreviousWindowAccelerator = KeyCombination.keyCombination("Shortcut+Alt+[");

    private static class WindowItem extends MenuItem {
        private final CraftEditorApp theApp;

        private WindowItem(CraftEditorApp theApp, CraftEditorApp app) {
            this.theApp = theApp;
            this.textProperty().bind(Bindings.createStringBinding(
                    () -> theApp.titleProperty.getValue() == null ?
                            resources.getString("WindowMenu.Items.EmptyWindowItem.Name") :
                            theApp.titleProperty.getValue(),
                    theApp.titleProperty
            ));
            if (theApp == app) {
                this.setGraphic(new ImageView(BulletIcon));
            }
        }

    }

    private void addWindowMenu() {
        Menu menu = new Menu(resources.getString("WindowMenu.Name"));
        /*
         * New Window Item
         */
        MenuItem newWindowItem = new MenuItem(resources.getString("WindowMenu.Items.NewWindow.Name"));
        newWindowItem.setOnAction(event -> {
            try {
                new CraftEditorApp().start(new Stage());
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
        MenuItem nextWindowItem = new MenuItem(resources.getString("WindowMenu.Items.NextWindow.Name"));
        nextWindowItem.setAccelerator(NextWindowAccelerator);
        nextWindowItem.setOnAction(event -> {
            ObservableList<CraftEditorApp> apps = CraftEditorApp.apps;
            assert CraftEditorApp.apps.size() > 0;
            int i = apps.indexOf(app);
            if (apps.size() > i + 1) {
                apps.get(i + 1).getStage().toFront();
            } else {
                apps.get(0).getStage().toFront();
            }
        });


        /*
         * Previous Window Item
         */
        MenuItem previousWindowItem = new MenuItem(resources.getString("WindowMenu.Items.PreviousWindow.Name"));
        previousWindowItem.setAccelerator(PreviousWindowAccelerator);
        previousWindowItem.setOnAction(event -> {
            ObservableList<CraftEditorApp> apps = CraftEditorApp.apps;
            assert CraftEditorApp.apps.size() > 0;
            int i = apps.indexOf(app);
            if (i == 0) {
                apps.get(apps.size() - 1).getStage().toFront();
            } else {
                apps.get(i - 1).getStage().toFront();
            }
        });

        ListChangeListener<CraftEditorApp> l = c -> {
            if (CraftEditorApp.apps.size() == 1) {
                nextWindowItem.setDisable(true);
                previousWindowItem.setDisable(true);
            } else {
                nextWindowItem.setDisable(false);
                previousWindowItem.setDisable(false);
            }
        };
        if (CraftEditorApp.apps.size() == 1) {
            nextWindowItem.setDisable(true);
            previousWindowItem.setDisable(true);
        } else {
            nextWindowItem.setDisable(false);
            previousWindowItem.setDisable(false);
        }
        CraftEditorApp.apps.addListener(new WeakListChangeListener<>(l));
        NodeHelper.save(this, l);

        menu.getItems().addAll(
                newWindowItem, closeWindowItem,
                new SeparatorMenuItem(),
                nextWindowItem, previousWindowItem,
                new SeparatorMenuItem()
        );
        CollectionHelper.mapToEnd(
                CraftEditorApp.apps, menu.getItems(),
                a -> new WindowItem(a, app)
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

            HyperlinkLabel text = new HyperlinkLabel(
                    String.format(resources.getString("HelpMenu.Items.About.InfoText"), CraftEditorApp.VERSION)
            );
            text.setOnAction(e -> {
                String link = ((Hyperlink) e.getSource()).getText();
                if (link != null) {
                    app.getHostServices().showDocument(link);
                }
            });
            pane.setCenter(text);

            Scene scene = new Scene(pane);
            scene.setFill(Color.TRANSPARENT);

            stage.setScene(scene);
            stage.setTitle("About");
            Settings.applySettingsFor(stage);
            stage.show();
        });

        menu.getItems().addAll(aboutItem);

        this.getMenus().add(menu);
    }
}
