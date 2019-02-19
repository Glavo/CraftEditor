package org.glavo.craft.gui;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Objects;

public final class FontHelper {
    public static final Font DefaultUIFont;
    public static final Font DefaultTextFont;


    public static final String DEFAULT_UI_FONT_FAMILY;
    public static final String DEFAULT_TEXT_FONT_FAMILY;

    public static final double DEFAULT_UI_FONT_SIZE;
    public static final double DEFAULT_TEXT_FONT_SIZE;

    public static final FontWeight DEFAULT_UI_FONT_WEIGHT;
    public static final FontWeight DEFAULT_TEXT_FONT_WEIGHT;

    public static String fontWeightToString(FontWeight weight) {
        Objects.requireNonNull(weight);
        switch (weight) {
            case THIN:
                return "Thin";
            case EXTRA_LIGHT:
                return "Extra Light";
            case LIGHT:
                return "Light";
            case NORMAL:
                return "Regular";
            case MEDIUM:
                return "Medium";
            case SEMI_BOLD:
                return "Semi Bold";
            case BOLD:
                return "Bold";
            case EXTRA_BOLD:
                return "Extra Bold";
            case BLACK:
                return "Black";
        }
        throw new AssertionError();
    }

    public static String styleToCssWeight(String style) {
        FontWeight w = FontWeight.findByName(style);
        return w == null ? style : Integer.toString(w.getWeight());
    }

    static {
        DefaultUIFont = Font.getDefault();
        DefaultTextFont = Font.getDefault();

        DEFAULT_UI_FONT_FAMILY = DefaultUIFont.getFamily();
        DEFAULT_TEXT_FONT_FAMILY = DefaultTextFont.getFamily();

        DEFAULT_UI_FONT_SIZE = DefaultUIFont.getSize();
        DEFAULT_TEXT_FONT_SIZE = DefaultTextFont.getSize();

        DEFAULT_UI_FONT_WEIGHT = FontWeight.findByName(DefaultUIFont.getStyle());
        DEFAULT_TEXT_FONT_WEIGHT = FontWeight.findByName(DefaultTextFont.getStyle());
    }
}
