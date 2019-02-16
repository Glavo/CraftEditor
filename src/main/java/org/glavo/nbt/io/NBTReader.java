package org.glavo.nbt.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public final class NBTReader extends DataInputStream {
    public NBTReader(InputStream in) {
        super(in);
    }


    public static NBTReader open(Path p) throws IOException {
        Objects.requireNonNull(p);
        String name = p.getFileName().toString();
        Objects.requireNonNull(name);

        boolean compressed = !"Servers.dat".equals(name) && !"idcounts.dat".equals(name); //TODO
        return open(p, compressed);
    }

    public static NBTReader open(Path p, boolean compressed) throws IOException {
        Objects.requireNonNull(p);
        if (Files.notExists(p)) {
            throw new FileNotFoundException(p + " (No such file or directory)");
        }
        if (Files.isDirectory(p)) {
            throw new FileNotFoundException(p + " (Is a directory)");
        }

        InputStream in = Files.newInputStream(p);

        if (compressed) {
            return new NBTReader(new GZIPInputStream(in));
        } else {
            return new NBTReader(new BufferedInputStream(in));
        }
    }
}
