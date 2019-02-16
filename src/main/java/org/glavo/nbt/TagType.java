package org.glavo.nbt;

public @interface TagType {
    int TAG_End = 0;
    int TAG_Byte = 1;
    int TAG_Short = 2;
    int TAG_Int = 3;
    int TAG_Long = 4;
    int TAG_Float = 5;
    int TAG_Double = 6;
    int TAG_Byte_Array = 7;
    int TAG_String = 8;
    int TAG_List = 9;
    int TAG_Compound = 10;
    int TAG_Int_Array = 11;
    int TAG_Long_Array = 12;
}
