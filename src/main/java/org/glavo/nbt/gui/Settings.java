package org.glavo.nbt.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.css.Styleable;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.glavo.nbt.util.Resources;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Observable;
import java.util.Properties;

public final class Settings implements Cloneable {
    private final Properties properties;

    public Settings(Properties properties) {
        this.properties = properties;
    }

    public void setProperty(String key, String value) {
        Objects.requireNonNull(key);
        if (key.startsWith("craftEditor.ui.font")) {
            uiFontCache = null;
        } else if (key.startsWith("craftEditor.text.font")) {
            textFontCache = null;
        }
        properties.setProperty(key, value);
    }

    private Font uiFontCache;

    public Font getUIFont() {
        if (uiFontCache == null) {
            String family = properties.getProperty("craftEditor.ui.font.family", FontHelper.DEFAULT_UI_FONT_FAMILY);
            String size = properties.getProperty("craftEditor.ui.font.size");
            String weight = properties.getProperty("craftEditor.ui.font.weight");

            double fontSize;

            try {
                fontSize = Double.parseDouble(size);
            } catch (NumberFormatException | NullPointerException ignored) {
                fontSize = FontHelper.DEFAULT_UI_FONT_SIZE;
            }

            FontWeight fontWeight;
            try {
                int w = Integer.parseInt(weight);
                fontWeight = FontWeight.findByWeight(w);
            } catch (NumberFormatException | NullPointerException ignored) {
                fontWeight = FontWeight.findByName(weight);
                if (fontWeight == null) {
                    fontWeight = FontHelper.DEFAULT_UI_FONT_WEIGHT;
                }
            }
            uiFontCache = Font.font(family, fontWeight, fontSize);
        }
        return uiFontCache;
    }

    public void setUIFont(Font font) {
        if (font == null) {
            uiFontCache = FontHelper.DefaultUIFont;
            properties.setProperty("craftEditor.ui.font.family", FontHelper.DEFAULT_UI_FONT_FAMILY);
            properties.setProperty("craftEditor.ui.font.size", Double.toString(FontHelper.DEFAULT_UI_FONT_SIZE));
            properties.setProperty("craftEditor.ui.font.weight", FontHelper.fontWeightToString(FontHelper.DEFAULT_UI_FONT_WEIGHT));
        } else {
            uiFontCache = font;
            properties.setProperty("craftEditor.ui.font.family", font.getFamily());
            properties.setProperty("craftEditor.ui.font.size", Double.toString(font.getSize()));
            properties.setProperty("craftEditor.ui.font.weight", font.getStyle());
        }
    }

    private Font textFontCache;

    public Font getTextFont() {
        if (textFontCache == null) {
            String family = properties.getProperty("craftEditor.text.font.family", FontHelper.DEFAULT_TEXT_FONT_FAMILY);
            String size = properties.getProperty("craftEditor.text.font.size");
            String weight = properties.getProperty("craftEditor.text.font.weight");

            double fontSize;

            try {
                fontSize = Double.parseDouble(size);
            } catch (NumberFormatException | NullPointerException ignored) {
                fontSize = FontHelper.DEFAULT_TEXT_FONT_SIZE;
            }


            FontWeight fontWeight;
            try {
                int w = Integer.parseInt(weight);
                fontWeight = FontWeight.findByWeight(w);
            } catch (NumberFormatException | NullPointerException ignored) {
                fontWeight = FontWeight.findByName(weight);
                if (fontWeight == null) {
                    fontWeight = FontHelper.DEFAULT_TEXT_FONT_WEIGHT;
                }
            }
            textFontCache = Font.font(family, fontWeight, fontSize);
        }
        return textFontCache;
    }

    public void setTextFont(Font font) {
        if (font == null) {
            textFontCache = FontHelper.DefaultTextFont;
            properties.setProperty("craftEditor.text.font.family", FontHelper.DEFAULT_TEXT_FONT_FAMILY);
            properties.setProperty("craftEditor.text.font.size", Double.toString(FontHelper.DEFAULT_TEXT_FONT_SIZE));
            properties.setProperty("craftEditor.text.font.weight", FontHelper.fontWeightToString(FontHelper.DEFAULT_TEXT_FONT_WEIGHT));
        } else {
            textFontCache = font;
            properties.setProperty("craftEditor.text.font.family", font.getFamily());
            properties.setProperty("craftEditor.text.font.size", Double.toString(font.getSize()));
            properties.setProperty("craftEditor.text.font.weight", font.getStyle());
        }
    }

    public void saveTo(Writer writer) throws IOException {
        properties.store(writer, "CraftEditor settings data");
    }

    public String saveToTempCss() throws IOException {
        Path css = Files.createTempFile("craft", ".css");
        try (Writer writer = Files.newBufferedWriter(css)) {
            buildCss(writer);
        }
        return URLDecoder.decode(css.toUri().toString(), "UTF-8");
    }

    public void buildCss(Writer writer) throws IOException {
        Font uiFont = getUIFont();
        Font textFont = getTextFont();
        writer.append('.').append(UI_CSS_CLASS).append(',')
                .append(".tooltip,")
                .append(".button")
                .append(" {\n")
                .append("    -fx-font-family: \"").append(uiFont.getFamily()).append("\";\n")
                .append("    -fx-font-size: ").append(Double.toString(uiFont.getSize())).append(";\n")
                .append("    -fx-font-weight: ").append(FontHelper.styleToCssWeight(uiFont.getStyle())).append(";\n")
                .append("}\n")
        ;
        writer.append('\n');
        writer.append('.').append(TEXT_CSS_CLASS).append(',')
                .append(".text-field,")
                .append(".text-area,")
                .append(".custom-text-field")
                .append(" {\n")
                .append("    -fx-font-family: \"").append(textFont.getFamily()).append("\";\n")
                .append("    -fx-font-size: ").append(Double.toString(textFont.getSize())).append(";\n")
                .append("    -fx-font-weight: ").append(FontHelper.styleToCssWeight(textFont.getStyle())).append(";\n")
                .append("}\n")
        ;
    }

    @Override
    public Settings clone() {
        Settings ans = new Settings((Properties) properties.clone());
        ans.uiFontCache = this.uiFontCache;
        ans.textFontCache = this.textFontCache;
        return ans;
    }

    public static final Settings Default = new Settings(new Properties());

    public static final String UI_CSS_CLASS = "craft-ui";
    public static final String TEXT_CSS_CLASS = "craft-text";

    public static StringProperty CssUrl = new SimpleStringProperty();

    private static Settings Global;

    public static Settings Global() {
        if (Global == null) {
            synchronized (Settings.class) {
                if (Global == null) {
                    try {
                        Global = loadFrom(Resources.SettingsPath);
                        CssUrl.setValue(Global.saveToTempCss());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }
        }

        return Global;
    }

    public static Settings loadFrom(Path p) throws IOException {
        if (Files.notExists(p)) {
            throw new FileNotFoundException(p + " (No such file or directory)");
        }
        if (Files.isDirectory(p)) {
            throw new FileNotFoundException(p + " (Is a directory)");
        }
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(p)) {
            properties.load(reader);
        }
        return new Settings(properties);
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(Resources.SettingsPath)) {
            Global().saveTo(writer);
            Files.deleteIfExists(Paths.get(URI.create(CssUrl.get())));
            CssUrl.set(Global().saveToTempCss());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void addUIStyleClass(Styleable s) {
        s.getStyleClass().addAll(UI_CSS_CLASS);
    }

    public static void addUIStyleClass(Styleable... s) {
        for (Styleable styleable : s) {
            styleable.getStyleClass().addAll(UI_CSS_CLASS);
        }
    }

    public static void addTextStyleClass(Styleable s) {
        s.getStyleClass().addAll(TEXT_CSS_CLASS);
    }

    public static void addTextStyleClass(Styleable... s) {
        for (Styleable styleable : s) {
            styleable.getStyleClass().addAll(TEXT_CSS_CLASS);
        }
    }
}
