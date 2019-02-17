package org.glavo.nbt;

import javafx.scene.image.Image;

public enum TagType {
    TAG_End,
    TAG_Byte,
    TAG_Short,
    TAG_Int,
    TAG_Long,
    TAG_Float,
    TAG_Double,
    TAG_Byte_Array(true),
    TAG_String,
    TAG_List(true),
    TAG_Compound(true),
    TAG_Int_Array(true),
    TAG_Long_Array(true);

    public final Image icon;
    public final boolean hasChildren;

    TagType() {
        this(null);
    }

    TagType(Image icon) {
        this(icon, false);
    }

    TagType(boolean hasChildren) {
        this(null, hasChildren);
    }

    TagType(Image icon, boolean hasChildren) {
        this.icon = icon;
        this.hasChildren = hasChildren;
    }

    private static TagType[] _values = TagType.values();

    public static TagType tagOf(byte tag) {
        if (tag >= 0 && tag < _values.length) {
            return _values[tag];
        }
        return null;
    }
}
