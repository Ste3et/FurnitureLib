package de.Ste3et_C0st.FurnitureLib.NBT;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LimitStream extends FilterInputStream {
    private final NBTReadLimiter limit;

    public LimitStream(InputStream is, NBTReadLimiter limit) {
        super(is);
        this.limit = limit;
    }

    public int read() throws IOException {
        this.limit.readBytes(1L);
        return super.read();
    }

    public int read(byte[] b) throws IOException {
        this.limit.readBytes(b.length);
        return super.read(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        this.limit.readBytes(len);
        return super.read(b, off, len);
    }
}
