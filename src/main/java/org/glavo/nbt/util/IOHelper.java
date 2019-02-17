package org.glavo.nbt.util;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public final class IOHelper {
    public static List<Path> mapFileList(List<? extends File> f) {
        int size = f.size();
        Path[] arr = new Path[size];
        int i = 0;
        for (File file : f) {
            arr[i] = file == null ? null : file.toPath();
            i++;
        }
        return Arrays.asList(arr);
    }
}
