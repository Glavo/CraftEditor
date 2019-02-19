package org.glavo.craft.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    public static String pathToURIString(Path path) {
        try {
            return URLDecoder.decode(path.toUri().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    public static URL pathToURL(Path path) {
        try {
            return new URL(URLDecoder.decode(path.toUri().toString(), "UTF-8"));
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path uriToPath(URI uri) {
        Objects.requireNonNull(uri);
        try {
            return Paths.get(uri);
        } catch (FileSystemNotFoundException e) {
            try {
                FileSystems.newFileSystem(uri, Collections.emptyMap());
                return Paths.get(uri);
            } catch (IOException e1) {
                return null;
            }
        }
    }

    public static Path urlToPath(URL url) {
        Objects.requireNonNull(url);
        try {
            return uriToPath(url.toURI());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static Path toPath(String str) {
        Objects.requireNonNull(str);
        try {
            return uriToPath(new URI(str));
        } catch (URISyntaxException | IllegalArgumentException e) {
            return null;
        }
    }
}
