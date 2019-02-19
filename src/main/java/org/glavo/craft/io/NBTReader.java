package org.glavo.craft.io;

import org.glavo.craft.TagType;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public final class NBTReader extends DataInputStream {
    public final Boolean compressed;

    public NBTReader(InputStream in) {
        super(in);
        this.compressed = null;
    }

    public NBTReader(InputStream in, boolean compressed) {
        super(in);
        this.compressed = compressed;
    }

    public TagType readTag() throws IOException {
        TagType tag = TagType.tagOf(readByte());
        return tag;
    }

    public static NBTReader open(Path p) throws IOException {
        Objects.requireNonNull(p);
        FileChannel channel = FileChannel.open(p, StandardOpenOption.READ);
        ByteBuffer flag = ByteBuffer.allocate(2);
        channel.read(flag);
        flag.flip();
        byte b1 = flag.get();
        byte b2 = flag.get();
        channel.position(0);
        //noinspection ConstantConditions
        return (b1 == (byte) 0x1F && b2 == (byte) 0x8B) ?
                new NBTReader(new GZIPInputStream(Channels.newInputStream(channel)), true) :
                new NBTReader(new BufferedInputStream(Channels.newInputStream(channel)), false);
    }

}
